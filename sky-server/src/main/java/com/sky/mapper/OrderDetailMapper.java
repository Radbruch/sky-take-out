package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper {

    void insertOrderDetails(ShoppingCart shoppingCart, Long id);
}
