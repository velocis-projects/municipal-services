package org.egov.echallan.repository;

import java.util.List;

import javax.validation.Valid;

import org.egov.echallan.config.EchallanConfiguration;
import org.egov.echallan.producer.Producer;
import org.egov.echallan.repository.builder.EcQueryBuilder;
import org.egov.echallan.repository.rowmapper.ViolationDetailRowMapper;
import org.egov.echallan.web.models.EcSearchCriteria;
import org.egov.echallan.web.models.RequestInfoWrapper;
import org.egov.echallan.web.models.VendorRegistration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class VendorRegistrationRepository {

	private JdbcTemplate jdbcTemplate;

	private Producer producer;

	private EchallanConfiguration config;

	public VendorRegistrationRepository(JdbcTemplate jdbcTemplate, Producer producer, EchallanConfiguration config,
			ViolationDetailRowMapper rowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.producer = producer;
		this.config = config;
	}
	
	/**
     * fetches the list of vendor details
     *
     * @param searchCriteria Search criteria to apply filter
     * @return Returns the list of vendor details
     */

	public List<VendorRegistration> getVendor(EcSearchCriteria searchCriteria) {
		log.info("VendorRegistration Repository - getVendor Method");
		List<VendorRegistration> vendor;

		String parameter = "%" + searchCriteria.getSearchText() + "%";

		if (null != searchCriteria.getSearchText() && !searchCriteria.getSearchText().isEmpty()) {

			vendor = jdbcTemplate.query(EcQueryBuilder.GET_VENDOR_DETAIL_SEARCH,
					new Object[] { parameter, parameter, parameter },
					new BeanPropertyRowMapper<VendorRegistration>(VendorRegistration.class));
			return vendor;
		} else {
			vendor = jdbcTemplate.query(EcQueryBuilder.GET_VENDOR_DETAIL, new Object[] {},
					new BeanPropertyRowMapper<VendorRegistration>(VendorRegistration.class));
			return vendor;

		}

	}

	 /**
     * Pushes the request in createVendor topic to save vendor data 
     *
     * @param vendorRegistration VendorRegistration model
     */
	public void saveVendor(@Valid VendorRegistration vendorRegistration) {
		log.info("VendorRegistration Repository - saveVendor Method");
		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(vendorRegistration).build();
		producer.push(config.getCreateVendorTopic(), infoWrapper);

	}

	 /**
    * Pushes the request in updateVendor topic to update vendor data 
    *
    * @param vendorRegistration VendorRegistration model
    */
	public void updateVendor(@Valid VendorRegistration vendorRegistration) {
		log.info("VendorRegistration Repository - updateVendor Method");

		RequestInfoWrapper infoWrapper = RequestInfoWrapper.builder().requestBody(vendorRegistration).build();
		producer.push(config.getUpdateVendorTopic(), infoWrapper);

	}

}
