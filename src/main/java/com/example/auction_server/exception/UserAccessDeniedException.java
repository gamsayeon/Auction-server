package com.example.auction_server.exception;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException(String code) {
        super(code);
    }
}
