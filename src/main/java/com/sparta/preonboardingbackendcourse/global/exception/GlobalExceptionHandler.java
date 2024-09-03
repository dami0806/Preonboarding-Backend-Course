package com.sparta.preonboardingbackendcourse.global.exception;

import com.sparta.preonboardingbackendcourse.global.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //valid 에러 처리:
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<String>> handleValidationException(MethodArgumentNotValidException ex) {
        ResponseDto<String> errorResponse = ResponseDto.<String>builder()
                .message(ex.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ResponseDto<String>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ResponseDto<String> errorResponse = ResponseDto.<String>builder()
                .message(errorCode.getMessage())
                .data(null)
                .build();
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}
