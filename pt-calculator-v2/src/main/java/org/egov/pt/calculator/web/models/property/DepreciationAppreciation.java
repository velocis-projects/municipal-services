package org.egov.pt.calculator.web.models.property;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "code", "depreciationAppreciation", "ageOfBuilding", "active", "usageCategoryMajor",
        "occupancyType" })
@Getter
@Setter
public class DepreciationAppreciation {

    @JsonProperty("code")
    private String code;
    @JsonProperty("depreciationAppreciation")
    private BigDecimal depreciationAppreciation;
    @JsonProperty("ageOfBuilding")
    private AgeOfBuilding ageOfBuilding;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("usageCategoryMajor")
    private String usageCategoryMajor;
    @JsonProperty("occupancyType")
    private String occupancyType;
}