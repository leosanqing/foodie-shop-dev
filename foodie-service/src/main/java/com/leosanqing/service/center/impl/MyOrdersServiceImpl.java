package com.leosanqing.service.center.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.mapper.OrderStatusMapper;
import com.leosanqing.mapper.OrdersMapper;
import com.leosanqing.pojo.OrderStatus;
import com.leosanqing.pojo.Orders;
import com.leosanqing.pojo.vo.MyOrdersVO;
import com.leosanqing.pojo.vo.OrderStatusCountsVO;
import com.leosanqing.service.center.MyOrdersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: leosanqing
 * @Date: 2019/12/21 下午10:42
 * @Package: com.leosanqing.service.center.impl
 * @Description: 我的订单相关服务实现
 */
@Service
public class MyOrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements MyOrdersService {

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public IPage<MyOrdersVO> queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {
        return baseMapper.queryMyOrders(userId, orderStatus, new Page<>(page, pageSize));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus updateOrder = new OrderStatus();
        updateOrder.setOrderStatus(OrderStatus.OrderStatusEnum.WAIT_RECEIVE.type);
        updateOrder.setDeliverTime(new Date());

        orderStatusMapper.update(
                updateOrder,
                Wrappers.lambdaUpdate(OrderStatus.class)
                        .eq(OrderStatus::getOrderId, orderId)
                        .eq(OrderStatus::getOrderStatus, OrderStatus.OrderStatusEnum.WAIT_DELIVER.type)
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteOrder(String userId, String orderId) {
        return lambdaUpdate()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .set(Orders::getIsDelete, YesOrNo.YES.type)
                .set(Orders::getUpdatedTime, new Date())
                .update();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean confirmReceive(String orderId) {
        OrderStatus updateOrder = new OrderStatus();
        updateOrder.setOrderStatus(OrderStatus.OrderStatusEnum.SUCCESS.type);
        updateOrder.setSuccessTime(new Date());

        return 1 == orderStatusMapper.update(
                updateOrder,
                Wrappers.lambdaUpdate(OrderStatus.class)
                        .eq(OrderStatus::getOrderId, orderId)
                        .eq(OrderStatus::getOrderStatus, OrderStatus.OrderStatusEnum.WAIT_RECEIVE.type)
        );
    }

    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        return lambdaQuery()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .eq(Orders::getIsDelete, YesOrNo.NO.type)
                .one();
    }

    @Override
    public OrderStatusCountsVO getOrderStatusCount(String userId) {

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        map.put("orderStatus", OrderStatus.OrderStatusEnum.WAIT_PAY.type);

        int waitPayCounts = baseMapper.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatus.OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = baseMapper.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatus.OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = baseMapper.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatus.OrderStatusEnum.SUCCESS.type);
        map.put("isComment", YesOrNo.NO.type);

        int waitCommentCounts = baseMapper.getMyOrderStatusCounts(map);

        return new OrderStatusCountsVO(
                waitPayCounts,
                waitDeliverCounts,
                waitReceiveCounts,
                waitCommentCounts
        );
    }

    @Override
    public IPage<OrderStatus> getMyOrderTrend(String userId, Integer page, Integer pageSize) {
        return baseMapper.getMyOrderTrend(userId, new Page<>(page, pageSize));
    }
}
