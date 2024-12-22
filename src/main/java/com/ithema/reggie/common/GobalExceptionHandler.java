package com.ithema.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
@Slf4j
//定义一个全局的异常处理器或一个全局的切面，所有RestController和Controller出现的异常都会集中被交给其处理
@ControllerAdvice(annotations ={RestController.class, Controller.class})
//注意这个必须加，因为返回给客户端的不是一个页面，而是实体，必须实体类转为JSON数据传给客户端。有些注解其中包括了这个注解，如@RestController注解，就不用重复写了。
@ResponseBody
public class GobalExceptionHandler {
    //捕捉SQLIntegrityConstraintViolationException.class类的异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){

        //判断该错误是否是因为Duplicate entry
        if(ex.getMessage().contains("Duplicate entry")){
            String[] para=ex.getMessage().split(" ");
            String username=para[2];
            return R.error(username+"已存在!");

        }
        return R.error("添加失败，未知错误！");

    }

    /**
     * 自定义的当删除分类时，该分类已经关联的异常。
     * @param ex
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public R<String> exceptionHandler(RuntimeException ex){
        log.info("发生错误："+ex.getMessage());
        return R.error(ex.getMessage());
    }

}
