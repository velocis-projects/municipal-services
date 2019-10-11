package org.egov.pt.calculator.web.models.property;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "name", "usageCategoryMinor", "usageCategoryMajor", "active", "fromFY", "ARVFactor",
        "exemption" })
@Getter
@Setter
public class UsageCategorySubMinor {

    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("usageCategoryMinor")
    private String usageCategoryMinor;
    @JsonProperty("usageCategoryMajor")
    private String usageCategoryMajor;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("fromFY")
    private String fromFY;
    @JsonProperty("ARVFactor")
    private BigDecimal aRVFactor;
    @JsonProperty("exemption")
    private Exemption exemption;
}