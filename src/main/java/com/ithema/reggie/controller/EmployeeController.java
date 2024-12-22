package com.ithema.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.Employee;
import com.ithema.reggie.service.EmployeeService;
import com.sun.net.httpserver.Authenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, HttpServletResponse response, @RequestBody Employee employee) {
//        当客户端向 /login 路径发送 POST 请求，并在请求体中包含一个 JSON 格式的用户数据时，Spring 会自动将这个 JSON 数据转换为 Employee 类的实例，并将其作为参数传递给 login 方法。


        //1.将密码md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

//        无论是QueryWrapper还是UpdateWrapper在构造条件的时候都需要写死字段名称，会出现字符串魔法值。这在编程规范中显然是不推荐的。
//        那怎么样才能不写字段名，又能知道字段名呢？
//
//        其中一种办法是基于变量的gettter方法结合反射技术。因此我们只要将条件对应的字段的getter方法传递给MybatisPlus，它就能计算出对应的变量名了。而传递方法可以使用JDK8中的方法引用和Lambda表达式。
//        因此MybatisPlus又提供了一套基于Lambda的Wrapper，包含两个：
//        - LambdaQueryWrapper
//        - LambdaUpdateWrapper
//        分别对应QueryWrapper和UpdateWrapper

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee e = employeeService.getOne(queryWrapper);
//        System.out.println(e.getStatus());
//        System.out.println(e);
        //如果无此用户，则登录失败
        if (e == null) {
            return R.error("无此用户！");
        }
        //如果密码错误，则登录失败
        //字符串用equals比较，Integer用==比较
        else if (!e.getPassword().equals(password)) {
            return R.error("密码错误！");
        }
        //否则如果员工状态禁用，则登录失败
        else if (e.getStatus() == 0) {
            System.out.println("该员工已经禁用");
            return R.error("该员工已禁用");
        }
        //登录成功
        else {
            //设置Session
            request.getSession().setAttribute("employee", e.getId());
            //System.out.println(request.getSession().getAttribute("employee"));
            return R.success(e);
        }

    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(@RequestBody Employee requestBody, HttpServletRequest request) {
        //主要获取前端传来的参数不要用httpServletRequest.getParameter().要用依赖注入@RequestBody进行依赖注入
        //System.out.println(requestBody);
//        String name = requestBody.getName();
//        String username = requestBody.getUsername();
//        String password = requestBody.getPassword();
//        String idNumber = requestBody.getIdNumber();
//        String sex = requestBody.getSex();

//        QueryWrapper<Employee> queryWrapper = new QueryWrapper<Employee>();
//        queryWrapper.lambda().select(Employee::getId).eq(Employee::getUsername, username);
        requestBody.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        requestBody.setCreateTime(LocalDateTime.now());
//        requestBody.setUpdateTime(LocalDateTime.now());
//        requestBody.setCreateUser((long)request.getSession().getAttribute("employee"));
//        requestBody.setUpdateUser((long)request.getSession().getAttribute("employee"));

        employeeService.save(requestBody);
        return R.success("添加成功");


        /**
         *    如果用户名已经存在 但是这样会增加数据库负担，一般采用全局异常捕获技术（common下的GobalExceptionHandler类）
         *         if(employeeService.getOne(queryWrapper)!=null)
         *         {
         *             return R.error("该用户已经存在");
         *         }
         *         else
         *         {
         *             //默认密码123456
         *             requestBody.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
         *             requestBody.setCreateTime(LocalDateTime.now());
         *             requestBody.setUpdateTime(LocalDateTime.now());
         *             requestBody.setCreateUser((long)request.getSession().getAttribute("employee"));
         *             requestBody.setUpdateUser((long)request.getSession().getAttribute("employee"));
         *             employeeService.save(requestBody);
         *             return R.success(requestBody);
         *         }
         */
    }

    /**
     * 分页功能
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    /**
     * @RequestParam和@PathVariable的区别
     * @RequsetParam是需要url传的是带?的参数格式，而@PathVariable需要的URL路径中提取参数，如page/name
     * @RequestParam中若前端没有传指定的参数会报错400，需要指定required=false.
     */
    public R<Page> page(@RequestParam("page")int page,@RequestParam("pageSize")int pageSize,@RequestParam(value = "name",required = false)String name){
        //log.info("page={}+ pageSize={} name={}" ,page,pageSize,name);

        Page<Employee>page1=new Page<>(page,pageSize);
        LambdaQueryWrapper<Employee>lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(page1,lambdaQueryWrapper);
      //  System.out.println("page=:/n"+page1);
      //  System.out.println(page1.getRecords());
        return R.success(page1);

    }

    /**
     * 控制员工的状态和信息修改
     * 注意这里需要JacksonObjectMapper类，具体功能见该类注解。
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        /**
         * 用来测试Filter、Controller、MyMetaObjectHandler运行的是同一个线程，可以传递数据。
         */
         Long id=Thread.currentThread().getId();
         log.info("线程id:{}",id);


        //需要根据id更新更改该员工的更新时间、更新人、status信息。
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateTime(LocalDateTime.now());
//        //employeeService.updateById(employee);不是employeeService.save()
        employeeService.updateById(employee);
        return R.success("修改成功！");
    }
    @GetMapping("/{id}")
    public R<Employee> edit(@PathVariable long id){
        //System.out.println(id);
        Employee employee = employeeService.getById(id);
        //这里需要返回employee是因为前端是编辑修改员工信息的界面，需要数据回显。
        return R.success(employee);
    }

}