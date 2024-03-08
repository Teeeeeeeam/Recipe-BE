package com.team.RecipeRadar.exception.advice;

import com.team.RecipeRadar.exception.ErrorResponse;
import com.team.RecipeRadar.exception.ex.UnprocessableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerErrorException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.team.RecipeRadar.controller", "com.team.RecipeRadar.controller"})
public class ApiControllerAdvice {


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> ServerError (ServerErrorException ex){
        ErrorResponse errorResponse = new ErrorResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}
