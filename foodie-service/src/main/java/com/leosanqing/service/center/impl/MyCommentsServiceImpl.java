package com.leosanqing.service.center.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.mapper.ItemsCommentsMapper;
import com.leosanqing.mapper.OrderItemsMapper;
import com.leosanqing.mapper.OrderStatusMapper;
import com.leosanqing.mapper.OrdersMapper;
import com.leosanqing.pojo.OrderItems;
import com.leosanqing.pojo.OrderStatus;
import com.leosanqing.pojo.Orders;
import com.leosanqing.pojo.bo.center.OrderItemsCommentBO;
import com.leosanqing.pojo.vo.MyCommentVO;
import com.leosanqing.service.center.MyCommentsService;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: leosanqing
 * @Date: 2019/12/22 下午4:09
 * @Package: com.leosanqing.service.center.impl
 * @Description: 我的评价服务实现
 */

@Service
public class MyCommentsServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements MyCommentsService {

    @Resource
    private OrderItemsMapper orderItemsMapper;

    @Resource
    private ItemsCommentsMapper itemsCommentsMapper;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private Sid sid;

    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        return orderItemsMapper.selectList(
                Wrappers.lambdaQuery(OrderItems.class)
                        .eq(OrderItems::getOrderId, orderId)
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> orderItemList) {
        // 1.保存订单评价 item_comments
        for (OrderItemsCommentBO item : orderItemList) {
            item.setCommentId(sid.nextShort());
        }

        final HashMap<String, Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("commentList",orderItemList);

        itemsCommentsMapper.saveComments(map);

        // 2.修改订单 Orders
        final Orders orders = new Orders();
        orders.setIsComment(YesOrNo.YES.type);
        orders.setId(orderId);
        baseMapper.updateById(orders);

        // 3. 修改订单状态的  commentTime
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCommentTime(new Date());
        orderStatus.setOrderId(orderId);
        orderStatusMapper.updateById(orderStatus);
    }

    @Override
    public IPage<MyCommentVO> queryMyComments(String userId, Integer page, Integer pageSize) {
        return itemsCommentsMapper.queryMyComments(userId, new Page<>(page, pageSize));
    }

//    private PagedGridResult setterPage(List<?> list, int page) {
//        PageInfo<?> pageList = new PageInfo<>(list);
//        PagedGridResult grid = new PagedGridResult();
//        grid.setPage(page);
//        grid.setRows(list);
//        grid.setTotal(pageList.getPages());
//        grid.setRecords(pageList.getTotal());
//        return grid;
//    }

}
