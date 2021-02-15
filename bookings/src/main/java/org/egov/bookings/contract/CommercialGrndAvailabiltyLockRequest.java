package org.egov.bookings.contract;

import java.util.List;

import org.egov.bookings.model.CommercialGrndAvailabilityModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommercialGrndAvailabiltyLockRequest {

	private List<CommercialGrndAvailabilityModel> commercialGrndAvailabilityLock;
}
