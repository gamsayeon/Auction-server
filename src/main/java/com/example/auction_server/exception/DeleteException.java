package com.example.auction_server.exception;

public class DeleteException extends AuctionCommonException {
    public DeleteException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
