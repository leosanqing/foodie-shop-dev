package com.leosanqing.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leosanqing.enums.OrderStatusEnum;
import com.leosanqing.enums.YesOrNo;
import com.leosanqing.mapper.OrderItemsMapper;
import com.leosanqing.mapper.OrderStatusMapper;
import com.leosanqing.mapper.OrdersMapper;
import com.leosanqing.pojo.*;
import com.leosanqing.pojo.bo.ShopCartBO;
import com.leosanqing.pojo.bo.SubmitOrderBO;
import com.leosanqing.pojo.vo.OrderVO;
import com.leosanqing.service.AddressService;
import com.leosanqing.service.ItemService;
import com.leosanqing.service.OrderService;
import com.leosanqing.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: leosanqing
 * @Date: 2019-12-15 12:31
 * @Package: com.leosanqing.service.impl
 * @Description: TODO
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Resource
    private AddressService addressService;

    @Resource
    private ItemService itemService;

    @Resource
    private OrderItemsMapper orderItemsMapper;

    @Resource
    private Sid sid;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectById(orderId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void closeOrder() {
        // 查询所有未付款订单，判断时间是否超时（1天），超时则关闭交易
        List<OrderStatus> list = orderStatusMapper.selectList(
                Wrappers
                        .lambdaQuery(OrderStatus.class)
                        .eq(OrderStatus::getOrderStatus, OrderStatus.OrderStatusEnum.WAIT_PAY.type)
        );

        list.forEach(
                orderStatus -> {
                    // 和当前时间进行对比
                    if (1 <= DateUtil.daysBetween(orderStatus.getCreatedTime(), new Date())) {
                        // 超过1天，关闭订单
                        doCloseOrder(orderStatus.getOrderId());
                    }
                }
        );
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void doCloseOrder(String orderId) {
        OrderStatus close = new OrderStatus();
        close.setOrderId(orderId);
        close.setOrderStatus(OrderStatusEnum.CLOSE.type);
        close.setCloseTime(new Date());
        orderStatusMapper.updateById(close);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVO createOrder(List<ShopCartBO> shopCartBOList, SubmitOrderBO submitOrderBO) throws InterruptedException {
        String userId = submitOrderBO.getUserId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        String addressId = submitOrderBO.getAddressId();
        String leftMsg = submitOrderBO.getLeftMsg();
        Integer payMethod = submitOrderBO.getPayMethod();

        Integer postAmount = 0;

        // 1.生成 新订单 ，填写 Orders表
        final String orderId = sid.nextShort();

        UserAddress userAddress = addressService.queryAddress(userId, addressId);

        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setUserId(userId);
        orders.setLeftMsg(leftMsg);
        orders.setPayMethod(payMethod);

        orders.setReceiverAddress(userAddress.getProvince() +
                " " + userAddress.getCity() +
                " " + userAddress.getDistrict() +
                " " + userAddress.getDetail());
        orders.setReceiverMobile(userAddress.getMobile());
        orders.setReceiverName(userAddress.getReceiver());

        orders.setPostAmount(postAmount);


        orders.setIsComment(YesOrNo.NO.type);
        orders.setIsDelete(YesOrNo.NO.type);
        orders.setCreatedTime(new Date());
        orders.setUpdatedTime(new Date());


        /*
            分库分表：orderItems 作为orders的子表，所有插入时，要先插入Orders，
            这样在插入OrderItems时，才能找到对应的分片。所以这里先插入Orders,
            计算金额后，再更新一下Orders.
         */
        orders.setTotalAmount(0);
        orders.setRealPayAmount(0);
        baseMapper.insert(orders);


        // 2.1 循环根据商品规格表，保存到商品规格表

        int totalAmount = 0;
        int realPayTotalAmount = 0;
        final String[] itemSpecIdArray = StringUtils.split(itemSpecIds, ',');
        List<ShopCartBO> toBeRemovedList = new ArrayList<>();
        for (String itemSpecId : itemSpecIdArray) {

            // 查询每个商品的规格
            ItemsSpec itemsSpec = itemService.queryItemBySpecId(itemSpecId);

            Optional<ShopCartBO> optional = shopCartBOList
                    .stream()
                    .filter(bo -> bo.getSpecId().equals(itemSpecId))
                    .findFirst();

            int counts = 0;
            if (optional.isPresent()) {
                toBeRemovedList.add(optional.get());
                counts = optional.get().getBuyCounts();
            }

            // 获取价格
            totalAmount += itemsSpec.getPriceNormal() * counts;
            realPayTotalAmount += itemsSpec.getPriceDiscount() * counts;

            // 2.2 根据商品id，获得商品图片和信息
            final String itemId = itemsSpec.getItemId();
            final String imgUrl = itemService.queryItemImgByItemId(itemId);

            // 2.3 将商品规格信息写入 订单商品表
            final OrderItems subOrderItem = new OrderItems();
            subOrderItem.setBuyCounts(counts);
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setItemId(itemId);
            subOrderItem.setId(sid.nextShort());
            subOrderItem.setItemName(itemsSpec.getName());
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemSpecName(itemsSpec.getName());
            subOrderItem.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);

            // 2.4 减库存
            itemService.decreaseItemSpecStock(itemSpecId, counts);
        }

        orders.setTotalAmount(totalAmount);
        orders.setRealPayAmount(realPayTotalAmount);

        // 因为 userId 是分片项.不能修改，所以在更新时设置成 null
        orders.setUserId(null);
        baseMapper.updateById(orders);

//        ordersMapper.insert(orders);

        // 3. 订单状态表
        final OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);

        // 模拟付款过程
        Thread.sleep(3 * 1000L);

        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_DELIVER.type);
        orderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(orderStatus);

        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setToBeRemovedList(toBeRemovedList);
        return orderVO;
    }

}
