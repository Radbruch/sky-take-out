package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.WorkspaceMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private WorkspaceMapper workspaceMapper;

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

    @Override
    public OrderOverViewVO statistics() {
        Integer toBeConfirmed = reportMapper.countOrderByDateAndStatus(null, null, Orders.TO_BE_CONFIRMED);
        Integer delivering = reportMapper.countOrderByDateAndStatus(null, null, Orders.DELIVERY_IN_PROGRESS);
        Integer completed = reportMapper.countOrderByDateAndStatus(null, null, Orders.COMPLETED);
        Integer cancelled = reportMapper.countOrderByDateAndStatus(null, null, Orders.CANCELLED);
        Integer allOrders = toBeConfirmed+delivering+completed+cancelled;
        OrderOverViewVO orderOverViewVO = OrderOverViewVO.builder()
                .waitingOrders(toBeConfirmed)
                .deliveredOrders(delivering)
                .completedOrders(completed)
                .cancelledOrders(cancelled)
                .allOrders(allOrders)
                .build();
        return orderOverViewVO;
    }

    @Override
    public BusinessDataVO dataPerday(LocalDate date) {
        LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

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
    public BusinessDataVO dataPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

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


    public void export(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate EndDate = LocalDate.now().minusDays(1);
        LocalDate startDate = EndDate.minusDays(29);
        BusinessDataVO businessDataPerMonth = dataPeriod(startDate, EndDate); //30天数据
        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/report.xlsx");
        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + startDate + "至" + EndDate);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataPerMonth.getTurnover());
            row.getCell(4).setCellValue(businessDataPerMonth.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataPerMonth.getNewUsers());
            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataPerMonth.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataPerMonth.getUnitPrice());
            //填充明细数据
            LocalDate date = startDate;
            int rowNum = 7;
            while (!date.isEqual(EndDate.plusDays(1))){
                //查询某一天的营业数据
                BusinessDataVO businessDataVO = dataPerday(date);
                //获得某一行
                row = sheet.getRow(rowNum);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());

                date = date.plusDays(1);
                rowNum+=1;
            }
            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
