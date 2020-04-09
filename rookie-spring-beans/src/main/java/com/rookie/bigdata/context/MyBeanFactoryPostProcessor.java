package com.rookie.bigdata.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
		BeanDefinition bd = bf.getBeanDefinition("animal");
		bd.getPropertyValues().addPropertyValue("name", "zhangsan");
		System.out.println("调用MyBeanFactoryPostProcessor.postProcessBeanFactory()！");
	}
}
