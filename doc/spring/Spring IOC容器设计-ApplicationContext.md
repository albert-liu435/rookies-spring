### ApplicationContext

ApplicationContext的主要实现类是ClassPathXmlApplicationContext和FileSystemXmlApplicationContext，前者默认从类路径加载配置文件，后者默认从文件系统中装载配置文件。

##### ApplicatonContext类体系结构

![ClassPathXmlApplicationContext](.\pic\ClassPathXmlApplicationContext.png)

- ApplicationEventPublisher:让容器拥有发布应用上下文事件的功能，包括容器启动事件、关闭事件等。实现了ApplicationListener事件监听接口的Bean可以接受到容器事件，并对事件进行相应处理。在ApplicationContext抽象实现类AbstracApplicationContext中存在一个ApplicationEventMulticaster，它负责保存所有的监听器，以便在容器产生上下文事件时通知这些事件监听者。
- MessageSource:为应用提供i18n国际化消息访问的功能
- ResourcePatternResolver:所有的ApplicationContext实现类都实现了类似于PathMatchingResourcePatternResolver的功能，可以通过带前缀的Ant风格的资源文件路径装载Spring配置文件。
- LifeCycle:该接口提供了start()和stop()方法，主要用于控制异步过程。

##### ApplicationContext的声明周期

ApplicationContext同BeanFactory声明周期有些类似，不同的是ApplicationContext会利用java的反射机制自动识别出配置文件中定义的BeanPostProcessor、InstantiationAwareBeanPostProcessor和BeanFactoryProcesssor()，并自动注册到应用的上下文中。

![ApplicationContext](.\pic\ApplicationContext.png)

ApplicationContext声明周期实例

定义一个MybeanFactoryPostProcessor

```java
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor{

   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
      BeanDefinition bd = bf.getBeanDefinition("animal");
      bd.getPropertyValues().addPropertyValue("name", "zhangsan");
      System.out.println("调用MyBeanFactoryPostProcessor.postProcessBeanFactory()！");
   }
}
```

MyBeanPostProcessor

```java
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
```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">

    <bean id="animal" class="com.rookie.bigdata.factory.domain.Animal" destroy-method="myDestory" init-method="myInit">
        <property name="name" value="哈士奇"></property>
        <property name="age" value="3"></property>
    </bean>

    <bean id="myBeanPostProcessor" class="com.rookie.bigdata.context.MyBeanPostProcessor"/>
    <bean id="myBeanFactoryPostProcessor" class="com.rookie.bigdata.context.MyBeanFactoryPostProcessor"/>
</beans>
```

```java
public class ApplicationTest {
    public static void main(String[] args) throws Exception{
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("context/bean.xml");

        Animal animal = (Animal)applicationContext.getBean("animal");

        System.out.println(animal);
       // animal.destroy();
        ((ClassPathXmlApplicationContext) applicationContext).close();


    }
}
```

运行上面的实例即可看到ApplicationContext的声明周期的过程。

