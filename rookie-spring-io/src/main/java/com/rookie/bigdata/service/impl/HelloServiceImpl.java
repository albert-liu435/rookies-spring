package com.rookie.bigdata.service.impl;

import com.rookie.bigdata.service.HelloService;
import org.springframework.beans.BeanInfoFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/28 20:16
 */
public class HelloServiceImpl implements BeanInfoFactory {

    public HelloServiceImpl(){
        System.out.println("实例化 HelloServiceImpl");
    }

    @Override
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        return null;
    }
}
