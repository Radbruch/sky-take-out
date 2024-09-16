package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品/口味名
     * @param dishDTO
     */
    public void insertWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜单
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteDish(List<Long>ids);

    /**
     * 根据id查询dish，包括分类和口味List
     * @param id
     * @return
     */
    DishVO queryDishVO(Long id);

    /**
     * 修改菜品信息
     * @param dishDTO
     */
    void updateDishWithFlavor(DishDTO dishDTO);

    void startOrStop(Integer status, Long id);

    List<Dish> queryDishByCategoryId(Long categoryId);

    List<DishVO> queryDishVOByCategoryId(Long categoryId);
}
