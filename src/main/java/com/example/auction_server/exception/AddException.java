package com.example.auction_server.exception;

public class AddException extends AuctionCommonException {
    public AddException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
