package org.egov.pt.calculator.web.models.property;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "yearFrom", "yearTo" })
@Getter
@Setter
public class AgeOfBuilding {

    @JsonProperty("yearFrom")
    private Integer yearFrom;
    @JsonProperty("yearTo")
    private Integer yearTo;

}