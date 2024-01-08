package com.ccommit.auction_server.model.toss;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundsResponse {
    private int code;
    private String refundNo;
    private LocalDateTime approvalTime;
    private int refundableAmount;
    private int discountedAmount;
    private int paidPoint;
    private int paidAmount;
    private int refundedAmount;
    private int refundedDiscountAmount;
    private int refundedPoint;
    private int refundedPaidAmount;
    private String payToken;
    private String transactionId;
    private String payMethod;
    private String accountBankCode;
    private String accountBankName;
    private String accountNumber;
    private String cashReceiptMgtKey;
}
