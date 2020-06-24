package org.egov.hc.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.hc.contract.RequestInfoWrapper;

import org.egov.hc.contract.ServiceResponse;
import org.egov.hc.model.ActionInfo;
import org.egov.hc.model.ServiceRequestData;
import org.egov.hc.model.user.UserResponse;
import org.egov.hc.producer.HCConfiguration;
import org.egov.hc.repository.ServiceRepository;
import org.egov.hc.utils.HCConstants;
import org.egov.hc.utils.HCUtils;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.stereotype.Service
public class NotificationService {

	
	@Autowired
	private HCConfiguration hcConfiguration; 

	@Autowired
	private ServiceRepository serviceRepository;
	
	@Autowired
	private HCUtils hCUtils;
	
	public static final Map<String, Map<String, String>> localizedMessageMap = new HashMap<>();

	/**
	 * Fetches Employee Details
	 * 
	 * @param tenantId
	 * @param id
	 * @param requestInfo
	 * @return
	 */
	public Map<String, String> getEmployeeDetails(String tenantId, String id, RequestInfo requestInfo) {
		StringBuilder uri = new StringBuilder();
		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);
		uri.append(hcConfiguration.getEgovHRMShost()).append(hcConfiguration.getEgovHRMSSearchEndpoint()).append("?ids=" + id)
				.append("&tenantId=" + tenantId);
		Object response = null;
		Map<String, String> employeeDetails = new HashMap<>();
		try {
			response = serviceRepository.fetchResult(uri, requestInfoWrapper);
			if (null == response) {
				return employeeDetails;
			}
			employeeDetails.put("name", JsonPath.read(response, HCConstants.EMPLOYEE_NAME_JSONPATH));
			employeeDetails.put("phone", JsonPath.read(response, HCConstants.EMPLOYEE_PHNO_JSONPATH));
			employeeDetails.put("email", JsonPath.read(response, HCConstants.EMPLOYEE_EMAILID_JSONPATH));
//			employeeDetails.put("department", ((List<String>) JsonPath.read(response, HCConstants.EMPLOYEE_DEPTCODE_JSONPATH)).get(0));
//			employeeDetails.put("designation", ((List<String>)JsonPath.read(response, HCConstants.EMPLOYEE_DESGCODE_JSONPATH)).get(0));
		} catch (Exception e) {
			log.error("Exception: ", e);
		}
		return employeeDetails;
	}



	/**
	 * Populates the localized msg cache
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @param locale
	 * @param module
	 */
	public void getLocalisedMessages(RequestInfo requestInfo, String tenantId, String locale, String module) {
		Map<String, String> mapOfCodesAndMessages = new HashMap<>();
		StringBuilder uri = new StringBuilder();
		RequestInfoWrapper requestInfoWrapper = hCUtils.prepareRequestForLocalization(uri, requestInfo, locale,
				tenantId, module);
		List<String> codes = null;
		List<String> messages = null;
		Object result = null;
		try {
			result = serviceRepository.fetchResult(uri, requestInfoWrapper);
			codes = JsonPath.read(result, HCConstants.LOCALIZATION_CODES_JSONPATH);
			messages = JsonPath.read(result, HCConstants.LOCALIZATION_MSGS_JSONPATH);
		} catch (Exception e) {
			log.error("Exception while fetching from localization: " + e);
		}
		if (null != result && codes != null && messages != null) {
			for (int i = 0; i < codes.size(); i++) {
				mapOfCodesAndMessages.put(codes.get(i), messages.get(i));
			}
			localizedMessageMap.put(locale + "|" + tenantId, mapOfCodesAndMessages);
		}
	}
	
	/**
	 * Fetches phone number for notification based on the recepient of the notif.
	 * 
	 * @param requestInfo
	 * @param userId
	 * @param tenantId
	 * @param assignee
	 * @param role
	 * @return
	 */
	public String getMobileAndIdForNotificationService(RequestInfo requestInfo,  String tenantId, String assignee, String role) {
		String phoneNumber = null;
		String employeeName=null;
		Object response = null;
		String employeeEmail = null;
		ObjectMapper mapper = hCUtils.getObjectMapper();
		StringBuilder uri = new StringBuilder();
		Object request = new HashMap<>();
		if(role.equals(HCConstants.ROLE_CITIZEN)) {
		
			return phoneNumber;
		}else if(role.equals(HCConstants.ROLE_EMPLOYEE)) {
			Map<String, String> employeeDetails = getEmployeeDetails(tenantId, assignee, requestInfo);
			if(!StringUtils.isEmpty(employeeDetails.get("phone"))) {
				phoneNumber = employeeDetails.get("phone");
				employeeName=employeeDetails.get("name");
				employeeEmail=employeeDetails.get("email");
				}
		}
		return phoneNumber + "|" + employeeName + "|" + employeeEmail ;
		
	}

}