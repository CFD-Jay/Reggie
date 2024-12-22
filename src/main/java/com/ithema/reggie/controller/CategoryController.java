package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.Category;
import com.ithema.reggie.entity.Employee;
import com.ithema.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService service;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("新增分类{}",category);
        category.setIsDeleted(0);
        service.save(category);
        return R.success("新增分类成功！");
    }


    /**
     * 种类分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public R<Page> page(@RequestParam("page")int page, @RequestParam("pageSize")int pageSize, @RequestParam(value = "name",required = false)String name){
        //log.info("page={}+ pageSize={} name={}" ,page,pageSize,name);

        Page<Category>page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Category::getSort);
        service.page(page1,lambdaQueryWrapper);
        //System.out.println(page1.getRecords());
        return R.success(page1);

    }
    /**
     * 修改分类
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info(category.toString());
       // System.out.println(category);
        service.updateById(category);
        return R.success("修改分类成功!");
    }

    /**
     *删除分类需要先检测该分类是否用已经有菜品了。
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long ids){
        log.info("当前需要删除的ids={}",ids);
        service.remove(ids);
        return R.success("分类删除成功！");
    }

    /**
     * 点击新建菜品时，返回给前端菜品分类（在下拉菜单中）
     * 前端传过来的是type=1.注意不能使用@ReqestBody Category category, 因为是get方法没有响应体，要不然就直接Category category也可以。
     *更新：前端传过来的不一定是type=1，当前端用户登录后需要显示所有种类的菜品，这时候需要返回所有菜品种类，前端不会有任何参数。queryWrapper.eq(category.getType()!=null,Category::getType,category.getType()).orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);当category.getType()=null不会报错，而是会忽略该条件。所以最好不要用@RequestParam这种，容易报错。
     * @return
     */
    @GetMapping("/list")
    public R<List> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType()).orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = service.list(queryWrapper);
        return R.success(list);
    }

}
