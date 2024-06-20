package com.team.RecipeRadar.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component("aspectAdvice")
public class AspectAdvice {

    private final String PATH = "com.team.RecipeRadar.";

    @AfterThrowing(value = "com.team.RecipeRadar.global.aop.Pointcuts.exMessage()",throwing = "e")
    public void doThrowing(JoinPoint joinPoint, Exception e){
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = replacePath(signature.getDeclaringTypeName());
        log.error("[EX] {}.{}() [message] : {} [type]: {}", className, methodName, e.getMessage(), e.getClass().getSimpleName());
    }

    private String replacePath(String path){
        return path.replace(PATH,"");
    }
}
