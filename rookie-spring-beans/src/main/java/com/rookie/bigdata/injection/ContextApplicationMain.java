package com.rookie.bigdata.injection;

import com.rookie.bigdata.domain.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/9 22:51
 */
public class ContextApplicationMain {

    public static void main(String[] args) {

        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("injection/bean.xml");

        User user=(User)applicationContext.getBean("user");
        System.out.println(user);


    }


}
