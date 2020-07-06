package org.egov.echallan.repository;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.echallan.config.EchallanConfiguration;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.repository.builder.EcQueryBuilder;
import org.egov.echallan.repository.rowmapper.ViolationDetailRowMapper;
import org.egov.echallan.web.models.EcPayment;
import org.egov.echallan.web.models.EcSearchCriteria;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.egov.echallan.web.models.Violation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ViolationRepository {

	private JdbcTemplate jdbcTemplate;

	private ViolationDetailRowMapper rowMapper;

	private Producer producer;

	private EchallanConfiguration config;

	private EcQueryBuilder ecQueryBuilder;

	public ViolationRepository(JdbcTemplate jdbcTemplate, Producer producer, EchallanConfiguration config,
			ViolationDetailRowMapper rowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.rowMapper = rowMapper;
		this.producer = producer;
		this.config = config;
		this.ecQueryBuilder = ecQueryBuilder;
	}

	 /**
    * Pushes the request in generateChallan topic to save challan data 
    *
    * @param violationMaster Violation model
    */
	public void generateChallan(@Valid Violation violationMaster) {
		log.info("Violation Repository - generateChallan Method");
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(violationMaster).build();
		producer.push(config.getGenerateChallanTopic(), infoWrapper);

	}

	 /**
	    * Pushes the request in updateChallan topic to update challan status 
	    *
	    * @param violationMaster Violation model
	    */
	public void updateChallan(@Valid Violation violationMaster) {
		log.info("Violation Repository - updateChallan Method");
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(violationMaster).build();
		producer.push(config.getUpdateChallanTopic(), infoWrapper);
	}

	
	/**
     * fetches the list of challans for different screens
     *
     * @param searchCriteria Search criteria to apply filter
     * @return Returns the list of challans
     */

	public List<Violation> getChallan(EcSearchCriteria searchCriteria) {
		log.info("Violation Repository - getChallan Method");

		List<Violation> violationDetailList;

		String parameter = "%" + searchCriteria.getSearchText() + "%";

		if (null != searchCriteria.getSearchText() && !searchCriteria.getSearchText().isEmpty()) {

			violationDetailList = jdbcTemplate.query(EcQueryBuilder.GET_VIOLATION_MASTER_SEARCH,
					new Object[] { parameter, parameter, parameter, parameter, parameter, parameter, parameter,
							searchCriteria.getTenantId() },
					rowMapper);

			if (violationDetailList.size() >= searchCriteria.getLimit()) {

				return new ArrayList<Violation>(violationDetailList.subList(0, searchCriteria.getLimit()));
			} else {
				return violationDetailList;
			}

		} else if (searchCriteria.getAction().equalsIgnoreCase("auctionChallan")) {

			violationDetailList = jdbcTemplate.query(EcQueryBuilder.GET_VIOLATION_MASTER_AUTION,
					new Object[] { searchCriteria.getTenantId() }, rowMapper);
			return violationDetailList;

		} else if (searchCriteria.getAction().equalsIgnoreCase("ChallanSM")) {

			violationDetailList = jdbcTemplate.query(EcQueryBuilder.GET_VIOLATION_MASTER_SM,
					new Object[] { searchCriteria.getTenantId() }, rowMapper);
			return violationDetailList;

		} else {
			violationDetailList = jdbcTemplate.query(EcQueryBuilder.GET_VIOLATION_MASTER,
					new Object[] { searchCriteria.getTenantId() }, rowMapper);

			if (violationDetailList.size() >= searchCriteria.getLimit()) {
				return new ArrayList<Violation>(violationDetailList.subList(0, searchCriteria.getLimit()));

			} else {
				return violationDetailList;
			}
		}

	}

	/**
     * fetches the penalty amount and fine date validation
     *
     * @param violationMaster Violation model
     * @return Returns the penalty amount
     */
	public String getpenalty(@Valid Violation violationMaster) {
		log.info("Violation Repository - getpenalty Method");
		String numberOfViolation;
		String x = null;
		try {
			if (violationMaster.getEncroachmentType().equals("Seizure of Vehicles")) {
				numberOfViolation = violationMaster.getViolationItem().get(0).getItemType();
			} else {
				numberOfViolation = violationMaster.getNumberOfViolation() + "";
			}
			x = jdbcTemplate.queryForObject(EcQueryBuilder.GET_FINE_PENALTY_AMOUNT,
					new Object[] { violationMaster.getEncroachmentType(), numberOfViolation },

					(String.class));
		} catch (Exception e) {
			return x;
		}
		return x;

	}

	 /**
	    * Pushes the request in AddpaymentHistory topic to to maintain payment history for online payment
	    *
	    * @param ecPayment EcPayment model
	    */
	public void addPayment(EcPayment ecPayment) {
		log.info("Violation Repository - addPayment Method");
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(ecPayment).build();
		producer.push(config.getAddpaymentHistoryTopic(), infoWrapper);

	}

	/**
     * fetches the list of challans which are marked as defective by storemanager
     *
     * @param searchCriteria EcSearchCriteria model
     * @return Returns the list of challans
     */
	public List<Violation> getChallanForHOD(EcSearchCriteria searchCriteria) {
		log.info("Violation Repository - getChallanForHOD Method");

		List<Violation> violationDetailList = jdbcTemplate.query(EcQueryBuilder.GET_VIOLATION_MASTER_HOD,
				new Object[] { searchCriteria.getTenantId() }, rowMapper);
		return violationDetailList;
	}

	/**
     * fetches the list of challans whoose auctions are need to be approved by hod
     *
     * @param searchCriteria EcSearchCriteria model
     * @return Returns the list of challans
     */
	public List<Violation> getChallanForAuctionHOD(EcSearchCriteria searchCriteria) {
		log.info("Violation Repository - getChallanForAuctionHOD Method");

		List<Violation> violationDetailList = jdbcTemplate.query(EcQueryBuilder.GET_VIOLATION_MASTER_AUCTION_HOD,
				new Object[] { searchCriteria.getTenantId() }, rowMapper);
		return violationDetailList;
	}

}
