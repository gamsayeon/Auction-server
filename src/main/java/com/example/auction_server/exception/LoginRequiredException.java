package com.example.auction_server.exception;

public class LoginRequiredException extends RuntimeException {
    public LoginRequiredException(String code) {
        super(code);
    }
}
