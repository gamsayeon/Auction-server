package com.ccommit.auction_server.model.toss;

import com.ccommit.auction_server.enums.PayMethod;
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
    private PayMethod payMethod;

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

    private String cashReceiptMgtKey;
    private int cardCompanyCode;
    private String cardCompanyName;
    private String cardAuthorizationNo;
    private int spreadOut;
    private boolean noInterest;
    private String salesCheckLinkUrl;
    private String cardMethodType;
    private String cardNumber;
    private String cardUserType;
    private String cardNum4Print;
    private String cardBinNumber;
}
