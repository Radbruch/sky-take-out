package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ReportServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/28 15:40
 * @Version 1.0
 **/
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    /**
     * 营业额统计
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = startDate;
        while (!date.isEqual(endDate)){
            dateList.add(date);
            date = date.plusDays(1);
        }
        dateList.add(endDate);
        List<Double> amountList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime begin = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(localDate, LocalTime.MAX);
            Double amount = reportMapper.getAmountByDateandStatus(begin, end, Orders.COMPLETED);
            amountList.add(amount == null ? 0 : amount);
        }


        TurnoverReportVO turnoverReportVO = TurnoverReportVO
                .builder()
                .turnoverList(StringUtils.join(amountList, ","))//把amountList转换成字符串，以逗号分隔
                .dateList(StringUtils.join(dateList, ","))//把dateList转换成字符串，以逗号分隔
                .build();

        return turnoverReportVO;
    }

    @Override
    public UserReportVO countUserByDateAndStatus(LocalDate begin, LocalDate end, Integer completed) {

        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        while (!date.isEqual(end)){
            dateList.add(date);
            date = date.plusDays(1);
        }
        List<Long> totalUserList = new ArrayList<>();
        List<Long> newUserList = new ArrayList<>();

        for (LocalDate localDate: dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Long totalUser = reportMapper.countUserByDateAndStatus(null, endTime, Orders.COMPLETED);
            Long newUser = reportMapper.countUserByDateAndStatus(beginTime, endTime, Orders.COMPLETED);

            totalUserList.add(totalUser == null ? 0 : totalUser);
            newUserList.add(newUser == null ? 0 : newUser);
        }
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();

        return userReportVO;
    }

    @Override
    public OrderReportVO countOrderByDateAndStatus(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate date = begin;
        while (!date.isEqual(end)){
            dateList.add(date);
            date = date.plusDays(1);
        }
        List<Integer> orderCountList = new ArrayList<>(); //每日订单list
        List<Integer> validOrderCountList = new ArrayList<>(); //每日有效订单list
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        Integer orderCountPerDay = 0;
        Integer validOrderCountPerDay = 0;

        for (LocalDate localDate: dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);

            orderCountPerDay = reportMapper.countOrderByDateAndStatus(beginTime, endTime, null);
            validOrderCountPerDay = reportMapper.countOrderByDateAndStatus(beginTime, endTime, Orders.COMPLETED);
            orderCountPerDay = orderCountPerDay == null ? 0 : orderCountPerDay;
            validOrderCountPerDay = validOrderCountPerDay == null ? 0 : validOrderCountPerDay;
            orderCountList.add(orderCountPerDay);
            validOrderCountList.add(validOrderCountPerDay);
            totalOrderCount += orderCountPerDay;
            validOrderCount += validOrderCountPerDay;
        }


        Double orderCompletionRate;
        if (totalOrderCount == 0){
            orderCompletionRate = 0.0;
        }else{
            orderCompletionRate = validOrderCount * 1.0 / totalOrderCount;
        }
        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> salesTop10 = new ArrayList<>();
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        salesTop10 = reportMapper.getSalesTop10(beginTime, endTime, 5);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for (GoodsSalesDTO salesTop: salesTop10){
            nameList.add(salesTop.getName());
            numberList.add(salesTop.getNumber());
        }
        String nameListStr = StringUtils.join(nameList, ",");
        String numberListStr = StringUtils.join(numberList, ",");
        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
        return salesTop10ReportVO;
    }
}
