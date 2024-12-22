package com.ithema.reggie.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;


//@TableName("employee")
//指定该实体类对应的表名，大部分默认应该是相同的，如果不同也应满足以下约定
//1.数据库中_转驼峰命名法
//2.类中的id属性默认对应数据表中id为主键

/**@TableField(fill = FieldFill.INSERT)  公共字段填充，表示该字段在插入（INSERT）操作时自动填充
 * 每当你插入一个新的 User 实例时，createTime 字段会自动被填充为当前时间。这样，你就不需要在每次插入数据时手动设置创建时间了
 *
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}

//一般情况下我们并不需要给字段添加@TableField注解，一些特殊情况除外：
//        - 成员变量名与数据库字段名不一致
//        - 成员变量是以isXXX命名，按照JavaBean的规范，MybatisPlus识别字段时会把is去除，这就导致与数据库不符。
//        - 成员变量名与数据库一致，但是与数据库的关键字冲突。使用@TableField注解给字段名添加转义字符：``