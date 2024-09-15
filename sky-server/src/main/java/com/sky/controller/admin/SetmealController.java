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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api("套餐相关接口") //接口测试的注解
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
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
}
