package com.leosanqing.constant;

import lombok.Builder;
import lombok.Data;

public interface IExceptionCode {
    int getErrorCode();

    String getErrorMessage();

    @Builder
    @Data
    class DynamicResultCode implements IExceptionCode {
        private int errorCode;
        private String errorMessage;

        @Override
        public int getErrorCode() {
            return this.errorCode;
        }

        @Override
        public String getErrorMessage() {
            return this.errorMessage;
        }
    }
}
