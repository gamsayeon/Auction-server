package com.example.auction_server.validation.annotation;

import com.example.auction_server.validation.UniqueUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUserValidator.class)
public @interface UniqueUser {
    String message() default "중복된 아이디입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
