package com.rookie.bigdata.factory.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/6/27 12:38
 */
public class PropertyPlaceholderConfigurerMain {

    public static void main(String[] args) {

        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("factory/config/bean.xml");

        ComboPooledDataSource comboPooledDataSource=(ComboPooledDataSource) applicationContext.getBean("dataSource");
        System.out.println(comboPooledDataSource.getJdbcUrl());
    }
}
