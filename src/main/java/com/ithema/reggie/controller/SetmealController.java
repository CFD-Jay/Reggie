package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.dto.SetmealDto;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.Setmeal;
import com.ithema.reggie.entity.SetmealDish;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishService;
import com.ithema.reggie.service.SetmealDishService;
import com.ithema.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐 一般该功能是写在Service层的，这里我不想改了。
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        List<SetmealDish>setmealDishes=setmealDto.getSetmealDishes();
        //先检查套餐中的菜品是否有停售的
        for(SetmealDish item:setmealDishes)
        {
            Long dishid=item.getDishId();
            LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<Dish>();
            queryWrapper.select(Dish::getStatus).eq(Dish::getId,dishid);
            if(dishService.getOne(queryWrapper).getStatus()==0) throw new RuntimeException("有些菜品已经停售，无法添加至套餐");
        }

        //保存于Setmeal表，保存套餐。
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        setmealService.save(setmeal);


        //只有执行setmealService.save(setmeal);后，setmeal的id才有值，此时再赋给SetmealDish
        for(SetmealDish item:setmealDishes)
        {
            item.setSetmealId(setmeal.getId());
        }

        //存入Setmealdish表，保存套餐于菜品的关系。
        setmealDishService.saveBatch(setmealDishes);
        return R.success("新增套餐成功！");
    }

    /**
     * 套餐信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam("page")Long page,@RequestParam("pageSize")Long pageSize,@RequestParam(value = "name",required = false)String name){
        //先查询套餐的信息。
        Page<Setmeal>page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name).orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(page1,queryWrapper);


        //因为使用page1封装的是Setmeal信息，没有Categoryname,所以需要新建一个page2封装SetmealDto
        Page<SetmealDto>page2=new Page<>();
        //将除了records信息复制给page2,records信息需要单独设置，也就是补全page1的records里没有Categoryname
        BeanUtils.copyProperties(page1,page2,"records");

        List<Setmeal> page1_records = page1.getRecords();
        List<SetmealDto> list=new ArrayList<>();
        for(Setmeal item:page1_records)
        {
            Long id=item.getCategoryId();
            String category_name = categoryService.getById(id).getName();
            SetmealDto setmealDto=new SetmealDto();
            //将基本信息赋值给新records列表
            BeanUtils.copyProperties(item,setmealDto);
            //将查询到了套菜种类名字信息赋值给新records列表
            setmealDto.setCategoryName(category_name);
            list.add(setmealDto);
        }
        //将新records封装给page2
        page2.setRecords(list);

        return R.success(page2);
    }

    /**
     * 套餐（批量）删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids")String ids)
    {
        String[] split = ids.split(",");
        List<Long>list=new ArrayList<>();
        for (String item:split)
        {
            list.add(Long.valueOf(item));
            //如果套餐是在售状态，那么不能删除。
            if(setmealService.getOne(new LambdaQueryWrapper<Setmeal>().select(Setmeal::getStatus).eq(Setmeal::getId,Long.valueOf(item))).getStatus()==1)
                throw new RuntimeException("您选中的套餐中有正在起售的，无法删除！");
        }
        setmealService.removeByIds(list);
        setmealDishService.removeByIds(list);
        return R.success("套餐删除成功！");

    }
    /**
     * 修改套餐状态0->1
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> changeStatus_0to1(@RequestParam("ids")String ids){
        String[] split = ids.split(",");
        ArrayList<Long>list=new ArrayList<>();
        for(String item:split){
            list.add(Long.valueOf(item));
        }
        for(Long item:list){
            LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId,item);
            Setmeal one = setmealService.getOne(queryWrapper);
//            if(one.getStatus()==1)throw new RuntimeException("操作失败，您勾选的套餐里有套餐已经是起售状态！");
            one.setStatus(1);
            setmealService.updateById(one);
        }
        return R.success("");
    }
    /**
     * 修改套餐状态1->0
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> changeStatus_1to0(@RequestParam("ids")String ids){
        String[] split = ids.split(",");
        ArrayList<Long>list=new ArrayList<>();
        for(String item:split){
            list.add(Long.valueOf(item));
        }
        for(Long item:list){
            LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId,item);
            Setmeal one = setmealService.getOne(queryWrapper);
//            if(one.getStatus()==0)throw new RuntimeException("操作失败，您勾选的套餐里有套餐已经是停售状态！");
            one.setStatus(0);
            setmealService.updateById(one);
        }
        return R.success("");
    }


    /**
     * 修改套餐时，用于套餐信息回显
     * @param Setmeal_id
     * @return
     */
    @GetMapping(value = "/{Setmeal_id}")
    private R<SetmealDto> setmeal_message(@PathVariable Long Setmeal_id){
        log.info("当前修改菜品的套餐id为{}",Setmeal_id);
        SetmealDto setmealDto=setmealDishService.select(Setmeal_id);
        return R.success(setmealDto);
    }


    /**
     * 修改套餐时，需要根据菜品的种类显示出所有的菜品。
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestParam("categoryId")Long categoryId){
        LambdaQueryWrapper<Setmeal>queryWrapper=new LambdaQueryWrapper<>();
        //11.26 新增了status检索，只显示已经启用的套餐。
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId).eq(Setmeal::getStatus,1);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealDishService.update(setmealDto);
        return R.success("套餐信息修改成功！");
    }

}
