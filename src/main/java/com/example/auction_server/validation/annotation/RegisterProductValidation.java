package com.example.auction_server.validation.annotation;

import com.example.auction_server.validation.RegisterProductValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegisterProductValidator.class)
public @interface RegisterProductValidation {
    String message() default "상품을 등록하지 못했습니다. 설정들을 다시 확인해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
