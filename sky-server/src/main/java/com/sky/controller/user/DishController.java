package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName DishController
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/16 23:01
 * @Version 1.0
 **/
@RestController("userDishController")
@RequestMapping("/user/dish")
@Api("菜单相关")
@Slf4j
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品和口味")
    public Result<List<DishVO>> queryDishWithFlavorsByCategoryId(Long categoryId) {

        //构造redis中的key，规则：dish_分类id
        String key = "dish_" + categoryId;
        //查询redis中是否存在菜品数据
        List<DishVO> dishVOS = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果存在，直接返回，无需查询数据库
        if (dishVOS != null && dishVOS.size() > 0) {
            return Result.success(dishVOS);
        } else {
        //如果不存在，查询数据库，将查询结结果放入redis中
        dishVOS = dishService.queryDishVOByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key, dishVOS);
        return Result.success(dishVOS);
        }
    }
}
