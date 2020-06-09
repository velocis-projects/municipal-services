package org.egov.tlcalculator.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.tlcalculator.service.CTLBillingslabService;
import org.egov.tlcalculator.utils.BillingslabConstants;
import org.egov.tlcalculator.utils.CTLConstants;
import org.egov.tlcalculator.utils.ErrorConstants;
import org.egov.tlcalculator.web.models.CTLBillingSlab;
import org.egov.tlcalculator.web.models.CTLBillingSlabReq;
import org.egov.tlcalculator.web.models.CTLBillingSlabRes;
import org.egov.tlcalculator.web.models.CTLBillingSlabSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CTLBillingslabValidator {
	@Autowired
	private CTLBillingslabService service;
	
	/**
	 * Validates the create request for billing slabs. The validation involves:
	 * 1. Checking if all the billing slabs belong to a same tenant
	 * 2. Checking if the billing slab being created already exist in the system.
	 * 3. Checking if the slab is valid with respect to business rules.
	 * 4. Checking if all the provided MDMS codes are valid.
	 * @param ctlBillingSlabReq
	 */
	public void validateCreate(CTLBillingSlabReq ctlBillingSlabReq) {
		Map<String, String> errorMap = new HashMap<>();
		tenantIdCheck(ctlBillingSlabReq, errorMap);
		duplicateCheck(ctlBillingSlabReq, errorMap);
		dataNotNullCheck(ctlBillingSlabReq, errorMap);
		Map<String, List<String>> mdmsDataMap = service.getMDMSDataForValidation(ctlBillingSlabReq);
		ctlBillingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab -> validateMDMSCodes(slab, mdmsDataMap, errorMap));
		if(!CollectionUtils.isEmpty(errorMap.keySet())) {
			throw new CustomException(errorMap);
		}
		log.info("All validations passed.");
	}
	
	/**
	 * Validates the update request for billing slabs. The validation involves:
	 * 1. Checking if all the billing slabs belong to a same tenant
	 * 2. Checking if the billing slab being created are existing in the system.
	 * 3. Checking if the slab is valid with respect to business rules.
	 * 4. Checking if an existing slab is being updated to a slab that is duplicate.
	 * 5. Checking if all the provided MDMS codes are valid.
	 * 
	 * @param billingSlabReq
	 */
	public void validateUpdate(CTLBillingSlabReq billingSlabReq) {
		Map<String, String> errorMap = new HashMap<>();
		tenantIdCheck(billingSlabReq, errorMap);
		areRecordsExisiting(billingSlabReq, errorMap);
		dataNotNullCheck(billingSlabReq, errorMap);
//		duplicateCheck(billingSlabReq, errorMap); //Suppose slab s is being updated to s'. If that s' is already available, the update shouldn't be allowed.
		Map<String, List<String>> mdmsDataMap = service.getMDMSDataForValidation(billingSlabReq);
		billingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab -> validateMDMSCodes(slab, mdmsDataMap, errorMap));
		if(!CollectionUtils.isEmpty(errorMap.keySet())) {
			throw new CustomException(errorMap);
		}
		log.info("All validations passed.");
	}
	
	/**
	 * Checks if all billing slabs belong to the same tenant
	 * @param billingSlabReq
	 * @param errorMap
	 */
	public void tenantIdCheck(CTLBillingSlabReq billingSlabReq, Map<String, String> errorMap) {
		Set<String> tenantIds = billingSlabReq.getCtlBillingSlab().parallelStream().map(CTLBillingSlab::getTenantId).collect(Collectors.toSet());
		if(tenantIds.size() > 1) {
			errorMap.put(ErrorConstants.MULTIPLE_TENANT_CODE, ErrorConstants.MULTIPLE_TENANT_MSG);
			throw new CustomException(errorMap);
		}
	}
	
	/**
	 * Checks if the billing slabs being created are duplicate.
	 * @param billingSlabReq
	 * @param errorMap
	 */
	public void duplicateCheck(CTLBillingSlabReq billingSlabReq, Map<String, String> errorMap) {
		List<CTLBillingSlab> duplicateSlabs = new ArrayList<>();
		billingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab -> {
			CTLBillingSlabSearchCriteria criteria = CTLBillingSlabSearchCriteria.builder().tenantId(slab.getTenantId())
					.businessService(slab.getBusinessService()).applicationType(null == slab.getApplicationType() ? null : slab.getApplicationType().toString())
					.feeType(slab.getFeeType())
					.from(slab.getFromUom())
					.to(slab.getToUom()).build();
			CTLBillingSlabRes slabRes = service.searchSlabs(criteria, billingSlabReq.getRequestInfo());
			if(!CollectionUtils.isEmpty(slabRes.getCtlBillingSlab())) {
				if(!(slabRes.getCtlBillingSlab().size()==1 &&
						slabRes.getCtlBillingSlab().get(0).getId().equalsIgnoreCase(slab.getId())))
				duplicateSlabs.add(slab);
			}
		});
		if(!CollectionUtils.isEmpty(duplicateSlabs)) {
			errorMap.put(ErrorConstants.DUPLICATE_SLABS_CODE, ErrorConstants.DUPLICATE_SLABS_MSG + ": "+duplicateSlabs);
			throw new CustomException(errorMap);	
		}
	}
	/**
	 * Checking if the slab is valid with respect to business rules.
	 * @param billingSlabReq
	 * @param errorMap
	 */
	public void dataNotNullCheck(CTLBillingSlabReq billingSlabReq, Map<String, String> errorMap) {
		billingSlabReq.getCtlBillingSlab().parallelStream().forEach(slab -> {
			if(slab.getBusinessService()==null){
				errorMap.put(ErrorConstants.INVALID_SLAB_CODE, CTLConstants.INVALID_CTL_BUSINESS_MSG);
			}
			if(slab.getFeeType()==null) {
				errorMap.put(ErrorConstants.INVALID_SLAB_CODE, CTLConstants.INVALID_CTL_FEETYPE_MSG);
			}
			if(slab.getFromUom()==null){
				errorMap.put(ErrorConstants.INVALID_SLAB_CODE, CTLConstants.INVALID_CTL_FROMUOM_MSG);
			}
			if(slab.getToUom()==null){
				errorMap.put(ErrorConstants.INVALID_SLAB_CODE, CTLConstants.INVALID_CTL_TOUOM_MSG);
			}
			if(slab.getRate()==null){
				slab.setRate(BigDecimal.ZERO);
			}
		});
		if(!CollectionUtils.isEmpty(errorMap.keySet())) {
			throw new CustomException(errorMap);
		}
	}
	
	/**
	 * Validates MDMS codes present in the create/update request 
	 * @param billingSlab
	 * @param mdmsDataMap
	 * @param errorMap
	 */
	public void validateMDMSCodes(CTLBillingSlab billingSlab, Map<String, List<String>> mdmsDataMap, Map<String, String> errorMap) {
		if(!StringUtils.isEmpty(billingSlab.getFeeType())) {
			if(!mdmsDataMap.get(CTLConstants.CTL_MDMS_TAXHEADMASTER).contains(billingSlab.getFeeType()))
				errorMap.put(CTLConstants.INVALID_CATEGORY_CODE, CTLConstants.INVALID_CATEGORY_MSG + ": "+billingSlab.getFeeType());
			
		}
		if(!StringUtils.isEmpty(billingSlab.getUom())) {
			if(!mdmsDataMap.get(BillingslabConstants.TL_MDMS_UOM).contains(billingSlab.getUom()))
				errorMap.put(ErrorConstants.INVALID_UOM_CODE, ErrorConstants.INVALID_UOM_MSG + ": "+billingSlab.getUom());
		}
	}
	
	/**
	 * Verifies if the billing slabs being updated are there in the system.
	 * @param billingSlabReq
	 * @param errorMap
	 */
	public void areRecordsExisiting(CTLBillingSlabReq billingSlabReq, Map<String, String> errorMap) {
		CTLBillingSlabSearchCriteria criteria = CTLBillingSlabSearchCriteria.builder().tenantId(billingSlabReq.getCtlBillingSlab().get(0).getTenantId())
				.ids(billingSlabReq.getCtlBillingSlab().parallelStream().map(CTLBillingSlab :: getId).collect(Collectors.toList())).build();
		CTLBillingSlabRes slabRes = service.searchSlabs(criteria, billingSlabReq.getRequestInfo());
		List<String> ids = new ArrayList<>();
		if(billingSlabReq.getCtlBillingSlab().size() != slabRes.getCtlBillingSlab().size()) {
			List<String> responseIds = slabRes.getCtlBillingSlab().parallelStream().map(CTLBillingSlab :: getId).collect(Collectors.toList());
			for(CTLBillingSlab slab: billingSlabReq.getCtlBillingSlab()) {
				if(!responseIds.contains(slab.getId()))
					ids.add(slab.getId());
			}
			errorMap.put(ErrorConstants.INVALID_IDS_CODE, ErrorConstants.INVALID_IDS_MSG + ": "+ids);
			throw new CustomException(errorMap);
		}
		
	}
	
}
