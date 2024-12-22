package com.ithema.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithema.reggie.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService extends IService<DishFlavor> {
    List<DishFlavor> select(Long dish_id);
}
