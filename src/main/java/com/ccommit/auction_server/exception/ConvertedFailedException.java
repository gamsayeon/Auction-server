package com.ccommit.auction_server.exception;

public class ConvertedFailedException extends AuctionCommonException{
    public ConvertedFailedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
