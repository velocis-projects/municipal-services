package org.egov.pt.calculator.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.calculator.repository.Repository;
import org.egov.pt.calculator.util.CalculatorConstants;
import org.egov.pt.calculator.util.CalculatorUtils;
import org.egov.pt.calculator.util.Configurations;
import org.egov.pt.calculator.validator.CalculationValidator;
import org.egov.pt.calculator.web.models.Calculation;
import org.egov.pt.calculator.web.models.CalculationCriteria;
import org.egov.pt.calculator.web.models.CalculationReq;
import org.egov.pt.calculator.web.models.GetBillCriteria;
import org.egov.pt.calculator.web.models.TaxHeadEstimate;
import org.egov.pt.calculator.web.models.demand.Bill;
import org.egov.pt.calculator.web.models.demand.BillResponse;
import org.egov.pt.calculator.web.models.demand.Demand;
import org.egov.pt.calculator.web.models.demand.DemandDetail;
import org.egov.pt.calculator.web.models.demand.DemandRequest;
import org.egov.pt.calculator.web.models.demand.DemandResponse;
import org.egov.pt.calculator.web.models.demand.TaxHeadMaster;
import org.egov.pt.calculator.web.models.demand.TaxPeriod;
import org.egov.pt.calculator.web.models.property.OwnerInfo;
import org.egov.pt.calculator.web.models.property.Property;
import org.egov.pt.calculator.web.models.property.PropertyDetail;
import org.egov.pt.calculator.web.models.property.PropertyDetail.SourceEnum;
import org.egov.pt.calculator.web.models.property.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private EstimationService estimationService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Configurations configs;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private CalculatorUtils utils;

	@Autowired
	private Repository repository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PayService payService;

	@Autowired
	private MasterDataService mstrDataService;

	@Autowired
	private CalculationValidator validator;



	/**
	 * Filter request based on source
	 * Estimates map with tax heads
	 * Save temporary values
	 * Generate Demand
	 * Save assessments 
	 * TODO handle validation
	 * @param request
	 * @return
	 */
	public Map<String, Calculation> calculate(CalculationReq request) {
		List<CalculationCriteria> requestCriterias = request.getCalculationCriteria();
		List<CalculationCriteria> criterias = request.getCalculationCriteria().stream().filter(criteria -> !criteria
				.getProperty().getPropertyDetails().get(0).getSource().equals(SourceEnum.LEGACY_RECORD))
				.collect(Collectors.toList());
		request.setCalculationCriteria(criterias);
		Map<String, String> consumerCodeFinYearMap = new HashMap<>();
		//validate
		if (!CollectionUtils.isEmpty(request.getCalculationCriteria())) {
			Map<String, Calculation> estimateMap = estimationService.getEstimationPropertyMap(request);
			//saveIntermediateValues(estimateMap);
			List<Demand> demands = generateDemands(request, estimateMap, consumerCodeFinYearMap);
			assessmentService.saveAssessments(demands, consumerCodeFinYearMap, request.getRequestInfo());
			return estimateMap;
		} else {
			Map<String, Calculation> estimateMap = new HashMap<String, Calculation>();
			requestCriterias.stream().forEach(criteria -> estimateMap.put(criteria.getProperty().getPropertyDetails().get(0).getAssessmentNumber(), Calculation.builder().build()));
			return estimateMap;
		}
	}


	private List<Demand> generateDemands(CalculationReq request, Map<String, Calculation> estimateMap, Map<String, String>  consumerCodeFinYearMap) {

		//String tenantId = request.getCalculationCriteria().get(0).getTenantId();
		List<Demand> demands = new ArrayList<>();

		for(CalculationCriteria criteria:  request.getCalculationCriteria()){
			PropertyDetail detail = criteria.getProperty().getPropertyDetails().get(0);
			Demand demand = prepareDemand(criteria.getProperty(), estimateMap.get(detail.getAssessmentNumber()),
			request.getRequestInfo());
			demands.add(demand);
			consumerCodeFinYearMap.put(demand.getConsumerCode(), detail.getFinancialYear());
		}

		DemandRequest dmReq = DemandRequest.builder().demands(demands).requestInfo(request.getRequestInfo()).build();
		String url = new StringBuilder().append(configs.getBillingServiceHost())
				.append(configs.getDemandCreateEndPoint()).toString();
		DemandResponse res = new DemandResponse();

		try {
			if(!CollectionUtils.isEmpty(demands))
			res = restTemplate.postForObject(url, dmReq, DemandResponse.class);

		} catch (HttpClientErrorException e) {
			log.error(e.getMessage(), e);
			throw new ServiceCallException(e.getResponseBodyAsString());
		}
		log.info(" The demand Response is : " + res);

		return res.getDemands();
	}

	/**
	 * Generates and returns bill from billing service
	 * 
	 * updates the demand with penalty and rebate if applicable before generating
	 * bill
	 * 
	 * @param getBillCriteria
	 * @param requestInfoWrapper
	 */
	public BillResponse getBill(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {

		DemandResponse res = updateDemands(getBillCriteria, requestInfoWrapper);

		/**
		 * Loop through the demands and call generateBill for each demand.
		 * Group the Bills and return the bill responsew
		 */
		List<Bill> bills = new LinkedList<>();
		BillResponse billResponse;
		ResponseInfo responseInfo = null;
		StringBuilder billGenUrl;

		for(Demand demand : res.getDemands()){
			billGenUrl = utils.getBillGenUrl(getBillCriteria.getTenantId(), demand.getId(), demand.getConsumerCode());
			billResponse = mapper.convertValue(repository.fetchResult(billGenUrl, requestInfoWrapper), BillResponse.class);
			responseInfo = billResponse.getResposneInfo();
			bills.addAll(billResponse.getBill());
		}

		return BillResponse.builder().resposneInfo(responseInfo).bill(bills).build();
	}

	/**
	 * Method updates the demands based on the getBillCriteria
	 * 
	 * The response will be the list of demands updated for the 
	 * @param getBillCriteria
	 * @param requestInfoWrapper
	 * @return
	 */
	public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {
		
		if(getBillCriteria.getAmountExpected() == null) getBillCriteria.setAmountExpected(BigDecimal.ZERO);
		validator.validateGetBillCriteria(getBillCriteria);
		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		Map<String, List<Object>> masterMap = new HashMap<>();
		mstrDataService.setPropertyMasterValues(requestInfo, getBillCriteria.getTenantId(),
		masterMap);

		if(CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes()))
			getBillCriteria.setConsumerCodes(Collections.singletonList(getBillCriteria.getPropertyId()+ CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR +getBillCriteria.getAssessmentNumber()));

		DemandResponse res = mapper.convertValue(
				repository.fetchResult(utils.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);
		if (CollectionUtils.isEmpty(res.getDemands())) {
			Map<String, String> map = new HashMap<>();
			map.put(CalculatorConstants.EMPTY_DEMAND_ERROR_CODE, CalculatorConstants.EMPTY_DEMAND_ERROR_MESSAGE);
			throw new CustomException(map);
		}


		/**
		 * Loop through the consumerCodes and re-calculate the time based applicables
		 */

		Map<String,Demand> consumerCodeToDemandMap = res.getDemands().stream()
				.collect(Collectors.toMap(Demand::getConsumerCode,Function.identity()));

		List<Demand> demandsToBeUpdated = new LinkedList<>();

		String tenantId = getBillCriteria.getTenantId();

		List<TaxPeriod> taxPeriods = mstrDataService.getTaxPeriodList(requestInfoWrapper.getRequestInfo(), tenantId);

		for (String consumerCode : getBillCriteria.getConsumerCodes()) {
			Demand demand = consumerCodeToDemandMap.get(consumerCode);
			if (demand == null)
				throw new CustomException(CalculatorConstants.EMPTY_DEMAND_ERROR_CODE,
						"No demand found for the consumerCode: " + consumerCode);

			if (demand.getStatus() != null
					&& CalculatorConstants.DEMAND_CANCELLED_STATUS.equalsIgnoreCase(demand.getStatus().toString()))
				throw new CustomException(CalculatorConstants.EG_PT_INVALID_DEMAND_ERROR,
						CalculatorConstants.EG_PT_INVALID_DEMAND_ERROR_MSG);

			//applytimeBasedApplicables(demand, requestInfoWrapper,taxPeriods);

			roundOffDecimalForDemand(demand, requestInfoWrapper);

			demandsToBeUpdated.add(demand);

		}


		/**
		 * Call demand update in bulk to update the interest or penalty
		 */
		DemandRequest request = DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfo).build();
		StringBuilder updateDemandUrl = utils.getUpdateDemandUrl();
		repository.fetchResult(updateDemandUrl, request);
		return res;
	}

	/**
	 * Prepares Demand object based on the incoming calculation object and property
	 * 
	 * @param property
	 * @param calculation
	 * @return
	 */
	private Demand prepareDemand(Property property, Calculation calculation, RequestInfo requestInfo) {

		String tenantId = property.getTenantId();
		PropertyDetail detail = property.getPropertyDetails().get(0);
		String propertyType = detail.getPropertyType();
		String consumerCode = property.getPropertyId() + CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR + detail.getAssessmentNumber();
		OwnerInfo owner = null;
		if (null != detail.getCitizenInfo())
			owner = detail.getCitizenInfo();
		else
			owner = detail.getOwners().iterator().next();

		List<DemandDetail> details = new ArrayList<>();

		for (TaxHeadEstimate estimate : calculation.getTaxHeadEstimates()) {
			details.add(DemandDetail.builder().taxHeadMasterCode(estimate.getTaxHeadCode())
			.taxAmount(estimate.getEstimateAmount())
			.collectionAmount(BigDecimal.ZERO)
			.tenantId(tenantId).build());
		}

		return Demand.builder().tenantId(tenantId).businessService(configs.getPtModuleCode()).consumerType(propertyType)
				.consumerCode(consumerCode).payer(owner.toCommonUser()).taxPeriodFrom(calculation.getFromDate())
				.taxPeriodTo(calculation.getToDate()).status(Demand.StatusEnum.ACTIVE)
				.minimumAmountPayable(BigDecimal.valueOf(configs.getPtMinAmountPayable())).demandDetails(details)
				.build();
	}

	/**
	 * 
	 * Balances the decimal values in the newly updated demand by performing a roundoff
	 * 
	 * @param demand
	 * @param requestInfoWrapper
	 */
	public void roundOffDecimalForDemand(Demand demand, RequestInfoWrapper requestInfoWrapper) {
		
		List<DemandDetail> details = demand.getDemandDetails();
		String tenantId = demand.getTenantId();
		String demandId = demand.getId();

		BigDecimal taxAmount = BigDecimal.ZERO;

		// Collecting the taxHead master codes with the isDebit field in a Map
		Map<String, Boolean> isTaxHeadDebitMap = mstrDataService.getTaxHeadMasterMap(requestInfoWrapper.getRequestInfo(), tenantId).stream()
				.collect(Collectors.toMap(TaxHeadMaster::getCode, TaxHeadMaster::getIsDebit));

		/*
		 * Summing the credit amount and Debit amount in to separate variables(based on the taxhead:isdebit map) to send to roundoffDecimal method
		 */
		for (DemandDetail detail : demand.getDemandDetails()) {
				taxAmount = taxAmount.add(detail.getTaxAmount());
		}

		/*
		 *  An estimate object will be returned incase if there is a decimal value
		 *  
		 *  If no decimal value found null object will be returned 
		 */
		TaxHeadEstimate roundOffEstimate = payService.roundOfDecimals(taxAmount);

		BigDecimal decimalRoundOff = null != roundOffEstimate
				? roundOffEstimate.getEstimateAmount() : BigDecimal.ZERO;

		if(decimalRoundOff.compareTo(BigDecimal.ZERO)!=0){
				details.add(DemandDetail.builder().taxAmount(roundOffEstimate.getEstimateAmount())
						.taxHeadMasterCode(roundOffEstimate.getTaxHeadCode()).demandId(demandId).tenantId(tenantId).build());
		}


	}

}
