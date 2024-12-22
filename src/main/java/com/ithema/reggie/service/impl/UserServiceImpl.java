package com.ithema.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithema.reggie.entity.Employee;
import com.ithema.reggie.entity.User;
import com.ithema.reggie.mapper.EmployeeMapper;
import com.ithema.reggie.mapper.UserMapper;
import com.ithema.reggie.service.EmployeeService;
import com.ithema.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
