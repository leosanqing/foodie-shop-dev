package com.leosanqing.rpc;

import com.leosanqing.constant.IExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResBody<T> implements Serializable {
    private int status;

    private T data;

    private Integer code;
    private String message;

    public enum Status {
        OK(200),
        ERROR(500),
        ;

        private Integer code;

        Status(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }

    public static ResBody success(Object... o) {
        return ResBody.builder()
                .status(Status.OK.code)
                .code(Status.OK.getCode())
                .data(o.length == 0 ? null : o[0])
                .build();
    }

    public static ResBody error(IExceptionCode resultCode, Object... o) {
        return ResBody.builder()
                .status(Status.ERROR.code)
                .code(resultCode.getErrorCode())
                .message(resultCode.getErrorMessage())
                .data(o.length == 0 ? null : o[0])
                .build();
    }
}
