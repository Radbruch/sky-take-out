package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName OrderTask
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/27 21:39
 * @Version 1.0
 **/
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的方法
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder(){
        log.info("处理超时订单: {}", LocalDateTime.now());
        List<Orders> OverTimeOrders = orderMapper.getByStatusAndCreateTime(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15));
        if (OverTimeOrders != null && OverTimeOrders.size() > 0){
            for (Orders order : OverTimeOrders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    /**
     * 自动完成一直在派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("处理派送中订单: {}", LocalDateTime.now());
        List<Orders> deliveryOrders = orderMapper.getByStatusAndCreateTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusMinutes(60));
        if (deliveryOrders != null && deliveryOrders.size() > 0){
            for (Orders order : deliveryOrders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
