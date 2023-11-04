package com.ccommit.auction_server.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    @JsonProperty("code")
    private int code;

    @JsonProperty("status")
    private int status;

    @JsonProperty("payToken")
    private String payToken;

    @JsonProperty("checkoutPage")
    private String checkoutPage;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("errorCode")
    private String errorCode;

    @JsonProperty("result")
    private int result;
}
