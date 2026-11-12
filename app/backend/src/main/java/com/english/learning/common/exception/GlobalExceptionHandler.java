package com.english.learning.common.exception;

import com.english.learning.common.exception.BusinessException;
import com.english.learning.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value={BusinessException.class})
    @ResponseStatus(value=HttpStatus.OK)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("\u4e1a\u52a1\u5f02\u5e38: {}",e.getMessage());
        return Result.fail((Integer)e.getCode(), (String)e.getMessage());
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
        log.error("\u53c2\u6570\u6821\u9a8c\u5f02\u5e38: {}",message);
        return Result.fail((Integer)400, (String)message);
    }

    @ExceptionHandler(value={BindException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
        log.error("\u53c2\u6570\u7ed1\u5b9a\u5f02\u5e38: {}",message);
        return Result.fail((Integer)400, (String)message);
    }

    @ExceptionHandler(value={ConstraintViolationException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
        log.error("\u7ea6\u675f\u8fdd\u53cd\u5f02\u5e38: {}",message);
        return Result.fail((Integer)400, (String)message);
    }

    @ExceptionHandler(value={Exception.class})
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("\u7cfb\u7edf\u5f02\u5e38", (Throwable)e);
        return Result.fail((String)"\u7cfb\u7edf\u7e41\u5fd9,\u8bf7\u7a0d\u540e\u518d\u8bd5");
    }
}

