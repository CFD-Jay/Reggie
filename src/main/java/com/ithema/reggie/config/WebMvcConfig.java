package com.ithema.reggie.config;

import com.ithema.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
//设置静态资源映射
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/static/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/static/front/");
    }

    /**
     * 重载mvc框架的消息转化器，改变在从服务端的数据向前端传送时的格式，具体是将long转为String（前端处理long数据时可能会发生精度丢失）,将时间类信息转化为更好处理的格式。
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
        //JacksonObjectMapper是我们自定义的消息转化器
        converter.setObjectMapper(new JacksonObjectMapper());
        //将消息转化器加入容器队列的守卫，否则可能不会生效。
        converters.add(0,converter);
    }
}
