package com.example.auction_server.exception;

public class AddException extends RuntimeException {
    public AddException(String code) {
        super(code);
    }
}
