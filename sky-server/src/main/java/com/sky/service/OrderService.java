package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO makeOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 根据订单id查询订单所有信息
     * @param id
     * @return
     */
    OrderDetailVO orderDetail(Long id);

    /**
     * 分页查询所有订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 取消订单
     * @param id
     */
    void cancel(Long id, String cancelReason);

    /**
     * 再来一单
     * @param id
     */
    void repetition(Long id);

    /**
     * 订单搜索
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 完成订单
     * @param id
     */
    void complete(Long id);

    /**
     * 拒单
     * @param ordersRejectionDTO
     */
    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 接单
     * @param ordersConfirmDTO
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 派送
     * @param id
     */
    void delivery(Long id);
}
