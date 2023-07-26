package com.example.auction_server.exception;

public class NotMatchingException extends RuntimeException {
    public NotMatchingException(String msg) {
        super(msg);
    }
}
