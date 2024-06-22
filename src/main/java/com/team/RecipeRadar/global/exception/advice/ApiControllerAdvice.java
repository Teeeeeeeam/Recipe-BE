package com.team.RecipeRadar.global.exception.advice;

import com.team.RecipeRadar.global.exception.ex.*;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.payload.ErrorResponse;
import com.team.RecipeRadar.global.exception.ex.img.ImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;


@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> ServerError (Exception e){
        ErrorResponse errorResponse = new ErrorResponse(false, "서버 오류 발생");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> TokenError(JwtTokenException e){
        ErrorResponse errorResponse = new ErrorResponse(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> UnSupport(UnsupportedMediaTypeStatusException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /* IllegalArgumentException 예외를 403예외 처리 사용자가 아닌 타인 사용시)*/
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegal_Forbidden(IllegalArgumentException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> illegalState_BadRequest(IllegalStateException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* 숫자 아닐시 400 예외 */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> number_BadRequest(NumberFormatException e){
        ErrorResponse response = new ErrorResponse<>(false, "숫자만 입력해주세요.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /* 이미지 관련 예외 */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> Image_BadRequest(ImageException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> NoSuch_BadRequest(NoSuchDataException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> Invalid_BadRequest(InvalidIdException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> Unauthorized_Forbidden(UnauthorizedException e){
        ErrorResponse response = new ErrorResponse<>(false, e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

}
