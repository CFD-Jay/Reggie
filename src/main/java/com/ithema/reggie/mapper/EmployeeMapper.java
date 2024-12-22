package com.ithema.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ithema.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    //BaseMapper是MP中封装好的，里面有很多的SQL语句，但是泛型需要自己填(Employee)
}
