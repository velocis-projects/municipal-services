package org.egov.pt.calculator.web.models.demand;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * BillDetail
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillDetail {

    @JsonProperty("id")
    private String id;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("demandId")
    private String demandId;

    @JsonProperty("bill")
    private String bill;

    @JsonProperty("businessService")
    private String businessService;

    @JsonProperty("billNumber")
    private String billNumber;

    @JsonProperty("billDate")
    private Long billDate;

    @JsonProperty("consumerCode")
    private String consumerCode;

    @JsonProperty("consumerType")
    private String consumerType;

    @JsonProperty("minimumAmount")
    private BigDecimal minimumAmount;

    @JsonProperty("totalAmount")
    @NotNull
    private BigDecimal totalAmount;

    @JsonProperty("amountPaid")
    @NotNull
    private BigDecimal amountPaid;

    @JsonProperty("fromPeriod")
    private Long fromPeriod;

    @JsonProperty("toPeriod")
    private Long toPeriod;

    @JsonProperty("collectedAmount")
    private BigDecimal collectedAmount;

    @JsonProperty("collectionModesNotAllowed")
    private List<String> collectionModesNotAllowed;

    @JsonProperty("partPaymentAllowed")
    private Boolean partPaymentAllowed;

    @JsonProperty("additionalDetails")
    private JsonNode additionalDetails;

    @JsonProperty("receiptNumber")
    private String receiptNumber;

    @JsonProperty("receiptDate")
    private Long receiptDate;

    @JsonProperty("receiptType")
    private String receiptType;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("voucherHeader")
    private String voucherHeader;

    @JsonProperty("boundary")
    private String boundary;

    @JsonProperty("reasonForCancellation")
    private String reasonForCancellation;

    @JsonProperty("manualReceiptNumber")
    private String manualReceiptNumber;

    @JsonProperty("manualReceiptDate")
    private Long manualReceiptDate;

    @JsonProperty("stateId")
    private String stateId;

    @JsonProperty("fund")
    private String fund;

    @JsonProperty("function")
    private String function;

    @JsonProperty("department")
    private String department;

    @JsonProperty("billAccountDetails")
    private List<BillAccountDetail> billAccountDetails;

    @JsonProperty("status")
    private String status;

    @JsonProperty("collectionType")
    private CollectionType collectionType;


}