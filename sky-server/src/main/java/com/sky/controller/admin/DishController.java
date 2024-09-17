package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @ClassName DishController
 * @Description 菜品管理
 * @Author msjoy
 * @Date 2024/9/13 13:24
 * @Version 1.0
 **/
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) { //ResquestBody 封装json格式的数据
        log.info("新增菜品：{}",dishDTO);
        dishService.insertWithFlavor(dishDTO);

        //清理改动的缓存
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }

    /**
     * 分页查询菜单
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜单")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 请求方式；query,地址栏?id=1,2,3...
     * 删除菜品
     * 1 单个菜品删除/批量删除
     * 2 起售的菜品不能删除
     * 被套餐关联的菜品不能删除
     * 菜品删除时，对应的口味也要删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}",ids);
        dishService.deleteDish(ids);
        //清理所有缓存
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品，包括分类和口味")
    public Result<DishVO> queryDishVO(@PathVariable Long id) {
        log.info("根据id查询dish，包括分类和口味List: #{}", id);
        DishVO dishVO = dishService.queryDishVO(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}",dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        //清理所有缓存
        cleanCache("dish_*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("起售/停售")
    public Result StartOrStop(@PathVariable Integer status, Long id){
        log.info("起售/停售 id={},status={}",id,status);
        dishService.startOrStop(status, id);
        //清理所有缓存
        cleanCache("dish_*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List> queryDishByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品，分类id是：{}", categoryId);
        List<Dish> dishes = dishService.queryDishByCategoryId(categoryId);
        return Result.success(dishes);
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
