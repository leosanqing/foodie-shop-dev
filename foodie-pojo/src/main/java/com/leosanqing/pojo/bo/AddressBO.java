package com.leosanqing.pojo.bo;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Author: leosanqing
 * @Date: 2019-12-14 09:03
 * @Package: com.leosanqing.pojo.bo
 * @Description: 地址对象
 */

@Data
@Validated
public class AddressBO {
    @NotBlank
    private String addressId;
    @NotBlank
    private String userId;
    @NotBlank
    private String receiver;
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$")
    private String mobile;
    @NotBlank
    private String province;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @NotBlank
    private String detail;
}
