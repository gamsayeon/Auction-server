package com.example.auction_server.exception;

public class BidFailedNotStartException extends RuntimeException {
    public BidFailedNotStartException(String code) {
        super(code);
    }
}
