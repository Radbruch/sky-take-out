package com.sky.mapper;

import com.sky.vo.BusinessDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface WorkspaceMapper {

    /**
     * 每天营业额
     * @param start
     * @param end
     * @param status
     * @return
     */
    Double amountOneDay(LocalDateTime start, LocalDateTime end, Integer status);

    @Select("SELECT COUNT(*) FROM setmeal WHERE status = #{status}")
    Integer countSetmealByStatus(Integer status);

    @Select("SELECT COUNT(*) FROM dish WHERE status = #{status}")
    Integer countDishByStatus(Integer status);
}
