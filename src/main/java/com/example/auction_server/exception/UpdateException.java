package com.example.auction_server.exception;

public class UpdateException extends RuntimeException {
    public UpdateException(String code) {
        super(code);
    }
}
