package com.ccommit.auction_server.exception;

public class ConnectionException extends RuntimeException{

    public ConnectionException(String code) {
        super(code);
    }
}
