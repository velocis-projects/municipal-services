package org.egov.tlcalculator.web.models;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CTLBillingSlabSearchCriteria {

	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("id")
	private List<String> ids;

	@JsonProperty("applicationType")
	private String applicationType;

	@JsonProperty("feeType")
	private String feeType;

	@JsonProperty("businessService")
	private String businessService;
	
	@JsonProperty("uom")
	private String uom;

	@JsonProperty("from")
	private Double from;

	@JsonProperty("to")
	private Double to;

	@JsonIgnore
	private Double uomValue;


}
