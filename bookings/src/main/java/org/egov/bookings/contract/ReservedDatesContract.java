package org.egov.bookings.contract;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import org.egov.bookings.contract.JurisdictionAvailabilityRequest.JurisdictionAvailabilityRequestBuilder;
import org.egov.bookings.model.CommercialGrndAvailabilityModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservedDatesContract implements Serializable {

	private static final long serialVersionUID = -3783257254333784296L;

	private List<CommercialGrndAvailabilityModel> commercialGrndAvailabilityLock;

}
