package com.leosanqing.controller;

import com.leosanqing.constant.ExceptionCodeEnum;
import com.leosanqing.exception.BaseRuntimeException;
import com.leosanqing.pojo.Users;
import com.leosanqing.pojo.bo.UserBO;
import com.leosanqing.pojo.vo.UsersVO;
import com.leosanqing.service.UserService;
import com.leosanqing.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * @Author: leosanqing
 * @Date: 2019-12-06 00:22
 */
@RestController
@Api(value = "注册登录", tags = {"用于注册的接口"})
@Slf4j
@Validated
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;


    @GetMapping("api/v1/passport/usernameIsExist")
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    public void usernameIsExist(@RequestParam @NotBlank String username) {
        // 判断用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            throw new BaseRuntimeException(ExceptionCodeEnum.USERNAME_IS_EXIST);
        }
    }

    @PostMapping("api/v1/passport/regist")
    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    public void register(@RequestBody @Validated UserBO userBO,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            throw new BaseRuntimeException(ExceptionCodeEnum.USERNAME_IS_EXIST);
        }

        // 判断两次密码是否一致
        if (!password.equals(confirmPassword)) {
            throw new BaseRuntimeException(ExceptionCodeEnum.CONFIRM_PASSWORD_INCORRECT);
        }

        // 实现注册
        Users users = userService.createUser(userBO);

//        Users userResult = setNullProperty(users);

        // 生成token，用于分布式会话
        UsersVO usersVO = convertUsersVO(users);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);

        // 同步数据到redis
        syncShopCartData(users.getId(), request, response);
    }


    @PostMapping("api/v1/passport/login")
    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    public UsersVO login(@RequestBody @Validated UserBO userBO,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 查询用户名是否存在
        Users users = null;
        try {
            users = userService.queryUsersForLogin(username, MD5Utils.getMD5Str(password));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (users == null) {
            throw new BaseRuntimeException(ExceptionCodeEnum.PASSWORD_INCORRECT);
        }

        // 分布式会话
        UsersVO usersVO = convertUsersVO(users);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        // 实现登录

        //  同步数据到redis
        syncShopCartData(usersVO.getId(), request, response);

        return usersVO;
    }


    /**
     * 同步购物车数据
     *
     * @param userId
     * @param request
     * @param response
     */
    private void syncShopCartData(String userId, HttpServletRequest request, HttpServletResponse response) {
        /*
         *  1. redis 为空，cookie 也为空。
         *                cookie 不为空，将 cookie的数据直接存入redis
         *  2. redis 不为空，cookie 为空，将redis数据覆盖cookie数据
         *                  cookie 不为空，如果存在相同商品，以cookie为主
         *
         */
        final String shopCartRedisStr = redisOperator.get(SHOP_CART + ":" + userId);
        final String cookieValue = CookieUtils.getCookieValue(request, SHOP_CART, true);

        // redis为为空，cookie不为空
        if (StringUtils.isBlank(shopCartRedisStr)) {
            if (StringUtils.isNotBlank(cookieValue)) {
                redisOperator.set(SHOP_CART + ":" + userId, cookieValue);
            }
        } else {
            if (StringUtils.isNotBlank(cookieValue)) {
            } else {
                CookieUtils.setCookie(request, response, SHOP_CART, shopCartRedisStr, true);
            }
        }
    }

    @PostMapping("api/v1/passport/logout")
    @ApiOperation(value = "退出登录", notes = "退出登录", httpMethod = "POST")
    public void logout(@RequestParam @NotBlank String userId,
                       HttpServletRequest request, HttpServletResponse response) {

        CookieUtils.deleteCookie(request, response, "user");

        // 清除redis数据
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
    }


}
