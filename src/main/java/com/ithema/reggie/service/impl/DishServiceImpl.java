package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.DishFlavor;
import com.ithema.reggie.mapper.DishMapper;
import com.ithema.reggie.service.DishFlavorService;
import com.ithema.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishService dishService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Override
    //开启事务
    @Transactional
    public void savaWithFlavor(DishDto dishDto) {
        //log.info("第一次："+dishDto.toString());
        dishService.save(dishDto);
        //log.info("第二次："+dishDto.toString());
        //接下来保存dish_flavor

        //不能直接使用 dishFlavorService.saveBatch(dishDto.getFlavors());因为获取出的list里面的dishid没有值，需要先赋值。因为已经经过dishService.save(dishDto);此时dishDto的菜品id也就是id已经生成了一个随机值了。
        Long id = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor item:flavors){
            item.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);


    }

    /**
     * 根据前段传来的DishDto修改菜品信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        Long id = dishDto.getId();
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        dishService.updateById(dish);

        List<DishFlavor> flavors = dishDto.getFlavors();
        //11.26更新 若将菜品的口味全删除，此时传过来的dishDto里面的flavors是空的，也就没有id.所以此时执行 dishFlavorService.updateBatchById(flavors);什么都不会发生。
        if(flavors.isEmpty())
        {
                dishFlavorService.update(new LambdaUpdateWrapper<DishFlavor>().eq(DishFlavor::getDishId,id).set(DishFlavor::getValue,null));
        }
        else
        dishFlavorService.updateBatchById(flavors);
    }

    /**
     * 用作点击修改菜品是对菜品数据的数据回显，前端传过来的是dish_id.需要封装成一个DishDto类回显给前端。
     * @param id
     * @return
     */
    @Override
    public DishDto select(Long id) {
        List<DishFlavor> list = dishFlavorService.select(id);
        DishDto dishDto=new DishDto();
        dishDto.setFlavors(list);
        Dish dish = dishService.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        return dishDto;
    }
}
