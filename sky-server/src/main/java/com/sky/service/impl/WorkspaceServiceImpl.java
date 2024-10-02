package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @ClassName WorkspaceServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/29 00:46
 * @Version 1.0
 **/
@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private WorkspaceMapper workspaceMapper;
    @Autowired
    private ReportMapper reportMapper;

    @Override
    public BusinessDataVO dataToday() {
        LocalDate now = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now, LocalTime.MAX);

        BusinessDataVO businessDataVO = new BusinessDataVO();
        Double turnoverToday = workspaceMapper.amountOneDay(start, end, Orders.COMPLETED); //营业额
        turnoverToday = turnoverToday == null ? 0 : turnoverToday;
        businessDataVO.setTurnover(turnoverToday);

        Integer validOrderCount = reportMapper.countOrderByDateAndStatus(start, end, Orders.COMPLETED); //有效订单数
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;
        businessDataVO.setValidOrderCount(validOrderCount);

        if (validOrderCount == 0) {
            businessDataVO.setUnitPrice(0.0);
        } else {
            businessDataVO.setUnitPrice(turnoverToday * 1.0 / validOrderCount);//平均客单价
        }

        Long numberOfNewUserToday = reportMapper.countUserByDateAndStatus(start, end, Orders.COMPLETED);
        numberOfNewUserToday = numberOfNewUserToday == null ? 0 : numberOfNewUserToday;
        businessDataVO.setNewUsers(numberOfNewUserToday.intValue()); //新增用户数

        Integer allOrder = reportMapper.countOrderByDateAndStatus(start, end, Orders.PAID)+reportMapper.countOrderByDateAndStatus(start, end, Orders.COMPLETED)+reportMapper.countOrderByDateAndStatus(start, end, Orders.DELIVERY_IN_PROGRESS);
        allOrder = allOrder == null ? 0 : allOrder;
        if (allOrder == 0) {
            businessDataVO.setOrderCompletionRate(0.0);
        } else {
            businessDataVO.setOrderCompletionRate(businessDataVO.getValidOrderCount() * 1.0 / allOrder); //订单完成率
        }
        return businessDataVO;
    }

    @Override
    public SetmealOverViewVO overviewSetmeals() {
        Integer NumberOfSellingSetmeal = workspaceMapper.countSetmealByStatus(1);
        Integer NumberOfUnSellingSetmeal = workspaceMapper.countSetmealByStatus(0);
        SetmealOverViewVO setmealOverViewVO = SetmealOverViewVO.builder()
                .sold(NumberOfSellingSetmeal)
                .discontinued(NumberOfUnSellingSetmeal)
                .build();
        return setmealOverViewVO;
    }

    @Override
    public DishOverViewVO dishStatistics() {
        Integer sold = workspaceMapper.countDishByStatus(1);
        Integer discontinued = workspaceMapper.countDishByStatus(0);
        DishOverViewVO dishOverViewVO = DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
        return dishOverViewVO;
    }
}
