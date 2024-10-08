package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐+套餐对应菜品
     * @param setmealDTO
     */
    void insertSetmeal(SetmealDTO setmealDTO);

    /**
     * 根据id查询套餐和套餐内菜品
     * @param id
     * @return
     */
    SetmealVO querySetmealWithDishById(Long id);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐起售/停售
     * @param status
     * @param id
     */
    void StartOrStop(Integer status, Long id);

    /**
     * 更新套餐和套餐内菜品
     * @param setmealDTO
     */
    void updateSetmealWithDish(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐和套餐内菜品
     *
     * @param ids
     */
    void deleteSetmealBatch(List<Long> ids);

    /**
     * 根据分类id查询套餐们
     * @param categoryId
     * @return
     */
    List<Setmeal> querySetmealsByCategoryId(Long categoryId);

    List<DishItemVO> queryDishItemVOBySetmealId(Long setmealId);
}
