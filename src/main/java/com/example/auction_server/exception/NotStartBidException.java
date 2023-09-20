package com.example.auction_server.exception;

public class NotStartBidException extends RuntimeException {
    public NotStartBidException(String code) {
        super(code);
    }
}
