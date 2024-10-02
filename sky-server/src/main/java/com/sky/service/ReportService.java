package com.sky.service;

import com.sky.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(LocalDate startDate, LocalDate endDate);

    UserReportVO countUserByDateAndStatus(LocalDate begin, LocalDate end, Integer completed);

    OrderReportVO countOrderByDateAndStatus(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    OrderOverViewVO statistics();

    BusinessDataVO dataPerday(LocalDate date);

    BusinessDataVO dataPeriod(LocalDate startDate, LocalDate endDate);

    void export(HttpServletResponse response);
}
