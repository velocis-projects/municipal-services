package org.egov.hc.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import java.util.UUID;

import java.util.stream.Collectors;


import org.apache.commons.lang3.StringUtils;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.hc.consumer.HCNotificationConsumer;

import org.egov.hc.contract.RequestInfoWrapper;
import org.egov.hc.contract.ResponseInfoWrapper;

import org.egov.hc.contract.ServiceRequest;

import org.egov.hc.contract.ServiceResponse;
import org.egov.hc.model.ActionHistory;
import org.egov.hc.model.ActionInfo;


import org.egov.hc.model.ServiceRequestData;
import org.egov.hc.model.auditDetails;
import org.egov.hc.model.RequestData;

import org.egov.hc.model.user.Citizen;
import org.egov.hc.model.user.CreateUserRequest;
import org.egov.hc.model.user.UserResponse;
import org.egov.hc.model.user.UserSearchRequest;
import org.egov.hc.model.user.UserType;
import org.egov.hc.producer.HCProducer;
import org.egov.hc.producer.HCConfiguration;

import org.egov.hc.repository.IdGenRepository;
import org.egov.hc.repository.ServiceRepository;

import org.egov.hc.utils.DeviceSource;

import org.egov.hc.utils.HCConstants;
import org.egov.hc.utils.HCUtils;
import org.egov.hc.utils.ResponseInfoFactory;
import org.egov.hc.utils.WorkFlowConfigs;

import org.egov.hc.web.models.Idgen.IdGenerationResponse;
import org.egov.hc.workflow.Document;
import org.egov.hc.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class ServiceRequestService {

	@Autowired
	private ResponseInfoFactory factory;

	@Autowired
	private HCConfiguration hcConfiguration;

	@Autowired
	private HCUtils hCUtils;

	@Autowired
	private HCProducer hCProducer;

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private HCNotificationConsumer notificationConsumer;

	@Autowired
	private WorkflowIntegrator wfIntegrator;

	@Autowired
	private IdGenRepository idgenrepository;

	@Autowired
	private DeviceSource deviceSource;

	@Autowired
	private RestTemplate rest;

	@Autowired
	public ServiceRequestService(HCConfiguration config, HCConfiguration hcConfiguration,
			WorkflowIntegrator wfIntegrator, ObjectMapper objectMapper, IdGenRepository idgenrepository,
			ServiceRepository serviceRepository, HCNotificationConsumer notificationConsumer, HCConstants hcConstants) {

		
		this.hcConfiguration = hcConfiguration;
		this.idgenrepository = idgenrepository;
		this.notificationConsumer = notificationConsumer;
		this.wfIntegrator = wfIntegrator;

	}

	// private Producer producer;

	/***
	 * Asynchronous method performs business logic if any and adds the data to
	 * persister queue on create topic
	 * 
	 * @param request
	 */

	public ServiceResponse create(ServiceRequest request, String requestHeader) {
		log.info("Service layer for createss");

		// generate Service request id using IdGen

		String service_request_id = generateServiceRequestId(request);

		enrichserviceRequestForcreate(request, service_request_id);

		// call workflow service if it's enable else uses internal workflow process
		
		if (hcConfiguration.getIsExternalWorkFlowEnabled()) {
			wfIntegrator.callWorkFlow(request, service_request_id);
		}

		String role = HCConstants.ROLE;
		String status = HCConstants.INITIATED_STATUS;
		String history_service_request_id = null;

		serviceRequest(request, service_request_id, requestHeader, role,
				status, history_service_request_id);

		return getServiceResponse(request);
	}

	private String generateServiceRequestId(ServiceRequest request) {

		String service_request_id = "";
		IdGenerationResponse id = idgenrepository.getId(request.getRequestInfo(), request.getServices().get(0).getTenantId(),
				hcConfiguration.getApplicationNumberIdgenName(), hcConfiguration.getApplicationNumberIdgenFormat(), 1);
		if (id.getIdResponses() != null && id.getIdResponses().get(0) != null) {
			// serviceRequest.setService_request_id(id.getIdResponses().get(0).getId());
			service_request_id = id.getIdResponses().get(0).getId();
		} else
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
					"service_request_id Generation Failed");

		return service_request_id;

	}

	private ServiceRequestData parseServiceRequestData(JSONObject serviceRequest) {
		ServiceRequestData serviceRequestData = new ServiceRequestData();
		try {

			// Get service request object within list

			String ownerName = (String) serviceRequest.get("owner_name");
			serviceRequestData.setOwnerName(ownerName);

			String serviceType = (String) serviceRequest.get("service_type");
			serviceRequestData.setServiceType(serviceType);

			String current_assignee = (String) serviceRequest.get("current_assignee");
			serviceRequestData.setCurrent_assignee(current_assignee);

			String houseNoAndStreetName = (String) serviceRequest.get("houseNoAndStreetName");
			serviceRequestData.setHouseNoAndStreetName(houseNoAndStreetName);

			String landmark = (String) serviceRequest.get("landmark");
			serviceRequestData.setLandmark(landmark);

			String latitude = (String) serviceRequest.get("latitude");
			serviceRequestData.setLatitude(latitude);

			String longitude = (String) serviceRequest.get("longitude");
			serviceRequestData.setLongitude(longitude);

			String mohalla = (String) serviceRequest.get("mohalla");
			serviceRequestData.setMohalla(mohalla);

			String createdBy = (String) serviceRequest.get("createdby");
			serviceRequestData.setCreatedBy(createdBy);

			String service_request_status = (String) serviceRequest.get("service_request_status");
			serviceRequestData.setService_request_status(service_request_status);

			String service_request_uuid = (String) serviceRequest.get("service_request_uuid");
			serviceRequestData.setService_request_uuid(service_request_uuid);
			
			String servicerequest_lang = (String) serviceRequest.get("servicerequest_lang");
			serviceRequestData.setServicerequest_lang(servicerequest_lang);

			String contactNumber = (String) serviceRequest.get("contact_number");
			// System.out.println(contactNumber);
			serviceRequestData.setContactNumber(contactNumber);

			String email = (String) serviceRequest.get("email_id");
			// System.out.println(email);

			serviceRequestData.setEmail(email);

			String tenantId = (String) serviceRequest.get("tenantId");
			serviceRequestData.setTenantId(tenantId);

			org.json.JSONObject objDocument = new org.json.JSONObject(
					serviceRequest.get("service_request_document").toString());

			org.json.JSONArray documentList = objDocument.getJSONArray("document");

			List<String> newdoclist = new ArrayList<>();

			for (int i = 0; i < documentList.length(); i++) {

				org.json.JSONObject empDetails = new org.json.JSONObject(documentList.get(i).toString());

				String document = empDetails.getString("media");

				newdoclist.add(document);

			}

			serviceRequestData.setMediaList(newdoclist);

		} catch (Exception ex) {

		}

		return serviceRequestData;
	}

	/**
	 * Asynchronous method performs business logic if any and adds the data to
	 * persister queue on update topic
	 * 
	 * @param request
	 */
	public ServiceResponse update(ServiceRequest request, String requestHeader) {
		enrichServiceRequestForUpdate(request, requestHeader);
		if (null == request.getActionInfo())
			request.setActionInfo(new ArrayList<ActionInfo>());

		return getServiceResponse(request);
	}

	/**
	 * private method to enrich request with Ids and action infos for create
	 * 
	 * @param serviceRequest
	 */
	private void enrichserviceRequestForcreate(ServiceRequest serviceRequest, String service_request_id) {
		log.info("enriching service request create.");
		Map<String, String> actionStatusMap = WorkFlowConfigs.getActionStatusMap();
		RequestInfo requestInfo = serviceRequest.getRequestInfo();

		List<ServiceRequestData> serviceReqs = serviceRequest.getServices();

		String tenantId = serviceReqs.get(0).getTenantId();
		overRideCitizenAccountId(serviceRequest);
		validateAndCreateUser(serviceRequest);


		auditDetails auditDetails = hCUtils.getAuditDetails(String.valueOf(requestInfo.getUserInfo().getId()), true);
		String by = auditDetails.getCreatedBy() + ":" + requestInfo.getUserInfo().getRoles().get(0).getName();
		List<ActionInfo> actionInfos = new LinkedList<>();
		if (!CollectionUtils.isEmpty(serviceRequest.getActionInfo())) {
			actionInfos = serviceRequest.getActionInfo();
		}
		for (int servReqCount = 0; servReqCount < serviceReqs.size(); servReqCount++) {

			serviceRequest.getServices().get(servReqCount).setIsRoleSpecific(true);
			serviceRequest.getServices().get(servReqCount).setAction(HCConstants.INITIATE);
			serviceRequest.getServices().get(servReqCount).setService_request_id(service_request_id);

			ActionInfo actionInfo = null;
			try {
				actionInfo = actionInfos.get(servReqCount);
				if (null != actionInfo) {
					actionInfo.setUuid(UUID.randomUUID().toString());
					actionInfo.setBusinessKey(serviceReqs.get(servReqCount).getBusinessService());
					actionInfo.setAction(WorkFlowConfigs.ACTION_OPEN);
					actionInfo.setAssignee(requestInfo.getUserInfo().getId().toString());
					actionInfo.setBy(by);
					actionInfo.setWhen(auditDetails.getCreatedTime());
					actionInfo.setTenantId(tenantId);
					actionInfo.setStatus(actionStatusMap.get(WorkFlowConfigs.ACTION_OPEN));
				} else {
					ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
							.businessKey(serviceReqs.get(servReqCount).getBusinessService())
							.action(WorkFlowConfigs.ACTION_OPEN).assignee(null).by(by)
							.when(auditDetails.getCreatedTime()).tenantId(tenantId)
							.status(actionStatusMap.get(WorkFlowConfigs.ACTION_OPEN)).build();
					actionInfos.add(newActionInfo);
				}
			} catch (Exception e) {
				ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
						.businessKey(serviceReqs.get(servReqCount).getBusinessService())
						.action(WorkFlowConfigs.ACTION_OPEN).assignee(null).by(by).when(auditDetails.getCreatedTime())
						.tenantId(tenantId).status(actionStatusMap.get(WorkFlowConfigs.ACTION_OPEN)).build();
				actionInfos.add(newActionInfo);
			}

		}
		serviceRequest.setActionInfo(actionInfos);
	}

	/**
	 * Override the accountId of every request with the user id
	 * 
	 * @param serviceRequest
	 */
	private void overRideCitizenAccountId(ServiceRequest serviceRequest) {
		User user = serviceRequest.getRequestInfo().getUserInfo();
		List<String> codes = user.getRoles().stream().map(Role::getCode).collect(Collectors.toList());
		if (codes.contains(HCConstants.ROLE_CITIZEN) || codes.contains(HCConstants.ROLE_NAME_CITIZEN))
			serviceRequest.getServices().forEach(service -> service.setAccountId(String.valueOf(user.getId())));
	}

	/**
	 * Checks if the user is present for the given citizen object.
	 * 
	 * @param citizen
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	private String isUserPresent(Citizen citizen, RequestInfo requestInfo, String tenantId) {
		ObjectMapper mapper = hCUtils.getObjectMapper();
		UserSearchRequest searchRequest = UserSearchRequest.builder().userName(citizen.getMobileNumber())
				.tenantId(tenantId).userType(HCConstants.ROLE_CITIZEN).requestInfo(requestInfo).build();
		StringBuilder url = new StringBuilder(
				hcConfiguration.getUserBasePath() + hcConfiguration.getUserSearchEndPoint());
		UserResponse res = mapper.convertValue(serviceRepository.fetchResult(url, searchRequest), UserResponse.class);
		if (CollectionUtils.isEmpty(res.getUser())) {
			return null;
		}
		return res.getUser().get(0).getId().toString();
	}

	/**
	 * When CSR files a complaint, this method captures the user information if the
	 * user exists otherwise creates the user.
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	private void validateAndCreateUser(ServiceRequest serviceRequest) {
		RequestInfo requestInfo = serviceRequest.getRequestInfo();
		List<String> roles = requestInfo.getUserInfo().getRoles().stream().map(Role::getCode)
				.collect(Collectors.toList());
		if (roles.contains(HCConstants.ROLE_NAME_CSR) || roles.contains(HCConstants.ROLE_CSR)) {
			serviceRequest.getServices().stream().forEach(request -> {
				String accId = null;
				if (null != request.getCitizen()) {
					accId = isUserPresent(request.getCitizen(), requestInfo, request.getTenantId());
					if (StringUtils.isEmpty(accId)) {
						accId = createUser(request.getCitizen(), requestInfo, request.getTenantId());
					}
					request.setAccountId(accId);
				}
			});
		}
	}

	/**
	 * This method creates user in user svc.
	 * 
	 * @param citizen
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	private String createUser(Citizen citizen, RequestInfo requestInfo, String tenantId) {
		ObjectMapper mapper = hCUtils.getObjectMapper();
		citizen.setUserName(citizen.getMobileNumber());
		citizen.setActive(true);
		citizen.setTenantId(tenantId);
		citizen.setType(UserType.CITIZEN);
		citizen.setRoles(Arrays.asList(org.egov.hc.model.user.Role.builder().code(HCConstants.ROLE_CITIZEN).build()));
		StringBuilder url = new StringBuilder(
				hcConfiguration.getUserBasePath() + hcConfiguration.getUserCreateEndPoint());
		CreateUserRequest req = CreateUserRequest.builder().citizen(citizen).requestInfo(requestInfo).build();
		UserResponse res = mapper.convertValue(serviceRepository.fetchResult(url, req), UserResponse.class);
		return res.getUser().get(0).getId().toString();
	}

	/**
	 * Util method for the update to enrich the actions in the request
	 * 
	 * @param request
	 */
	private void enrichServiceRequestForUpdate(ServiceRequest request, String requestHeader) {
		
		Map<String, List<String>> errorMap = new HashMap<>();
		RequestInfo requestInfo = request.getRequestInfo();
		List<ServiceRequestData> serviceReqs = request.getServices();
		List<ActionInfo> actionInfos = new ArrayList<>();
		String action = null;

		final auditDetails auditDetails = hCUtils.getAuditDetails(String.valueOf(requestInfo.getUserInfo().getId()),
				false);
		String by = auditDetails.getCreatedBy() + ":" + requestInfo.getUserInfo().getRoles().get(0).getName();
		for (int servReqCount = 0; servReqCount < serviceReqs.size(); servReqCount++) {

			String service_request_id = request.getServices().get(servReqCount).getService_request_id();
			List<String> roleList = request.getServices().get(servReqCount).getRoleList();
			List<String> assigneeList = new ArrayList<>();
			if (null != roleList && !roleList.isEmpty()) {
				request.getServices().get(servReqCount).setRole(roleList.get(0));
			}
			if (request.getServices().get(servReqCount).getIsRoleSpecific().equals(true)) {
				
				
				if ((request.getServices().get(servReqCount).getAction().equals(HCConstants.INSPECT))
						|| (request.getServices().get(servReqCount).getAction().equals(HCConstants.APPROVE))) {
					request.getServices().get(servReqCount).setRole(HCConstants.JUNIER_ENGINEERE);
				}

				if (request.getServices().get(servReqCount).getAction().equals(HCConstants.VERIFY_AND_FORWARD_TO_SDO)) {
					request.getServices().get(servReqCount).setRole(HCConstants.SDO);
				}
				if (request.getServices().get(servReqCount).getAction().equals(HCConstants.FORWARD_FOR_INSPECTION)) {
					request.getServices().get(servReqCount).setRole(HCConstants.JUNIER_ENGINEERE);
				}
				
				log.info("Select role depend upon the action");
				

				if (null != request.getServices().get(servReqCount).getRoleList()
						&& request.getServices().get(servReqCount).getRoleList().size() > 0) {

					String role = request.getServices().get(servReqCount).getRoleList().get(0);
					assigneeList = serviceRepository.getRoleDetails(request, role);
				}

			} else {

				assigneeList = request.getServices().get(servReqCount).getAssignee();
			}

			

			RequestData requestData = new RequestData();
			requestData.setService_request_id(service_request_id);
			requestData.setRequestInfo(request.getRequestInfo());

			// get data from service request table with request id

			ServiceRequestData serviceRequest = null;
			JSONObject serviceRequestJson = serviceRepository.getServiceRequestData(requestData);
			serviceRequest = parseServiceRequestData(serviceRequestJson);

			log.info("Get details with service request id : " + serviceRequest );
			

			// call workflow

			if (hcConfiguration.getIsExternalWorkFlowEnabled()) {
				wfIntegrator.callWorkFlow(request, service_request_id);
			}

			// Update service request form

			ServiceRequestData updateRequest = new ServiceRequestData();

			List<String> documentlist = new ArrayList<>(serviceRequest.getMediaList());

			for (int fileCnt = 0; fileCnt < request.getServices().get(0).getWfDocuments().size(); fileCnt++) {
				documentlist.add(request.getServices().get(0).getWfDocuments().get(fileCnt).getFileStoreId());
			}

			JSONObject documentDetailsJson = new JSONObject();
			JSONArray jsonArray = new JSONArray();

			for (String document : documentlist) {

				JSONObject formDetailsJson = new JSONObject();
				formDetailsJson.put("media", document);

				jsonArray.add(formDetailsJson);
			}
			documentDetailsJson.put("document", jsonArray); // Here you can see the data in json forma

			serviceRequest.setServiceMedia(documentDetailsJson.toJSONString());

			updateRequest.setService_request_id(service_request_id);

			if (request.getServices().get(servReqCount).getAction().equals(HCConstants.APPROVE))
				updateRequest.setService_request_status(HCConstants.APPROVED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.COMPLETE))
				updateRequest.setService_request_status(HCConstants.COMPLETED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.VERIFY_AND_FORWARD))
				updateRequest.setService_request_status(HCConstants.ASSIGNED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.VERIFY_AND_FORWARD_TO_SDO))
				updateRequest.setService_request_status(HCConstants.ASSIGNED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.INSPECT))
				updateRequest.setService_request_status(HCConstants.INSPECTED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.REQUEST_CLARIFICATION))
				updateRequest.setService_request_status(HCConstants.ASSIGNED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.REJECT))
				updateRequest.setService_request_status(HCConstants.REJECTED_STATUS);
			else if (request.getServices().get(servReqCount).getAction().equals(HCConstants.FORWARD_FOR_INSPECTION))
				updateRequest.setService_request_status(HCConstants.INSPECTED_STATUS);

			updateRequest.setServiceMedia(documentDetailsJson.toJSONString());

			if (request.getServices().get(servReqCount).getIsRoleSpecific().equals(true))
				updateRequest.setCurrent_assignee(request.getServices().get(servReqCount).getRole());
			else {
				if (null != request.getServices().get(servReqCount).getAssignee()
						&& !request.getServices().get(servReqCount).getAssignee().isEmpty())
					updateRequest.setCurrent_assignee(request.getServices().get(servReqCount).getAssignee().get(0));
			}
			RequestInfoWrapper infowraperforupdate = RequestInfoWrapper.builder().requestBody(updateRequest).build();

			// notification to user when reject or completed request

			action = request.getServices().get(servReqCount).getAction().replace(" ", "");
			request.getServices().get(servReqCount).setAction(action);
			if (request.getServices().get(servReqCount).getAction().equals(WorkFlowConfigs.ACTION_REJECT)
					|| (request.getServices().get(servReqCount).getAction().equals(WorkFlowConfigs.ACTION_COMPLETE)))

			{

				request.getServices().get(0).setContactNumber(serviceRequest.getContactNumber());
				request.getServices().get(0).setOwnerName(serviceRequest.getOwnerName());
				request.getServices().get(0).setEmail(serviceRequest.getEmail());
				request.getServices().get(0).setService_request_status(request.getServices().get(servReqCount).getAction());
				String msgId = requestInfo.getMsgId().split("[|]")[0];
				requestInfo.setMsgId(msgId+"|"+serviceRequest.getServicerequest_lang());
				
				if (serviceRequest != null) {
					ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
							.businessKey(serviceReqs.get(servReqCount).getBusinessService())
							.action(action)
							.assignee(serviceRequest.getCreatedBy()).by(by).when(auditDetails.getCreatedTime())
							.tenantId(serviceReqs.get(servReqCount).getTenantId())
							.status(action).build();
					actionInfos.add(newActionInfo);
				}

				infowraperforupdate = RequestInfoWrapper.builder().actionInfo(actionInfos).requestInfo(requestInfo)
						.requestBody(updateRequest).services(request.getServices()).build();

				log.info("Update service request");
				
				hCProducer.push(hcConfiguration.getUpdateTopic(), infowraperforupdate);

			} else {
				
				String msgId = request.getRequestInfo().getMsgId().split("[|]")[0];
				request.getRequestInfo().setMsgId(msgId+"|"+hcConfiguration.getFallbackLocale());
				
				if(request.getServices().get(servReqCount).getIsRoleSpecific().equals(true))
				{
					Map<String, String> messageMap = notificationConsumer.getLocalizationMessage(HCConstants.TENANT_ID_CITIZEN,
							request.getRequestInfo());
					String role = request.getServices().get(0).getRole();
					
					//notificationConsumer.getRolewiseUserList(request, role, messageMap);
					notificationConsumer.getRolewiseUserList(request, role, messageMap);
				}
				else
				{

					
				if (null != assigneeList && assigneeList.size() > 0) {
					for (String assignee : assigneeList) {
						ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
								.businessKey(serviceReqs.get(servReqCount).getBusinessService())
								.action(action).assignee(assignee).by(by)
								.when(auditDetails.getCreatedTime())
								.tenantId(serviceReqs.get(servReqCount).getTenantId())
								.status(action).build();
						actionInfos.add(newActionInfo);
					}

				}
				}

				infowraperforupdate = RequestInfoWrapper.builder().actionInfo(actionInfos).requestInfo(requestInfo)
						.requestBody(updateRequest).services(request.getServices()).build();

				hCProducer.push(hcConfiguration.getUpdateTopic(), infowraperforupdate);

			}

		}

		if (!errorMap.isEmpty()) {
			Map<String, String> newMap = new HashMap<>();
			errorMap.keySet().forEach(key -> newMap.put(key, errorMap.get(key).toString()));
			throw new CustomException(newMap);
		}
	}

	/**
	 * returns ServiceResponse fetched from database/built based on the given
	 * ServiceRequest
	 * 
	 * @param serviceReqRequest
	 * @return serviceReqResponse
	 */
	public ServiceResponse getServiceResponse(ServiceRequest serviceReqRequest) {
		return ServiceResponse.builder()
				.responseInfo(factory.createResponseInfoFromRequestInfo(serviceReqRequest.getRequestInfo(), true))
				.services(serviceReqRequest.getServices())
				.actionHistory(convertActionInfosToHistorys(serviceReqRequest.getActionInfo())).build();
	}

	public ServiceResponse getServiceResponse(ResponseEntity<ServiceRequest> serviceReqRequest) {
		ServiceRequest objServiceRequest = serviceReqRequest.getBody();

		return ServiceResponse.builder()
				.responseInfo(factory.createResponseInfoFromRequestInfo(objServiceRequest.getRequestInfo(), true))
				.services(objServiceRequest.getServices())
				.actionHistory(convertActionInfosToHistorys(objServiceRequest.getActionInfo())).build();
	}

	/**
	 * helper method to convert list of actioninfos to list of actionHistorys
	 * 
	 * @param actionInfos
	 * @return
	 */
	private List<ActionHistory> convertActionInfosToHistorys(List<ActionInfo> actionInfos) {
		List<ActionHistory> historys = new ArrayList<>();
		if (!CollectionUtils.isEmpty(actionInfos))
			actionInfos.forEach(a -> {
				List<ActionInfo> infos = new ArrayList<>();
				infos.add(a);
				historys.add(new ActionHistory(infos));
			});
		return historys;
	}

	/**
	 * Method fetches a map of code vs name of the boundary types as per the
	 * request.
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param mohallaCodes
	 * @param hierarchyType
	 * @param boundaryType
	 * @return
	 */

	private ResponseEntity<ServiceRequest> serviceRequest(ServiceRequest request, String service_request_id,
			String requestHeader, String role, String status, String history_service_request_id) {

		RequestInfoWrapper infoWrapper = new RequestInfoWrapper();
		try {

			if (null != request.getServices() && !request.getServices().isEmpty()) {
			
				String service_request_uuid = UUID.randomUUID().toString();

				deviceSource.saveDeviceDetails(requestHeader, HCConstants.DEVICE_HORTICULTURE,
						request.getServices().get(0).getTenantId(), HCConstants.MODULE_CODE, request.getAuditDetails(),
						service_request_uuid);

				List<String> documentList = new ArrayList<>();

				if (request.getServices().get(0).getIsEditState() == 1) {
					documentList.addAll(request.getServices().get(0).getMediaList());

					JSONObject documentDetailsJson = new JSONObject();
					JSONArray jsonArray = new JSONArray();

					for (String document : documentList) {

						JSONObject formDetailsJson = new JSONObject();
						formDetailsJson.put("media", document);

						jsonArray.add(formDetailsJson);
					}
					documentDetailsJson.put("document", jsonArray);

					request.getServices().get(0).setServiceMedia(documentDetailsJson.toJSONString());
					request.getServices().get(0).setService_request_id(service_request_id);
					request.getServices().get(0).setHistory_service_request_id(history_service_request_id);

				} else {

					documentList.addAll(request.getServices().get(0).getMedia());

					JSONObject documentDetailsJson = new JSONObject();
					JSONArray jsonArray = new JSONArray();

					for (String document : documentList) {

						JSONObject formDetailsJson = new JSONObject();
						formDetailsJson.put("media", document);

						jsonArray.add(formDetailsJson);
					}
					documentDetailsJson.put("document", jsonArray); // Here you can see the data in json forma

					request.getServices().get(0).setServiceMedia(documentDetailsJson.toJSONString());

				}
				String langData = request.getRequestInfo().getMsgId().replace("|", "#");
				String langDataSplit[] = langData.split("#");

				request.getServices().get(0).setServicerequest_lang(langDataSplit[1]);

				request.getServices().get(0)
						.setServiceType(request.getServices().get(0).getServiceType().toUpperCase());
				request.getServices().get(0).setService_request_uuid(service_request_uuid);
				request.getServices().get(0).setCurrent_assignee(role);
				request.getServices().get(0).setService_request_status(status);
				request.getServices().get(0).setCreatedBy(request.getAuditDetails().getCreatedBy());
				request.getServices().get(0).setCreatedTime(request.getAuditDetails().getCreatedTime());
				request.getServices().get(0).setLastModifiedBy(request.getAuditDetails().getLastModifiedBy());
				request.getServices().get(0).setLastModifiedTime(request.getAuditDetails().getLastModifiedTime());

				List<ServiceRequestData> applicatinFormList = new ArrayList<>();
				applicatinFormList.add(request.getServices().get(0));

				List<ActionInfo> actionInfos = new ArrayList<>();

				final auditDetails auditDetails = hCUtils
						.getAuditDetails(String.valueOf(request.getRequestInfo().getUserInfo().getId()), false);
				String by = auditDetails.getCreatedBy() + ":"
						+ request.getRequestInfo().getUserInfo().getRoles().get(0).getName();

				ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())

						.action(WorkFlowConfigs.ACTION_OPEN).assignee(request.getServices().get(0).getCreatedBy())
						.by(by).when(request.getServices().get(0).getCreatedTime())
						.tenantId(request.getServices().get(0).getTenantId()).status(WorkFlowConfigs.ACTION_OPEN)
						.build();
				actionInfos.add(newActionInfo);

				infoWrapper = RequestInfoWrapper.builder().services(applicatinFormList).actionInfo(actionInfos)
						.requestInfo(request.getRequestInfo()).requestBody(request.getServices().get(0)).build();
				
				log.info("Service request create : " + infoWrapper);

				hCProducer.push(hcConfiguration.getSaveTopic(), infoWrapper);

			}
		} catch (Exception e) {
			throw new CustomException("COMMITTEE_EXCEPTION", e.getMessage());
		}

		return new ResponseEntity<>(ServiceRequest.builder()
				.responseInfo(ResponseInfo.builder().status("Success").build()).responseBody(infoWrapper).build(),
				HttpStatus.OK);

	}

	public ResponseEntity<?> getServiceRequestDetails(RequestData requestData) {
		JSONArray serviceRequest = serviceRepository.getServiceRequest(requestData);
		String bisinesssla = getBussinessServiceSla(requestData);

		if (serviceRequest == null || serviceRequest.isEmpty()) {
			return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().msgId("Invalid service request Id").build())

					.responseBody(serviceRequest).build(), HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(ResponseInfoWrapper.builder()
					.responseInfo(ResponseInfo.builder().status(HCConstants.SUCCESS).resMsgId(bisinesssla).build())
					.responseBody(serviceRequest).build(), HttpStatus.OK);
		}
	}

	private String getBussinessServiceSla(RequestData requestData) {
		long sla = 0;
		int days = 0;
		String serviceRequestStatus;
		String serviceRequestId = requestData.getService_request_id();
		String payloadData = "{\"RequestInfo\": {\"userInfo\": {\"roles\": [{ \"code\": \"EE\",\"name\": \"EE\",\"tenantId\": \"ch.chandigarh\"}]}}}";
		try {
			String procesinstanceData = sendPostRequest(
					hcConfiguration.getWfHost().concat(hcConfiguration.getWfProcessSearch()).concat("?").concat("businessIds="
							+ serviceRequestId + "&history=" + HCConstants.TRUE + "&tenantId=" + HCConstants.TENANT_ID),
					payloadData);

			org.json.JSONObject obj = new org.json.JSONObject(procesinstanceData);
			org.json.JSONArray ProcessInstances = obj.getJSONArray("ProcessInstances");

			org.json.JSONObject ProcessInstancesDetails = new org.json.JSONObject(ProcessInstances.get(0).toString());

			org.json.JSONObject stateObject = ProcessInstancesDetails.getJSONObject("state");

			serviceRequestStatus = stateObject.getString("applicationStatus");
			sla = ProcessInstancesDetails.getLong("businesssServiceSla");
			if (serviceRequestStatus.equalsIgnoreCase("REJECTED") || serviceRequestStatus.equalsIgnoreCase("COMPLETED"))
				days = 0;
			else
				days = (int) ((sla / (1000 * 60 * 60 * 24)) % 7);

		} catch (Exception e) {

		}

		return days + "";

	}

	public static String sendPostRequest(String requestUrl, String payload) {
		StringBuffer jsonString = new StringBuffer();
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(payload);
			writer.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
				System.out.println(line);
			}
			br.close();
			connection.disconnect();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return jsonString.toString();
	}

	public ArrayList<String> getUserDetails(String service_request_id) {
		JSONArray serviceRequest = serviceRepository.getuserDetails(service_request_id);
		ArrayList<String> list = new ArrayList<String>();
		JSONArray jsonArray = (JSONArray) serviceRequest;
		if (jsonArray != null) {
			int len = jsonArray.size();
			for (int i = 0; i < len; i++) {
				list.add(jsonArray.get(i).toString());
			}
		}

		return list;

	}

	public ResponseEntity<ServiceResponse> searchRequest(RequestData requestData, RequestInfo requestInfo) {

		List<ServiceRequestData> serviceRequest = null;
		try {
			serviceRequest = serviceRepository.findRequest(requestData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.info("Parse Exception");
		}
		if (serviceRequest == null) {
			return new ResponseEntity(ServiceResponse.builder()
					.responseInfo(ResponseInfo.builder().msgId("Invalid role  or blank data ").build())
					.services(serviceRequest).build(), HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity(
					ServiceResponse.builder().responseInfo(ResponseInfo.builder().status(HCConstants.SUCCESS).build())
							.services(serviceRequest).build(),
					HttpStatus.OK);
		}
	}

	public ServiceResponse updateServiceRequest(ServiceRequest serviceRequest, String requestHeader) {
		ServiceRequestData serviceRequestGet = null;
		String role = null;
		String status = null;
		String service_request_id_new = null;
		String service_request_uuid = null;
		ResponseEntity<ServiceRequest> responseBody = null;
		List<String> documentList = null;

		String service_request_id = serviceRequest.getServices().get(0).getService_request_id();

		// making request data

		RequestData requestdata = new RequestData();
		requestdata.setService_request_id(service_request_id);

		// get service_request data for this id

		JSONObject serviceRequestJson = serviceRepository.getServiceRequestData(requestdata);
		serviceRequestGet = parseServiceRequestData(serviceRequestJson);
		log.info("get deta : "+ serviceRequestGet);
		
		role = serviceRequestGet.getCurrent_assignee();
		status = serviceRequestGet.getService_request_status();
		service_request_uuid = serviceRequestGet.getService_request_uuid();
		documentList = new ArrayList<>();
		for (int docCnt = 0; docCnt < serviceRequestGet.getMediaList().size(); docCnt++) {

			documentList.add(serviceRequestGet.getMediaList().get(docCnt).toString());

		}
		for (int docCnt = 0; docCnt < serviceRequest.getServices().get(0).getMedia().size(); docCnt++) {
			documentList.add((String) serviceRequest.getServices().get(0).getMedia().get(docCnt).toString());
		}

		serviceRequest.getServices().get(0).setMediaList(documentList);

		serviceRequest.getAuditDetails().setCreatedBy(serviceRequestGet.getCreatedBy());

		// add data in device source detail
		
		deviceSource.saveDeviceDetails(requestHeader, HCConstants.DEVICE_HORTICULTURE,
				HCConstants.TENANT_ID_CITIZEN, HCConstants.MODULE_CODE, serviceRequest.getAuditDetails(),
				service_request_id);

		// service request type
		String serviceRequestServiceType = serviceRequestGet.getServiceType().toUpperCase();
		String requestdataServiceType = serviceRequest.getServices().get(0).getServiceType().toUpperCase();

		// checking service request type

		log.info("Checking service requet type");
		if (serviceRequestServiceType.equals(requestdataServiceType)) {

			responseBody = updateServiceRequest(serviceRequest, service_request_id, requestHeader, role, status,
					service_request_uuid);
		} else {

			// generate id Split old Service Request id and append proper version

			String[] splitServiceRequestId = service_request_id.split("_");
			int version = 1;

			if (null != splitServiceRequestId && splitServiceRequestId.length > 1) {
				version = Integer.parseInt(splitServiceRequestId[1]);
				version += 1;
			}
			String service_version = "_" + version;

			if (splitServiceRequestId != null) {
				service_request_id_new = splitServiceRequestId[0].concat(service_version);
				log.info("Generate new service requet ID : " + service_request_id_new);
			}

			// add entry in service request table with service_request_id = current_1
			responseBody = serviceRequest(serviceRequest, service_request_id_new, requestHeader, role, status,
					service_request_id);

			// geting data from processinstance with service_request_id
			ServiceRequest procesinstancedata = getProcesinstanceData(serviceRequest, service_request_id_new,
					service_request_id); // here we use simple query is possible

			// service request id (old) this service request status update(mark as Rejected)
			updateStatus(procesinstancedata, service_request_id_new,
					service_request_id);

			// workflow call for with old service request id
			updateServiceRequestStatus(procesinstancedata, service_request_id, serviceRequestServiceType);

			log.info("updated service request");
			// old notification to citizen
			serviceRequest.getServices().get(0).setService_request_id(service_request_id_new);

		}
		serviceRequest.getServices().get(0).setService_request_id(service_request_id_new);

		return getServiceResponse(responseBody);
	}

	private void updateServiceRequestStatus(ServiceRequest serviceRequest, String serviceRequestId,
			String serviceRequestTypeOld) {

		String comment = HCConstants.COMMENT;

		serviceRequest.getServices().get(0).setService_request_id(serviceRequestId);
		serviceRequest.getServices().get(0).setServiceType(serviceRequestTypeOld);
		serviceRequest.getServices().get(0).setComment(comment);
		serviceRequest.getServices().get(0).setAction(HCConstants.REJECT);

		if (hcConfiguration.getIsExternalWorkFlowEnabled()) {
			wfIntegrator.callWorkFlow(serviceRequest, serviceRequestId);
		}

	}

	private ResponseEntity<ServiceRequest> updateServiceRequest(ServiceRequest request, String service_request_id,
			String requestHeader, String role, String status, String service_request_uuid) {
		RequestInfoWrapper infoWrapper = new RequestInfoWrapper();
		try {

			if (null != request.getServices() && !request.getServices().isEmpty()) {
				
				List<String> documentList = new ArrayList<>();
				documentList.addAll(request.getServices().get(0).getMediaList());

				JSONObject documentDetailsJson = new JSONObject();
				JSONArray jsonArray = new JSONArray();

				for (String document : documentList) {

					JSONObject formDetailsJson = new JSONObject();
					formDetailsJson.put("media", document);

					jsonArray.add(formDetailsJson);
				}
				documentDetailsJson.put("document", jsonArray);
				request.getServices().get(0).setServiceMedia(documentDetailsJson.toJSONString());
				request.getServices().get(0).setService_request_id(service_request_id);
				request.getServices().get(0).setCurrent_assignee(role);
				request.getServices().get(0).setService_request_status(status);
				request.getServices().get(0).setCreatedBy(request.getAuditDetails().getCreatedBy());
				request.getServices().get(0).setCreatedTime(request.getAuditDetails().getCreatedTime());
				request.getServices().get(0).setLastModifiedBy(request.getAuditDetails().getLastModifiedBy());
				request.getServices().get(0).setLastModifiedTime(request.getAuditDetails().getLastModifiedTime());
				List<ServiceRequestData> applicatinFormList = new ArrayList<>();
				applicatinFormList.add(request.getServices().get(0));

				List<ActionInfo> actionInfos = new ArrayList<>();
				final auditDetails auditDetails = hCUtils
						.getAuditDetails(String.valueOf(request.getRequestInfo().getUserInfo().getId()), false);
				String by = auditDetails.getCreatedBy() + ":"
						+ request.getRequestInfo().getUserInfo().getRoles().get(0).getName();

				ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
						.action(HCConstants.ACTION_UPDATE).assignee(request.getServices().get(0).getCreatedBy()).by(by)
						.when(request.getServices().get(0).getCreatedTime())
						.tenantId(request.getServices().get(0).getTenantId()).status(HCConstants.ACTION_UPDATE).build();
				actionInfos.add(newActionInfo);

				request.setActionInfo(actionInfos);

				infoWrapper = RequestInfoWrapper.builder().services(applicatinFormList).actionInfo(actionInfos)
						.requestInfo(request.getRequestInfo()).requestBody(request.getServices().get(0)).build();

				hCProducer.push(hcConfiguration.getUpdateServiceRequestData(), infoWrapper);

			}
		} catch (Exception e) {
			throw new CustomException("COMMITTEE_EXCEPTION", e.getMessage());
		}

		return new ResponseEntity<>(ServiceRequest.builder()
				.responseInfo(ResponseInfo.builder().status("Success").build()).responseBody(infoWrapper).build(),
				HttpStatus.OK);

	}

	private String updateStatus(ServiceRequest serviceRequest, String service_request_id_new,
			String service_request_id) {

		String action = serviceRequest.getServices().get(0).getAction();
		ServiceRequestData updateRequest = new ServiceRequestData();
		updateRequest.setService_request_status(HCConstants.REJECTED_STATUS);
		updateRequest.setService_request_id(service_request_id);
		updateRequest.setComment(HCConstants.COMMENT);
		updateRequest.setAction(action);
		updateRequest.setCurrent_assignee("");
		RequestInfoWrapper infowraperforupdate = RequestInfoWrapper.builder().requestBody(updateRequest).build();
		hCProducer.push(hcConfiguration.getUpdateStatusTopic(), infowraperforupdate);

		return null;
	}

	private ServiceRequest getProcesinstanceData(ServiceRequest serviceRequestGetData, String service_request_id_new,
			String service_request_id) {

		
		String procesinstanceData = null;
		String tenentId = null;
		String businessService = null;
		String action = null;
		String comment=null;
//
//		RequestInfo preservRequestInfo = new RequestInfo();
//
//		ServiceRequestData service = new ServiceRequestData();

	//	RequestInfo preservRequestInfo = serviceRequestGetData.getRequestInfo();
		User userData = new User();
		String tenantId = serviceRequestGetData.getRequestInfo().getUserInfo().getTenantId();
		String name = serviceRequestGetData.getRequestInfo().getUserInfo().getName();
		String userName = serviceRequestGetData.getRequestInfo().getUserInfo().getUserName();
		String uuid = serviceRequestGetData.getRequestInfo().getUserInfo().getUuid();
		Long userId = serviceRequestGetData.getRequestInfo().getUserInfo().getId();
		List<Role> userRoleList = serviceRequestGetData.getRequestInfo().getUserInfo().getRoles();
		String userMobileNumer = serviceRequestGetData.getRequestInfo().getUserInfo().getMobileNumber();
		String userEmail = serviceRequestGetData.getRequestInfo().getUserInfo().getEmailId();
		String type = serviceRequestGetData.getRequestInfo().getUserInfo().getType();

		userData.setType(type);
		userData.setEmailId(userEmail);
		userData.setName(name);
		userData.setId(userId);
		userData.setUuid(uuid);
		userData.setRoles(userRoleList);
		userData.setMobileNumber(userMobileNumer);
		userData.setUserName(userName);
		userData.setTenantId(tenantId);
		

		try {
			procesinstanceData = rest.postForObject(
					hcConfiguration.getWfHost().concat(hcConfiguration.getWfProcessSearch()).concat("?").concat(
							"businessIds=" + service_request_id + "&history=true&tenantId=" + HCConstants.TENANT_ID),
					serviceRequestGetData, String.class);

			try {
				org.json.JSONObject obj = new org.json.JSONObject(procesinstanceData);
				org.json.JSONArray ProcessInstances = obj.getJSONArray("ProcessInstances");

				ServiceRequest serviceRequestData = serviceRequestGetData.clone();
				//String response="";
//				boolean flag=false;
//			int i= ProcessInstances.length() - 1;
				for (int i = ProcessInstances.length() - 1; i >= 0; i--) 
//				while(i >= 0)
				{
//					boolean	response=false;
//							 flag=false;
//					 formulate the new requestInfo as per te workflow

					RequestInfo wfRequestInfo = new RequestInfo();

					System.out.println(ProcessInstances.get(i).toString());
					org.json.JSONObject ProcessInstancesDetails = new org.json.JSONObject(
							ProcessInstances.get(i).toString());

					List<Document> wfAddDocument = new ArrayList<>();

					org.json.JSONObject wfAssigner = ProcessInstancesDetails.getJSONObject("assigner");

					User wfUser = new User();
					if (null != wfAssigner.getString("mobileNumber"))
						wfUser.setMobileNumber(wfAssigner.getString("mobileNumber"));
					else
						wfUser.setMobileNumber("");
					if (null != wfAssigner.getString("name"))
						wfUser.setName(wfAssigner.getString("name"));
					else
						wfUser.setName("");
					if (null != wfAssigner.getString("tenantId"))
						wfUser.setTenantId(wfAssigner.getString("tenantId"));
					else
						wfUser.setTenantId("");
					if (null != wfAssigner.getString("type"))
						wfUser.setType(wfAssigner.getString("type"));
					else
						wfUser.setType("");
					if (!wfAssigner.isNull("emailId"))
						wfUser.setEmailId(wfAssigner.getString("emailId").toString());
					else
						wfUser.setEmailId("");
					wfUser.setUuid(wfAssigner.getString("uuid"));
					long id = wfAssigner.getLong("id");
					wfUser.setId(id);
					if (!wfAssigner.isNull("userName"))
						wfUser.setUserName(wfAssigner.getString("userName"));
					else
						wfUser.setUserName("");

					org.json.JSONArray wfRoles = wfAssigner.getJSONArray("roles");
					List<Role> roleList = new ArrayList();
					if (null != wfRoles) {
						org.json.JSONObject roleDetails = null;
						for (int roleCnt = 0; roleCnt < wfRoles.length(); roleCnt++) {
							roleDetails = new org.json.JSONObject(wfRoles.get(roleCnt).toString());
							Role role = new Role();
							role.setCode(roleDetails.getString("code"));
							role.setName(roleDetails.getString("name"));
							roleList.add(role);

						}
						wfUser.setRoles(roleList);

					}

					if (!ProcessInstancesDetails.isNull("documents")) {
						org.json.JSONArray wfDocuments = ProcessInstancesDetails.getJSONArray("documents");
						if (null != wfDocuments) {
							for (int docCnt = 0; docCnt < wfDocuments.length(); docCnt++) {

								org.json.JSONObject docDetails = new org.json.JSONObject(
										wfDocuments.get(docCnt).toString());
								Document doc = new Document();
								doc.setId(docDetails.getString("id"));

								if (!docDetails.isNull("documentType"))
									doc.setDocumentType(docDetails.getString("documentType"));
								else
									doc.setDocumentType("");
								doc.setFileStoreId(docDetails.getString("fileStoreId"));
								doc.setTenantId(docDetails.getString("tenantId"));

								wfAddDocument.add(doc);

							}
						}
					}

					// fileStoreId,documentType
					if (!ProcessInstancesDetails.isNull("tenantId"))
						tenentId = ProcessInstancesDetails.getString("tenantId");
					else
						tenentId = "";
					if (!ProcessInstancesDetails.isNull("action"))
						action = ProcessInstancesDetails.getString("action");
					else
						action = "";

					businessService = serviceRequestData.getServices().get(0).getServiceType();

					if (!ProcessInstancesDetails.isNull("comment"))
						comment = ProcessInstancesDetails.getString("comment");
					else
						comment = "";

					serviceRequestData.getServices().get(0).setTenantId(tenentId);
					serviceRequestData.getServices().get(0).setAction(action);
					serviceRequestData.getServices().get(0).setTenantId(tenentId);
					serviceRequestData.getServices().get(0).setService_request_id(service_request_id_new);
					serviceRequestData.getServices().get(0).setComment(comment);
					serviceRequestData.getServices().get(0).setServiceType(businessService);
					serviceRequestData.getServices().get(0).setIsRoleSpecific(true);
					serviceRequestData.getServices().get(0).setWfDocuments(wfAddDocument);
					serviceRequestData.getRequestInfo().setUserInfo(wfUser);

					if (hcConfiguration.getIsExternalWorkFlowEnabled()) {
						System.out.println("Process instance call"+i);
						
						 wfIntegrator.callWorkFlow(serviceRequestData, service_request_id_new);
						Thread.sleep(20000);
					}

				}

				List<ActionInfo> actionInfos = new ArrayList<>();
				ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
						.businessKey(serviceRequestData.getServices().get(0).getBusinessService())
						.action(serviceRequestData.getServices().get(0).getAction())

						.tenantId(serviceRequestData.getServices().get(0).getTenantId())
						.status(serviceRequestData.getServices().get(0).getAction()).build();
				actionInfos.add(newActionInfo);
				serviceRequestGetData.setActionInfo(actionInfos);

			} catch (Exception ex) {

			}

		} catch (HttpClientErrorException e) {
			System.out.print("Handled exception");
		}

		serviceRequestGetData.getRequestInfo().setUserInfo(userData);
		
		return serviceRequestGetData;

	}

}
