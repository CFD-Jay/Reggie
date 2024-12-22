package com.ithema.reggie;

import com.ithema.reggie.entity.Employee;
import com.ithema.reggie.mapper.EmployeeMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReggieApplicationTests {
    @Autowired
    private EmployeeMapper employeeMapper;
    @Test
    public  void t1(){
        System.out.println("1");
        return;
    }
    @Test
    public void t2(){
        Employee e=employeeMapper.selectById(1);
        System.out.println(e);
    }
}
