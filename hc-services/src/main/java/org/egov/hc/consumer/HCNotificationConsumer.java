package org.egov.hc.consumer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.hc.contract.Action;
import org.egov.hc.contract.ActionItem;
import org.egov.hc.contract.EmailRequest;
import org.egov.hc.contract.Event;
import org.egov.hc.contract.EventRequest;
import org.egov.hc.contract.Recepient;
import org.egov.hc.contract.RequestInfoWrapper;
import org.egov.hc.contract.SMSRequest;
import org.egov.hc.contract.ServiceRequest;
import org.egov.hc.contract.ServiceResponse;
import org.egov.hc.model.ActionInfo;
import org.egov.hc.model.ServiceRequestData;
import org.egov.hc.model.Source;
import org.egov.hc.producer.HCConfiguration;
import org.egov.hc.producer.HCProducer;
import org.egov.hc.service.NotificationService;
import org.egov.hc.service.ServiceRequestService;
import org.egov.hc.utils.HCConstants;
import org.egov.hc.utils.HCUtils;
import org.egov.hc.utils.WorkFlowConfigs;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class HCNotificationConsumer {

	@Autowired
	private HCProducer hCProducer;
	
	@Autowired
	private HCConfiguration hcConfiguration;  	

	@Autowired
	private NotificationService notificationService;
	

	@Autowired
	private RestTemplate rest;

	@KafkaListener(topics = { "${kafka.topics.save.service}", "${kafka.topics.update.service}" })

	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		ObjectMapper mapper = new ObjectMapper();
		ServiceRequest serviceReqRequest = new ServiceRequest();
		try {
			serviceReqRequest = mapper.convertValue(record, ServiceRequest.class);

		} catch (final Exception e) {
			log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
		}
		process(serviceReqRequest);
	}

	/**
	 * Sends notifications on different topics for the consumer to pick.
	 *
	 * @param serviceReqRequest
	 */
	public void process(ServiceRequest serviceReqRequest) {

		// get all the messages from localization table
		Map<String, String> messageMap = getLocalizationMessage(HCConstants.TENANT_ID,
				serviceReqRequest.getRequestInfo());

		if (!CollectionUtils.isEmpty(serviceReqRequest.getActionInfo())) {

			sendNotification(serviceReqRequest, messageMap, null);

			// check citizen raised a request, then get Executive engg role user list from
			
			String msgId = serviceReqRequest.getRequestInfo().getMsgId().split("[|]")[0];
			serviceReqRequest.getRequestInfo().setMsgId(msgId+"|"+hcConfiguration.getFallbackLocale());
			
			 messageMap = getLocalizationMessage(HCConstants.TENANT_ID,
					serviceReqRequest.getRequestInfo());
			
			if (serviceReqRequest.getActionInfo().get(0).getAction().equals(WorkFlowConfigs.ACTION_OPEN)) {
				String role = HCConstants.EXECUTIVE_ENGINEER;

				List<String> roleList = getRolewiseUserList(serviceReqRequest, role, messageMap);

			}

		}

	}

	public List getRolewiseUserList(ServiceRequest serviceReqRequest, String role, Map<String, String> messageMap) {

		List requestInfoList = new ArrayList();
		String mobileNumber = null;
		String uuid = null;
	//	String emailId = null;
		String userName = null;

		String allRoles = null;

		//List<ModuleDetail> moduleDetail = new ArrayList<ModuleDetail>();
		//RequestInfo requestInfo = new RequestInfo();

		try {

			allRoles = rest.postForObject(hcConfiguration.getEgovHRMShost().concat(hcConfiguration.getEgovHRMSSearchEndpoint()).concat("?")
					.concat("roles=" + role + "&tenantId=" + HCConstants.TENANT_ID), serviceReqRequest, String.class);

			try {
				org.json.JSONObject obj = new org.json.JSONObject(allRoles);

				org.json.JSONObject rreq = obj.getJSONObject("ResponseInfo");

				org.json.JSONArray employeesList = obj.getJSONArray("Employees");

				// action inforlist
				List<String> employee = new ArrayList<>();
				ServiceRequestData serviceRequest = new ServiceRequestData();

				for (int i = 0; i < employeesList.length(); i++) {

					List Actioninfolist = new ArrayList();
					RequestInfoWrapper infoWrapper = null;
					org.json.JSONObject empDetails = new org.json.JSONObject(employeesList.get(i).toString());

					org.json.JSONObject user = empDetails.getJSONObject("user");

					
					if (!user.isNull("mobileNumber"))
						mobileNumber = user.getString("mobileNumber");
					else
						mobileNumber = user.getString("");
					uuid = user.getString("uuid");
					
					userName = user.getString("userName");
					
					if (!user.isNull("emailId"))
						serviceRequest.setEmail(user.getString("emailId").toString());
					else
						serviceRequest.setEmail("");

					serviceRequest.setOwnerName(userName);
					//serviceRequest.setEmail(emailId);
					serviceRequest.setContactNumber(mobileNumber);
					serviceRequest.setService_request_uuid(uuid);
					serviceRequest.setService_request_id(serviceReqRequest.getServices().get(0).getService_request_id());
					serviceRequest.setIsRoleSpecific(true);
					String by = serviceRequest.getCreatedBy();

					ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
							.businessKey(serviceReqRequest.getServices().get(0).getBusinessService())
							.action(HCConstants.VERIFYANDFORWARD).assignee(uuid).by(by)
							.when(serviceRequest.getCreatedTime())
							.tenantId(serviceReqRequest.getServices().get(0).getTenantId()).status("VERIFYANDFORWARD")
							.build();
					Actioninfolist.add(newActionInfo);

					List<ServiceRequestData> serviceRequestList = new ArrayList<>();
					serviceRequestList.add(serviceRequest);
					RequestInfo requestInfoset = serviceReqRequest.getRequestInfo();
					requestInfoset.getUserInfo().setUuid(uuid);
					requestInfoset.getUserInfo().setName(userName);

					infoWrapper = RequestInfoWrapper.builder().services(serviceRequestList).actionInfo(Actioninfolist)
							.requestInfo(requestInfoset).requestBody(serviceRequest).build();

					ServiceRequest serviceRequestobj = new ServiceRequest();
					serviceRequestobj.setServices(serviceRequestList);
					serviceRequestobj.setActionInfo(Actioninfolist);
					serviceRequestobj.setRequestInfo(requestInfoset);
					sendNotification(serviceRequestobj, messageMap, null);

				}

			}

			catch (Exception ex) {

			}

		} catch (HttpClientErrorException e) {
			System.out.print("Handled exception");
		}
		System.out.println(allRoles);

		return requestInfoList;
	}

	public void sendNotification(ServiceRequest serviceReqRequest, Map<String, String> messageMap,
			String service_request_id_new) {

		for (ActionInfo actionInfo : serviceReqRequest.getActionInfo()) {
			if (null != actionInfo && (!StringUtils.isEmpty(actionInfo.getStatus()))) {
				ServiceRequestData service = serviceReqRequest.getServices().get(0);
				if (isNotificationEnabled(actionInfo.getStatus())) {
					if (hcConfiguration.getIsSMSNotificationEnabled()) {
						SMSRequest smsRequest = prepareSMSRequest(service, actionInfo,
								serviceReqRequest.getRequestInfo(), messageMap, service_request_id_new);
						if(null != smsRequest)
							hCProducer.push(hcConfiguration.getSmsNotifTopic(), smsRequest);
						else
							log.info("SMS notification is empty in this case");
					}

					if (hcConfiguration.getIsEmailNotificationEnabled() && (null != service.getEmail() && !service.getEmail().isEmpty())) {
						EmailRequest emailRequests = prepareEmailRequest(service, actionInfo, serviceReqRequest.getRequestInfo(),messageMap,service_request_id_new);
						if(null != emailRequests)
							hCProducer.push(hcConfiguration.getEmailNotifTopic(), emailRequests);
						else
							log.info("email body is empty in this case");
					}

					if (hcConfiguration.getIsUsrEventNotificationEnabled()) {
						EventRequest request = preparePushNotificationRequest(service, actionInfo,
								serviceReqRequest.getRequestInfo(), messageMap, service_request_id_new);
						if(null != request)
							hCProducer.push(hcConfiguration.getSaveUserEventsTopic(), request);
						else
							log.info("PUSH body is empty in this case");
					}

				} else {
					log.info("Notification disabled for this case!");
					continue;
				}
			} else {
				continue;
			}
		}

	}

	/**
	 * Prepares event to be registered in user-event service, 
	 *
	 * @param serviceReq
	 * @param actionInfo
	 * @param messageMap
	 * @param service_request_id_new
	 * @param requestInf
	 * @return
	 * @messageMap
	 */

	public EventRequest preparePushNotificationRequest(ServiceRequestData serviceReq, ActionInfo actionInfo,
			RequestInfo requestInfo, Map<String, String> messageMap, String service_request_id_new) {

		String emailIdRetrived = null;
		String employeeDetailsRetrived = null;
		String employeeNameRetrived = null;
		String message = null;
		List<Event> events = new ArrayList<>();

		if (StringUtils.isEmpty(actionInfo.getAssignee())
				&& actionInfo.getAction().equals(WorkFlowConfigs.ACTION_OPEN)) {

			message = getMessage(WorkFlowConfigs.ACTION_OPEN, serviceReq, requestInfo, messageMap, null,
					HCConstants.PUSH, service_request_id_new);
			
		} else if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_APPROVE)
				|| actionInfo.getAction().equals(WorkFlowConfigs.ACTION_ASSIGN)
				|| actionInfo.getAction().equals(WorkFlowConfigs.ACTION_REQUEST_FOR_REASSIGN)
				||actionInfo.getAction().equals(WorkFlowConfigs.VERIFY_AND_FORWARD_TO_SDO)
				||actionInfo.getAction().equals(WorkFlowConfigs.FORWARDFORINSPECTION)
				||actionInfo.getAction().equals(WorkFlowConfigs.REQUESTCLARIFICATION)
				||actionInfo.getAction().equals(WorkFlowConfigs.VERIFYANDFORWARD)) {

			employeeNameRetrived = serviceReq.getOwnerName();
			message = getMessage(actionInfo.getAction(), serviceReq, requestInfo, messageMap, employeeNameRetrived,
					HCConstants.PUSH, service_request_id_new);
	

		} else if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_INITIATE)) {
			message = getMessage(WorkFlowConfigs.ACTION_INITIATE, serviceReq, requestInfo, messageMap, null,
					HCConstants.PUSH, service_request_id_new);

		} else

		{
			message = getMessage(actionInfo.getAction(), serviceReq, requestInfo, messageMap, null, HCConstants.PUSH,
					service_request_id_new);
		}

		List<String> toUsers = new ArrayList<>();
		toUsers.add(requestInfo.getUserInfo().getUuid());

		List<String> toRole = new ArrayList<>();
		toRole.add("All");
		Recepient recepient = Recepient.builder().toUsers(toUsers).toRoles(toRole).build();

		

		Event event = Event.builder()
				.tenantId(HCConstants.TENANT_ID).description(message).eventType(HCConstants.USREVENTS_EVENT_TYPE)
				.name(HCConstants.USREVENTS_EVENT_NAME).postedBy(HCConstants.USREVENTS_EVENT_POSTEDBY)
				.source(Source.WEBAPP).recepient(recepient).referenceId(serviceReq.getService_request_id())

				.eventDetails(null).build();

				

		events.add(event);

		return EventRequest.builder().requestInfo(requestInfo).events(events).build();

	}

	public EmailRequest prepareEmailRequest(ServiceRequestData serviceReq, ActionInfo actionInfo,
			RequestInfo requestInfo, Map<String, String> messageMap, String service_request_id_new) {

		List<Event> events = new ArrayList<>();
		String emailIdRetrived = null;
		String employeeDetailsRetrived = null;
		String employeeNameRetrived = null;
		String message = null;
		String subject = null;
		String phoneNumberRetrived=null;
		
		if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_OPEN)) {
			emailIdRetrived = serviceReq.getEmail();
			message = getMessage(WorkFlowConfigs.ACTION_OPEN, serviceReq, requestInfo, messageMap, null,
					HCConstants.EMAIL, service_request_id_new);
			subject = getSubject(actionInfo.getAction(), serviceReq, requestInfo, messageMap, null, HCConstants.EMAIL,
					service_request_id_new);
			
		} else if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_APPROVE)
				|| actionInfo.getAction().equals(WorkFlowConfigs.ACTION_ASSIGN)
				|| actionInfo.getAction().equals(WorkFlowConfigs.VERIFY_AND_FORWARD_TO_SDO)
				|| actionInfo.getAction().equals(WorkFlowConfigs.FORWARDFORINSPECTION)
				|| actionInfo.getAction().equals(WorkFlowConfigs.REQUESTCLARIFICATION)
				|| actionInfo.getAction().equals(WorkFlowConfigs.VERIFYANDFORWARD)) {

			emailIdRetrived = serviceReq.getEmail();
			employeeNameRetrived=serviceReq.getOwnerName();
			message = getMessage(actionInfo.getAction(), serviceReq, requestInfo, messageMap, employeeNameRetrived,
					HCConstants.EMAIL, service_request_id_new);
			subject = getSubject(actionInfo.getAction(), serviceReq, requestInfo, messageMap, employeeNameRetrived, HCConstants.EMAIL,
					service_request_id_new);

		} 
//		 else if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_INITIATE)) {
//			emailIdRetrived = serviceReq.getEmail();
//			message = getMessage(WorkFlowConfigs.ACTION_INITIATE, serviceReq, requestInfo, messageMap, null,
//					HCConstants.EMAIL, service_request_id_new);
//			subject = getSubject(actionInfo.getAction(), serviceReq, requestInfo, messageMap, null, HCConstants.EMAIL,
//					service_request_id_new);
//		}

		else

		{
			emailIdRetrived = serviceReq.getEmail();
			message = getMessage(actionInfo.getAction(), serviceReq, requestInfo, messageMap, null, HCConstants.EMAIL,
					service_request_id_new);
			subject = getSubject(actionInfo.getAction(), serviceReq, requestInfo, messageMap, null, HCConstants.EMAIL,
					service_request_id_new);
		}

		if (emailIdRetrived == null || message == null)
			return null;
		return EmailRequest.builder().email(emailIdRetrived).subject(subject).body(message).isHTML(true).build();
	}

	private String getSubject(String action, ServiceRequestData serviceReq, RequestInfo requestInfo,
			Map<String, String> messageMap, String employeeName, String email, String service_request_id_new) {

		String text = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateobj = new Date();
		String genratedDate = df.format(dateobj);

		switch (action) {

		case WorkFlowConfigs.ACTION_OPEN:
			text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_OPEN);
			break;
			
		case WorkFlowConfigs.ACTION_APPROVE:
			text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_ASSIGN_EMAIL_SUBJECT);
			break;
			
		case WorkFlowConfigs.ACTION_ASSIGN:
			text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_ASSIGN);
			break;
			
		case WorkFlowConfigs.VERIFY_AND_FORWARD_TO_SDO:
			text = messageMap.get(HCConstants.HC_EMPLOYEE_FORWARD_TO_SDO_EMAIL_SUBJECT);
			break;
			
		case WorkFlowConfigs.FORWARDFORINSPECTION:
			text = messageMap.get(HCConstants.HC_EMPLOYEE_INSPECTION_EMAIL_SUBJECT);
			break;
			
		case WorkFlowConfigs.REQUESTCLARIFICATION:
			text = messageMap.get(HCConstants.HC_EMPLOYEE_CLARIFICATION_EMAIL_SUBJECT);
			break;
			
		case WorkFlowConfigs.VERIFYANDFORWARD:
			text = messageMap.get(HCConstants.HC_EMPLOYEE_REQUEST_EMAIL_SUBJECT);
			break;
			
		case WorkFlowConfigs.ACTION_INITIATE:
			text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_ASSIGN);
			break;
			
		case WorkFlowConfigs.ACTION_UPDATE:
			text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_UPDATE);
			break;
			
		case WorkFlowConfigs.ACTION_REJECT:
			text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_REJECT);
			break;
			
		case WorkFlowConfigs.ACTION_COMPLETE:

			text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_SUBJECT_ACTION_COMPLETE);
			break;

		
		}
		if(text != null)
		{
		text = text.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id());
		}
		return text;
	}

	/**
	 * Prepares event to be registered in user-event service.
	 *
	 * @param serviceReq
	 * @param actionInfo
	 * @param requestInfo
	 * @param messageMap
	 * @param service_request_id_new
	 * @return
	 */

	public SMSRequest prepareSMSRequest(ServiceRequestData serviceReq, ActionInfo actionInfo, RequestInfo requestInfo,
			Map<String, String> messageMap, String service_request_id_new) {

		String phoneNumberRetrived = null;
		String employeeDetailsRetrived = null;
		String employeeNameRetrived = null;
		String message = null;
		String data = null;
		// List<SMSRequest> smsRequestsTobeSent = new ArrayList<>();
		if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_OPEN)) {
			phoneNumberRetrived = serviceReq.getContactNumber();
			message = getMessage(WorkFlowConfigs.ACTION_OPEN, serviceReq, requestInfo, messageMap, null,
					HCConstants.SMS, service_request_id_new);

		} 
		
		else if (actionInfo.getAction().equals(WorkFlowConfigs.ACTION_APPROVE)
				|| actionInfo.getAction().equals(WorkFlowConfigs.ACTION_ASSIGN)
				||actionInfo.getAction().equals(WorkFlowConfigs.VERIFY_AND_FORWARD_TO_SDO)
				||actionInfo.getAction().equals(WorkFlowConfigs.FORWARDFORINSPECTION)
				||actionInfo.getAction().equals(WorkFlowConfigs.REQUESTCLARIFICATION)
				|| actionInfo.getAction().equals(WorkFlowConfigs.VERIFYANDFORWARD)) {
			
			
			if (!serviceReq.getIsRoleSpecific())
			{
				employeeDetailsRetrived = notificationService.getMobileAndIdForNotificationService(requestInfo,
						HCConstants.TENANT_ID, actionInfo.getAssignee(), HCConstants.ROLE_EMPLOYEE);
				data = employeeDetailsRetrived.replace("|", "#");
				phoneNumberRetrived = data.split("#")[0];
				employeeNameRetrived = data.split("#")[1];
				serviceReq.setOwnerName(employeeNameRetrived);
				serviceReq.setEmail( data.split("#")[2]);

		
			}
			else
			{
				phoneNumberRetrived = serviceReq.getContactNumber();
				employeeNameRetrived = serviceReq.getOwnerName();
			
				
			}
			message = getMessage(actionInfo.getAction(), serviceReq, requestInfo, messageMap, employeeNameRetrived,
					HCConstants.SMS, service_request_id_new);
			
			
			

		} 
		else 
		{
			phoneNumberRetrived = serviceReq.getContactNumber();
			message = getMessage(actionInfo.getAction(), serviceReq, requestInfo, messageMap, null, HCConstants.SMS,
					service_request_id_new);
		}
		if (phoneNumberRetrived == null || message == null)
			return null;
		return SMSRequest.builder().mobileNumber(phoneNumberRetrived).message(message).build();
	}

	public Map<String, String> getLocalizationMessage(String tenantId, RequestInfo requestInfo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(hcConfiguration.getNotificationDateFormat());
		String date = dateFormat.format(new Date());
		String locale = null;
		String tenant_id = null;
		try {
			locale = requestInfo.getMsgId().split("[|]")[1]; 
															
			if (StringUtils.isEmpty(locale))
				locale = hcConfiguration.getFallbackLocale();
		} catch (Exception e) {
			locale = hcConfiguration.getFallbackLocale();
		}

		try {
			tenant_id = tenantId.split("[.]")[0]; 
													
		} catch (Exception e) {
			locale = hcConfiguration.getFallbackLocale();
		}

		if (null == NotificationService.localizedMessageMap.get(locale + "|" + tenant_id)) 
																																														
			notificationService.getLocalisedMessages(requestInfo, tenant_id, locale,
					HCConstants.LOCALIZATION_MODULE_NAME);
		Map<String, String> messageMap = NotificationService.localizedMessageMap.get(locale + "|" + tenant_id);
		if (null == messageMap)
			return null;
		return messageMap;
	}

	public String getMessage(String action, ServiceRequestData serviceReq, RequestInfo requestInfo,
			Map<String, String> messageMap, String employeeName, String notifcationType,
			String service_request_id_new) {
		String text = null;
		DateFormat df = new SimpleDateFormat(hcConfiguration.getNotificationDateFormat());
		Date dateobj = new Date();
		String genratedDate = df.format(dateobj);

		switch (action) {
		
		case WorkFlowConfigs.ACTION_OPEN:
			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_EMAIL_NOTIFICATION); 
																						
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_PUSH_NOTIFICATION);
			}

			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, requestInfo.getUserInfo().getName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_TYPE, serviceReq.getServiceType())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
			
		case WorkFlowConfigs.ACTION_APPROVE:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_APPROVE_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_APPROVE_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_APPROVE_PUSH_NOTIFICATION);
			}
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, employeeName)
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;


		case WorkFlowConfigs.ACTION_ASSIGN:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_ASSIGN_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_ASSIGN_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_ACTION_ASSIGN_PUSH_NOTIFICATION);
			}
			
			if(text != null) {
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, employeeName)
					.replace(HCConstants.SMS_NOTIFICATION_USER_NAME_KEY, serviceReq.getOwnerName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;

		case WorkFlowConfigs.VERIFY_AND_FORWARD_TO_SDO:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_FORWARD_TO_SDO_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_EMPLOYEE_FORWARD_TO_SDO_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_FORWARD_TO_SDO_PUSH_NOTIFICATION);
			}
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, serviceReq.getOwnerName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
			
		case WorkFlowConfigs.FORWARDFORINSPECTION:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_INSPECTION_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_INSPECTION_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_INSPECTION_PUSH_NOTIFICATION);
			}
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, serviceReq.getOwnerName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
			
		case WorkFlowConfigs.REQUESTCLARIFICATION:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_CLARIFICATION_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_EMPLOYEE_CLARIFICATION_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_CLARIFICATION_PUSH_NOTIFICATION);
			}
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, serviceReq.getOwnerName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
			
			
			
		case WorkFlowConfigs.VERIFYANDFORWARD:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_REQUEST_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_EMPLOYEE_REQUEST_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_EMPLOYEE_REQUEST_PUSH_NOTIFICATION);
			}
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, employeeName)
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;

		case WorkFlowConfigs.ACTION_REJECT:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_REJECTSERVICEREQUEST_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_CITIZEN_REJECTSERVICEREQUEST_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_REJECTSERVICEREQUEST_PUSH_NOTIFICATION);
			}

			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, serviceReq.getOwnerName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_TYPE, serviceReq.getServiceType())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
		case WorkFlowConfigs.ACTION_COMPLETE:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_SERVICEREQUESTCOMPLETED_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_CITIZEN_SERVICEREQUESTCOMPLETED_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_SERVICEREQUESTCOMPLETED_PUSH_NOTIFICATION);
			}
			
			if(text != null)
			{
			text = text.replaceAll(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, serviceReq.getOwnerName())
					.replaceAll(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replaceAll(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_TYPE, serviceReq.getServiceType())
					.replaceAll(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
			
//
//		case WorkFlowConfigs.ACTION_INITIATE:
//
//			if (notifcationType.equals(HCConstants.SMS)) {
//				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_INITIATE_SMS_NOTIFICATION);
//			} else if (notifcationType.equals(HCConstants.EMAIL)) {
//
//				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_INITIATE_EMAIL_NOTIFICATION);
//			} else if (notifcationType.equals(HCConstants.PUSH)) {
//				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_INITIATE_PUSH_NOTIFICATION);
//			}
//			if(text != null)
//			{
//			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY, employeeName)
//					.replace(HCConstants.SMS_NOTIFICATION_USER_NAME_KEY, serviceReq.getOwnerName())
//					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
//					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_TYPE, serviceReq.getServiceType())
//					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
//			}
//			break;
		case WorkFlowConfigs.ACTION_UPDATE:

			if (notifcationType.equals(HCConstants.SMS)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_UPDATE_SMS_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.EMAIL)) {

				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_UPDATE_EMAIL_NOTIFICATION);
			} else if (notifcationType.equals(HCConstants.PUSH)) {
				text = messageMap.get(HCConstants.HC_CITIZEN_REQUEST_UPDATE_PUSH_NOTIFICATION);
			}
			if(text != null)
			{
			text = text.replace(HCConstants.SMS_NOTIFICATION_EMP_NAME_KEY,  serviceReq.getOwnerName())
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_ID, serviceReq.getService_request_id())
					.replace(HCConstants.NOTIFICATION_SERVICEREQUEST_ID_NEW, service_request_id_new)
					.replace(HCConstants.SMS_NOTIFICATION_SERVICEREQUEST_DATE_KEY, genratedDate);
			}
			break;
		}
		

		return text;
	}


	public boolean isNotificationEnabled(String status) {
		boolean isNotifEnabled = false;
		List<String> notificationEnabledStatusList = Arrays.asList(hcConfiguration.getNotificationEnabledStatuses().split(","));
		if (notificationEnabledStatusList.contains(status.toUpperCase())) {
			isNotifEnabled = true;
		}
		return isNotifEnabled;
	}

	public void sendNotificationToEmployee(ServiceRequest serviceReqRequest, String role, Map<String, String> messageMap,String assigneeUuid) {
		List requestInfoList = new ArrayList();
		String mobileNumber = null;
		String uuid = null;
		String emailId = null;
		String userName = null;

		String allRoles = null;

		List<ModuleDetail> moduleDetail = new ArrayList<ModuleDetail>();
		RequestInfo requestInfo = new RequestInfo();

		try {

			allRoles = rest.postForObject(hcConfiguration.getEgovHRMShost().concat(hcConfiguration.getEgovHRMSSearchEndpoint()).concat("?")
					.concat("roles=" + role + "&tenantId=" + HCConstants.TENANT_ID), serviceReqRequest, String.class);

			try {
				org.json.JSONObject obj = new org.json.JSONObject(allRoles);

				org.json.JSONObject rreq = obj.getJSONObject("ResponseInfo");

				org.json.JSONArray employeesList = obj.getJSONArray("Employees");

				// action inforlist
				List<String> employee = new ArrayList<>();
				ServiceRequestData serviceRequest = new ServiceRequestData();

				for (int i = 0; i < employeesList.length(); i++) {

					List Actioninfolist = new ArrayList();
					RequestInfoWrapper infoWrapper = null;
					org.json.JSONObject empDetails = new org.json.JSONObject(employeesList.get(i).toString());

					org.json.JSONObject user = empDetails.getJSONObject("user");

					if (user.getString("uuid").equals(assigneeUuid))
					
					{
						if (!user.isNull("mobileNumber"))
							mobileNumber = user.getString("mobileNumber");
						else
							mobileNumber = user.getString("");
						uuid = user.getString("uuid");
						
						userName = user.getString("userName");
						
						if (!user.isNull("emailId"))
							serviceRequest.setEmail(user.getString("emailId").toString());
						else
							serviceRequest.setEmail("");
	
						serviceRequest.setOwnerName(userName);
						//serviceRequest.setEmail(emailId);
						serviceRequest.setContactNumber(mobileNumber);
						serviceRequest.setService_request_uuid(uuid);
						serviceRequest.setService_request_id(serviceReqRequest.getServices().get(0).getService_request_id());
						String by = serviceRequest.getCreatedBy();
	
						ActionInfo newActionInfo = ActionInfo.builder().uuid(UUID.randomUUID().toString())
								.businessKey(serviceReqRequest.getServices().get(0).getBusinessService())
								.action(HCConstants.VERIFYANDFORWARD).assignee(uuid).by(by)
								.when(serviceRequest.getCreatedTime())
								.tenantId(serviceReqRequest.getServices().get(0).getTenantId()).status("VERIFYANDFORWARD")
								.build();
						Actioninfolist.add(newActionInfo);
	
						List<ServiceRequestData> serviceRequestList = new ArrayList<>();
						serviceRequestList.add(serviceRequest);
						RequestInfo requestInfoset = serviceReqRequest.getRequestInfo();
						requestInfoset.getUserInfo().setUuid(uuid);
						requestInfoset.getUserInfo().setName(userName);
	
						infoWrapper = RequestInfoWrapper.builder().services(serviceRequestList).actionInfo(Actioninfolist)
								.requestInfo(requestInfoset).requestBody(serviceRequest).build();
	
						ServiceRequest serviceRequestobj = new ServiceRequest();
						serviceRequestobj.setServices(serviceRequestList);
						serviceRequestobj.setActionInfo(Actioninfolist);
						serviceRequestobj.setRequestInfo(requestInfoset);
						sendNotification(serviceRequestobj, messageMap, null);
					}
				}

			}

			catch (Exception ex) {

			}

		} catch (HttpClientErrorException e) {
			System.out.print("Handled exception");
		}
		System.out.println(allRoles);
		
	}
}