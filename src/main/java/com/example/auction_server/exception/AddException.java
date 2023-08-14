package com.example.auction_server.exception;

public class AddException extends RuntimeException {
    private Object responseBody;
    public AddException(String code, Object responseBody) {
        super(code);
        this.responseBody = responseBody;
    }
    public Object getResponseBody() {
        return responseBody;
    }
}
