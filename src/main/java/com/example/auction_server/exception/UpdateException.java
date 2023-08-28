package com.example.auction_server.exception;

public class UpdateException extends AuctionCommonException {
    public UpdateException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
