package com.team.RecipeRadar.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Around("com.team.RecipeRadar.global.aop.Pointcuts.checkTime()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            // 실행하기 전에 할 작업들
            log.info("시작 method: {}", methodName);
            Object result = joinPoint.proceed(); // 메서드 실행

            // 실행 후에 할 작업들
            long executionTime = System.currentTimeMillis() - start;
            log.info("메서드 {} 실행시간 in {} ms", methodName, executionTime);

            return result;
        } catch (Throwable e) {
            // 예외 발생 시
            log.error("Exception in method {}: {}", methodName, e.getMessage());
            throw e;
        }
    }


    private String replacePath(String path){
        return path.replace(PATH,"");
    }
}
