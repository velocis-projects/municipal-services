package org.egov.tlcalculator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tlcalculator.config.CTLBillingSlabConfigs;
import org.egov.tlcalculator.kafka.broker.TLCalculatorProducer;
import org.egov.tlcalculator.repository.CTLBillingslabRepository;
import org.egov.tlcalculator.repository.builder.CTLBillingslabQueryBuilder;
import org.egov.tlcalculator.utils.BillingslabConstants;
import org.egov.tlcalculator.utils.BillingslabUtils;
import org.egov.tlcalculator.utils.CTLConstants;
import org.egov.tlcalculator.utils.ResponseInfoFactory;
import org.egov.tlcalculator.web.models.AuditDetails;
import org.egov.tlcalculator.web.models.CTLBillingSlab;
import org.egov.tlcalculator.web.models.CTLBillingSlabReq;
import org.egov.tlcalculator.web.models.CTLBillingSlabRes;
import org.egov.tlcalculator.web.models.CTLBillingSlabSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CTLBillingslabService {
	@Autowired
	private BillingslabUtils util;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private TLCalculatorProducer producer;
	
	@Autowired
	private ResponseInfoFactory factory;
	
	@Autowired
	private CTLBillingslabRepository repository;
	
	@Autowired
	private CTLBillingslabQueryBuilder queryBuilder;
	
	@Autowired
	private CTLBillingSlabConfigs ctlBillingSlabConfigs;
	
	/**
	 * Service layer for creating billing slabs
	 * @param billingSlabReq
	 * @return
	 */
	public CTLBillingSlabRes createSlabs(CTLBillingSlabReq billingSlabReq) {
		enrichSlabsForCreate(billingSlabReq);
		billingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab -> {
			List<CTLBillingSlab> slabs = new ArrayList<>();
			slabs.add(slab);
			CTLBillingSlabReq req = CTLBillingSlabReq.builder().requestInfo(billingSlabReq.getRequestInfo()).ctlBillingSlab(slabs).build();
			producer.push(ctlBillingSlabConfigs.getPersisterCTLSaveTopic(), req);
		});
		return CTLBillingSlabRes.builder().responseInfo(factory.createResponseInfoFromRequestInfo(billingSlabReq.getRequestInfo(), true))
				.ctlBillingSlab(billingSlabReq.getCtlBillingSlab()).build();
	}
	
	/**
	 * Service layer for searching billing slabs from the db
	 * @param criteria
	 * @param requestInfo
	 * @return
	 */
	public CTLBillingSlabRes searchSlabs(CTLBillingSlabSearchCriteria criteria, RequestInfo requestInfo) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getSearchQuery(criteria, preparedStmtList);
		return CTLBillingSlabRes.builder().responseInfo(factory.createResponseInfoFromRequestInfo(requestInfo, true))
				.ctlBillingSlab(repository.getDataFromDB(query, preparedStmtList)).build();
	}
	
	/**
	 * Service layer for updating billing slabs
	 * @param billingSlabReq
	 * @return
	 */
	public CTLBillingSlabRes updateSlabs(CTLBillingSlabReq billingSlabReq) {
		enrichSlabsForUpdate(billingSlabReq);
		billingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab -> {
			List<CTLBillingSlab> slabs = new ArrayList<>();
			slabs.add(slab);
			CTLBillingSlabReq req = CTLBillingSlabReq.builder().requestInfo(billingSlabReq.getRequestInfo()).ctlBillingSlab(slabs).build();
			producer.push(ctlBillingSlabConfigs.getPersisterCTLUpdateTopic(), req);
		});
		return CTLBillingSlabRes.builder().responseInfo(factory.createResponseInfoFromRequestInfo(billingSlabReq.getRequestInfo(), true))
				.ctlBillingSlab(billingSlabReq.getCtlBillingSlab()).build();
	}
	
	

	public Map<String, List<String>> getMDMSDataForValidation(CTLBillingSlabReq ctlBillingSlabReq) {
		Map<String, List<String>> mdmsMap = new HashMap<>();
		String[] masters = {CTLConstants.CTL_MDMS_TAXHEADMASTER, BillingslabConstants.TL_MDMS_UOM};
		for(String master: Arrays.asList(masters)) {
			StringBuilder uri = new StringBuilder();
			String module = CTLConstants.CTL_MDMS_BILLINGSERVICE;
			if(master.equals(BillingslabConstants.TL_MDMS_UOM))
				module = BillingslabConstants.COMMON_MASTERS_MDMS_MODULE_NAME;
			MdmsCriteriaReq request = util.prepareMDMSSearchReq(uri, ctlBillingSlabReq.getCtlBillingSlab().get(0).getTenantId(), module, master, null, ctlBillingSlabReq.getRequestInfo());
			try {
				Object response = restTemplate.postForObject(uri.toString(), request, Map.class);
				if(null != response) {
					String jsonPath=null;
					if(master.equals(BillingslabConstants.TL_MDMS_UOM))
						 jsonPath = BillingslabConstants.MDMS_JSONPATH_FOR_MASTER_CODES.replaceAll("#module#", module).replaceAll("#master#", master);
					else
						jsonPath = CTLConstants.MDMS_JSONPATH_FOR_MASTER_CATEGORY.replaceAll("#module#", module).replaceAll("#master#", master);
					List<String> data = JsonPath.read(response, jsonPath);
					mdmsMap.put(master, data);
				}
			}catch(Exception e) {
				log.error("Couldn't fetch master: "+master);
				log.error("Exception: "+e);
				mdmsMap.put(master, new ArrayList<>());
				continue;
			}
			
		}
		
		return mdmsMap;
	}
	
	/**
	 * Enriches the request for creating billing slabs. Enrichment includes:
	 * 1. Preparing audit information for the slab
	 * 2. Setting id to the billing slabs
	 * @param billingSlabReq
	 */
	public void enrichSlabsForCreate(CTLBillingSlabReq billingSlabReq) {
		AuditDetails audit = AuditDetails.builder().createdBy(billingSlabReq.getRequestInfo().getUserInfo().getUuid())
				.createdTime(new Date().getTime()).lastModifiedBy(billingSlabReq.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(new Date().getTime()).build();
		for(CTLBillingSlab slab: billingSlabReq.getCtlBillingSlab()) {
			slab.setId(UUID.randomUUID().toString());
			slab.setAuditDetails(audit);
		}
	}
	
	/**
	 * Enriches the request for updating billing slabs. Enrichment includes:
	 * 1. Preparing audit information for the slab
	 * @param billingSlabReq
	 */
	public void enrichSlabsForUpdate(CTLBillingSlabReq billingSlabReq) {
		AuditDetails audit = AuditDetails.builder().lastModifiedBy(billingSlabReq.getRequestInfo().getUserInfo().getUuid()).lastModifiedTime(new Date().getTime()).build();
		billingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab ->  slab.setAuditDetails(audit) );
	}
}
