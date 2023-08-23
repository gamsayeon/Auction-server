package com.example.auction_server.exception;

public class InputSettingException extends AuctionCommonException {
    public InputSettingException(String code, Object responseBody){
        super(code, responseBody);
    }

}
