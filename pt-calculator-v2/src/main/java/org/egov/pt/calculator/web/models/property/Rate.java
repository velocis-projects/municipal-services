package org.egov.pt.calculator.web.models.property;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Rate {

    @JsonProperty("rate")
    private BigDecimal rate;
    @JsonProperty("minAmount")
    private Object minAmount;
    @JsonProperty("flatAmount")
    private Object flatAmount;
    @JsonProperty("maxAmount")
    private Object maxAmount;
    @JsonProperty("usageCategoryMajor")
    private String usageCategoryMajor;
    @JsonProperty("taxhead")
    private String taxhead;
    @JsonProperty("fromFY")
    private String fromFY;
    @JsonProperty("startingDay")
    private String startingDay;
    @JsonProperty("endingDay")
    private String endingDay;
}