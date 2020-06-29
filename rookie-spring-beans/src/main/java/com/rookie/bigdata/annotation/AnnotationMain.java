package com.rookie.bigdata.annotation;

import com.rookie.bigdata.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/9 22:44
 */
public class AnnotationMain {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        User user = (User) applicationContext.getBean("user");
        System.out.println(user);
        Config config = (Config) applicationContext.getBean("config");
        System.out.println(config);
        config.print();
        // ApplicationContext applicationContext=new ClassPathXmlApplicationContext("annotation/bean.xml");

    }

}
