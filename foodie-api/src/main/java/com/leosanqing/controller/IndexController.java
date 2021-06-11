package com.leosanqing.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.pojo.Carousel;
import com.leosanqing.pojo.Category;
import com.leosanqing.pojo.vo.CategoryVO;
import com.leosanqing.pojo.vo.NewItemsVO;
import com.leosanqing.service.CarouselService;
import com.leosanqing.service.CategoryService;
import com.leosanqing.utils.JsonUtils;
import com.leosanqing.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2019-12-07 22:42
 */
@RestController
@Api(value = "首页", tags = {"首页展示的相关接口"})
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("api/v1/index/carousel")
    @ApiOperation(value = "获取首页了轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    public List<Carousel> carousel() throws IOException {

        /*
         * 轮播图失效时间：
         *  1. 由后台运行系统统一重置，然后删除缓存
         *  2. 定时重置，比如每天夜里
         *  3. 设置超时时间，时间过了重置
         */
        List<Carousel> carousels;
        final String carouselStr = redisOperator.get("carousel");
        if (StringUtils.isNotBlank(carouselStr)) {
            return objectMapper.readValue(
                    carouselStr, new TypeReference<List<Carousel>>() {
                    }
            );
        }

        carousels = carouselService.queryAll(YesOrNo.YES.type);
        redisOperator.set("carousel", JsonUtils.objectToJson(carousels));

        return carousels;
    }

    @GetMapping("api/v1/index/cats")
    @ApiOperation(value = "获取一级目录所有节点", notes = "获取一级目录所有节点", httpMethod = "GET")
    public List<Category> cats() throws IOException {

        List<Category> categoryList;

        final String catsStr = redisOperator.get("cats");
        if (StringUtils.isNotBlank(catsStr)) {
            return objectMapper.readValue(
                    catsStr, new TypeReference<List<Category>>() {
                    });
        }

        categoryList = categoryService.queryAllRootLevelCat();
        redisOperator.set("cats", JsonUtils.objectToJson(categoryList));
        return categoryList;
    }

    @GetMapping("api/v1/index/subCat/{rootCatId}")
    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    public List<CategoryVO> subCats(
            @ApiParam(name = "rootCatId", value = "一级分类Id", required = true)
            @PathVariable @NotNull Integer rootCatId) throws IOException {

        final String subCatStr = redisOperator.get("subCat:" + rootCatId);

        if (StringUtils.isNotBlank(subCatStr)) {
            return objectMapper.readValue(subCatStr, new TypeReference<List<CategoryVO>>() {
            });
        }

        List<CategoryVO> categoryVOList = categoryService.getSubCatList(rootCatId);
        if (CollectionUtils.isEmpty(categoryVOList)) {
            redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(categoryVOList));
        } else {
            redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(categoryVOList), 5 * 60 * 1000);
        }

        return categoryVOList;
    }


    @GetMapping("api/v1/index/sixNewItems/{rootCatId}")
    @ApiOperation(value = "查询每个分类下的六个最新商品", notes = "查询每个分类下的六个最新商品", httpMethod = "GET")
    public List<NewItemsVO> getSixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类Id", required = true)
            @PathVariable @NotNull Integer rootCatId) {
        return categoryService.getSixNewItemsLazy(rootCatId);
    }

}
