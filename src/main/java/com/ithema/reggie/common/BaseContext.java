package com.ithema.reggie.common;

/**
 * 线程工具类，可以在线程中存和取数据，在本项目的应用是在公共字段填充中获取当前用户ID。
 */
public class BaseContext{
    public static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){ threadLocal.set(id);}
    public static Long getCurrentId(){ return threadLocal.get();}
    public static void setCountEmployeeId(Long id){
        threadLocal.set(id);
    }
    public static Long getCountEmployeeId(){
        return (Long)threadLocal.get();
    }

}
