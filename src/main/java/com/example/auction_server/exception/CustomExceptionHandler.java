package com.example.auction_server.exception;

import com.example.auction_server.model.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = { AddException.class })
    public ResponseEntity<Object> handleAddException(AddException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = { DuplicateException.class })
    public ResponseEntity<Object> handleDuplicateException(DuplicateException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = { LoginRequiredException.class })
    public ResponseEntity<Object> handleLoginRequiredException(LoginRequiredException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = { LogoutFailedException.class })
    public ResponseEntity<Object> handleLogoutFailedException(LogoutFailedException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = { NotMatchingException.class })
    public ResponseEntity<Object> handleNotMatchingException(NotMatchingException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = { UpdateException.class })
    public ResponseEntity<Object> handleUpdateException(UpdateException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

    @ExceptionHandler(value = { UserAccessDeniedException.class })
    public ResponseEntity<Object> handleUserAccessDeniedException(UserAccessDeniedException ex, HttpServletRequest request) {
        CommonResponse commonResponse = new CommonResponse("ERR_1000", ex.getMessage(), request.getServletPath());
        return ResponseEntity.badRequest().body(commonResponse);
    }

}
