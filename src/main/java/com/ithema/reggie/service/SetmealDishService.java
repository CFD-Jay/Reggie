package com.ithema.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithema.reggie.dto.SetmealDto;
import com.ithema.reggie.entity.SetmealDish;

public interface SetmealDishService extends IService<SetmealDish> {

     SetmealDto select(Long setmeal_id);
     void update(SetmealDto setmealDto);
}
