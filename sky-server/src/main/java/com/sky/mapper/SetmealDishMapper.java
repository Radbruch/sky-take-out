package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据dish id[] 查询套餐id[]
     * @param ids
     * @return
     */
    List<Long> getSetmealIdsByDishId(List<Long> ids);

    /**
     * 新增套餐内菜品
     * @param setmealDishes
     */
    void insertSetmealDish(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐内菜品
     * @param setmealId
     * @return
     */
    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<SetmealDish> queryDishBySetmealId(Long setmealId);

    /**
     * 根据套餐id删除套餐内菜品
     * @param setmealId
     */
    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteDishBySetmealId(Long setmealId);

    /**
     * 批量根据套餐id删除套餐内菜品
     * @param ids
     */
    void deleteDishBatchBySetmealId(List<Long> ids);
}
