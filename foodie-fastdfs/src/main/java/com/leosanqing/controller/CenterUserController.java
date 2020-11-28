package com.leosanqing.controller;

import com.leosanqing.constant.ExceptionCodeEnum;
import com.leosanqing.exception.BaseRuntimeException;
import com.leosanqing.pojo.Users;
import com.leosanqing.pojo.vo.UsersVO;
import com.leosanqing.resource.FileResource;
import com.leosanqing.service.FdfsService;
import com.leosanqing.service.center.CenterUserService;
import com.leosanqing.utils.CookieUtils;
import com.leosanqing.utils.JsonUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @Author: leosanqing
 * @Date: 2019-12-15 20:35
 * @Package: com.leosanqing.controller.center
 * @Description: TODO
 */
@RestController
@RequestMapping("fdfs")
@Validated
public class CenterUserController extends BaseController{

    @Autowired
    private FdfsService fdfsService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private CenterUserService centerUserService;


    @PostMapping("uploadFace")
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息", httpMethod = "POST")
    public void queryUserInfo(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "file", value = "用户头像", required = true)
            @NotNull MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response

    ) throws IOException {

        String path = "";
        String filename = file.getOriginalFilename();
        if (StringUtils.isNotBlank(filename)) {
            final String[] split = StringUtils.split(filename, "\\.");
            final String suffix = split[split.length - 1];
            if (!"png".equalsIgnoreCase(suffix)
                    && !"jpg".equalsIgnoreCase(suffix)
                    && !"jpeg".equalsIgnoreCase(suffix)) {
                throw new BaseRuntimeException(ExceptionCodeEnum.IMG_TYPE_ERROR);
            }

            path = fdfsService.upload(file, suffix);
        }
        if (StringUtils.isBlank(path)) {
            throw new BaseRuntimeException(ExceptionCodeEnum.FACE_UPLOAD_FAILED);
        } else {
            String finalUserServerUrl = fileResource.getHost() + path;

            Users users = centerUserService.updateUserFace(userId, finalUserServerUrl);

            // 后续增加令牌 整合进redis
            UsersVO usersVO = convertUsersVO(users);
            CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(usersVO), true);
        }
    }

}
