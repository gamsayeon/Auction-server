package com.example.auction_server.exception;

public class DeleteException extends RuntimeException {
    public DeleteException(String code) {
        super(code);
    }
}
