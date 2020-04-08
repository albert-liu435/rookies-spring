package com.rookie.bigdata.factory.domain;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

/**
 * @ClassName Animal
 * @Description Animal
 * @Author
 * @Date 2020/2/13 22:42
 * @Version 1.0
 */
//@Data
public class Animal implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {
    private String name;
    private int age;
    private BeanFactory beanFactory;
    private String beanName;

    public Animal() {
        System.out.println("调用Animal的构造方法");
    }

    public void setName(String name) {
        System.out.println("调用AnimalsetName设置属性");
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("调用BeanFactoryAware.setBeanFactory()");
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println("调用BeanNameAware.setBeanName()。");
        this.beanName = beanName;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("调用InitializingBean.afterPropertiesSet()。");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("调用DisposableBean.destory()。");
    }

    public void myInit() {
        System.out.println("调用myInit()，");

    }

    public void myDestory() {
        System.out.println("调用myDestroy()。");
    }

    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", beanFactory=" + beanFactory +
                '}';
    }
}
