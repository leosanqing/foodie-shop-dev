package com.leosanqing.controller.center;

import com.leosanqing.constant.ExceptionCodeEnum;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.exception.BaseRuntimeException;
import com.leosanqing.pojo.OrderItems;
import com.leosanqing.pojo.Orders;
import com.leosanqing.pojo.bo.center.OrderItemsCommentBO;
import com.leosanqing.service.center.MyCommentsService;
import com.leosanqing.service.center.MyOrdersService;
import com.leosanqing.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2019/12/22 下午4:14
 * @Package: com.leosanqing.controller.center
 * @Description: 我的订单接口
 */
@Api(value = "我的订单-我的评价", tags = {"我的评价-用户中心展示的相关接口"})
@RestController
@RequestMapping("api/vi/mycomment")
@Validated
public class MyCommentsController {
    @Resource
    private MyCommentsService myCommentsService;

    @Resource
    private MyOrdersService myOrdersService;

    @PostMapping("pending")
    @ApiOperation(value = "查询我的订单", notes = "查询我的订单", httpMethod = "POST")
    public List<OrderItems> pending(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "orderId", value = "订单Id")
            @RequestParam @NotBlank String orderId

    ) {
        Orders orders = checkUserOrder(userId, orderId);
        if (orders.getIsComment() == YesOrNo.YES.type) {
            throw new BaseRuntimeException(ExceptionCodeEnum.PRODUCT_HAS_COMMENT);
        }

        return myCommentsService.queryPendingComment(orderId);
    }

    @PostMapping("query")
    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    public PagedGridResult queryMyComment(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "page", value = "当前页数")
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "页面展示条数")
            @RequestParam(defaultValue = "10") Integer pageSize

    ) {
        return PagedGridResult.pageSetter(myCommentsService.queryMyComments(userId, page, pageSize));
    }

    @PostMapping("saveList")
    @ApiOperation(value = "保存评价列表", notes = "保存评价列表", httpMethod = "POST")
    public void saveList(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "orderId", value = "订单Id")
            @RequestParam @NotBlank String orderId,
            @ApiParam(name = "orderItemList", value = "订单项列表")
            @RequestBody List<OrderItemsCommentBO> orderItemList

    ) {
        checkUserOrder(userId, orderId);
        if (orderItemList == null || orderItemList.isEmpty()) {
            throw new BaseRuntimeException(ExceptionCodeEnum.COMMENT_LIST_IS_EMPTY);
        }
        myCommentsService.saveComments(userId, orderId, orderItemList);
    }


    /**
     * 用于验证是否为用户订单，防止恶意查询
     *
     * @param userId
     * @param orderId
     * @return
     */
    private Orders checkUserOrder(String userId, String orderId) {
        final Orders orders = myOrdersService.queryMyOrder(userId, orderId);
        if (orders == null) {
            throw new BaseRuntimeException(ExceptionCodeEnum.ORDER_LIST_IS_EMPTY);
        }
        return orders;
    }

}
