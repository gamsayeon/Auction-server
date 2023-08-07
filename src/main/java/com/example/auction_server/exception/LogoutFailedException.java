package com.example.auction_server.exception;

public class LogoutFailedException extends RuntimeException {
    public LogoutFailedException(String msg) {
        super(msg);
    }
}
