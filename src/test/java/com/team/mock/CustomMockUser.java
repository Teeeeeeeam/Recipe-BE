package com.team.mock;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//시큐리티 테스트시 목유저 생성
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomContextHolder.class)
public @interface CustomMockUser{
    String loginId() default "test";
}