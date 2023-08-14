package com.example.auction_server.exception;

import com.example.auction_server.model.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class})
    @ResponseBody
    public ResponseEntity<Object> handleException(RuntimeException ex, HttpServletRequest request) {
        String exceptionCode = ex.getMessage();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = {DuplicateException.class, AddException.class, NotMatchingException.class, InputSettingException.class})
    @ResponseBody
    public ResponseEntity<Object> handleAuctionCommonException(AuctionCommonException ex, HttpServletRequest request) {
        String exceptionCode = ex.getMessage();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath(), ex.getResponseBody());
        return ResponseEntity.badRequest().body(commonResponse);
    }
}
