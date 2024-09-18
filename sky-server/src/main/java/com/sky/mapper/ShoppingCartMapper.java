package com.sky.mapper;

import com.sky.annocation.AutoFill;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    ShoppingCart queryByShoppingCart(ShoppingCart shoppingCart);

    @Insert("INSERT INTO shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            "VALUES " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insertShoppingCart(ShoppingCart shoppingCart);

    void update(ShoppingCart shoppingCart);

    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId}")
    List<ShoppingCart> queryAllByUser(Long userId);

    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId} AND dish_id = #{dishId}")
    void deleteByDishId(ShoppingCart shoppingCart);

    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId} AND setmeal_id = #{setmealId}")
    void deleteBySetmealId(ShoppingCart shoppingCart);
    @Delete("DELETE FROM shopping_cart")
    void deleteAll();
}
