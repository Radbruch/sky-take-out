package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.Websocket.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.constant.OrderConstant;
import com.sky.constant.PaymentConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailForAdminVO;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sky.utils.WeChatPayUtil;
import com.alibaba.fastjson.JSONObject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @ClassName OrderServiceImpl
 * @Description
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
    @Autowired
    private WebSocketServer webSocketServer;

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

        // 通过websocket向客户端浏览器推送消息 type orderId content
        Map map = new HashMap();
        map.put("type", 1); // 1:来单提醒， 2：客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号： "+ outTradeNo);
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }

    @Override
    public OrderDetailVO orderDetail(Long id) {
        Long userId = BaseContext.getCurrentId();
        Orders order = orderMapper.getOrderById(id);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);
        List<OrderDetail> orderDetails = orderDetailMapper.getDetailByOrderId(id);
        orderDetailVO.setOrderDetailList(orderDetails);
        return orderDetailVO;
    }

    @Override
    public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderDetailVO> page = orderMapper.getOrdersByUserId(userId);
        page.forEach(orderDetailVO -> {
            orderDetailVO.setOrderDetailList(orderDetailMapper.getDetailByOrderId(orderDetailVO.getId()));
        });
        long total = page.getTotal();
        List<OrderDetailVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void cancel(Long id,String cancelReason) {
        Orders order = orderMapper.getOrderById(id);
        order.setStatus(6);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(cancelReason);
        orderMapper.cancel(order);
    }

    @Override
    public void repetition(Long id) {
        List<OrderDetail> detailByOrderId = orderDetailMapper.getDetailByOrderId(id);
        LocalDateTime createTime = LocalDateTime.now();
        detailByOrderId.forEach(orderDetail -> {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .name(orderDetail.getName())
                    .image(orderDetail.getImage())
                    .userId(BaseContext.getCurrentId())
                    .dishId(orderDetail.getDishId())
                    .setmealId(orderDetail.getSetmealId())
                    .dishFlavor(orderDetail.getDishFlavor())
                    .number(orderDetail.getNumber())
                    .amount(orderDetail.getAmount())
                    .createTime(createTime)
                    .build();
            shoppingCartMapper.insertShoppingCart(shoppingCart);
        });
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<OrderDetailForAdminVO> page = orderMapper.conditionSearch(ordersPageQueryDTO);
        page.forEach(orderDetailForAdminVO -> {
            List<String> orderNames = orderMapper.getOrdersNameById(orderDetailForAdminVO.getId());
            String orderDishes = orderNames.toString();
            orderDetailForAdminVO.setOrderDishes(orderDishes);
        });
        long total = page.getTotal();
        List<OrderDetailForAdminVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void complete(Long id) {
        orderMapper.complete(id);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        orderMapper.rejection(ordersRejectionDTO);
    }

    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(OrderConstant.ORDER_ACCEPTED);
        orderMapper.confirm(ordersConfirmDTO);
    }

    @Override
    public void delivery(Long id) {
        orderMapper.delivery(id);
    }

    @Override
    public void reminder(Long orderId) {

        Map map = new HashMap();
        map.put("type", 2); // 1:来单提醒， 2：客户催单
        map.put("orderId", orderId);
        map.put("content", "订单号： "+ orderId.toString());
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);
    }
}
