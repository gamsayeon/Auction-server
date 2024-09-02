package com.ccommit.auction_server.exception;

public class ProductLockedException extends AuctionCommonException {
    public ProductLockedException(String code, Object responseBody) {
        super(code, responseBody);
    }
}
