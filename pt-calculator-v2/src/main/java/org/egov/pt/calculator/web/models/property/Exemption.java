package org.egov.pt.calculator.web.models.property;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "rate", "maxAmount", "flatAmount" })
public class Exemption {

  @JsonProperty("rate")
  private BigDecimal rate;
  @JsonProperty("maxAmount")
  private Integer maxAmount;
  @JsonProperty("flatAmount")
  private Integer flatAmount;
}