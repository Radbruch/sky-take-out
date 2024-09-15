package com.sky.service.impl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;

import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName SetmealServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/15 01:47
 * @Version 1.0
 **/
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐+套餐对应菜品
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void insertSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insertSetmeal(setmeal);

        Long setmealId = setmeal.getId();//主键回显拿到新增的setmeal id
        log.info("回显的sealmeal_id = ", setmealId);
        // 回显的setmeal_id赋值给 setmealDishes里的每个菜
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish ->
            {setmealDish.setSetmealId(setmealId);});
        }
        log.info("新增的setmeal_dish有：{}", setmealDishes);
        setmealDishMapper.insertSetmealDish(setmealDishes);
    }

    @Override
    public SetmealVO querySetmealWithDishById(Long id) {
        //查询套餐
        SetmealVO setmealVO = setmealMapper.querySetmealById(id);
        //查询套餐内菜单
        List<SetmealDish> setmealDishes = setmealDishMapper.queryDishBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<SetmealVO> records = page.getResult();
        return new PageResult(total, records);
    }

    @Override
    public void StartOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.StartOrStop(setmeal);
    }

    @Transactional
    @Override
    public void updateSetmealWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateSetmeal(setmeal); //更新套餐

        Long setmealId = setmeal.getId();//主键回显拿到新增的setmeal id
        log.info("回显的sealmeal_id = ", setmealId);
        // 回显的setmeal_id赋值给 setmealDishes里的每个菜
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            setmealDishes.forEach(setmealDish ->
            {setmealDish.setSetmealId(setmealId);});
        }

        setmealDishMapper.deleteDishBySetmealId(setmealId);
        setmealDishMapper.insertSetmealDish(setmealDishes);
    }

    @Transactional
    @Override
    public void deleteSetmealBatch(List<Long> ids) {
        // 1. 判断套餐是否是起售状态
        for (Long id : ids) {
            SetmealVO setmealVO = setmealMapper.querySetmealById(id);
            if (setmealVO.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        setmealMapper.deleteSetmealBatch(ids);
        setmealDishMapper.deleteDishBatchBySetmealId(ids);

    }
}
