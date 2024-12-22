package com.ithema.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ithema.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 元数据处理，也就是公共字段自动填充功能，某个字段在插入或者更新时可以统一在此处理，就不需要在Controller或者Service下处理了。需要字段上有@TableField(fill=FieldFill.INSERT/UPDATE)注解。
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
//    @Autowired
//    HttpServletRequest request;
    @Override
    public void insertFill(MetaObject metaObject) {

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //这里有一个问题，因为当新增或者更新时，程序是先走Controller的save或者update的，那么怎么获取当前用户的id呢(注意该类中无法通过参数解析HttpServletRequest对象,但是通过@Autowired进行依赖注入)？用线程传递数据。
        metaObject.setValue("createUser",BaseContext.getCountEmployeeId());
        metaObject.setValue("updateUser",BaseContext.getCountEmployeeId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        /**
         * 用来测试Filter、Controller、MyMetaObjectHandler运行的是同一个线程，可以传递数据。
         */
        Long id=Thread.currentThread().getId();
         log.info("线程id:{}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCountEmployeeId());
    }
}
