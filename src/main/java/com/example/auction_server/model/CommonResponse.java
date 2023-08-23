package com.example.auction_server.model;

import lombok.Getter;

@Getter
public class CommonResponse<T> {
    private String code;
    private String message;
    private T url;
    private T responseBody;

    public CommonResponse(String code, String message, T url) {
        this.code = code;
        this.message = message;
        this.url = url;
    }

    public CommonResponse(String code, String message, T url, T responseBody) {
        this.code = code;
        this.message = message;
        this.url = url;
        this.responseBody = responseBody;
    }
}