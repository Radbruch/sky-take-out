package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDateTime;
import java.util.List;

import com.sky.dto.GoodsSalesDTO;

@Mapper
public interface ReportMapper {

    Double getAmountByDateandStatus(LocalDateTime begin, LocalDateTime end, Integer status);

    Long countUserByDateAndStatus(LocalDateTime begin, LocalDateTime end, Integer status);

    Integer countOrderByDateAndStatus(LocalDateTime begin, LocalDateTime end, Integer status);

    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end, Integer status);
}
