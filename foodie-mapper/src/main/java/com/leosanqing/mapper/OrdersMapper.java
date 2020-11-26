package com.leosanqing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leosanqing.pojo.OrderStatus;
import com.leosanqing.pojo.Orders;
import com.leosanqing.pojo.vo.MyOrdersVO;
import org.apache.ibatis.annotations.Param;


import java.util.Map;

/**
 * @author zhuerchong
 */
public interface OrdersMapper extends BaseMapper<Orders> {
    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderStatus
     * @param page
     * @return
     */
    IPage<MyOrdersVO> queryMyOrders(@Param("userId") String userId, @Param("orderStatus") Integer orderStatus, @Param("page") Page<MyOrdersVO> page);

    /**
     * 查询各类订单状态
     *
     * @param map
     * @return
     */
    int getMyOrderStatusCounts(@Param("paramsMap") Map<String, Object> map);

    /**
     * 查询订单动态
     *
     * @param userId
     * @return
     */
    IPage<OrderStatus> getMyOrderTrend(@Param("userId")String userId, Page<OrderStatus> page);
}