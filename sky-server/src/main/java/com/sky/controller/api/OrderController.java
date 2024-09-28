package com.sky.controller.api;

import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName OrderController
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/28 14:07
 * @Version 1.0
 **/
@RequestMapping("/api/order")
@RestController("apiOrderController")
@Slf4j
@Api(tags = "订单相关")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/details/{id}")
    public Result<OrderDetailVO> queryOrderById(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderService.orderDetail(id);
        return Result.success(orderDetailVO);
    }
}
