package com.example.auction_server.exception;

public class InputMismatchException extends AuctionCommonException {
    public InputMismatchException(String code, Object responseBody) {
        super(code, responseBody);
    }

}
