package com.github.shangtanlin.common.exception;

import com.github.shangtanlin.result.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        // 获取注解中定义的错误信息
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(message); // 返回给前端：{"code": 500, "msg": "省份不能为空"}
    }
}