package com.team.RecipeRadar.global.aop;

import com.team.RecipeRadar.global.payload.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

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
            log.info("start method: {}", methodName);
            Object result = joinPoint.proceed(); // 메서드 실행

            // 실행 후에 할 작업들
            long executionTime = System.currentTimeMillis() - start;
            log.info("end method:{} time in {} ms", methodName, executionTime);

            return result;
        } catch (Throwable e) {
            // 예외 발생 시
            log.error("Exception in method {}: {}", methodName, e.getMessage());
            throw e;
        }
    }

    @Around("com.team.RecipeRadar.global.aop.Pointcuts.ResultSet() && !@annotation(com.team.RecipeRadar.global.annotations.ExcludeResultSet)")
    public Object resultSet(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        log.error("An error occurred during request processing. (BingResultSet)");


        for (Object arg :args){
            if(arg instanceof BindingResult){
                BindingResult bindingResult =  (BindingResult)arg;
                List<String> errors = new ArrayList<>();
               if(bindingResult.hasErrors()){
                   for (FieldError error : bindingResult.getFieldErrors()){
                       errors.add(error.getDefaultMessage());
                   }
               }
                return ResponseEntity.badRequest().body(new ErrorResponse<>(false, "모든 값을 입력해주세요", errors));
            }
        }
        return joinPoint.proceed();
    }

    private String replacePath(String path){
        return path.replace(PATH,"");
    }
}
