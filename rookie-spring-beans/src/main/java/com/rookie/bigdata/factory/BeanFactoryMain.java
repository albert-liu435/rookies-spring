package com.rookie.bigdata.factory;

import com.rookie.bigdata.factory.domain.Person;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @ClassName BeanFactoryMain
 * @Description BeanFactoryMain
 * @Author
 * @Date 2020/2/13 20:26
 * @Version 1.0
 */
public class BeanFactoryMain {

    public static void main(String[] args) {


        ResourcePatternResolver resolver=new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:factory/bean.xml");

        DefaultListableBeanFactory factory=new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader=new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(resource);

        Person person = (Person)factory.getBean("person");

        System.out.println(person);


    }

}
