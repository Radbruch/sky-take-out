package com.sky.controller.user;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderController
 * @Description
 * @Author msjoy
 * @Date 2024/9/19 16:27
 * @Version 1.0
 **/
@RestController
@RequestMapping("/user/order")
@Api(tags = "订单相关")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> makeOrder(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.makeOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    @GetMapping("orderDetail/{id}")
    @ApiOperation("历史订单查询")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderService.orderDetail(id);
        return Result.success(orderDetailVO);
    }

    @ApiOperation("分页查询所有历史订单")
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pages = orderService.page(ordersPageQueryDTO);
        return Result.success(pages);
    }

    @ApiOperation("取消订单")
    @PutMapping("cancel/{id}")
    public Result cancelOrder(@PathVariable Long id){
        //status = 6
        //cancel_time
        orderService.cancel(id,"");
        return Result.success();
    }

    @ApiOperation("再来一单")
    @PostMapping("repetition/{id}")
    public Result repetition(@PathVariable Long id){
        orderService.repetition(id);
        return Result.success();
    }

}
