package com.ithema.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.dto.DishDto;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.DishFlavor;
import com.ithema.reggie.entity.SetmealDish;
import com.ithema.reggie.service.CategoryService;
import com.ithema.reggie.service.DishFlavorService;
import com.ithema.reggie.service.DishService;
import com.ithema.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.savaWithFlavor(dishDto);
        return R.success("新增菜品成功！");
    }

    /**
     * 根据前段传来的DishDto修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功！");
    }

    /**
     * 菜品分页查询
     * @param page
     * @param pagesize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(@RequestParam("page")Long page,@RequestParam("pageSize")Long pagesize,@RequestParam(value = "name",required = false)String name){

        Page<Dish>page1=new Page<Dish>(page,pagesize);
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name).orderByDesc(Dish::getUpdateTime);

        //经过page查询后，此时的page1已经有值了。
        dishService.page(page1,queryWrapper);
        //因为Dish实体类只有Category_id属性，没有Category_name属性，所以如果只把page1传给前端，那么只传给了前端Category_id，无法正常显示菜品的种类，我们现在要传给前端DishDto,里面有Category_name。


        Page<DishDto>page2=new Page<>(page,pagesize);
        //将page1中的除了records数据外，其他数据复制给page2
        BeanUtils.copyProperties(page1,page2,"records");
        //需要改造一下records<Dish>，其实是新建一个records<DishDto>是要给Category_name赋值。
        List<Dish> records = page1.getRecords();
        List<DishDto>list1=new ArrayList<>();
        for(Dish item:records){
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getCategoryId();
            String Category_name=categoryService.getById(id).getName();
            dishDto.setCategoryName(Category_name);
            list1.add(dishDto);
        }
        page2.setRecords(list1);
        return  R.success(page2);
    }

    /**
     * 用作点击修改菜品是对菜品数据的数据回显，前端传过来的是dish_id.需要封装成一个DishDto类回显给前端。
     * @param Dish_id
     * @return
     */
    @GetMapping(value = "/{Dish_id}")
    private R<DishDto> dish_message(@PathVariable Long Dish_id){
        log.info("当前修改菜品的菜品id为{}",Dish_id);
        DishDto dishDto=dishService.select(Dish_id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品状态0->1 注意传来的是Sring，里面包括了所有需要更改的菜品id，批量修改。其实和下面的可以合并成    @PostMapping("/status/{status}")
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
            LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId,item);
            Dish one = dishService.getOne(queryWrapper);
//            if(one.getStatus()==1)throw new RuntimeException("操作失败，您勾选的菜品里有菜品已经是起售状态！");
            one.setStatus(1);
            dishService.updateById(one);
        }
        return R.success("");
    }
    /**
     * 修改菜品状态1->0
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
            LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId,item);
            Dish one = dishService.getOne(queryWrapper);
//            if(one.getStatus()==0)throw new RuntimeException("操作失败，您勾选的菜品里有菜品已经是停售状态！");
            one.setStatus(0);
            dishService.updateById(one);
        }
        return R.success("");
    }

    /**
     * 菜品（批量）删除 前端传来的String类型的字符串。注意如果该菜品绑定了套餐，无法直接删除。
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") String ids){
        System.out.println(ids);
        String[] split = ids.split(",");
        ArrayList<Long>list=new ArrayList<>();
        for(String item:split){
            Long id=Long.valueOf(item);
            //如果有菜品已经绑定了套餐
            if(setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getDishId,id)).size()!=0)throw new RuntimeException("您选择删除的菜品有些已经绑定某些套餐，无法直接删除");
            dishService.removeByIds(list);
        }
        dishService.removeByIds(list);
       // list.forEach(System.out::println);
        return R.success("批量删除成功");
    }

    /**
     * 新增套餐时，需要根据菜品的种类显示出所有的菜品。
     * //11.26 更改的实参为Dish dish
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        //11.26必须筛选起售的商品
        queryWrapper.eq(Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto>res=new ArrayList<>();
        for(Dish item:list){
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long id = item.getId();
            List<DishFlavor> list1=dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,id));
            dishDto.setFlavors(list1);
            res.add(dishDto);
        }
        return R.success(res);
    }
}
