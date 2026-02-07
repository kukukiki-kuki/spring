package com.spring.learning.week3.mvc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 考题 4: 统一异常处理
 * 
 * 目标：
 * 实现 GlobalHandler，捕获所有 BusinessException，
 * 并返回 JSON 格式的错误响应（包含 code 和 message）。
 * 
 * 提示：
 * - 使用 @RestControllerAdvice
 * - 使用 @ExceptionHandler(BusinessException.class)
 */
public class GlobalExceptionHandlerTask {

    // 业务异常类
    public static class BusinessException extends RuntimeException {
        private final int code;
        public BusinessException(int code, String message) {
            super(message);
            this.code = code;
        }
        public int getCode() { return code; }
    }

    // 错误响应类
    public static class ErrorResponse {
        private int code;
        private String message;
        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
        public int getCode() { return code; }
        public String getMessage() { return message; }
    }

    // TODO: 请添加 @RestControllerAdvice 注解
    public static class GlobalHandler {
        
        // TODO: 请添加 @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
            // TODO: 构建并返回 ErrorResponse
            throw new UnsupportedOperationException("请实现异常处理方法");
        }
    }
    
    // 用于测试的 Controller
    @RestController
    public static class TestController {
        @GetMapping("/test/exception")
        public void throwException() {
            throw new BusinessException(400, "Invalid Request");
        }
    }
}
