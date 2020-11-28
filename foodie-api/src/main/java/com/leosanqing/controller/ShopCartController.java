package com.leosanqing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leosanqing.pojo.bo.ShopCartBO;
import com.leosanqing.utils.JsonUtils;
import com.leosanqing.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: leosanqing
 * @Date: 2019-12-12 07:59
 */
@RestController
@RequestMapping("api/v1/shop_cart")
@Api(value = "购物车相关接口api", tags = {"用于购物车相关操作"})
public class ShopCartController extends BaseController{

    @Autowired
    private RedisOperator redisOperator;

    @PostMapping("add")
    @ApiOperation(value = "添加购物车", notes = "添加购物车", httpMethod = "POST")
    public void add(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "shopCartBO", value = "从前端传来的购物车对象")
            @RequestBody ShopCartBO shopCartBO
//            HttpServletRequest request,
//            HttpServletResponse response
    ) throws IOException {
        System.out.println(shopCartBO);
        // 前端用户在登录情况下，添加商品到购物车，会同步数据到redis

        List<ShopCartBO> shopCartBOList;
        final String shopCartStr = redisOperator.get(SHOP_CART + ":" + userId);
        if (StringUtils.isBlank(shopCartStr)) {
            shopCartBOList = new ArrayList<>();
            shopCartBOList.add(shopCartBO);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            shopCartBOList = objectMapper.readValue(shopCartStr, new TypeReference<List<ShopCartBO>>() {});

            boolean isExist = false;

            for (ShopCartBO cartBO : Objects.requireNonNull(shopCartBOList)) {
                final String specId = cartBO.getSpecId();
                if (specId.equals(shopCartBO.getSpecId())) {
                    cartBO.setBuyCounts(cartBO.getBuyCounts() + shopCartBO.getBuyCounts());
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                shopCartBOList.add(shopCartBO);
            }
        }

        redisOperator.set(SHOP_CART, JsonUtils.objectToJson(shopCartBOList));
    }


    @PostMapping("del")
    @ApiOperation(value = "删除购物车", notes = "删除购物车", httpMethod = "POST")
    public void del(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "itemSpecId", value = "购物车中的商品规格")
            @RequestBody @NotBlank String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        //  前端用户在登录情况下，删除商品到购物车，会同步数据到redis
        final String shopCartStr = redisOperator.get(SHOP_CART + ":" + userId);
        if (StringUtils.isBlank(shopCartStr)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<ShopCartBO> shopCartBOList = objectMapper.readValue(shopCartStr, new TypeReference<List<ShopCartBO>>() {});
        if (shopCartBOList != null) {
            shopCartBOList.removeIf(shopCartBO -> shopCartBO.getSpecId().equals(itemSpecId));
        }
        redisOperator.set(SHOP_CART + ":" + userId, JsonUtils.objectToJson(shopCartBOList));
    }
}
