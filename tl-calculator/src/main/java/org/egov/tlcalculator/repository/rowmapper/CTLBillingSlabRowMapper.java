package org.egov.tlcalculator.repository.rowmapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.egov.tlcalculator.web.models.AuditDetails;
import org.egov.tlcalculator.web.models.CTLBillingSlab;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class CTLBillingSlabRowMapper implements ResultSetExtractor<List<CTLBillingSlab>> {

	/**
	 * Rowmapper that maps every column of the search result set to a key in the model.
	 */
	@Override
	public List<CTLBillingSlab> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, CTLBillingSlab> billingSlabMap = new HashMap<>();
		while (rs.next()) {
			String currentId = rs.getString("id");
			CTLBillingSlab currentBillingSlab = billingSlabMap.get(currentId);
			if (null == currentBillingSlab) {
				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
						.createdTime(rs.getLong("createdtime")).lastModifiedBy(rs.getString("lastmodifiedby"))
						.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();

				currentBillingSlab = CTLBillingSlab.builder().id(rs.getString("id"))
						.applicationType(CTLBillingSlab.ApplicationTypeEnum.fromValue(rs.getString("applicationType")))
						.feeType(rs.getString("feetype"))
						.businessService(rs.getString("businessService"))
						.fromUom(rs.getDouble("fromUom"))
						.rate(getBigDecimalValue(rs.getBigDecimal("rate")))
						.tenantId(rs.getString("tenantid"))
						.uom(rs.getString("uom"))
						.toUom(rs.getDouble("toUom"))
						.auditDetails(auditDetails).build();

				billingSlabMap.put(currentId, currentBillingSlab);
			}

		}

		return new ArrayList<>(billingSlabMap.values());

	}

	private BigDecimal getBigDecimalValue(BigDecimal amount){
		return Objects.isNull(amount) ? BigDecimal.ZERO : amount;
	}

}
