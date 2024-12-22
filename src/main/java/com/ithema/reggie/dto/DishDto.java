package com.ithema.reggie.dto;

import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.DishFlavor;
import com.ithema.reggie.entity.Dish;
import com.ithema.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO是数据传输对象，通常用于当前端传过来的数据，找不到一个合适的实体类来接受时，创建一个能与之匹配并且接受的类来接收前端的数据。
 */
@Data
public class DishDto extends Dish {

    //因为继承了Dish类，所以有Dish类的全部属性。
    //下面的属性是针对于前端传来的参数的。
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    @Override
    public String toString() {
        return "DishDto{" +
                "flavors=" + flavors +
                ", categoryName='" + categoryName + '\'' +
                ", copies=" + copies +
                '}'
                +"|||"+super.toString();
    }

    private Integer copies;
}
