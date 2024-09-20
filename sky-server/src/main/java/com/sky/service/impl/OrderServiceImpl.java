package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.OrderConstant;
import com.sky.constant.PaymentConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sky.utils.WeChatPayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName OrderServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/19 16:29
 * @Version 1.0
 **/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

    @Override
    @Transactional
    public OrderSubmitVO makeOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);

        Long userId = BaseContext.getCurrentId();

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.queryAllByUser(userId);

        AddressBook addressBook = addressBookMapper.queryAddressById(order.getAddressBookId(), userId);

        User user = userMapper.getUserByUserId(userId);


        // 1 处理异常
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        else if (addressBook == null) {
            throw new OrderBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        else{
            //生成订单号
            UUID uuid = UUID.randomUUID();
            String number = uuid.toString();
            order.setNumber(number);
            //设置status = 1 未支付
            order.setStatus(OrderConstant.Pending_Payment);
            // 设置user_id
            order.setUserId(userId);
            // 生成下单时间
            LocalDateTime orderTime = LocalDateTime.now();
            order.setOrderTime(orderTime);
            //checkout_time还没有
            //支付状态 pay_status
            order.setPayStatus(PaymentConstant.NOT_PAID);
            //设置手机号
            order.setPhone(addressBook.getPhone());
            //设置地址
            order.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
            //获取用户名
            order.setUserName(user.getName());
            // 收货人
            order.setConsignee(addressBook.getConsignee());

            //insert order
            orderMapper.insertOrder(order);

            //准备order_detail
            shoppingCarts.forEach(shoppingCart -> {
                orderDetailMapper.insertOrderDetails(shoppingCart, order.getId());
            });


            //封装VO返回结果
            OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                    .id(order.getId()) //回显
                    .orderNumber(number)
                    .orderAmount(order.getAmount())
                    .orderTime(orderTime)
                    .build();

            return orderSubmitVO;
        }
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getUserByUserId(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }
}
