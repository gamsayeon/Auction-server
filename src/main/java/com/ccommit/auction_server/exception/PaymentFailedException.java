package com.ccommit.auction_server.exception;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String code) {
        super(code);
    }
}
