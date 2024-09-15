package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DishServiceImpl
 * @Description
 * @Author msjoy
 * @Date 2024/9/13 13:32
 * @Version 1.0
 **/
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Transactional
    public void insertWithFlavor(DishDTO dishDTO) {
        // dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insertDish(dish);

        //用主键回显获取了插入dish的id
        Long dishId = dish.getId();

        // dish flavor
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        // 把主键回显的 dish id 赋值给每个flavor
        if(dishFlavorList != null && dishFlavorList.size() > 0) {
            dishFlavorList.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishMapper.insertFlavor(dishFlavorList);
        }
    }

    /**
     * 分页查询菜单
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        // 1. 判断当前菜品是否能删除 -- 是否起售中
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 2. 判断当前菜品是否能删除 -- 是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishId(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 3. 删除菜品数据
        // 4. 删除菜品关联的口味数据
            dishMapper.deleteDish(ids);
            dishMapper.deleteFlavor(ids);
    }

    /**
     * 根据id查询dish，包括分类和口味List
     * @param id
     * @return
     */
    @Override
    public DishVO queryDishVO(Long id) {
        DishVO dishVO = dishMapper.queryDishVOById(id);
        List<DishFlavor> flavors = dishMapper.queryFlavorByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     *修改菜品信息
     * @param dishDTO
     */
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);

        // 删除原本的flavors
        List<Long> flavor = new ArrayList<>();
        flavor.add(dishDTO.getId());
        dishMapper.deleteFlavor(flavor);
        //新增更新的flavors
        // 把主键回显的 dish id 赋值给每个flavor
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishMapper.insertFlavor(dishFlavors);
        }
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.updateDish(dish);
    }

    @Override
    public List<Dish> queryDishByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.queryDishByCategoryId(categoryId);
        return dishes;
    }
}
