package com.ccommit.auction_server.model.toss;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSelectResponse {
    private int code;
    private String mode;
    private String payToken;
    private String orderNo;
    private String payStatus;
    private String payMethod;
    private int amount;
    private int discountedAmount;
    private int paidPoint;
    private int discountAmountV2;
    private int paidPointV2;
    private int paidAmount;
    private int refundableAmount;
    private int amountTaxable;
    private int amountTaxFree;
    private int amountVat;
    private int amountServiceFee;
    private int disposableCupDeposit;
    private String accountBankCode;
    private String accountBankName;
    private String accountNumber;
    private CardInfo card;
    private List<PaymentTransaction> transactions;
    private LocalDateTime createdTs;
    private LocalDateTime paidTs;
}