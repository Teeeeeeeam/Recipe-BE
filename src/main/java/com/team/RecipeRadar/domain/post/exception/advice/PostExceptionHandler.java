package com.team.RecipeRadar.domain.post.exception.advice;

import com.team.RecipeRadar.domain.post.exception.PostErrorResponse;
import com.team.RecipeRadar.domain.post.exception.ex.AccessDeniedPostException;
import com.team.RecipeRadar.domain.post.exception.ex.InvalidPostRequestException;
import com.team.RecipeRadar.domain.post.exception.ex.PostNotFoundException;
import com.team.RecipeRadar.domain.post.exception.ex.UnauthorizedPostException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PostExceptionHandler {

    @ExceptionHandler(PostNotFoundException.class) // 404 Not Found
    public ResponseEntity<PostErrorResponse> handlePostNotFoundException(PostNotFoundException ex) {
        PostErrorResponse response = PostErrorResponse.from(HttpStatus.NOT_FOUND, "Post Not Found", 404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidPostRequestException.class) // 400 Bad Request
    public ResponseEntity<PostErrorResponse> handleInvalidPostRequestException(InvalidPostRequestException ex) {
        PostErrorResponse response = PostErrorResponse.from(HttpStatus.BAD_REQUEST, "Invalid Post Request", 400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedPostException.class) // 403 Forbidden (권한 부족)
    public ResponseEntity<PostErrorResponse> handleAccessDeniedPostException(AccessDeniedPostException ex) {
        PostErrorResponse response = PostErrorResponse.from(HttpStatus.FORBIDDEN, "Access Denied", 403, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UnauthorizedPostException.class) // 401 Unauthorized (인증 오류)
    public ResponseEntity<PostErrorResponse> handleUnauthorizedPostException(UnauthorizedPostException ex) {
        PostErrorResponse response = PostErrorResponse.from(HttpStatus.UNAUTHORIZED, "Unauthorized", 401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
