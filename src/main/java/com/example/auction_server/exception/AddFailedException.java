package com.example.auction_server.exception;

public class AddFailedException extends AuctionCommonException {
    public AddFailedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
