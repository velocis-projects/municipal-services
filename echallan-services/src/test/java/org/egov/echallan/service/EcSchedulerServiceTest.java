package org.egov.echallan.service;

import org.egov.echallan.repository.FineMasterRepository;
import org.egov.echallan.repository.StoreItemRegisterRepository;
import org.egov.echallan.web.models.AuditDetails;
import org.egov.echallan.web.models.ItemMaster;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

public class EcSchedulerServiceTest {
	
	@Mock
	private FineMasterRepository fineRepository;
	
	@Mock
	private StoreItemRegisterRepository storeItemRegisterRepository;

	@InjectMocks
	private EcSchedulerService service;
	
	@Test public void testUpdateFineAmount() {
		
		  }

}
