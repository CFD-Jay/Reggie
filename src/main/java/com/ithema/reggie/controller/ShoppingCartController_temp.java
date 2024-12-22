package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ithema.reggie.common.BaseContext;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.ShoppingCart;
import com.ithema.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
//@RestController
//@RequestMapping("/shoppingCart")
public class ShoppingCartController_temp {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("当前购物车数据:{}",shoppingCart);
        //设置用户id
        Long userid= BaseContext.getCurrentId();
        shoppingCart.setUserId(userid);
        String flavors=shoppingCart.getDishFlavor();
        //若此时添加到购物车的是菜品
        if(shoppingCart.getDishId()!=null)
        {
            Long dishid=shoppingCart.getDishId();
            //查询当前菜品或者套餐是否在购物车中
            //不在套餐中
            if(shoppingCartService.getOne(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getDishId,dishid).eq(ShoppingCart::getUserId,userid).eq(ShoppingCart::getDishFlavor,flavors))==null)
            {
                shoppingCartService.save(shoppingCart);
            }
            //在套餐中，检查是否口味相同
            else
            {

                List<ShoppingCart> list = shoppingCartService.list(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getDishId, dishid).eq(ShoppingCart::getUserId,userid));
                boolean flag=false;
                for(ShoppingCart item:list)
                {
                    if(flavors.equals(item.getDishFlavor()))
                    {
                        flag=true;break;
                    }
                }
                //如果没有相同口味，新插入
                if(!flag)
                {
                    shoppingCartService.save(shoppingCart);
                }
                //如果有相同口味，在当前菜品的number数量+1.
                else
                {
                    LambdaQueryWrapper<ShoppingCart>queryWrapper= new LambdaQueryWrapper<>();
                    queryWrapper.eq(ShoppingCart::getDishId, dishid).eq(ShoppingCart::getUserId,userid);
                    Integer number=shoppingCartService.getOne(queryWrapper).getNumber();
                    LambdaUpdateWrapper<ShoppingCart> updateWrapper= new LambdaUpdateWrapper<>();
                    updateWrapper.eq(ShoppingCart::getUserId,userid).eq(ShoppingCart::getDishId,dishid).eq(ShoppingCart::getDishFlavor,flavors);
                    updateWrapper.set(ShoppingCart::getNumber,number+1);
                    shoppingCartService.update(updateWrapper);
                }

            }
            return R.success(shoppingCart);

        }
        //若此时添加到购物车的是套餐
        else if(shoppingCart.getSetmealId()!=null)
        {
            //System.out.println(2);
        }
        //查询当前菜品或者套餐是否在购物车中

        //若没在购物车或者在购物车但是口味不同需要在数据库中新添加数据。




        return null;
    }
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

}
