package com.rookie.bigdata.annotation;

import com.rookie.bigdata.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基于注释实例化Bean
 *
 * @author rookie
 * @version 1.0
 * @date 2020/4/9 22:42
 */
@Configuration
public class Config {

    @Bean(name = "user")
    public User buildUser() {
        User user = new User();
        user.setAddress("北京");
        user.setUsername("张三");
        return user;
    }
}
