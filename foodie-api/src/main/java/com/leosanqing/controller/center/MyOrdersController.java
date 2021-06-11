package com.leosanqing.controller.center;

import com.leosanqing.constant.ExceptionCodeEnum;
import com.leosanqing.exception.BaseRuntimeException;
import com.leosanqing.pojo.Orders;
import com.leosanqing.pojo.vo.OrderStatusCountsVO;
import com.leosanqing.service.center.MyOrdersService;
import com.leosanqing.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * @Author: leosanqing
 * @Date: 2019-12-15 20:00
 * @Package: com.leosanqing.controller.center
 * @Description: 用户中心订单Controller
 */
@Api(value = "我的订单-用户中心", tags = {"我的订单-用户中心展示的相关接口"})
@RestController
@Validated
public class MyOrdersController {

    @Autowired
    private MyOrdersService myOrdersService;

    @PostMapping("api/v1/my_orders/query")
    @ApiOperation(value = "查询我的订单", notes = "查询我的订单", httpMethod = "POST")
    public PagedGridResult queryMyOrders(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "orderStatus", value = "订单状态")
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "当前页数")
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "页面展示条数")
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return PagedGridResult.pageSetter(myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize));
    }


    @GetMapping("api/v1/my_orders/trend")
    @ApiOperation(value = "查询我的订单", notes = "查询我的订单", httpMethod = "GET")
    public PagedGridResult getTrend(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "page", value = "当前页数")
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "页面展示条数")
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return PagedGridResult.pageSetter(myOrdersService.getMyOrderTrend(userId, page, pageSize));
    }


    /**
     * @description: 商家发货没有后端，所以这个接口仅仅只是用于模拟
     * @author: zhuerchong
     * @date: 2020/11/28 1:36 下午
     * @param: null
     * @return:
     */
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("api/v1/my_orders/deliver")
    public void deliver(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam @NotBlank String orderId) {

        myOrdersService.updateDeliverOrderStatus(orderId);
    }


    @ApiOperation(value = "确认收货", notes = "确认收货", httpMethod = "POST")
    @PostMapping("api/v1/my_orders/confirm_receive")
    public void confirmReceive(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam @NotBlank String orderId) {

        checkUserOrder(userId, orderId);
        final boolean isSuccess = myOrdersService.confirmReceive(orderId);
        if (!isSuccess) {
            throw new BaseRuntimeException(ExceptionCodeEnum.CONFIRM_RECEIVE_FAILED);
        }
    }


    @ApiOperation(value = "删除订单", notes = "删除订单", httpMethod = "POST")
    @DeleteMapping("api/v1/my_orders/order")
    public void deleteOrder(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        checkUserOrder(userId, orderId);

        final boolean isSuccess = myOrdersService.deleteOrder(userId, orderId);
        if (!isSuccess) {
            throw new BaseRuntimeException(ExceptionCodeEnum.DELETE_ORDER_FAILED);
        }
    }


    @ApiOperation(value = "查询订单状态", notes = "查询订单状态", httpMethod = "POST")
    @PostMapping("api/v1/my_orders/status_counts")
    public OrderStatusCountsVO statusCounts(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam @NotBlank String userId) {
        return myOrdersService.getOrderStatusCount(userId);
    }

    /**
     * 用于验证是否为用户订单，防止恶意查询
     *
     * @param userId
     * @param orderId
     * @return
     */
    private void checkUserOrder(String userId, String orderId) {
        final Orders orders = myOrdersService.queryMyOrder(userId, orderId);
        if (orders == null) {
            throw new BaseRuntimeException(ExceptionCodeEnum.ORDER_NOT_EXIST);
        }
    }

}
