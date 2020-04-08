package com.rookie.bigdata.factory;

import com.rookie.bigdata.factory.domain.Animal;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @ClassName MyBeanPostProcessor
 * @Description MyBeanPostProcessor
 * @Author
 * @Date 2020/2/13 22:45
 * @Version 1.0
 */
public class MyBeanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("animal")){
            Animal animal=(Animal)bean;
            System.out.println("调用MyBeanPostProcessor.postProcessAfterInitialization(): "+animal);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("animal")){
            Animal animal=(Animal)bean;
            System.out.println("调用MyBeanPostProcessor.postProcessBeforeInitialization(): "+animal);
        }
        return bean;
    }
}
