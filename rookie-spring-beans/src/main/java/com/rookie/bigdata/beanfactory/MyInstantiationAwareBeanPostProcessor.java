package com.rookie.bigdata.beanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.beans.PropertyDescriptor;

/**
 * @ClassName MyInstantiationAwareBeanPostProcessor
 * @Description MyInstantiationAwareBeanPostProcessor
 * @Author
 * @Date 2020/6/30 11:09
 * @Version 1.0
 */
public class MyInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    public Object postProcessBeforeInstantiation(Class beanClass, String beanName) throws BeansException {
        if("car".equals(beanName)){
            System.out.println("MyInstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation");
        }
        return null;
    }

    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if("car".equals(beanName)){
            System.out.println("InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation");
        }
        return true;
    }

    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeansException {
        if("car".equals(beanName)){
            System.out.println("InstantiationAwareBeanPostProcessor.postProcessPropertyValues");
        }
        return pvs;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
