package com.leosanqing.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @Author: leosanqing
 * @Date: 2019-12-06 08:28
 */

@ApiModel(value = "用户对象的BO",description = "从客户端界面传输到后端的对象，封装到此Entity")
@Data
@Valid
public class UserBO {
    @ApiModelProperty(value = "用户名",name = "username",example = "leosanqing",required = true)
    @NotBlank
    private String username;

    @ApiModelProperty(value = "密码",name = "password",example = "123456",required = true)
    @NotBlank
    @Length(min = 6,max = 15)
    private String password;

    @ApiModelProperty(value = "确认密码",name = "confirmPassword",example = "123456",required = false)
    private String confirmPassword;

    @Override
    public String toString() {
        return "UserBO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
