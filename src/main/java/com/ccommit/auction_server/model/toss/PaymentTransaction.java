package com.ccommit.auction_server.model.toss;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {
    private String stepType;
    private String transactionId;
    private int paidAmount;
    private int transactionAmount;
    private int discountAmount;
    private int pointAmount;
    private LocalDateTime regTs;
}
