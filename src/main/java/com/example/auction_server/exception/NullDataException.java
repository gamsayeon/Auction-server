package com.example.auction_server.exception;

public class NullDataException extends RuntimeException {
    public NullDataException(String code) {
        super(code);
    }
}