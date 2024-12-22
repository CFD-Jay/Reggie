package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.BaseContext;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.OrderDetail;
import com.ithema.reggie.entity.Orders;
import com.ithema.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }
    @GetMapping("/userPage")
    public R<Page<Orders>> page(@RequestParam("page")Long page, @RequestParam("pageSize")Long pageSize)
    {
        Page<Orders>page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        orderService.page(page1,queryWrapper);
        //List<Orders> records = page1.getRecords();
        return R.success(page1);
    }

    @GetMapping("/page")
    public R<Page> OrderList(@RequestParam("page")Long page, @RequestParam("pageSize")Long pageSize)
    {
        Page<Orders>page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders>queryWrapper=new LambdaQueryWrapper<>();
        Page<Orders> page2 = orderService.page(page1, queryWrapper);
        return R.success(page2);
    }


}