package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annocation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insertDish(Dish dish);

    /**
     * 新增口味集合
     * @param dishFlavorList
     */
    void insertFlavor(List<DishFlavor> dishFlavorList);


    /**
     * 分页查询菜谱
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 根据id查询菜单Dish
     * @param id
     * @return
     */
    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish getById(Long id);

    /**
     * 根据菜品ids批量删除菜品
     * @param ids
     */
    void deleteDish(List<Long> ids);

    /**
     * 根据菜品dish_ids批量删除对应的口味
     * @param dishIds
     */
    void deleteFlavor(List<Long> dishIds);

    /**
     * 根据id查询dish，包括分类和口味List
     * @param id
     * @return
     */
    DishVO queryDishVOById(Long id);

    /**
     * 根据dishid查找flavor[]
     * @param id
     * @return
     */
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{id}")
    List<DishFlavor> queryFlavorByDishId(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

}
