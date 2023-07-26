package com.example.auction_server.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginCheck {
    public static enum LoginType {
        LOGIN_SELLER, LOGIN_BUYER, LOGIN_ADMIN, WAITING_EMAIL
    }
    LoginType[] types() default {};
}