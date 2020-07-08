package org.egov.echallan.service;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.echallan.config.EchallanConfiguration;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.web.models.AuditDetails;
import org.egov.echallan.web.models.DeviceSources;
import org.egov.echallan.web.models.Document;
import org.egov.echallan.web.models.EcPayment;
import org.egov.echallan.web.models.NotificationTemplate;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.egov.echallan.web.models.Violation;
import org.egov.echallan.web.models.ViolationItem;
import org.egov.echallan.web.models.Idgen.IdGenerationResponse;
import org.egov.echallan.web.models.Idgen.IdResponse;
import org.egov.echallan.web.models.workflow.ProcessInstanceRequest;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

@RunWith(MockitoJUnitRunner.class)
public class DeviceSourceServiceTest {
	
	@InjectMocks
	private DeviceSourceService service;
	
	@Mock
	private Producer producer;
	
	@Mock
	private EchallanConfiguration config;
	
	@Mock
	UserAgent agent;
	
	@Mock
	DeviceSources deviceSources;
	
	@Test
	public void testSaveDeviceDetails() {
		
		JSONObject deviceDetails = new JSONObject();
		AuditDetails auditDetails = AuditDetails.builder().createdBy("1").createdTime(1546515646L).lastModifiedBy("1")
				.lastModifiedTime(15645455L).build();
		
		OperatingSystem os=OperatingSystem.WINDOWS_MOBILE;
		
		DeviceSources deviceSources = DeviceSources.builder().sourceUuid("dkdkbkdbkd")//.moduleCode(moduleCode)
				.deviceDetails(deviceDetails.toJSONString())
				.deviceType(os.getName()).tenantId("ch")
				.moduleType("dkhbd").createdBy(auditDetails.getCreatedBy())
				.createdTime(auditDetails.getCreatedTime()).build();
		
		String uuid=UUID.randomUUID().toString();
							
				service.saveDeviceDetails("djvdh", "djhvdj", "ch", auditDetails);
		 
		 
		
	}
	
	@Test
	public void testSaveDeviceDetails_1() {
		
		JSONObject deviceDetails = new JSONObject();
		AuditDetails auditDetails = AuditDetails.builder().createdBy("1").createdTime(1546515646L).lastModifiedBy("1")
				.lastModifiedTime(15645455L).build();
		
		
		DeviceSources deviceSources = DeviceSources.builder().sourceUuid("dkdkbkdbkd")//.moduleCode(moduleCode)
				.deviceDetails(deviceDetails.toJSONString())
				.deviceType("").tenantId("ch")
				.moduleType("dkhbd").createdBy(auditDetails.getCreatedBy())
				.createdTime(auditDetails.getCreatedTime()).build();
		
		String uuid=UUID.randomUUID().toString();
							
				service.saveDeviceDetails("djvdh", "djhvdj", "ch", auditDetails);
		 
		 
		
	}

}
