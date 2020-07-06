package org.egov.echallan.workflow;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.echallan.config.EchallanConfiguration;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.egov.echallan.web.models.workflow.ProcessInstanceRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Service
@Slf4j
public class WorkflowIntegrator {

	private RestTemplate rest;

	private EchallanConfiguration config;

	@Autowired
	public WorkflowIntegrator(RestTemplate rest, EchallanConfiguration config) {
		this.rest = rest;
		this.config = config;
	}

	/**
	 * Method to integrate with workflow
	 *
	 * takes the trade-license request as parameter constructs the work-flow request
	 *
	 * and sets the resultant status from wf-response back to trade-license object
	 *
	 * @param tradeLicenseRequest
	 */
	public ResponseInfo callWorkFlow(ProcessInstanceRequest workFlowRequest) {

		try {
			JsonNode response = rest.postForObject(config.getWfHost().concat(config.getWfTransitionPath()),workFlowRequest,
					JsonNode.class);
			if (!isNull(response)) {
				ObjectMapper mapper = new ObjectMapper();
				ResponseInfo responseInfo = mapper.convertValue(response.get("ResponseInfo"), ResponseInfo.class);
				log.info("Workflow Created Success : " + responseInfo.getMsgId());
				return responseInfo;
			} else {
				log.info("Workflow Creation Failed : Reason " + response);
			}

		} catch (Exception e) {
			log.info("Workflow Exception while processing: ERROR " + e.getMessage());
			log.info("Workflow Exception while processing: ERROR " + workFlowRequest);
			ResponseInfo responseInfo = new ResponseInfo();
			responseInfo.setResMsgId(HttpStatus.BAD_REQUEST.toString());
			responseInfo.setStatus("Fail");
			responseInfo.setMsgId("Workflow Action Not Valid for Business Id:-"+workFlowRequest.getProcessInstances().get(0).getBusinessId());
			log.info("Workflow Created Success : " + responseInfo.getMsgId());
			return responseInfo;

		}
		return null;
	}

}