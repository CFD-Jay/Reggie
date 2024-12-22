package com.ithema.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，因为要同时插入菜品和口味，即同时操作两张表:dish、dish_flavor
    void savaWithFlavor(DishDto dishDto);
    void updateWithFlavor(DishDto dishDto);
    DishDto select(Long id);
}
