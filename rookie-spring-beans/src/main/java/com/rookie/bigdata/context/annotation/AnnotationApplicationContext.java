package com.rookie.bigdata.context.annotation;

import com.rookie.bigdata.beanfactory.domain.Car;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @ClassName AnnotationApplicationContext
 * @Description AnnotationApplicationContext
 * @Author
 * @Date 2020/6/30 17:24
 * @Version 1.0
 */
public class AnnotationApplicationContext {

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Beans.class);
        Car car = ctx.getBean("car", Car.class);

        System.out.println(car);
    }
}