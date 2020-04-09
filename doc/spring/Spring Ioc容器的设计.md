SpringIoC容器用来容纳我们开发的各种Bean，并且我们可以从中获取各种发布在Spring IoC容器中的Bean,并通过描述来获取它。

### Spring Ioc容器的设计

Spring IoC容器的设计主要是基于BeanFactory和ApplicationContext两个接口，其中ApplicationContext是BeanFactory的子接口，BeanFactory是Spring IoC容器所定义的最底层的接口，ApplicationContext是其高级接口之一，并且对BeanFactory的功能做了增强，所以我们主要使用ApplicatinContext作为Spring IoC的容器。

### IOC接口设计

#### BeanFactory类的体系结构

Spring为BeanFactory提供了很多的实现类，如图

![DefaultListableBeanFactory](.\pic\DefaultListableBeanFactory.png)

- BeanFactory:该接口位于类接口的顶层，它主要的方法就是getBean(String beanName),该方法送容器中返回特定名称的Bean,BeanFactory的功能通过其他接口得到不断扩展。
- ListableBeanFactory:该接口定义了访问容器中Bean基本信息的若干方法，如查看Bean的个数，获取某一类型Bean的配置名，查看容器中是否包含某一个Bean等。
- HierarchicalBeanFactory:父子级联IoC容器的接口，子容器可以通过接口方法访问父容器。
- ConfigurableBeanFactory:这个接口增强了IoC容器的可定制性，它定义了设置类装载器，属性编辑器，容器初始化后置处理器等方法
- AutowireCapableBeanFactory:定义了将容器中的Bean按某种规则（如按名字匹配，按类型匹配等）进行自动装配的方法
- SingletonBeanRegistry:定义了允许在运行期向容器注册单个实例Bean的方法
- BeanDefinitionRegistry:Spring配置文件中每一个节点元素在Spring里都通过一个BeanDefinition对象表示，它描述了Bean的配置信息。而接口BeanDefinitionRegistry接口提供了向容器手工注册BeanDefinition对象的方法。

#### BeanFactory的Bean的生命周期

![BeanFactory](.\pic\BeanFactory.png)

具体过程

1. 当调用者通过getBean(beanName)向容器中请求某一个Bean时，如果容器注册了org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor接口，则在实例化Bean之前，将调用接口postProcessBeforeInstantiation方法。具体可以查看AbstractAutowireCapableBeanFactory.resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd)方法
2. 根据配置情况调用Bean构造函数或工厂方法实例化Bean。可以查看AbstractAutowireCapableBeanFactory.doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)方法
3. 如果容器注册了InstantiationAwareBeanPostProcessor接口，那么在实例化Bean之后，调用该接口的postProcessAfterInstantiation方法，可在这里对已经实例化的对象进行一些处理
4. 如果Bean配置了属性信息，那么容器在这一步着手将配置设置到Bean队形的属性中，不过在设置每个属性之前将先调用InstantiationAwareBeanPostProcessor接口的postProcessPropertyValues方法
5. 调用Bean的属性设置方法设置属性值
6. 如果Bean实现了org.springframework.beans.factory.BeanNameAware接口，则将调用setBeanName接口方法，将配置文件中该Bean对应的名称设置到Bean中
7. 如果Bean实现了org.springframework.beans.factory.BeanFactoryAware接口，则将调用setBeanFactory接口方法，将BeanFactory容器实例设置到Bean中
8. 如果BeanFactory装配了org.springframework.beans.factory.config.BeanPostProcessor后处理器，则调用BeanPostProcessor的Object postProcessBeforeInitialization(Object bean, String beanName) 接口方法对Bean进行加工操作，其中，入参bean是当前正处理的Bean，而beanName是当前Bean的配置名，返回的对象为加工处理后的Bean。用户可以使用该方法对某些Bean进行特殊的处理，甚至改变Bean的行为
9. 如果Bean实现了InitializingBean接口，则将调用接口的afterPropertiesSet()方法
10. 如果在bean中通过init-method属性定义了初始化方法，则将执行这个方法
11. BeanPostProcessor后处理器定义了两个方法，postProcessBeforeInitialization（）在第八步调用，Object postProcessAfterInitialization(Object bean, String beanName)这个方法在此时调用，容器再次获得对Bean加工处理的机会
12. 如果在bean中指定Bean的作用范围为scope="prototype"，则将Bean返回给调用者，调用者负责Bean后续的生命管理，Spring不再管理这个Bean的生命周期。如果将作用范围设置为scope="singleton"，则将Bean放入Spring IoC容器的缓存池中，并将Bean引用返回给调用者，Spring继续对这些Bean进行后续的生命周期管理
13. 对于scope="singleton"的Bean，当容器关闭时，将触发Spring对Bean后续生命周期的管理工作。如果Bean实现了DisposableBean接口，则将调用接口的destory()方法，可以再次编写释放资源、记录日志等操作
14. 对于scope="singleton"的Bean，如果通过Bean的destroy-method属性执行了Bean的销毁方法，那么Spring将执行Bean的这个方法，完成Bean资源释放等操作

#### Bean声明周期实例

定义一个Bean类并实现BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean接口

```java
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
```

定义一个MyInstantiationAwareBeanPostProcessor并继承InstantiationAwareBeanPostProcessorAdapter

```java
public class MyInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {

        if ("animal".equals(beanName)) {
            System.out.println("MyInstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation");
        }
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if ("animal".equals(beanName)) {
            System.out.println("InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation");
        }
        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if ("animal".equals(beanName)) {
            System.out.println("InstantiationAwareBeanPostProcessor.postProcessPropertyValues");
        }
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {


        return super.postProcessBeforeInitialization(bean, beanName);
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return super.postProcessAfterInitialization(bean, beanName);
    }
}
```

定义一个MyBeanPostProcessor并实现BeanPostProcessor接口

```java
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
```

编写xml文件

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

</beans>
```

编写测试类

```java
public class BeanFactoryMain {

    public static void main(String[] args) {


        ResourcePatternResolver resolver=new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("classpath:beanfactory/beans.xml");

        DefaultListableBeanFactory factory=new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader=new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(resource);

        Person person = (Person)factory.getBean("person");

        System.out.println(person);


    }

}
```

这样在运行测试类，就可以看到BeanFactory的整个运行的声明周期过程啦。

