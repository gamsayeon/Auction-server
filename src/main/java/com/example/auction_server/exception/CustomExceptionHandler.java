package com.example.auction_server.exception;

import com.example.auction_server.model.CommonResponse;
import com.example.auction_server.validation.isExistUserIdlValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LogManager.getLogger(isExistUserIdlValidator.class);

//    @ExceptionHandler(value = {RuntimeException.class})
//    @ResponseBody
//    public ResponseEntity<Object> handleException(RuntimeException ex, HttpServletRequest request) {
//        String exceptionCode = ex.getMessage();
//        CommonResponse commonResponse = new CommonResponse(exceptionCode,
//                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath());
//        logger.error(commonResponse.toString());
//        return ResponseEntity.badRequest().body(commonResponse);
//    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseBody
    public ResponseEntity<Object> handleValidationException(ValidationException ex, HttpServletRequest request) {
        String exceptionCode = ex.getCause().getMessage();
        AuctionCommonException commonException = (AuctionCommonException) ex.getCause();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath(), commonException.getResponseBody());
        logger.error(commonResponse.toString());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = {DuplicateException.class, AddException.class, NotMatchingException.class,
            InputSettingException.class, UpdateException.class})
    @ResponseBody
    public ResponseEntity<Object> handleAuctionCommonException(AuctionCommonException ex, HttpServletRequest request) {
        String exceptionCode = ex.getMessage();
        CommonResponse commonResponse = new CommonResponse(exceptionCode,
                ExceptionMessage.getExceptionMessage(exceptionCode), request.getServletPath(), ex.getResponseBody());
        logger.error(commonResponse.toString());
        return ResponseEntity.badRequest().body(commonResponse);
    }
}
