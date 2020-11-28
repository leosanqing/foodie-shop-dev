package com.leosanqing.controller;

import com.leosanqing.constant.ExceptionCodeEnum;
import com.leosanqing.exception.BaseRuntimeException;
import com.leosanqing.service.ItemESService;
import com.leosanqing.utils.PagedGridResult;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;

/**
 * @Author: leosanqing
 * @Date: 2019-12-08 21:01
 */
@RestController
@RequestMapping("items")
@Api(value = "商品接口", tags = {"商品展示的相关接口"})
@Validated
public class ItemController {

    final static Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Resource
    private ItemESService itemESService;

    @GetMapping("/es/search")
    public PagedGridResult searchItems(
            @NotEmpty String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            throw new BaseRuntimeException(ExceptionCodeEnum.KEYWORD_IS_EMPTY);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        page--;
        return itemESService.searchItems(keywords, sort, page, pageSize);
    }

    @GetMapping("leosanqing")
    public String hello() {

        logger.info("hello");
        return "hello";
    }
}
