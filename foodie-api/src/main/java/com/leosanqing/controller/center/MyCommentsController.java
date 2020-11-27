package com.leosanqing.controller.center;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.pojo.OrderItems;
import com.leosanqing.pojo.Orders;
import com.leosanqing.pojo.bo.center.OrderItemsCommentBO;
import com.leosanqing.pojo.vo.MyCommentVO;
import com.leosanqing.service.center.MyCommentsService;
import com.leosanqing.service.center.MyOrdersService;
import com.leosanqing.utils.JSONResult;
import com.leosanqing.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public JSONResult pending(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "orderId", value = "订单Id")
            @RequestParam @NotBlank String orderId

    ) {
        final JSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }

        final Orders orders = (Orders) result.getData();
        if (orders.getIsComment() == YesOrNo.YES.type) {
            return JSONResult.errorMsg("商品已经评价过");
        }

        final List<OrderItems> orderItems = myCommentsService.queryPendingComment(orderId);
        return JSONResult.ok(orderItems);
    }


    @PostMapping("query")
    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    public JSONResult queryMyComment(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "page", value = "当前页数")
            @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(name = "pageSize", value = "页面展示条数")
            @RequestParam(defaultValue = "10") Integer pageSize

    ) {
        IPage<MyCommentVO> myCommentVOIPage = myCommentsService.queryMyComments(userId, page, pageSize);
        return JSONResult.ok(myCommentVOIPage);
    }



    @PostMapping("saveList")
    @ApiOperation(value = "保存评价列表", notes = "保存评价列表", httpMethod = "POST")
    public JSONResult saveList(
            @ApiParam(name = "userId", value = "用户id")
            @RequestParam @NotBlank String userId,
            @ApiParam(name = "orderId", value = "订单Id")
            @RequestParam @NotBlank String orderId,
            @ApiParam(name = "orderItemList", value = "订单项列表")
            @RequestBody List<OrderItemsCommentBO> orderItemList

    ) {
        final JSONResult result = checkUserOrder(userId, orderId);
        if (result.getStatus() != HttpStatus.OK.value()) {
            return result;
        }
        if(orderItemList == null || orderItemList.isEmpty()){
            return JSONResult.errorMsg("评价列表为空");
        }

        myCommentsService.saveComments(userId, orderId, orderItemList);
        return JSONResult.ok();
    }


    /**
     * 用于验证是否为用户订单，防止恶意查询
     *
     * @param userId
     * @param orderId
     * @return
     */
    private JSONResult checkUserOrder(String userId, String orderId) {
        final Orders orders = myOrdersService.queryMyOrder(userId, orderId);
        if (orders == null) {
            return JSONResult.errorMsg("查询到订单为空");
        }
        return JSONResult.ok(orders);
    }


}
