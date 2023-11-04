package com.ccommit.auction_server.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApproveRequest {
    private String apiKey;
    private String payToken;
}
