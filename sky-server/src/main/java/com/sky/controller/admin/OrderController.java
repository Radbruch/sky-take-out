package com.sky.controller.admin;

import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderController
 * @Description
 * @Author msjoy
 * @Date 2024/9/20 19:05
 * @Version 1.0
 **/

@RestController("adminOrderController")
@Api(tags = "订单相关")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancel(String cancelReason, Long id) {
        orderService.cancel(id, cancelReason);
        return Result.success();
    }

    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderDetailVO> queryOrderById(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderService.orderDetail(id);
        return Result.success(orderDetailVO);
    }

    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pages = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pages);
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("配送订单")
    public Result delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }
}
