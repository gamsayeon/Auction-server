package com.example.auction_server.validation.annotation;

import com.example.auction_server.validation.EmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface EmailValidation {
    String message() default "중복된 이메일입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}