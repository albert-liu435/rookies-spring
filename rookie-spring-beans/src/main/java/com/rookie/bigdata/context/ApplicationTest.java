package com.rookie.bigdata.context;

import com.rookie.bigdata.factory.domain.Animal;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName ApplicationTest
 * @Description ApplicationTest
 * @Author
 * @Date 2020/2/14 21:17
 * @Version 1.0
 */
public class ApplicationTest {
    public static void main(String[] args) throws Exception{
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("context/bean.xml");

        Animal animal = (Animal)applicationContext.getBean("animal");

        System.out.println(animal);
       // animal.destroy();
        ((ClassPathXmlApplicationContext) applicationContext).close();


    }
}