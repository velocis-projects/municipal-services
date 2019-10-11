package org.egov.pt.calculator.service;

import static org.egov.pt.calculator.util.CalculatorConstants.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.calculator.validator.CalculationValidator;
import org.egov.pt.calculator.web.models.BillingSlab;
import org.egov.pt.calculator.web.models.BillingSlabSearchCriteria;
import org.egov.pt.calculator.web.models.Calculation;
import org.egov.pt.calculator.web.models.CalculationCriteria;
import org.egov.pt.calculator.web.models.CalculationReq;
import org.egov.pt.calculator.web.models.CalculationRes;
import org.egov.pt.calculator.web.models.TaxHeadEstimate;
import org.egov.pt.calculator.web.models.demand.Category;
import org.egov.pt.calculator.web.models.demand.TaxHeadMaster;
import org.egov.pt.calculator.web.models.property.DepreciationAppreciation;
import org.egov.pt.calculator.web.models.property.Property;
import org.egov.pt.calculator.web.models.property.PropertyDetail;
import org.egov.pt.calculator.web.models.property.Rate;
import org.egov.pt.calculator.web.models.property.Unit;
import org.egov.pt.calculator.web.models.property.UnitAdditionalDetails;
import org.egov.pt.calculator.web.models.property.UsageCategorySubMinor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EstimationService {

	@Autowired
	private BillingSlabService billingSlabService;

	@Autowired
	private PayService payService;

	@Autowired
	private MasterDataService mDataService;

	@Autowired
	private DemandService demandService;

	@Autowired
	CalculationValidator calcValidator;

	/**
	 * Generates a map with assessment-number of property as key and estimation
	 * map(taxhead code as key, amount to be paid as value) as value will be called
	 * by calculate api
	 *
	 * @param request incoming calculation request containing the criteria.
	 * @return Map<String, Calculation> key of assessment number and value of
	 *         calculation object.
	 */
	public Map<String, Calculation> getEstimationPropertyMap(CalculationReq request) {

		RequestInfo requestInfo = request.getRequestInfo();
		List<CalculationCriteria> criteriaList = request.getCalculationCriteria();
		String tenantId = criteriaList.get(0).getProperty().getTenantId();

		Map<String, Object> masterFinancilMap = mDataService.getMasterMap(request);
		Map<String, List<Object>> masterMap = new HashMap<>();
		mDataService.setPropertyMasterValues(requestInfo, tenantId, masterMap);
		Map<String, Calculation> calculationPropertyMap = new HashMap<>();
		for (CalculationCriteria criteria : criteriaList) {
			Property property = criteria.getProperty();
			PropertyDetail detail = property.getPropertyDetails().get(0);
			calcValidator.validatePropertyForCalculation(detail);
			String assessmentNumber = detail.getAssessmentNumber();
			Calculation calculation = getCalculation(requestInfo, criteria,
					getEstimationMap(criteria, requestInfo, masterMap), masterFinancilMap);
			calculation.setServiceNumber(assessmentNumber);
			calculationPropertyMap.put(assessmentNumber, calculation);
		}
		return calculationPropertyMap;
	}

	/**
	 * Method to estimate the tax to be paid for given property will be called by
	 * estimate api
	 *
	 * @param request incoming calculation request containing the criteria.
	 * @return CalculationRes calculation object containing all the tax for the
	 *         given criteria.
	 */
	public CalculationRes getTaxCalculation(CalculationReq request) {

		CalculationCriteria criteria = request.getCalculationCriteria().get(0);
		Property property = criteria.getProperty();
		PropertyDetail detail = property.getPropertyDetails().get(0);
		calcValidator.validatePropertyForCalculation(detail);
		Map<String, Object> masterMap = mDataService.getMasterMap(request);
		return new CalculationRes(new ResponseInfo(), Collections.singletonList(getCalculation(request.getRequestInfo(),
				criteria, getEstimationMap(criteria, request.getRequestInfo(), null), masterMap)));
	}

	/**
	 * Gets billing slabs based on financial year, ward, mohalla, roadType,
	 * propertyType Calculate tax amount Prepare tax heads
	 *
	 * @param criteria    criteria based on which calculation will be done.
	 * @param requestInfo request info from incoming request.
	 * @param masterMap
	 * @return Map<String, Double>
	 */
	private Map<String, List> getEstimationMap(CalculationCriteria criteria, RequestInfo requestInfo,
			Map<String, List<Object>> masterMap) {

		BigDecimal exemption = BigDecimal.ZERO;
		Property property = criteria.getProperty();
		List<BillingSlab> filteredBillingSlabs = getSlabsFiltered(property, requestInfo);
		if (CollectionUtils.isEmpty(filteredBillingSlabs)) {
			// throw error No matching billing slabs
		}

		TaxHeadEstimate ptTaxHead = getPropertyTaxhead(property, filteredBillingSlabs, masterMap,
				criteria.getAssessmentYear(), exemption);

		List<TaxHeadEstimate> estimates = new ArrayList<TaxHeadEstimate>();
		estimates.add(ptTaxHead);
		estimates.add(getLatePenaltyTaxhead(ptTaxHead.getEstimateAmount(), masterMap, criteria.getAssessmentYear()));
		estimates.add(getLateAssessmentPenaltyTaxhead(property));
		estimates.add(getAdHocPenaltyTaxhead(property));
		estimates.add(getRebateTaxhead());
		estimates.add(getAdHocRebateTaxhead(property));
		estimates.add(getUsageExemptionTaxhead(exemption));

		log.info("estimates", estimates);
		Map<String, List> estimatesAndBillingSlabs = new HashMap<>();
		estimatesAndBillingSlabs.put("estimates", estimates);
		estimatesAndBillingSlabs.put("billingSlabIds",
				filteredBillingSlabs.stream().map(slab -> slab.getId()).collect(Collectors.toList()));

		return estimatesAndBillingSlabs;
	}

	/**
	 * 
	 * @return
	 */
	private TaxHeadEstimate getRebateTaxhead() {
		
		return TaxHeadEstimate.builder().taxHeadCode(PT_TIME_REBATE).estimateAmount(BigDecimal.ZERO).build();
	}

	private TaxHeadEstimate getUsageExemptionTaxhead(BigDecimal exemption) {

		return TaxHeadEstimate.builder().taxHeadCode(PT_USAGE_EXEMPTION).estimateAmount(exemption).build();
	}

	private TaxHeadEstimate getAdHocRebateTaxhead(Property property) {
		Map details = (Map) property.getPropertyDetails().get(0).getAdditionalDetails();
		BigDecimal amount = BigDecimal.valueOf((Integer) details.get(AD_HOC_REBATE_JSON_STRING));
		return TaxHeadEstimate.builder().taxHeadCode(AD_HOC_REBATE_JSON_STRING).estimateAmount(amount).build();

	}

	/**
	 * current date, assessment year, penalty rates
	 * 
	 * @param bigDecimal
	 * @param masterMap
	 * @return
	 */
	private TaxHeadEstimate getLatePenaltyTaxhead(BigDecimal propertyTax, Map<String, List<Object>> masterMap,
			String assessmentYear) {

		BigDecimal penalty = BigDecimal.ZERO;
		BigDecimal penaltyRate = getPenaltyRate(masterMap);

		int fromYear = Integer.parseInt(assessmentYear.split("-")[0]) + 1;
		int toYear = Year.now().getValue();
		int years = toYear - fromYear;

		penalty = propertyTax.multiply(penaltyRate).divide(HUNDRED).multiply(BigDecimal.valueOf(years));

		return TaxHeadEstimate.builder().taxHeadCode(PT_TIME_PENALTY).estimateAmount(penalty).build();
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	private TaxHeadEstimate getAdHocPenaltyTaxhead(Property property) {
		return TaxHeadEstimate.builder().taxHeadCode(PT_ADHOC_PENALTY)
				.estimateAmount(property.getPropertyDetails().get(0).getAdhocPenalty()).build();
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	private TaxHeadEstimate getLateAssessmentPenaltyTaxhead(Property property) {
		Map details = (Map) property.getPropertyDetails().get(0).getAdditionalDetails();
		BigDecimal amount = BigDecimal.valueOf((Integer)details.get(ONE_TIME_PENALTY_JSON_STRING));
		return TaxHeadEstimate.builder().taxHeadCode(PT_LATE_ASSESSMENT_PENALTY).estimateAmount(amount).build();
	}

	/**
	 * Calculates property taxes
	 * 
	 * @param property
	 * @param filteredBillingSlabs
	 * @param masterMap
	 * @return
	 */
	private TaxHeadEstimate getPropertyTaxhead(Property property, List<BillingSlab> filteredBillingSlabs,
			Map<String, List<Object>> masterMap, String assessmentYear, BigDecimal exemption) {

		List<BillingSlab> usedSlabs = new ArrayList<BillingSlab>();
		List<Unit> units = property.getPropertyDetails().get(0).getUnits();
		PropertyDetail propertyDetail = property.getPropertyDetails().get(0);
		BigDecimal monthMultiplier = BigDecimal.valueOf(12);
		// only one billing slab
		// vacant Land AV = Carpet Area * residential unit rate * multiplier factor *12
		if (propertyDetail.getPropertyType().equalsIgnoreCase(PT_TYPE_VACANT_LAND)) {
			if (filteredBillingSlabs.size() != 1) {
				// throw error
			}

			BigDecimal carpetArea = propertyDetail.getLandArea();
			BigDecimal unitRate = BigDecimal.valueOf(filteredBillingSlabs.get(0).getUnitRate());
			Unit unit = propertyDetail.getUnits().get(0);
			BigDecimal taxRate = getTaxRate(masterMap, unit);
			BigDecimal multipleFactor = getMultipleFactor(masterMap, unit);
			BigDecimal landAV = carpetArea.multiply(unitRate).multiply(multipleFactor).multiply(monthMultiplier);
			BigDecimal taxAmount = landAV.multiply(taxRate);

			return TaxHeadEstimate.builder().taxHeadCode(PT_TAX).estimateAmount(taxAmount).build();

		}

		// builtup AV = unitArea * unit rate * multiplier factor *12
		if (propertyDetail.getPropertyType().equalsIgnoreCase(BUILTUP)) {

			BigDecimal taxAmount = BigDecimal.ZERO;
			int unoccupiedLandCount = 0;
			BigDecimal unoccupiedLandTaxAmount = BigDecimal.ZERO;
			for (Unit unit : units) {
				BigDecimal unitTaxAmount = BigDecimal.ZERO;
				Optional<BillingSlab> billingSlab = filteredBillingSlabs.stream()
						.filter(slab -> slab.getConstructionType().equalsIgnoreCase(unit.getConstructionType()))
						.findFirst();
				if (billingSlab.isPresent()) {
					usedSlabs.add(billingSlab.get());
					if (unit.getUsageCategoryMajor().equals(NONRESIDENTIAL)) {
						BigDecimal carpetArea = unit.getUnitArea();
						BigDecimal unitRate = BigDecimal.valueOf(filteredBillingSlabs.get(0).getUnitRate());
						BigDecimal taxRate = getTaxRate(masterMap, unit);
						BigDecimal multipleFactor = getMultipleFactor(masterMap, unit);

						BigDecimal landAV = carpetArea.multiply(multipleFactor).multiply(monthMultiplier);
						unitTaxAmount = landAV.multiply(taxRate);

						if (unoccupiedLandCount == 0) {
							BigDecimal unoccupiedLandArea = propertyDetail.getLandArea();
							BigDecimal unoccupiedLandAV = unoccupiedLandArea.multiply(unitRate).multiply(multipleFactor)
									.multiply(monthMultiplier);
							unoccupiedLandTaxAmount = unoccupiedLandAV.multiply(taxRate);
							unoccupiedLandCount = unoccupiedLandCount + 1;
						}

					} else if (unit.getUsageCategoryMajor().equals(RESIDENTIAL)) {
						UnitAdditionalDetails unitAdtlDetails = unit.getAdditionalDetails();
						BigDecimal carpetArea = BigDecimal.ZERO;
						if (unitAdtlDetails.isInnerDimensionsKnown()) {
							BigDecimal bathroomArea = unitAdtlDetails.getBathroomArea()
									.multiply(BATHROOM_AREA_MULTIPLIER);
							BigDecimal commonArea = unitAdtlDetails.getCommonArea().multiply(COMMON_AREA_MULTIPLIER);
							BigDecimal garageArea = unitAdtlDetails.getGarageArea().multiply(GARAGE_AREA_MULTIPLIER);
							BigDecimal roomsArea = unitAdtlDetails.getRoomsArea().multiply(ROOMS_AREA_MULTIPLIER);
							carpetArea = bathroomArea.add(commonArea).add(garageArea).add(roomsArea);
						} else {
							carpetArea = unit.getUnitArea().multiply(COVERED_AREA_MULTIPLIER);
						}

						BigDecimal unitRate = BigDecimal.valueOf(filteredBillingSlabs.get(0).getUnitRate());
						BigDecimal taxRate = getTaxRate(masterMap, unit);
						BigDecimal exemptionRate = getExemptionRate(masterMap, unit);
						BigDecimal appreciationDepreciation = getAppreciationDepreciation(masterMap, unit,
								assessmentYear);
						BigDecimal appreDepreAmount = carpetArea.multiply(unitRate).multiply(appreciationDepreciation)
								.multiply(monthMultiplier);
						BigDecimal landAV = carpetArea.multiply(unitRate).multiply(appreciationDepreciation)
								.multiply(monthMultiplier);

						if (unit.getOccupancyType().equals(RENTED)) {
							landAV = landAV.add(appreDepreAmount);
						} else {
							landAV = landAV.subtract(appreDepreAmount);
						}

						unitTaxAmount = landAV.multiply(taxRate);
						exemption = exemption.add(unitTaxAmount.multiply(exemptionRate).divide(HUNDRED));
					}

				} else {
					// Throw error
				}

				taxAmount = taxAmount.add(unitTaxAmount).add(unoccupiedLandTaxAmount);
			}

			return TaxHeadEstimate.builder().taxHeadCode(PT_TAX).estimateAmount(taxAmount).build();

		}

		return null;
	}

	private BigDecimal getAppreciationDepreciation(Map<String, List<Object>> masterMap, Unit unit,
			String assessmentYear) {
		// TODO check below
		int fromYear = Integer.parseInt(unit.getConstructionYear());
		int toYear = Integer.parseInt(assessmentYear.split("-")[0]);
		int age = toYear - fromYear;

		List<DepreciationAppreciation> filtered = new ArrayList<>();
		for (Object val : masterMap.get(DEPRECIATION_APPRECIATION)) {
			DepreciationAppreciation castedVal = (DepreciationAppreciation) val;

			if (castedVal.getAgeOfBuilding().getYearFrom() <= age
					&& age <= castedVal.getAgeOfBuilding().getYearFrom()) {
				filtered.add(castedVal);
			}
		}

		if (filtered.size() == 1) {
			return filtered.get(0).getDepreciationAppreciation();
		}
		return null;

	}

	private BigDecimal getMultipleFactor(Map<String, List<Object>> masterMap, Unit unit) {

		List<UsageCategorySubMinor> filtered = new ArrayList<>();
		for (Object val : masterMap.get(USAGE_SUB_MINOR_MASTER)) {
			UsageCategorySubMinor castedVal = (UsageCategorySubMinor) val;

			if (castedVal.getCode().equalsIgnoreCase(unit.getUsageCategorySubMinor())) {
				filtered.add(castedVal);
			}
		}

		if (filtered.size() == 1) {
			return filtered.get(0).getARVFactor();
		}
		return null;
	}

	private BigDecimal getExemptionRate(Map<String, List<Object>> masterMap, Unit unit) {

		List<UsageCategorySubMinor> filtered = new ArrayList<>();
		for (Object val : masterMap.get(USAGE_SUB_MINOR_MASTER)) {
			UsageCategorySubMinor castedVal = (UsageCategorySubMinor) val;

			if (castedVal.getCode().equalsIgnoreCase(unit.getUsageCategorySubMinor())) {
				filtered.add(castedVal);
			}
		}

		if (filtered.size() == 1) {
			return filtered.get(0).getExemption().getRate();
		}
		return null;
	}

	private BigDecimal getPenaltyRate(Map<String, List<Object>> masterMap) {

		for (Object val : masterMap.get(PENALTY_INTREST_RATE)) {
			Rate castedVal = (Rate) val;

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate startDate = LocalDate.parse(castedVal.getStartingDay(), formatter);
			LocalDate currentDate = LocalDate.now();
			if (startDate.isBefore(currentDate)) {
				return castedVal.getRate();
			}
		}

		return null;
	}

	private BigDecimal getTaxRate(Map<String, List<Object>> masterMap, Unit unit) {

		List<Rate> filtered = new ArrayList<>();

		if (unit.getUsageCategoryMajor().equalsIgnoreCase("RESIDENTIAL")) {

			for (Object val : masterMap.get(TAX_RATE)) {
				Rate castedVal = (Rate) val;
				// TODO check below
				if (castedVal.getTaxhead().equalsIgnoreCase("General Tax (Residential)")) {
					filtered.add(castedVal);
				}
			}

			if (filtered.size() == 1) {
				return filtered.get(0).getRate();
			}
		} else {
			for (Object val : masterMap.get(TAX_RATE)) {
				Rate castedVal = (Rate) val;
				if (castedVal.getTaxhead().equalsIgnoreCase("General Tax (Non-residential)")) {
					filtered.add(castedVal);
				}
			}

			if (filtered.size() == 1) {
				return filtered.get(0).getRate();
			}
		}
		return null;
	}

	/**
	 * Prepares Calculation Response based on the provided TaxHeadEstimate List
	 *
	 * All the credit taxHeads will be payable and all debit tax heads will be
	 * deducted.
	 *
	 * @param criteria    criteria based on which calculation will be done.
	 * @param requestInfo request info from incoming request.
	 * @param masterMap2
	 * @return Calculation object constructed based on the resulting tax amount and
	 *         other applicables(rebate/penalty)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Calculation getCalculation(RequestInfo requestInfo, CalculationCriteria criteria,
			Map<String, List> estimatesAndBillingSlabs, Map<String, Object> masterMap) {

		List<TaxHeadEstimate> estimates = estimatesAndBillingSlabs.get("estimates");
		List<String> billingSlabIds = estimatesAndBillingSlabs.get("billingSlabIds");

		Property property = criteria.getProperty();
		PropertyDetail detail = property.getPropertyDetails().get(0);
		String assessmentYear = detail.getFinancialYear();
		String assessmentNumber = null != detail.getAssessmentNumber() ? detail.getAssessmentNumber()
				: criteria.getAssesmentNumber();
		String tenantId = null != property.getTenantId() ? property.getTenantId() : criteria.getTenantId();

		Map<String, Map<String, Object>> financialYearMaster = (Map<String, Map<String, Object>>) masterMap
				.get(FINANCIALYEAR_MASTER_KEY);

		Map<String, Object> finYearMap = financialYearMaster.get(assessmentYear);
		Long fromDate = (Long) finYearMap.get(FINANCIAL_YEAR_STARTING_DATE);
		Long toDate = (Long) finYearMap.get(FINANCIAL_YEAR_ENDING_DATE);
		Map<String, Category> taxHeadCategoryMap = ((List<TaxHeadMaster>) masterMap.get(TAXHEADMASTER_MASTER_KEY))
				.stream().collect(Collectors.toMap(TaxHeadMaster::getCode, TaxHeadMaster::getCategory));

		BigDecimal taxAmt = BigDecimal.ZERO;
		BigDecimal penalty = BigDecimal.ZERO;
		BigDecimal exemption = BigDecimal.ZERO;
		BigDecimal rebate = BigDecimal.ZERO;
		BigDecimal ptTax = BigDecimal.ZERO;

		for (TaxHeadEstimate estimate : estimates) {

			Category category = taxHeadCategoryMap.get(estimate.getTaxHeadCode());
			estimate.setCategory(category);
			// TODO detele below after updating tax heads in mdms
			if (category != null) {
				switch (category) {

				case TAX:
					taxAmt = taxAmt.add(estimate.getEstimateAmount());
					if (estimate.getTaxHeadCode().equalsIgnoreCase(PT_TAX))
						ptTax = ptTax.add(estimate.getEstimateAmount());
					break;

				case PENALTY:
					penalty = penalty.add(estimate.getEstimateAmount());
					break;

				case REBATE:
					rebate = rebate.add(estimate.getEstimateAmount());
					break;

				case EXEMPTION:
					exemption = exemption.add(estimate.getEstimateAmount());
					break;

				default:
					taxAmt = taxAmt.add(estimate.getEstimateAmount());
					break;
				}
			}
		}
		TaxHeadEstimate decimalEstimate = payService.roundOfDecimals(taxAmt.add(penalty), rebate.add(exemption));
		if (null != decimalEstimate) {
			decimalEstimate.setCategory(taxHeadCategoryMap.get(decimalEstimate.getTaxHeadCode()));
			estimates.add(decimalEstimate);
			if (decimalEstimate.getEstimateAmount().compareTo(BigDecimal.ZERO) >= 0)
				taxAmt = taxAmt.add(decimalEstimate.getEstimateAmount());
			else
				rebate = rebate.add(decimalEstimate.getEstimateAmount());
		}

		BigDecimal totalAmount = taxAmt.add(penalty).add(rebate).add(exemption);
		// false in the argument represents that the demand shouldn't be updated from
		// this call
		BigDecimal collectedAmtForOldDemand = demandService.getCarryForwardAndCancelOldDemand(ptTax, criteria,
				requestInfo, false);

		return Calculation.builder().totalAmount(totalAmount.subtract(collectedAmtForOldDemand)).taxAmount(taxAmt)
				.penalty(penalty).exemption(exemption).rebate(rebate).fromDate(fromDate).toDate(toDate)
				.tenantId(tenantId).serviceNumber(assessmentNumber).taxHeadEstimates(estimates)
				.billingSlabIds(billingSlabIds).build();
	}

	/**
	 * Getting billing slab for the financial year, ward, mohalla,
	 * roadType,propertyType for given Property
	 */
	private List<BillingSlab> getSlabsFiltered(Property property, RequestInfo requestInfo) {

		PropertyDetail detail = property.getPropertyDetails().get(0);
		String tenantId = property.getTenantId();
		LinkedHashMap additionalDetails = (LinkedHashMap) detail.getAdditionalDetails();
		String roadType = (String) additionalDetails.get(ROAD_TYPE_JSON_STRING);
		// TODO ward
		BillingSlabSearchCriteria slabSearchCriteria = BillingSlabSearchCriteria.builder().tenantId(tenantId).ward("")
				.propertyType(detail.getPropertyType()).roadType(roadType)
				.mohalla(property.getAddress().getLocality().getArea()).build();

		List<BillingSlab> billingSlabs = billingSlabService.searchBillingSlabs(requestInfo, slabSearchCriteria)
				.getBillingSlab();
		return billingSlabs;
	}

}
