package com.example.auction_server.exception;

public class NotMatchingException extends AuctionCommonException {
    public NotMatchingException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
