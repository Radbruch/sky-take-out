package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ShopController
 * @Description
 * @Author msjoy
 * @Date 2024/9/16 00:58
 * @Version 1.0
 **/
@RestController("userShopController") //为了和admin里的同名类区分开，否则会报冲突
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "user端店铺相关")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("user查询商店营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺的营业状态为: {}", status == 1? "营业中":"打烊");
        return Result.success(status);
    }
}