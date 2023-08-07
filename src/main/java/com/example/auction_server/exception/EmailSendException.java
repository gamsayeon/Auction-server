package com.example.auction_server.exception;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String code) {
        super(code);
    }
}
