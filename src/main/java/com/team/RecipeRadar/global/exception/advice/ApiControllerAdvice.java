package com.team.RecipeRadar.global.exception.advice;


import com.team.RecipeRadar.domain.like.ex.LikeException;
import com.team.RecipeRadar.global.exception.ex.*;
import com.team.RecipeRadar.global.exception.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.img.ImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
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

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> commentError(CommentException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> LikeError(LikeException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> BadRequest(BadRequestException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> Forbidden(ForbiddenException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> UnSupport(UnsupportedMediaTypeStatusException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }
    
    /* NoSuchElementException의 예외를 400예외 처리 */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> noSuch_BadRequest(NoSuchElementException e){
        log.error("Exception occurred:", e);
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* IllegalArgumentException 예외를 403예외 처리 사용자가 아닌 타인 사용시)*/
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegal_Forbidden(IllegalArgumentException e){
        log.error("Exception occurred:", e);
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegalState_BadRequest(IllegalStateException e){
        log.error("Exception occurred:", e);
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* 숫자 아닐시 400 예외 */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> number_BadRequest(NumberFormatException e){
        log.error("Exception occurred:", e);
        ErrorResponse response = new ErrorResponse<>(false, "숫자만 입력해주세요.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* 이미지 관련 예외 */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> Image_BadRequest(ImageException e){
        log.error("Exception occurred:", e);
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
