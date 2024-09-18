package com.sky.service.impl;

import com.alibaba.fastjson.serializer.BeanContext;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ShoppingCartServiceImpl
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/18 22:23
 * @Version 1.0
 **/
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    @Transactional
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 判断是否有相同的菜品或者套餐
        ShoppingCart shoppingCartResult = shoppingCartMapper.queryByShoppingCart(shoppingCart);
        if (shoppingCartResult == null) {
            //没有相同的，判断是菜品还是套餐
            if (shoppingCart.getDishId() == null) {
                //是套餐，新增套餐
                SetmealVO setmealVO = setmealMapper.querySetmealById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            else {
                //是菜品，新增菜品
                DishVO dishVO = dishMapper.queryDishVOById(shoppingCart.getDishId());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setImage(dishVO.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.insertShoppingCart(shoppingCart);
        } else {
            shoppingCartResult.setNumber(shoppingCartResult.getNumber()+1);
            shoppingCartMapper.update(shoppingCartResult);
        }
    }

    @Override
    public List<ShoppingCart> queryAllShoppingCartByUser() {
        List<ShoppingCart> list = shoppingCartMapper.queryAllByUser(BaseContext.getCurrentId());
        return list;
    }

    @Override
    public void deleteOneItem(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        if (shoppingCart.getDishId() != null) {
            shoppingCartMapper.deleteByDishId(shoppingCart);
        }
        shoppingCartMapper.deleteBySetmealId(shoppingCart);
    }

    @Override
    public void deleteAll() {
        shoppingCartMapper.deleteAll();
    }
}
