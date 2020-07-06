package org.egov.echallan.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.echallan.config.EchallanConfiguration;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.repository.IdGenRepository;
import org.egov.echallan.repository.ViolationRepository;
import org.egov.echallan.service.validator.CustomBeanValidator;
import org.egov.echallan.web.models.AuditDetails;
import org.egov.echallan.web.models.Document;
import org.egov.echallan.web.models.EcPayment;
import org.egov.echallan.web.models.EcSearchCriteria;
import org.egov.echallan.web.models.NotificationTemplate;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.egov.echallan.web.models.Violation;
import org.egov.echallan.web.models.ViolationItem;
import org.egov.echallan.web.models.Idgen.IdGenerationResponse;
import org.egov.echallan.web.models.Idgen.IdResponse;
import org.egov.echallan.web.models.workflow.ProcessInstanceRequest;
import org.egov.echallan.workflow.WorkflowIntegrator;
import org.egov.tracer.model.CustomException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class ViolationServiceTest {

	@Mock
	private ViolationRepository repository;

	@InjectMocks
	private ViolationService service;

	/*@Mock
	private ResponseInfoFactory responseInfoFactory;*/

	@InjectMocks
	private Violation violationMaster;

	@Mock
	private Producer producer;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	WorkflowIntegrator wfIntegrator;
	
	@Mock
	EcSearchCriteria ecSearchCriteria;
	
	@Mock
	IdGenerationResponse idGenerationResponse;
	
	@Mock
	IdGenRepository idGenRepository;
	
	@Mock
	EchallanConfiguration echallanConfiguration;
	
	@Mock
	EcPayment ecPayment;
	
	@Mock
	Document document;
	
	@Mock
	ViolationItem violationItem;
	
	@Mock
	NotificationTemplate notificationTemplate;
	
	@Mock
	CustomBeanValidator validate;	
	
	@Mock
	DeviceSourceService deviceSourceService;

	@Test
	public void testGetViolationChallan() throws Exception {
		EcSearchCriteria searchCriteria = EcSearchCriteria.builder().limit(null).tenantId("ch").challanId("").searchText("dsgshdfbdfhd").action("hhvhvj").build();
		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		org.egov.common.contract.request.User userInfo = new org.egov.common.contract.request.User();
		userInfo.setTenantId("ch.chandigarh");
		userInfo.setRoles(Arrays.asList(Role.builder().code("challanHOD").name("challanHOD").build()));
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(searchCriteria)
				.requestInfo(RequestInfo.builder().userInfo(userInfo).build()).build();
				
		  Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(),
		  EcSearchCriteria.class)).thenReturn(searchCriteria);
		 		
		Mockito.when(repository.getChallan(searchCriteria)).thenReturn(new ArrayList<Violation>());
		Assert.assertEquals(HttpStatus.OK, service.getChallan(infoWrapper).getStatusCode());
	}
	
	@Test
	public void testGetViolationChallan_2() throws Exception {
		EcSearchCriteria searchCriteria = EcSearchCriteria.builder().limit(null).tenantId("ch").challanId("").action("").searchText("gugbbbj").build();
		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		org.egov.common.contract.request.User userInfo = new org.egov.common.contract.request.User();
		userInfo.setTenantId("ch.chandigarh");
		userInfo.setRoles(Arrays.asList(Role.builder().code("challanHOD").name("challanHOD").build()));
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(searchCriteria)
				.requestInfo(RequestInfo.builder().userInfo(userInfo).build()).build();
				
		  Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(),
		  EcSearchCriteria.class)).thenReturn(searchCriteria);
		 		
		Mockito.when(repository.getChallan(searchCriteria)).thenReturn(new ArrayList<Violation>());
		Assert.assertEquals(HttpStatus.OK, service.getChallan(infoWrapper).getStatusCode());
	}
	
	@Test
	public void testGeViolationChallan_3() throws Exception {
		EcSearchCriteria searchCriteria = EcSearchCriteria.builder().limit(null).tenantId("ch").challanId("").action("auctionChallan").searchText("").build();
		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		org.egov.common.contract.request.User userInfo = new org.egov.common.contract.request.User();
		userInfo.setTenantId("ch.chandigarh");
		userInfo.setRoles(Arrays.asList(Role.builder().code("challanHOD").name("challanHOD").build()));
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(searchCriteria)
				.requestInfo(RequestInfo.builder().userInfo(userInfo).build()).build();
				
		  Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(),
		  EcSearchCriteria.class)).thenReturn(searchCriteria);
		 		
		Mockito.when(repository.getChallan(searchCriteria)).thenReturn(new ArrayList<Violation>());
		Assert.assertEquals(HttpStatus.OK, service.getChallan(infoWrapper).getStatusCode());
	}
	

	
	@Test
	public void testGetViolationChallan_1() throws Exception {
		EcSearchCriteria searchCriteria = EcSearchCriteria.builder().limit(-1).tenantId("ch").challanId("").build();
		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		org.egov.common.contract.request.User userInfo = new org.egov.common.contract.request.User();
		userInfo.setTenantId("ch.chandigarh");
		userInfo.setRoles(Arrays.asList(Role.builder().code("sfsdg").name("sfs").build()));
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(searchCriteria)
				.requestInfo(RequestInfo.builder().userInfo(userInfo).build()).build();
		
		  Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(),
		  EcSearchCriteria.class)).thenReturn(searchCriteria);
		 				
		Mockito.when(repository.getChallan(searchCriteria)).thenReturn(new ArrayList<Violation>());
		Assert.assertEquals(HttpStatus.OK, service.getChallan(infoWrapper).getStatusCode());
	}
	@Test(expected = CustomException.class)
	public void testGetViolationChallanException() throws CustomException {
		service.getChallan(null);
	}

	@Test
	public void testCreateViolationChallan() {

		EchallanConfiguration echallanConfiguration=EchallanConfiguration.builder().loginUrl("hdbjbdkd").build();
		EcPayment ecPayment = EcPayment.builder().paymentUuid("aasdjiasdu8ahs89asdy8a9h").paymentMode("jnjknjkn").
				paymentStatus("PENDING").build();
		List<Document> document=Arrays.asList(Document.builder().documentUuid("hbhjbhjbjb").documentType("hbhbj").build());
		List<ViolationItem> violationItem=Arrays.asList(ViolationItem.builder().violationItemUuid("hbhjbhjbjb").itemName("hbhbj").build());
		NotificationTemplate notification=NotificationTemplate.builder().body("hdbjbjkbkd").build();
		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").encroachmentType("dbhjdbhjd").tenantId("ch").
				paymentDetails(ecPayment).document(document).violationItem(violationItem).penaltyAmount("145").
				notificationTemplate(notification).build();
		ViolationService serv=new ViolationService(wfIntegrator, objectMapper, repository, validate, echallanConfiguration, idGenRepository, producer, deviceSourceService);
		AuditDetails auditDetails = AuditDetails.builder().createdBy("1").createdTime(1546515646L).lastModifiedBy("1")
				.lastModifiedTime(15645455L).build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().auditDetails(auditDetails).requestBody(violation)
				.build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), Violation.class)).thenReturn(violation);
		List<IdResponse> idResponses=Arrays.asList(IdResponse.builder().id("djbjbd").build());
		IdGenerationResponse list=IdGenerationResponse.builder().idResponses(idResponses).build();
		when(idGenRepository.getId((infoWrapper.getRequestInfo()), (violation.getTenantId()), 
				(echallanConfiguration.getApplicationNumberIdgenName()), (echallanConfiguration.getApplicationNumberIdgenFormat()), 
				(1)))
		.thenReturn(list);
		when(repository.getpenalty(Matchers.any(Violation.class)))
		.thenReturn("");
		when(wfIntegrator.callWorkFlow(Matchers.any(ProcessInstanceRequest.class)))
		.thenReturn(ResponseInfo.builder().status("successful").build());
		Assert.assertEquals(HttpStatus.OK, serv.generateChallan(infoWrapper,"vjh").getStatusCode());
		
	}
	
	@Test(expected = CustomException.class)
	public void testCreateViolationChallan_1() {
		EchallanConfiguration echallanConfiguration=EchallanConfiguration.builder().loginUrl("hdbjbdkd").build();
		EcPayment ecPayment = EcPayment.builder().paymentUuid("aasdjiasdu8ahs89asdy8a9h").paymentMode("jnjknjkn").
				paymentStatus("PENDING").build();
		List<Document> document=Arrays.asList(Document.builder().documentUuid("hbhjbhjbjb").documentType("hbhbj").build());
		List<ViolationItem> violationItem=Arrays.asList(ViolationItem.builder().violationItemUuid("hbhjbhjbjb").itemName("hbhbj").build());
		NotificationTemplate notification=NotificationTemplate.builder().body("hdbjbjkbkd").build();
		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").encroachmentType("dbhjdbhjd").tenantId("ch").
				paymentDetails(ecPayment).document(document).violationItem(violationItem).penaltyAmount(null).
				notificationTemplate(notification).build();		
		AuditDetails auditDetails = AuditDetails.builder().createdBy("1").createdTime(1546515646L).lastModifiedBy("1")
				.lastModifiedTime(15645455L).build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().auditDetails(auditDetails).requestBody(violation)
				.build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), Violation.class)).thenReturn(violation);
		when(idGenRepository.getId((infoWrapper.getRequestInfo()), (violation.getTenantId()), 
				(echallanConfiguration.getApplicationNumberIdgenName()), (echallanConfiguration.getApplicationNumberIdgenFormat()), 
				(1)))
		.thenReturn(IdGenerationResponse.builder().build());
		when(repository.getpenalty(Matchers.any(Violation.class)))
		.thenReturn(null);
		when(wfIntegrator.callWorkFlow(Matchers.any(ProcessInstanceRequest.class)))
		.thenReturn(ResponseInfo.builder().status("successful").build());
		Assert.assertEquals(HttpStatus.OK, service.generateChallan(infoWrapper,"vhjvjhv ").getStatusCode());
		
	}

	@Test(expected = CustomException.class)
	public void testCreateViolationChallanException() {

		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(violation).build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), Violation.class)).thenReturn(violation);
		service.generateChallan(infoWrapper,"");
	}

	@Test
	public void testUpdateViolationChallan() {

		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		AuditDetails auditDetails = AuditDetails.builder().createdBy("1").createdTime(1546515646L).lastModifiedBy("1")
				.lastModifiedTime(15645455L).build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().auditDetails(auditDetails).requestBody(violation)
				.build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), Violation.class)).thenReturn(violation);
		when(wfIntegrator.callWorkFlow(Matchers.any(ProcessInstanceRequest.class)))
		.thenReturn(ResponseInfo.builder().status("successful").build());
		Assert.assertEquals(HttpStatus.OK, service.updateChallan(infoWrapper).getStatusCode());
	}

	@Test(expected = CustomException.class)
	public void testUpdateViolationChallanException() {

		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(violation).build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), Violation.class)).thenReturn(violation);
		service.updateChallan(infoWrapper);
	}

	
	@Test
	public void testAddPayment() {

		EcPayment ecPayment = EcPayment.builder().paymentUuid("aasdjiasdu8ahs89asdy8a9h").paymentMode("jnjknjkn").build();
		AuditDetails auditDetails = AuditDetails.builder().createdBy("1").createdTime(1546515646L).lastModifiedBy("1")
				.lastModifiedTime(15645455L).build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().auditDetails(auditDetails).requestBody(ecPayment)
				.build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), EcPayment.class)).thenReturn(ecPayment);
		Assert.assertEquals(HttpStatus.OK, service.addPayment(infoWrapper).getStatusCode());

	}
	
	@Test(expected = CustomException.class)
	public void testAddPaymentException() {

		Violation violation = Violation.builder().violationUuid("aasdjiasdu8ahs89asdy8a9h").build();
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(violation).build();
		Mockito.when(objectMapper.convertValue(infoWrapper.getRequestBody(), Violation.class)).thenReturn(violation);
		service.addPayment(infoWrapper);
	}
}
