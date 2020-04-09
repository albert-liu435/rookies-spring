package com.rookie.bigdata.context;

import com.rookie.bigdata.factory.domain.Animal;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor{

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {		
		if(beanName.equals("animal")){
			Animal animal = (Animal)bean;

				System.out.println("调用MyBeanPostProcessor.postProcessBeforeInitialization()");


		}
		return bean;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {		
		if(beanName.equals("animal")){
			Animal animal = (Animal)bean;

				System.out.println("调用MyBeanPostProcessor.postProcessAfterInitialization()");

			}

		return bean;
	}
}
