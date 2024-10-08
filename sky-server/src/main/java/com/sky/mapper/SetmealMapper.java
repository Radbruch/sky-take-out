package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annocation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    /**
     * 根据id查找套餐
     * @param id
     * @return
     */
    @Select("SELECT * FROM setmeal WHERE id = #{id}")
    SetmealVO querySetmealById(Long id);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 起售、停售
     * @param setmeal
     */
    @Update("UPDATE setmeal SET status = #{status},update_time = #{updateTime},update_user=#{updateUser} WHERE id = #{id}")
    @AutoFill(value = OperationType.UPDATE)
    void StartOrStop(Setmeal setmeal);

    /**
     * 更新套餐
     * @param setmeal
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteSetmealBatch(List<Long> ids);

    /**
     * user端，通过categoryId查套餐
     * @param categoryId
     * @return
     */
    @Select("SELECT * FROM setmeal WHERE category_id = #{categoryId} AND status = 1")
    List<Setmeal> querySetmealsByCategoryId(Long categoryId);
}
