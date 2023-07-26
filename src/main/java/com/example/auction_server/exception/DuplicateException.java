package com.example.auction_server.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String msg) {
        super(msg);
    }
}
