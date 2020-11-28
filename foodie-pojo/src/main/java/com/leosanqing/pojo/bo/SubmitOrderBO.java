package com.leosanqing.pojo.bo;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: leosanqing
 * @Date: 2019-12-14 15:05
 * @Package: com.leosanqing.pojo.bo
 * @Description: 提交订单对象
 */
@Data
@Validated
public class SubmitOrderBO {
    @NotBlank
    private String userId;
    @NotBlank
    private String itemSpecIds;
    @NotBlank
    private String addressId;
    @NotNull
    private Integer payMethod;
    private String leftMsg;
}
