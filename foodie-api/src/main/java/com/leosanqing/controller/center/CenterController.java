package com.leosanqing.controller.center;

import com.leosanqing.pojo.Users;
import com.leosanqing.service.center.CenterUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * @Author: leosanqing
 * @Date: 2019-12-15 20:00
 * @Package: com.leosanqing.controller.center
 * @Description: 用户中心Controller
 */
@Api(value = "center-用户中心", tags = {"用户中心展示的相关接口"})
@RestController
@Validated
public class CenterController {

    @Autowired
    private CenterUserService centerUserService;

    @GetMapping("/api/v1/center/userInfo")
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息", httpMethod = "GET")
    public Users queryUserInfo(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId
    ) {
        return centerUserService.queryUserInfo(userId);
    }
}
