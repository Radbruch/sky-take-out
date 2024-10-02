package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkspaceService {

    /**
     * 查询今日运营数据
     * @return
     */
    BusinessDataVO dataToday();

    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO overviewSetmeals();

    /**
     * 查询菜品总览
     * @return
     */
    DishOverViewVO dishStatistics();
}
