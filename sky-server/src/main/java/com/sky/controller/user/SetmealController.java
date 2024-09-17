package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName SetmealController
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/16 23:22
 * @Version 1.0
 **/
@RequestMapping("/user/setmeal")
@RestController("userSetmealController")
@Slf4j
@Api("套餐相关")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId") //执行查询前先看看缓存里有没有数据
    public Result<List<Setmeal>> querySetmealByCategoryId(Long categoryId) {
        List<Setmeal> setmeals = setmealService.querySetmealsByCategoryId(categoryId);
        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> queryDishItemVOBySetmealId(@PathVariable Long id) {
        List<DishItemVO> dishs = setmealService.queryDishItemVOBySetmealId(id);
        return Result.success(dishs);
    }
}
