package org.egov.assets.service;

import org.egov.assets.model.BankContract;
import org.egov.assets.model.BankResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BankContractRepository {

	private RestTemplate restTemplate;
	private String hostUrl;
	public static final String SEARCH_URL = "/egf-master/banks/_search?";

	public BankContractRepository(@Value("${egf.master.host.url}") String hostUrl, RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.hostUrl = hostUrl;
	}

	public BankContract findById(BankContract bankContract, RequestInfo requestInfo) {

		String url = String.format("%s%s", hostUrl, SEARCH_URL);
		StringBuilder content = new StringBuilder();
		if (bankContract.getId() != null) {
			content.append("id=" + bankContract.getId());
		}

		if (bankContract.getTenantId() != null) {
			content.append("&tenantId=" + bankContract.getTenantId());
		}
		url = url + content.toString();

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		BankResponse result = restTemplate.postForObject(url, requestInfoWrapper, BankResponse.class);

		if (result.getBanks() != null && result.getBanks().size() == 1) {
			return result.getBanks().get(0);
		} else {
			return null;
		}

	}

	public BankContract findByCode(BankContract bankContract, RequestInfo requestInfo) {

		String url = String.format("%s%s", hostUrl, SEARCH_URL);
		StringBuilder content = new StringBuilder();
		if (bankContract.getCode() != null) {
			content.append("code=" + bankContract.getCode());
		}

		if (bankContract.getTenantId() != null) {
			content.append("&tenantId=" + bankContract.getTenantId());
		}

		ObjectMapper mapper = new ObjectMapper();
		try {
			String asa = mapper.writeValueAsString(requestInfo);
			System.out.println(asa);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		url = url + content.toString();
		BankResponse result = restTemplate.postForObject(url, requestInfo, BankResponse.class);

		if (result.getBanks() != null && result.getBanks().size() == 1) {
			return result.getBanks().get(0);
		} else {
			throw new CustomException("Bank", "Bank Code " + bankContract.getCode() + " doesnt exist");
		}

	}

	public RequestInfo getRequestInfo(RequestInfo requestInfo) {
		final ObjectMapper mapper = new ObjectMapper();

		// UserInfo userInfo = requestInfo.getUserInfo();
		//// User user = mapper.convertValue(userInfo, User.class);
		// User user = User.builder().name(userInfo.getUserName()).

		RequestInfo info = new RequestInfo();
		return info.builder().action(requestInfo.getAction()).apiId(requestInfo.getApiId())
				.authToken(requestInfo.getAuthToken()).correlationId(requestInfo.getCorrelationId())
				.did(requestInfo.getDid()).key(requestInfo.getKey()).msgId(requestInfo.getMsgId())
				.ts(requestInfo.getTs())/* .userInfo(user) */.ver(requestInfo.getVer()).build();
	}
}