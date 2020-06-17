package org.egov.pm.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponseInfo {
	private String code;
	private String message;
	private String description;
	private String params;
}
