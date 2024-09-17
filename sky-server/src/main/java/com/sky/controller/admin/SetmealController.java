package com.sky.controller.admin;

/**
 * @ClassName SetmealController
 * @Description TODO
 * @Author msjoy
 * @Date 2024/9/15 00:16
 * @Version 1.0
 **/

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口") //接口测试的注解
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result insertSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增的套餐：{}",setmealDTO);
        setmealService.insertSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> querySetmealWithDishById(@PathVariable Long id){
        log.info("根据id查询套餐，id:{}", id);
        SetmealVO setmealVO = setmealService.querySetmealWithDishById(id);
        return Result.success(setmealVO);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询：{}", setmealPageQueryDTO);
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result StartOrStop(@PathVariable Integer status, Long id){
        log.info("套餐起售、停售:status:{},id:{}", status, id);
        setmealService.StartOrStop(status, id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateSetmealWithDish(@RequestBody SetmealDTO setmealDTO){
        setmealService.updateSetmealWithDish(setmealDTO);
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteSetmealBatch(@RequestParam List<Long> ids){
        log.info("批量删除套餐：{}", ids);
        // 1. 在售的套餐不可删除
        // 2. 删除套餐的同时，包含的菜品一起删除
        setmealService.deleteSetmealBatch(ids);
        return Result.success();
    }
}
