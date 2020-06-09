package org.egov.tlcalculator.web.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CTLBillingSlab {
	
	@JsonProperty("tenantId")
	@NotNull
	@Size(min = 2, max = 128)
	private String tenantId = null;

	@JsonProperty("id")
	@Size(min = 2, max = 64)
	private String id = null;

	@JsonProperty("applicationType")
	private ApplicationTypeEnum applicationType = null;

	@JsonProperty("feeType")
	@Size(min = 2, max = 64)
	private String feeType = null;

	@JsonProperty("businessService")
	@Size(min = 2, max = 64)
	private String businessService = null;
	
	@JsonProperty("uom")
	@Size(min = 2, max = 32)
	private String uom = null;

	@JsonProperty("fromUom")
	private Double fromUom = null;

	@JsonProperty("toUom")
	private Double toUom = null;

	@JsonProperty("rate")
	private BigDecimal rate = null;
	
	private AuditDetails auditDetails;
	
	public enum ApplicationTypeEnum {
        NEW("New"),
        
        RENEW("Renew");

        private String value;

        ApplicationTypeEnum(String value) {
          this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
          return String.valueOf(value);
        }

        @JsonCreator
        public static ApplicationTypeEnum fromValue(String text) {
          for (ApplicationTypeEnum b : ApplicationTypeEnum.values()) {
            if (String.valueOf(b.value).equalsIgnoreCase(text)) {
              return b;
            }
          }
          return null;
        }
      }
}
