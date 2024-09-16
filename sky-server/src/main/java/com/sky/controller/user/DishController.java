package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private DishService dishService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品和口味")
    public Result<List<DishVO>> queryDishWithFlavorsByCategoryId(Long categoryId) {
        List<DishVO> dishVOS = dishService.queryDishVOByCategoryId(categoryId);
        return Result.success(dishVOS);
    }
}
