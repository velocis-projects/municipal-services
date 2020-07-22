package org.egov.hc.utils;


import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;



import org.egov.common.contract.request.RequestInfo;
import org.egov.hc.contract.AuditDetails;
import org.egov.hc.contract.RequestInfoWrapper;


import org.egov.hc.producer.HCConfiguration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import lombok.extern.slf4j.Slf4j;

@Component

public class HCUtils {



	@Autowired
	private HCConfiguration hcConfiguration;
	
	

	/**
	 * Util method to return Auditdetails for create and update processes
	 * 
	 * @param by
	 * @param isCreate
	 * @return
	 */
	public AuditDetails getAuditDetails(String by, Boolean isCreate) {

		Long dt = new Date().getTime();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).createdTime(dt).lastModifiedBy(by).lastModifiedTime(dt).build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(dt).build();
	}
	
	public AuditDetails getAuditDetail(String by, Boolean isCreate) {

		Long dt = new Date().getTime();
		
		return AuditDetails.builder().createdBy(by).createdTime(dt).lastModifiedBy(by).lastModifiedTime(dt).build();
		
	}


	
	public RequestInfoWrapper prepareRequestForLocalization(StringBuilder uri, RequestInfo requestInfo, String locale,
			String tenantId, String module) {
		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);
		uri.append(hcConfiguration.getLocalizationHost()).append(hcConfiguration.getLocalizationSearchEndpoint()).append("?tenantId=" + tenantId)
				.append("&module=" + module).append("&locale=" + locale);

		return requestInfoWrapper;
	}
	


	public Map<String, Object> prepareRequestForUserSearch(StringBuilder uri, RequestInfo requestInfo, String userId,
			String tenantId) {
		Map<String, Object> userServiceRequest = new HashMap();
		String[] userIds = { userId };
		userServiceRequest.put("RequestInfo", requestInfo);
		userServiceRequest.put("tenantId", tenantId);
		userServiceRequest.put("id", Arrays.asList(userIds));
		userServiceRequest.put("userType", HCConstants.ROLE_CITIZEN);

		uri.append(hcConfiguration.getEgovUserHost()).append(hcConfiguration.getEgovUserSearchEndpoint());

		return userServiceRequest;
	}
	
	/**
	 * Returns mapper with all the appropriate properties reqd in our
	 * functionalities.
	 * 
	 * @return ObjectMapper
	 */
	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		return mapper;
	}


}
