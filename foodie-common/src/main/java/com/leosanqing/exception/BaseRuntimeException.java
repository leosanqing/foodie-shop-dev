package com.leosanqing.exception;


import com.leosanqing.constant.IExceptionCode;
import lombok.Getter;

@Getter
public class BaseRuntimeException extends RuntimeException {

    private IExceptionCode resultCode;

    public BaseRuntimeException(IExceptionCode resultCode) {
        super(resultCode.getErrorMessage());
        this.resultCode = resultCode;
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(Throwable cause, IExceptionCode resultCode) {
        super(cause);
        this.resultCode = resultCode;
    }
}
