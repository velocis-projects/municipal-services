package org.egov.tlcalculator.web.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CTLBillingSlabRes {
	@JsonProperty("ResponseInfo")
	@Valid
	private ResponseInfo responseInfo = null;

	@JsonProperty("CTLBillingSlabs")
	@Valid
	private List<CTLBillingSlab> ctlBillingSlab = null;

}
