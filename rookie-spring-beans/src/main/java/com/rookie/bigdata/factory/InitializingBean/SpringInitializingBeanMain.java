package com.rookie.bigdata.factory.InitializingBean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/6/27 12:15
 */
public class SpringInitializingBeanMain {

    public static void main(String[] args) {


        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("factory/InitializingBean/bean.xml");

       // SpringInitializingBean springInitializingBean = (SpringInitializingBean) applicationContext.getBean("springInitializingBean");


    }
}
