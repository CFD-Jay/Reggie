package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.dto.SetmealDto;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.entity.SetmealDish;
import com.ithema.reggie.mapper.SetmealDishMapper;
import com.ithema.reggie.service.SetmealDishService;
import com.ithema.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public SetmealDto select(Long setmeal_id) {

        Setmeal setmeal = setmealService.getById(setmeal_id);
        SetmealDto setmealDto=new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        List<SetmealDish> list = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId,setmeal_id));
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * 修改套餐。注意这个逻辑必须是要先删除原来的套餐信息，再插入信息的信息，否则会变得很难处理。
     * @param setmealDto
     */
    @Override
    public void update(SetmealDto setmealDto) {


        Long setmeal_id=setmealDto.getId();

        //先更新套餐的基本信息
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        setmealService.updateById(setmeal);

        //更新套餐的菜品信息
        List<SetmealDish> setmealDishes=setmealDto.getSetmealDishes();
        //先移除原来的信息
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId,setmeal_id));

        //插入新信息。
            for(SetmealDish item:setmealDishes){
                item.setSetmealId(setmeal_id);
            }
            setmealDishService.saveBatch(setmealDishes);

    }
}
