package com.team.RecipeRadar.global.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Pointcuts {

    @Pointcut("execution(* com.team.RecipeRadar.*..*(..))")
    void exMessage(){}
    @Pointcut("execution(*  *..*Controller.*(..))")
    void checkTime(){}
}
