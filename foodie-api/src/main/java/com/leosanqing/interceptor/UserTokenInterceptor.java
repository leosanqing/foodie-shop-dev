package com.leosanqing.interceptor;

import com.leosanqing.constant.ExceptionCodeEnum;
import com.leosanqing.exception.BaseRuntimeException;
import com.leosanqing.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: leosanqing
 * @Date: 2020/2/27 上午9:39
 * @Package: com.leosanqing.interceptor
 * @Description: 用户会话拦截器
 */

public class UserTokenInterceptor implements HandlerInterceptor {

    @Resource
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";


    /**
     * 进入Controller之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isNotBlank(uniqueToken) && uniqueToken.equals(userToken)) {
                return true;
            }
            // 说明可能异地登录
        }
        throw new BaseRuntimeException(ExceptionCodeEnum.ANOTHER_LOCATION_NEED_TO_LOGIN);

    }

//
//    public void returnErrorResponse(HttpServletResponse response, JSONResult jsonResult) {
//
//        OutputStream outputStream = null;
//        try {
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("text/json");
//            outputStream = response.getOutputStream();
//            outputStream.write(Objects.requireNonNull(JsonUtils.objectToJson(jsonResult)).getBytes(StandardCharsets.UTF_8));
//            outputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 进入Controller之后，渲染视图之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 渲染视图之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {

    }
}
