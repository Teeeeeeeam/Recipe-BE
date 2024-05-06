package com.team.mock;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomContextHolderAdmin.class)
public @interface CustomMockAdmin {

    String loginId() default "admin";
}
