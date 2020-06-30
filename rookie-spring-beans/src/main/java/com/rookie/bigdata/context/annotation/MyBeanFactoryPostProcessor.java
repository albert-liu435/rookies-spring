package com.rookie.bigdata.context.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @ClassName MyBeanFactoryPostProcessor
 * @Description MyBeanFactoryPostProcessor
 * @Author
 * @Date 2020/6/30 17:25
 * @Version 1.0
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
        BeanDefinition bd = bf.getBeanDefinition("car");
        bd.getPropertyValues().addPropertyValue("brand", "奇瑞QQ");
        System.out.println("调用MyBeanFactoryPostProcessor.postProcessBeanFactory()！");
    }
}