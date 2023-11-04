package com.ccommit.auction_server.model;

import com.ccommit.auction_server.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApproveResponse {
    @JsonProperty("code")
    private int code;

    @JsonProperty("mode")
    private String mode;

    @JsonProperty("orderNo")
    private String orderNo;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("approvalTime")
    private String approvalTime;

    @JsonProperty("stateMsg")
    private String stateMsg;

    @JsonProperty("discountedAmount")
    private int discountedAmount;

    @JsonProperty("paidAmount")
    private int paidAmount;

    @JsonProperty("payMethod")
    private PaymentMethod payMethod;

    @JsonProperty("payToken")
    private String payToken;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("accountBankCode")
    private String accountBankCode;

    @JsonProperty("accountBankName")
    private String accountBankName;

    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("paidPoint")
    private int paidPoint;
}
