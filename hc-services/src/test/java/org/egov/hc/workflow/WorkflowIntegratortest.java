//package org.egov.hc.workflow;
//
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import org.egov.hc.contract.ServiceRequest;
//import org.egov.hc.model.ServiceRequestData;
//import org.egov.hc.producer.HCConfiguration;
//import org.egov.hc.service.ServiceRequestService;
//import org.egov.hc.utils.HCConstants;
//import org.egov.tracer.model.CustomException;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import com.jayway.jsonpath.DocumentContext;
//import com.jayway.jsonpath.JsonPath;
//import com.jayway.jsonpath.PathNotFoundException;
//
//import lombok.extern.slf4j.Slf4j;
//import net.minidev.json.JSONArray;
//import net.minidev.json.JSONObject;
//
//@Service
//@Slf4j
//public class WorkflowIntegratortest {
//
//	private static final String TENANTIDKEY = "tenantId";
//
//	private static final String BUSINESSSERVICEKEY = "businessService";
//
//	private static final String ACTIONKEY = "action";
//
//	private static final String COMMENTKEY = "comment";
//
//	private static final String MODULENAMEKEY = "moduleName";
//
//	private static final String BUSINESSIDKEY = "businessId";
//
//	private static final String DOCUMENTSKEY = "documents";
//
//	private static final String ASSIGNEEKEY = "assignes";
//
//	private static final String TLMODULENAMEVALUE = "HORTICULTURE";
//
//	private static final String WORKFLOWREQUESTARRAYKEY = "ProcessInstances";
//
//	private static final String REQUESTINFOKEY = "RequestInfo";
//
//	private static final String PROCESSINSTANCESJOSNKEY = "$.ProcessInstances";
//
//	private static final String BUSINESSIDJOSNKEY = "$.businessId";
//
//	private static final String STATUSJSONKEY = "$.state.applicationStatus";
//
//	@Mock
//	private RestTemplate rest;
//	
//	@Mock
//	private HCConfiguration config;
//	
//	@InjectMocks
//	private WorkflowIntegrator WorkflowIntegratort;
//
//	@Test
//	public void testCallWorkFlow()
//		throws Exception {
//		String wfTenantId = "ch.chandighrah";
//		ServiceRequest request = new ServiceRequest();
//		JSONArray array = new JSONArray();
//		JSONObject obj = new JSONObject();
//		obj.put(DOCUMENTSKEY, "wfDocument");
//		obj.put(BUSINESSIDKEY, "CH-HC-2020-06-14-001432_1");
//		obj.put(TENANTIDKEY, wfTenantId);	
//		ServiceRequestData servicerequestdata = new ServiceRequestData();
//		obj.put(BUSINESSSERVICEKEY, "PRUNING OF TREES GIRTH GREATER THAN 90 CMS" );  
//		
//
//		obj.put(MODULENAMEKEY, "HORTICULTURE");
//		obj.put(ACTIONKEY, "INITIATE");		
//		obj.put(COMMENTKEY, "Comment");
//		obj.put(ASSIGNEEKEY, "EE");
//		array.add(obj);
//	
//		if(!array.isEmpty())
//		{
//	
//			JSONObject workFlowRequest = new JSONObject();
//			workFlowRequest.put(REQUESTINFOKEY, request.getRequestInfo());
//			workFlowRequest.put(WORKFLOWREQUESTARRAYKEY, array);
//			String response = null;
//
//			
//			try {
////				EchallanConfiguration.builder().wfHost("http://localhost:8098/")
////				.wfTransitionPath("egov-workflow-v2/egov-wf/process/_transition").build(); 
//				
//				
//				response = rest.postForObject("http://localhost:8098/"
//						.concat("egov-workflow-v2/egov-wf/process/_transition"),
//						 workFlowRequest, String.class);
//				
//				
////				response = rest.postForObject(config.getWfHost()
////						.concat(config.getWfTransitionPath()),
////						 workFlowRequest, String.class);
//			} catch (HttpClientErrorException e) {
//		
//			}
//	    }
//		
//	}
//	
//}