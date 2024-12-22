package com.ithema.reggie.controller;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ithema.reggie.common.R;
import com.ithema.reggie.entity.SetmealDish;
import com.ithema.reggie.entity.User;
import com.ithema.reggie.service.UserService;
import com.ithema.reggie.utils.SMSUtils;
import com.ithema.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phoneNumber = user.getPhone();
        //System.out.println(phoneNumber);
        if (!StringUtils.isEmpty(phoneNumber)) {

            //生成随机验证码
            String yzm = ValidateCodeUtils.generateValidateCode4String(4);
            //调用阿里云提供的短信服务API完成发送短信
//            SMSUtils.sendMessage("姚堪reggie",
//                    "SMS_307730383",phoneNumber,yzm);
//
            log.info("验证码为：{}", yzm);
            session.setAttribute(phoneNumber, yzm);
            return R.success("短信发送成功！");
            //需要将生成的验证码保存到Session
        }

        return R.success("短信发送失败！");
    }

    /**
     * 用户登录因为前端传来的参数是json,要么新建一个DTO接受，要么用map接受
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        // System.out.println(map.toString());{phone=17692638035, code=ewee}
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        Long userid;
        if (session.getAttribute(phone) != null && session.getAttribute(phone).toString().equals(code)) {
            User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
            //判断是否是新用户，是新用户自动注册
            User user = new User();
            if (one == null) {
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
                userid = user.getId();
            }

            //这里不加会有bug，当不是新用户时，此时userid没有值。
            else
            {userid = one.getId();}
            //登录成功后必须设置session，否则会被拦截器拦截。
            System.out.println(user);
           request.getSession().setAttribute("user",userid);
            System.out.println(request.getSession().getAttribute("user"));
            return R.success(user);
        } else return R.error("登录失败");


    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session,HttpServletRequest request, HttpServletResponse response)
    {
        session.removeAttribute("user");
        return R.success("下线成功！");
    }
}