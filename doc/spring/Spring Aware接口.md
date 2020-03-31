### Spring Aware接口

Aware接口是一个标记接口，Aware的实现接口如下

![Aware](.\pic\Aware.png)

XXXAare在Spring中表示对XXX可以感知，通俗点解释就是：如果在某个类里面想要使用Spring的一些东西，就可以通过实现XXXAware接口告诉Spring,Spring看到后就会送过来，而接受的方式是通过实现接口唯一的方法setXXX.比如ApplicationContextAware

#### ApplicationContextAware使用

编写SpringAware实现ApplicationContext接口

```java
package com.rookie.bigdata.aware;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**

 * @author rookie
 * @version 1.0
 * @date 2020/3/21 22:58
   */

public class SpringAware implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    //Spring容器会将ApplicationContext对象传入过来
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("传入的IOC容器applicationContext = [" + applicationContext + "]");
        this.applicationContext = applicationContext;
    }
    
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }


}
```

User实体类

```java
package com.rookie.bigdata.domain;

public class User {
    private String username;
    private String address;

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

}
```

spring-aware.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="
                    http://www.springframework.org/schema/beans
                    http://www.springframework.org/schema/beans/spring-beans.xsd
                    http://www.springframework.org/schema/tx
                    http://www.springframework.org/schema/tx/spring-tx.xsd
                    http://www.springframework.org/schema/aop
                    http://www.springframework.org/schema/aop/spring-aop.xsd
                    http://www.springframework.org/schema/context
                    http://www.springframework.org/schema/context/spring-context.xsd">

    <bean id="user" class="com.rookie.bigdata.domain.User">
        <property name="username" value="张三"></property>
        <property name="address" value="北京"></property>
    </bean>
    
    <bean id="springAware" class="com.rookie.bigdata.aware.SpringAware"></bean>

</beans>
```

测试类

```java
	 @Test
    public void test1(){
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/aware/spring-aware.xml");

//        User user = (User) applicationContext.getBean("user");
//        System.out.println(user);

        SpringAware springAware = (SpringAware) applicationContext.getBean("springAware");
        ApplicationContext applicationContext1 = springAware.getApplicationContext();
        User user = (User) applicationContext1.getBean("user");
        System.out.println(user);
    }

```

源码 [github](https://github.com/albert-liu435/rookies-spring/tree/master/rookie-spring-beans/src/main/java/com/rookie/bigdata/aware ) 

