package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.mapper.CategoryMapper;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishService;
import com.ithema.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     *删除分类需要先检测该分类是否用已经有菜品了。
     * @param id
     * @return
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> DishQuery=new LambdaQueryWrapper<Dish>();
        DishQuery.eq(Dish::getCategoryId,id);
        int DishCount = dishService.count(DishQuery);

        LambdaQueryWrapper<Setmeal> SetmealQuery=new LambdaQueryWrapper<>();
        SetmealQuery.eq(Setmeal::getCategoryId,id);
        int SetmealCount=setmealService.count(SetmealQuery);

        log.info("当前分类共关联了{}个菜品{}个套餐",DishCount,SetmealCount);
        //如果当前分类被关联
        if(DishCount>0){
            throw new RuntimeException("当欠分类关联了菜品，无法删除！");
        }
        else if(SetmealCount>0)
        {
            throw new RuntimeException("当欠分类关联了套餐，无法删除！");
        }
        else
        {
            categoryService.removeById(id);
        }

    }
}
