package com.ccommit.auction_server.model.toss;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String apiKey;
    private String payToken;
}
