package com.team.RecipeRadar.domain.notice.exception.advice;

import com.team.RecipeRadar.domain.notice.exception.*;
import com.team.RecipeRadar.domain.notice.exception.ex.AccessDeniedNoticeException;
import com.team.RecipeRadar.domain.notice.exception.ex.InvalidNoticeRequestException;
import com.team.RecipeRadar.domain.notice.exception.ex.NoticeNotFoundException;
import com.team.RecipeRadar.domain.notice.exception.ex.UnauthorizedNoticeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NoticeExceptionHandler {

    @ExceptionHandler(NoticeNotFoundException.class) // 404 Not Found
    public ResponseEntity<NoticeErrorResponse> handleNoticeNotFoundException(NoticeNotFoundException ex) {
        NoticeErrorResponse response = NoticeErrorResponse.from(HttpStatus.NOT_FOUND, "Notice Not Found", 404, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidNoticeRequestException.class) // 400 Bad Request
    public ResponseEntity<NoticeErrorResponse> handleInvalidNoticeRequestException(InvalidNoticeRequestException ex) {
        NoticeErrorResponse response = NoticeErrorResponse.from(HttpStatus.BAD_REQUEST, "Invalid Notice Request", 400, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedNoticeException.class) // 403 Forbidden (권한 부족)
    public ResponseEntity<NoticeErrorResponse> handleAccessDeniedNoticeException(AccessDeniedNoticeException ex) {
        NoticeErrorResponse response = NoticeErrorResponse.from(HttpStatus.FORBIDDEN, "Access Denied", 403, ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UnauthorizedNoticeException.class) // 401 Unauthorized (인증 오류)
    public ResponseEntity<NoticeErrorResponse> handleUnauthorizedNoticeException(UnauthorizedNoticeException ex) {
        NoticeErrorResponse response = NoticeErrorResponse.from(HttpStatus.UNAUTHORIZED, "Unauthorized", 401, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
