package org.egov.pgr.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.pgr.contract.Address;
import org.egov.pgr.contract.AutoroutingMapReqSearchCriteria;
import org.egov.pgr.contract.AutoroutingMapRequest;
import org.egov.pgr.contract.AutoroutingMapResponse;
import org.egov.pgr.contract.CountResponse;
import org.egov.pgr.contract.IdResponse;
import org.egov.pgr.contract.RequestInfoWrapper;
import org.egov.pgr.contract.SearcherRequest;
import org.egov.pgr.contract.ServiceReqSearchCriteria;
import org.egov.pgr.contract.ServiceRequest;
import org.egov.pgr.contract.ServiceRequestDetails;
import org.egov.pgr.contract.ServiceResponse;
import org.egov.pgr.model.ActionHistory;
import org.egov.pgr.model.ActionInfo;
import org.egov.pgr.model.AuditDetails;
import org.egov.pgr.model.AutoroutingMap;
import org.egov.pgr.model.Service;
import org.egov.pgr.model.Service.StatusEnum;
import org.egov.pgr.model.user.Citizen;
import org.egov.pgr.model.user.CreateUserRequest;
import org.egov.pgr.model.user.UserResponse;
import org.egov.pgr.model.user.UserSearchRequest;
import org.egov.pgr.model.user.UserType;
import org.egov.pgr.producer.PGRProducer;
import org.egov.pgr.repository.FileStoreRepo;
import org.egov.pgr.repository.IdGenRepo;
import org.egov.pgr.repository.MasterDataRepository;
import org.egov.pgr.repository.ServiceRequestRepository;
import org.egov.pgr.utils.ErrorConstants;
import org.egov.pgr.utils.PGRConstants;
import org.egov.pgr.utils.PGRUtils;
import org.egov.pgr.utils.ResponseInfoFactory;
import org.egov.pgr.utils.WorkFlowConfigs;
import org.egov.pgr.validator.PGRRequestValidator;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class MasterDataService {

	@Value("${kafka.topics.save.autorouting}")
	private String saveTopic;

	@Value("${kafka.topics.update.autorouting}")
	private String updateTopic;
	
	@Value("${egov.user.host}")
	private String userBasePath;
	
	@Value("${egov.user.create.endpoint}")
	private String userCreateEndPoint;
	
	@Value("${egov.user.search.endpoint}")
	private String userSearchEndPoint;

	@Autowired
	private ResponseInfoFactory factory;

	@Autowired
	private PGRUtils pGRUtils;

	@Autowired
	private PGRProducer pGRProducer;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private MasterDataRepository masterDataRepository;

	@Autowired
    private ObjectMapper objectMapper;

	/***
	 * Asynchronous method performs business logic if any and adds the data to
	 * persister queue on create topic
	 * 
	 * @param AutoroutingMapRequest
	 * @return AutoroutingMapResponse
	 */
	public AutoroutingMapResponse create(AutoroutingMapRequest autoroutingMapRequest) {
		log.info("Master Data layer to create autorouting");
		enrichAutoroutingMapRequestForCreate(autoroutingMapRequest);
		pGRProducer.push(saveTopic, autoroutingMapRequest);
		return getAutoroutingMapResponse(autoroutingMapRequest);
	}

	/**
	 * Update autorouting data and adds the data to
	 * persister queue on update topic
	 * 
	 * @param AutoroutingMapRequest
	 * @return AutoroutingMapResponse
	 */
	public AutoroutingMapResponse update(AutoroutingMapRequest autoroutingMapRequest) {
		log.info("Master Data layer to update autorouting");
		enrichAutoroutingMapRequestForUpdate(autoroutingMapRequest);
		pGRProducer.push(updateTopic, autoroutingMapRequest);
		return getAutoroutingMapResponse(autoroutingMapRequest);
	}
	
	/**
	 * Get autorouting data from database as per criteria
	 * 
	 * @param RequestInfo
	 * @param AutoroutingMapReqSearchCriteria
	 * @return AutoroutingMapResponse
	 */
	public AutoroutingMapResponse getAutoroutingData(RequestInfo requestInfo, AutoroutingMapReqSearchCriteria criteria) {
		log.info("Get Autorouting data from database");
		Map<String, String> errorMap = new HashMap<>();
		AutoroutingMap autoroutingMap = masterDataRepository.getAutoRoutingData(criteria.getTenantId());
		if(null == autoroutingMap) {
			errorMap.put(ErrorConstants.NO_DATA_FOUND_CODE, ErrorConstants.NO_DATA_FOUND_MSG);
		}else {	
			autoroutingMap.setAutorouting(getFilterAutoroutingData(criteria, autoroutingMap));
		}
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
		AutoroutingMapRequest autoroutingMapRequest = AutoroutingMapRequest.builder().requestInfo(requestInfo).autoroutingMap(autoroutingMap).build();
		return getAutoroutingMapResponse(autoroutingMapRequest);
	}
	
	/**
	 * Util method for the create to enrich the actions in the request 
	 * 
	 * @param AutoroutingMapRequest
	 */
	private void enrichAutoroutingMapRequestForCreate(AutoroutingMapRequest autoroutingMapRequest) {
		Map<String, String> errorMap = new HashMap<>();
		RequestInfo requestInfo = autoroutingMapRequest.getRequestInfo();
		AutoroutingMap autoroutingMap =autoroutingMapRequest.getAutoroutingMap();
		
		AutoroutingMap mapDataFromDB = masterDataRepository.getAutoRoutingData(autoroutingMap.getTenantId());
		if(null != mapDataFromDB) {
			errorMap.put(ErrorConstants.AUTOROUTING_DATA_EXISTS_CODE, ErrorConstants.AUTOROUTING_DATA_EXISTS_MSG);
		}else {		
			autoroutingMap.setId(UUID.randomUUID().toString());
			autoroutingMap.setActive(true);
			AuditDetails auditDetails = pGRUtils.getAuditDetails(String.valueOf(requestInfo.getUserInfo().getId()), true);
			autoroutingMap.setAuditDetails(auditDetails);
		}
		
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}
	
	/**
	 * Util method for the update to enrich the actions in the request 
	 * 
	 * @param request
	 */
	private void enrichAutoroutingMapRequestForUpdate(AutoroutingMapRequest autoroutingMapRequest) {
		Map<String, String> errorMap = new HashMap<>();
		RequestInfo requestInfo = autoroutingMapRequest.getRequestInfo();
		AutoroutingMap autoroutingMap =autoroutingMapRequest.getAutoroutingMap();
		
		AutoroutingMap mapDataFromDB = masterDataRepository.getAutoRoutingData(autoroutingMap.getTenantId());
		if(null == mapDataFromDB) {
			errorMap.put(ErrorConstants.NO_DATA_FOUND_CODE, ErrorConstants.NO_DATA_FOUND_MSG);
		}else {
			mapDataFromDB.setAutorouting(updateAutoroutingData(mapDataFromDB.getAutorouting(), autoroutingMap.getAutorouting()));
			mapDataFromDB.setActive(true);
			AuditDetails auditDetails = pGRUtils.getAuditDetails(String.valueOf(requestInfo.getUserInfo().getId()), false);
			mapDataFromDB.setAuditDetails(auditDetails);
			autoroutingMapRequest.setAutoroutingMap(mapDataFromDB);
		}
		
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object updateAutoroutingData(Object dbAutoroutingData, Object selectedData) {
		String selectedCategory = JsonPath.read(selectedData, PGRConstants.AUTOROUTING_CATEGORY_JSONPATH);
		
		List dbData = new ArrayList<>();
		if(null != dbAutoroutingData) {
			dbData = JsonPath.read(((PGobject)dbAutoroutingData).getValue(), PGRConstants.JSONPATH_AUTOROUTING_MAP_CODES_DB);
		}
		
		//replace existing category wise autorouting with newer one
		boolean isFound =false;
		for (int i = 0; i < dbData.size(); i++) {
			String dbCat = JsonPath.read(dbData.get(i), PGRConstants.AUTOROUTING_CATEGORY_JSONPATH);
			if(!StringUtils.isEmpty(dbCat) && dbCat.equalsIgnoreCase(selectedCategory)) {
				dbData.set(i, selectedData);
				isFound = true;
				break;
			}
		}
		if(!isFound) {
			dbData.add(selectedData);
		}
		LinkedHashMap<String, Object> finalObject =null;
		try {
			finalObject = objectMapper.readValue(((PGobject)dbAutoroutingData).getValue(), LinkedHashMap.class);
			finalObject.put("AutoroutingEscalationMap", dbData);
		} catch (Exception e) {
			log.error("Unable to replace autorouting data ",e);
		}
		
		return finalObject;
	}
	
	private Object getFilterAutoroutingData(AutoroutingMapReqSearchCriteria criteria, AutoroutingMap autoroutingMap) {
		String filter ="[?(@.active == true)]";
		if(!StringUtils.isBlank(criteria.getCategory())) {
			filter = "[?((@.category == '" + criteria.getCategory() + "') && (@.active == true))]";
		}
		
		List filteredData = null;
		if(null != autoroutingMap.getAutorouting()) {
			List autoroutingData = JsonPath.read(((PGobject)autoroutingMap.getAutorouting()).getValue(), PGRConstants.JSONPATH_AUTOROUTING_MAP_CODES_DB);
			filteredData = JsonPath.read(autoroutingData, filter);
		}
		
		return filteredData;
	}
	
	/**
	 * This method creates user in user svc.
	 * 
	 * @param citizen
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	private String updateUser(Citizen citizen, RequestInfo requestInfo, String tenantId) {
		ObjectMapper mapper = pGRUtils.getObjectMapper();
		citizen.setUserName(citizen.getMobileNumber());
		citizen.setActive(true);
		citizen.setTenantId(tenantId);
		citizen.setType(UserType.CITIZEN);
		citizen.setRoles(Arrays.asList(org.egov.pgr.model.user.Role.builder().code(PGRConstants.ROLE_CITIZEN).build()));
		StringBuilder url = new StringBuilder(userBasePath+userCreateEndPoint); 
		CreateUserRequest req = CreateUserRequest.builder().citizen(citizen).requestInfo(requestInfo).build();
		UserResponse res = mapper.convertValue(serviceRequestRepository.fetchResult(url, req), UserResponse.class);
		return res.getUser().get(0).getId().toString();
	}
	
	/**
	 * Fetches Users to be populated in the response
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param userIds
	 * @return
	 */
	public UserResponse getUsers(RequestInfo requestInfo, String tenantId, List<Long> userIds) {
		ObjectMapper mapper = pGRUtils.getObjectMapper();
		UserSearchRequest searchRequest = UserSearchRequest.builder().id(userIds).tenantId(tenantId)
				.userType(PGRConstants.ROLE_CITIZEN).requestInfo(requestInfo).build();
		StringBuilder url = new StringBuilder();
		url.append(userBasePath).append(userSearchEndPoint);
		try {
			UserResponse res = mapper.convertValue(serviceRequestRepository.fetchResult(url, searchRequest), UserResponse.class);
			if(CollectionUtils.isEmpty(res.getUser())) {
				return null;
			}else {
				return res;
			}
		}catch(Exception e) {
			return null;
		}
		
	}
	

	/**
	 * Fetches Users to be populated in the response
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param userIds
	 * @return
	 */
	public UserResponse getUser(RequestInfo requestInfo, String tenantId, String employeeCode) {
		ObjectMapper mapper = pGRUtils.getObjectMapper();
		UserSearchRequest searchRequest = UserSearchRequest.builder().tenantId(tenantId).userName(employeeCode)
				.userType(PGRConstants.ROLE_EMPLOYEE).requestInfo(requestInfo).build();
		StringBuilder url = new StringBuilder();
		url.append(userBasePath).append(userSearchEndPoint);
		try {
			UserResponse res = mapper.convertValue(serviceRequestRepository.fetchResult(url, searchRequest), UserResponse.class);
			if(CollectionUtils.isEmpty(res.getUser())) {
				return null;
			}else {
				return res;
			}
		}catch(Exception e) {
			return null;
		}
	}	
	
	/**
	 * returns AutoroutingMapResponse fetched from database/built based on the given
	 * AutoroutingMapRequest
	 * 
	 * @param AutoroutingMapRequest
	 * @return AutoroutingMapResponse
	 */
	public AutoroutingMapResponse getAutoroutingMapResponse(AutoroutingMapRequest autoroutingMapRequest) {
			return AutoroutingMapResponse.builder()
					.responseInfo(factory.createResponseInfoFromRequestInfo(autoroutingMapRequest.getRequestInfo(), true))
					.autoroutingMap(autoroutingMapRequest.getAutoroutingMap()).build();
	}
	
	/**
	 * method to fetch AutoroutingEscalationMap from mdms based on category,sector
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param category
	 * @param sector
	 * @return Object
	 * @author Tonmoy
	 */
	public Object fetchAutoroutingEscalationMap(RequestInfo requestInfo, String tenantId, String category, String sector) {
		StringBuilder uri = new StringBuilder();
		MdmsCriteriaReq mdmsCriteriaReq = pGRUtils.prepareAutoroutingEscalationMapSearchMdmsRequestByCategoryAndSector(uri, tenantId, category, sector, requestInfo);
		Object response = null;
		try {
			response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
		} catch (Exception e) {
			log.error("Exception while fetching AutoroutingEscalationMap: " + e);
		}
		return response;

	}
	
	/**
	 * method to fetch catrgory list from mdms based on employeeode,escalation officer
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param employeCode
	 * @param escalationOfficer
	 * @return Object
	 * @author Tonmoy
	 */
	public Object fetchCategoriesFromAutoroutingEscalationMap(RequestInfo requestInfo, String tenantId) {
		StringBuilder uri = new StringBuilder();
		MdmsCriteriaReq mdmsCriteriaReq = pGRUtils.prepareCategoryMdmsRequestByEscalationOfficer(uri, tenantId, requestInfo);
		Object response = null;
		try {
			response = serviceRequestRepository.fetchResult(uri, mdmsCriteriaReq);
		} catch (Exception e) {
			log.error("Exception while fetching fetchCategoriesFromAutoroutingEscalationMap: " + e);
		}
		return response;

	}
}
