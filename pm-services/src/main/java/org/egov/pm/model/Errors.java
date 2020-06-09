package org.egov.pm.model;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Errors {

	private ResponseInfo responseInfo;
	private ErrorResponseInfo error;

}
