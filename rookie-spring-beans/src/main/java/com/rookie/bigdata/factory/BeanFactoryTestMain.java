package com.rookie.bigdata.factory;

import com.rookie.bigdata.factory.domain.Animal;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @ClassName BeanFactoryTestMain
 * @Description BeanFactoryTestMain
 * @Author
 * @Date 2020/2/13 22:38
 * @Version 1.0
 */
public class BeanFactoryTestMain {

    public static void main(String[] args) {

        Resource resource = new ClassPathResource("aop/before/bean.xml");

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);

        //向容器中注册MyBeanPostProcessor后处理器
        beanFactory.addBeanPostProcessor(new MyBeanPostProcessor());
        //向容器中注册MyInstantiationAwareBeanPostProcessor后处理器
        beanFactory.addBeanPostProcessor(
                new MyInstantiationAwareBeanPostProcessor());

        Animal animal1 = (Animal)beanFactory.getBean("animal");

       // Animal animal2 = (Animal)beanFactory.getBean("animal");

        beanFactory.destroySingletons();



    }
}
