package com.sky.mapper;

import com.sky.entity.OrderDetail;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    void insertOrderDetails(ShoppingCart shoppingCart, Long id);

    @Select("SELECT * FROM order_detail WHERE order_detail.order_id = #{id}")
    List<OrderDetail> getDetailByOrderId(Long id);
}
