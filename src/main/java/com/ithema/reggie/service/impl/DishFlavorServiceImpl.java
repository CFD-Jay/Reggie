package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.entity.DishFlavor;
import com.ithema.reggie.mapper.DishFlavorMapper;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishFlavorService;
import com.ithema.reggie.service.DishService;
import com.ithema.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Override
    public List<DishFlavor> select(Long dish_id) {
        LambdaQueryWrapper<DishFlavor>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish_id).orderByDesc(DishFlavor::getUpdateTime);
        List<DishFlavor> list =dishFlavorService.list(queryWrapper);
//        for (DishFlavor item:list){
//            System.out.println(item.toString());
//        }

        return list;
    }
}
