package com.ccommit.auction_server.exception;

public class NetworkConnectionException extends RuntimeException {
    public NetworkConnectionException(String code) {
        super(code);
    }
}
