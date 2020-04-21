package com.rookie.bigdata.context.support;

import com.rookie.bigdata.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/15 7:48
 */
public class ClassPathXmlApplicationContextMain {

    public static void main(String[] args) {

        ApplicationContext applicationContext=
                new ClassPathXmlApplicationContext("/context/support/ClassPathXmlApplicationContext1.xml",ClassPathXmlApplicationContextMain.class);

//        ApplicationContext applicationContext=
//                new ClassPathXmlApplicationContext("/context/support/ClassPathXmlApplicationContext2.xml","/context/support/ClassPathXmlApplicationContext1.xml");

        User user=(User)applicationContext.getBean("user");
        System.out.println(user);
    }



}
