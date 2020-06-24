package com.rookie.bigdata.context;

import com.rookie.bigdata.factory.domain.Animal;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * @ClassName ApplicationTest
 * @Description ApplicationTest
 * @Author
 * @Date 2020/2/14 21:17
 * @Version 1.0
 * {@link
 */
public class ApplicationTest {
    public static void main(String[] args) throws Exception {
        //  Thread.currentThread().getClass().getClassLoader().getClass()

         ApplicationContext applicationContext=new ClassPathXmlApplicationContext("context/bean.xml");

        //XmlBeanFactory applicationContext = new XmlBeanFactory(new ClassPathResource("context/bean.xml"));

        Animal animal = (Animal) applicationContext.getBean("animal");

        System.out.println(animal);
        // animal.destroy();
        // ((ClassPathXmlApplicationContext) applicationContext).close();


    }
}
