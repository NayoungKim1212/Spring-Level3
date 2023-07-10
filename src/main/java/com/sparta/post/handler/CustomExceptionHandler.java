package com.sparta.post.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 예외처리 담당
@RestControllerAdvice // 공통적으로 처리해주기 위한 스프링에서 제공하는 Annotation
// 모든 Controller에서 발생하는 모든 예외 처리들을 다 잡아올 수 있음
public class CustomExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handlerException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({MissingRequestHeaderException.class, UnauthorizedJwtException.class}) // 토큰이 없거나 인증되지 않으면
    public ResponseEntity<String> UnauthorizedException(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
    }
}
