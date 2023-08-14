package com.example.auction_server.exception;

public class DuplicateException extends RuntimeException {
    private Object responseBody;
    public DuplicateException(String code, Object responseBody) {
        super(code);
        this.responseBody = responseBody;
    }
    public Object getResponseBody() {
        return responseBody;
    }
}
