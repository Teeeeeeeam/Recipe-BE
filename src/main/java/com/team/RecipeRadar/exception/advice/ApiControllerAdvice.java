package com.team.RecipeRadar.exception.advice;

import com.team.RecipeRadar.exception.ErrorResponse;
import com.team.RecipeRadar.exception.ex.CommentException;
import com.team.RecipeRadar.exception.ex.JwtTokenException;
import com.team.RecipeRadar.exception.ex.UnprocessableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerErrorException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.team.RecipeRadar.controller", "com.team.RecipeRadar.controller","com.team.RecipeRadar.filter.jwt"})
public class ApiControllerAdvice {


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> ServerError (ServerErrorException ex){
        ErrorResponse errorResponse = new ErrorResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> AccessDenied(AccessDeniedException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> TokenError(JwtTokenException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        log.error("errorMessage={}",e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> commentError(CommentException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
