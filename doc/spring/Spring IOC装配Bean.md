Spring IOC装配Bean

### Spring的依赖注入

Spring主要支持两种依赖注入方式，分别是属性注入和构造函数注入。同时也支持工厂方法注入方式。

##### 属性注入

属性注入的方式非常简单，即指通过setXxx()方法注入Bean的属性值或依赖对象。如下实例

编写User类

```java
public class User {
    private String username;
    private String address;

    public User() {
    }
    
    public User(String username, String address) {
        this.username = username;
        this.address = address;
    }
    
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
    
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

}
```

编写xml文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">

    <bean id="user" class="com.rookie.bigdata.domain.User">
        <property name="username" value="张三"></property>
        <property name="address" value="北京"></property>
    </bean>

</beans>
```

测试

```java
    ApplicationContext applicationContext=new ClassPathXmlApplicationContext("injection/bean.xml");

    User user=(User)applicationContext.getBean("user");
    System.out.println(user);
```
构造函数注入

构造函数注入是属性注入的另一种常用的注入方式。

xml配置方式如下

```xml
<bean id="user" class="com.rookie.bigdata.domain.User">
    <constructor-arg name="username" value="李四"></constructor-arg>
    <constructor-arg name="address" value="上海"></constructor-arg>
</bean>
```
注入参数

XML中含有5个特殊符号，分别是&，<,>,”，‘，如果配置文件中的注入值包含这些特殊的字符，就需要进行特殊的处理。有两种解决方法，其一：采用<![CDATA[]]>特殊标签，将包含特殊字符的字符串封装起来。其二：使用XML转义序列表示这些特殊字符。

```xml
<bean id="user" class="com.rookie.bigdata.domain.User">
    <property name="username" value="张三"></property>
   <!-- <property name="address" value="北京"></property>-->
    <property name="address">
        <value><![CDATA[北京&通州]]></value>
    </property>
</bean>
```
| 特殊字符 | 转义序列  | 特殊字符 | 转义序列   |
| -------- | --------- | -------- | ---------- |
| <        | &amp;lt;  | “        | &amp;quot; |
| >        | &amp;gt;  | ’        | &amp;apos; |
| &        | &amp;amp; |          |            |

##### 基于注解的配置

```java
<context:component-scan base-package="com.rookie.bigdata.annotation"></context:component-scan>
```

component-scan 的base-package属性指定一个需要扫描的基类包，Spring容器会扫描这个基类包里面的所有的属性，并从类的注解信息中获取Bean的定义信息。

如果想扫描特定的类，可以使用resource-pattern属性过滤出特定的类。如：

```java
<context:component-scan base-package="com.rookie.bigdata" resource-pattern="annotation/*.class"></context:component-scan>
```

即Spring仅会扫描基类包里annotation子包中的类。

通过使用resource-pattern发现，还是有很多时候并不满足要求，此时可以通过过滤表达式。如下：

| 类别       | 示例                                    | 说明                                                         |
| ---------- | --------------------------------------- | ------------------------------------------------------------ |
| annotation | com.rookie.bigdata.XxxAnnotation        | 所有标注了XxxAnnotation的类，该类型采用目标类是否标注了某个注解进行过滤 |
| assignable | com.rookie.bigdata.XxxService           | 所有继承或扩展XxxService的类，该类型采用目标类是否继承或扩展了某个特定类进行过滤 |
| aspectj    | com.rookie.bigdata.*Service+            | 所有类名以Service结束的类及继承或扩展它们的类，该类采用AspectJ表达式进行过滤 |
| regex      | com\\.rookie\\.bigdata\\.annotation\\.* | 所有com.rookie.bigdata.annotation类包下的类，该类型采用正则表达式根据目标类的类名进行过滤 |
| custom     | com.rookie.bigdata.XxxTypeFilter        | 采用XxxTypeFile代码方式实现过滤规则。该类必须实现org.springframework.core.type.TypeFilter接口 |

&lt;context:component-scan/&gt;有一个容易忽视的use-default-filters属性，默认值为true;表示默认会对标注@Component、@Controller、@Service及Reposity的Bean进行扫描，&lt;context:component-scan/&gt;先根据&lt;exclude-filter&gt;列出需要排除的黑名单，再通过&lt;include-filter&gt;列出需要包含的白名单。

自动装配Bean

Spring通过@Authwired注解实现Bean的依赖注入

如下：

```java
@Repository
public class UserDao {

    public UserDao(){
        System.out.println("实例化userDao");
    }

}
```

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

}
```

@Autowired默认按照类型(byType)匹配的方式在容器中查找匹配的Bean,当有且仅有一个匹配的Bean时，Spring将其注入@Autowired标注的变量中

@Autowired还有一个required属性，默认情况下为true;表示必须找到匹配的Bean,否则会报NoSuchBeanDefinitionException异常。

```java
@Autowired(required =true)
private UserDao userDao;
```
@Qualifier注解

如果容器中有一个以上的匹配Bean时，可以通过@Qualifier注解限定Bean的名称。如下：

```java
@Repository
@Qualifier(value = "userDao")
public class UserDao {

    public UserDao(){
        System.out.println("实例化userDao");
    }

}
```

```java
@Service
public class UserService {

    @Autowired(required =true)
    @Qualifier(value = "userDao")
    private UserDao userDao;

}
```

源码见 [github]( https://github.com/albert-liu435/rookies-spring/tree/master/rookie-spring-beans/src/main/java/com/rookie/bigdata/annotation ) 