package com.ccommit.auction_server.model.toss;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {
    private String orderNo;
    private int amount;
    private int amountTaxFree;
    private String productDesc;
    private String apiKey;
    private String retUrl;
    private String retCancelUrl;
    private boolean autoExecute;
}
