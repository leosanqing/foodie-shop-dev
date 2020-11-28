package com.leosanqing.advice;

import com.leosanqing.constant.IExceptionCode;

import com.leosanqing.exception.BaseRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = BindException.class)
    public IExceptionCode handle(BindException e) {
        log.error("check system error : ", e);
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        fieldErrors.forEach(
                fieldError -> {
                    stringBuilder.append(fieldError.getField())
                            .append(":")
                            .append(fieldError.getDefaultMessage())
                            .append(System.lineSeparator());
                }
        );

        return IExceptionCode.DynamicResultCode
                .builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(stringBuilder.toString())
                .build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public IExceptionCode handle(MethodArgumentNotValidException e) {
        log.error("check system error : ", e);
        StringBuilder stringBuilder = new StringBuilder();

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        fieldErrors.forEach(
                fieldError -> {
                    stringBuilder
                            .append(fieldError.getField())
                            .append(":")
                            .append(fieldError.getDefaultMessage())
                            .append(System.lineSeparator());
                }
        );

        List<ObjectError> globalErrors = e.getBindingResult().getGlobalErrors();
        globalErrors.forEach(
                objectError -> {
                    stringBuilder
                            .append(objectError.getDefaultMessage())
                            .append(System.lineSeparator());
                }
        );

        return IExceptionCode.DynamicResultCode
                .builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(stringBuilder.toString())
                .build();
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public IExceptionCode handle(ConstraintViolationException e) {
        log.error("check system error : ", e);
        StringBuilder stringBuilder = new StringBuilder();

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        constraintViolations.forEach(
                constraintViolation -> {
                    stringBuilder
//                            ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().getName()
                            .append(constraintViolation.getInvalidValue())
                            .append(":")
                            .append(constraintViolation.getMessage())
                            .append(System.lineSeparator());
                }
        );
        return IExceptionCode.DynamicResultCode
                .builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(stringBuilder.toString())
                .build();
    }

    @ExceptionHandler(value = BaseRuntimeException.class)
    public IExceptionCode handle(BaseRuntimeException e) {
        log.error("check system error : ", e);
        return e.getResultCode();
    }

    @ExceptionHandler(value = Exception.class)
    public IExceptionCode handle(Exception e) {
        log.error("check system error : ", e);
        return IExceptionCode.DynamicResultCode
                .builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage(e.getMessage())
                .build();
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public IExceptionCode handlerMaxUploadSize(MaxUploadSizeExceededException e) {
        return IExceptionCode.DynamicResultCode
                .builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorMessage("上传图片大小过大")
                .build();
    }

}
