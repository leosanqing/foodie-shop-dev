package com.leosanqing.controller;

import com.leosanqing.pojo.Items;
import com.leosanqing.pojo.ItemsImg;
import com.leosanqing.pojo.ItemsParam;
import com.leosanqing.pojo.ItemsSpec;
import com.leosanqing.pojo.vo.CommentLevelCountsVO;
import com.leosanqing.pojo.vo.ItemInfoVO;
import com.leosanqing.pojo.vo.ShopcartVO;
import com.leosanqing.service.ItemService;
import com.leosanqing.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2019-12-08 21:01
 */
@RestController
@RequestMapping("api/v1/items")
@Api(value = "商品接口", tags = {"商品展示的相关接口"})
@Validated
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping("info/{itemId}")
    @ApiOperation(value = "商品详情", notes = "商品详情", httpMethod = "GET")
    public ItemInfoVO subCats(
            @ApiParam(name = "itemId", value = "商品Id", required = true)
            @PathVariable @NotBlank String itemId) {

        Items items = itemService.queryItemsById(itemId);
        List<ItemsImg> itemsImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(items);
        itemInfoVO.setItemImgList(itemsImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);
        return itemInfoVO;
    }


    @GetMapping("commentLevel")
    @ApiOperation(value = "商品评价等级", notes = "商品评价等级", httpMethod = "GET")
    public CommentLevelCountsVO getCommentsCount(
            @ApiParam(name = "itemId", value = "商品Id", required = true)
            @RequestParam @NotBlank String itemId) {
        return itemService.queryCommentCounts(itemId);
    }

    @GetMapping("comments")
    @ApiOperation(value = "查询商品评价", notes = "查询商品评价", httpMethod = "GET")
    public PagedGridResult getCommentsCount(
            @ApiParam(name = "itemId", value = "商品Id", required = true)
            @RequestParam @NotBlank String itemId,
            @ApiParam(name = "level", value = "商品等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "第几页", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "每页个数", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return PagedGridResult.pageSetter(itemService.queryPagedComments(itemId, level, page, pageSize));
    }


    @GetMapping("search")
    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    public PagedGridResult searchItems(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam @NotBlank String keywords,
            @ApiParam(name = "sort", value = "排序规则", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "第几页", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "每页个数", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize) {

        return PagedGridResult.pageSetter(itemService.searchItems(keywords, sort, page, pageSize));
    }

    @GetMapping("catItems")
    @ApiOperation(value = "根据第三级分类搜索商品列表", notes = "根据第三级分类搜索商品列表", httpMethod = "GET")
    public PagedGridResult searchItems(
            @ApiParam(name = "catId", value = "第三级分类id", required = true)
            @RequestParam @NotEmpty Integer catId,
            @ApiParam(name = "sort", value = "排序规则", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "第几页", required = false)
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "每页个数", required = false)
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return PagedGridResult.pageSetter(itemService.searchItemsByCatId(catId, sort, page, pageSize));
    }

    @GetMapping("refresh")
    @ApiOperation(value = "刷新购物车", notes = "刷新购物车", httpMethod = "GET")
    public List<ShopcartVO> queryItemsBySpecIds(
            @ApiParam(name = "itemSpecIds", value = "商品规格Id列表", required = true)
            @RequestParam @NotBlank String itemSpecIds
    ) {
        return itemService.queryItemsBySpecIds(itemSpecIds);
    }
}
