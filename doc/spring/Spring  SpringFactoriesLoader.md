Spring  SpringFactoriesLoader

Spring的SpringFactoriesLoader工厂的加载机制类似java提供的SPI机制一样，是Spring提供的一种加载方式。只需要在classpath路径下新建一个文件META-INF/spring.factories，并在里面按照Properties格式填写好借口和实现类即可通过SpringFactoriesLoader来实例化相应的Bean。其中key可以是接口、注解、或者抽象类的全名。value为相应的实现类，当存在多个实现类时，用“,”进行分割。

SpringFactoriesLoader的主要属性及方法

```java
public final class SpringFactoriesLoader {
	//文件位置，可以存在多个JAR文件中
	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";
	//用来缓存MultiValueMap对象
	private static final Map<ClassLoader, MultiValueMap<String, String>> cache = new ConcurrentReferenceHashMap<>();


	private SpringFactoriesLoader() {
	}


	/**
	 * 根据给定的类型加载并实例化工厂的实现类
	 */
	public static <T> List<T> loadFactories(Class<T> factoryType, @Nullable ClassLoader classLoader) {
		Assert.notNull(factoryType, "'factoryType' must not be null");
		//获取类加载器
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
		}
		//加载类的全限定名
		List<String> factoryImplementationNames = loadFactoryNames(factoryType, classLoaderToUse);
		if (logger.isTraceEnabled()) {
			logger.trace("Loaded [" + factoryType.getName() + "] names: " + factoryImplementationNames);
		}
		//创建一个存放对象的List
		List<T> result = new ArrayList<>(factoryImplementationNames.size());
		for (String factoryImplementationName : factoryImplementationNames) {
			//实例化Bean,并将Bean放入到List集合中
			result.add(instantiateFactory(factoryImplementationName, factoryType, classLoaderToUse));
		}
		//对List中的Bean进行排序
		AnnotationAwareOrderComparator.sort(result);
		return result;
	}
	
	/**
	 * 根据给定的类型加载类路径的全限定名
	 */
	public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
		//获取名称
		String factoryTypeName = factoryType.getName();
		//加载并获取所有META-INF/spring.factories中的value
		return loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
	}
	
	private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
		//根据类加载器从缓存中获取，如果缓存中存在,就直接返回，如果不存在就去加载
		MultiValueMap<String, String> result = cache.get(classLoader);
		if (result != null) {
			return result;
		}
	
		try {
			//获取所有JAR及classpath路径下的META-INF/spring.factories的路径
			Enumeration<URL> urls = (classLoader != null ?
					classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
			result = new LinkedMultiValueMap<>();
			//遍历所有的META-INF/spring.factories的路径
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				UrlResource resource = new UrlResource(url);
				//将META-INF/spring.factories中的key value加载为Prpperties对象
				Properties properties = PropertiesLoaderUtils.loadProperties(resource);
				for (Map.Entry<?, ?> entry : properties.entrySet()) {
					//key名称
					String factoryTypeName = ((String) entry.getKey()).trim();
					for (String factoryImplementationName : StringUtils.commaDelimitedListToStringArray((String) entry.getValue())) {
						//以factoryTypeName为key,value为值放入map集合中
						result.add(factoryTypeName, factoryImplementationName.trim());
					}
				}
			}
			//放入到缓存中 
			cache.put(classLoader, result);
			return result;
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load factories from location [" +
					FACTORIES_RESOURCE_LOCATION + "]", ex);
		}
	}
	
	//实例化Bean对象
	@SuppressWarnings("unchecked")
	private static <T> T instantiateFactory(String factoryImplementationName, Class<T> factoryType, ClassLoader classLoader) {
		try {
			Class<?> factoryImplementationClass = ClassUtils.forName(factoryImplementationName, classLoader);
			if (!factoryType.isAssignableFrom(factoryImplementationClass)) {
				throw new IllegalArgumentException(
						"Class [" + factoryImplementationName + "] is not assignable to factory type [" + factoryType.getName() + "]");
			}
			return (T) ReflectionUtils.accessibleConstructor(factoryImplementationClass).newInstance();
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException(
				"Unable to instantiate factory class [" + factoryImplementationName + "] for factory type [" + factoryType.getName() + "]",
				ex);
		}
	}

}
```

下面来进行测试

首先在classpath:路径下新建一个META-INF/spring.factories文件，在里面配置如下：

```java
#com.rookie.bigdata.service.HelloService=com.rookie.bigdata.service.impl.HelloServiceImpl
org.springframework.beans.BeanInfoFactory=com.rookie.bigdata.service.impl.HelloServiceImpl
```

新建一个HelloServiceImpl类，并实现BeanInfoFactory接口

```java
public class HelloServiceImpl implements BeanInfoFactory {

    public HelloServiceImpl(){
        System.out.println("实例化 HelloServiceImpl");
    }

    @Override
    public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        return null;
    }
}
```

进行相关的测试

```java
@Test
public void testLoadFactoryNames() {
    //获取所有META-INF/spring.factories中的value值
    List<String> applicationContextInitializers = SpringFactoriesLoader.loadFactoryNames(BeanInfoFactory.class, ClassUtils.getDefaultClassLoader());
    for (String applicationContextInitializer : applicationContextInitializers) {
        System.out.println(applicationContextInitializer);
    }
}

@Test
public void testLoadFactories() {
    //实例化所有在META-INF/spring.factories配置的且实现BeanInfoFactory接口的类
    List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, ClassUtils.getDefaultClassLoader());

    for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
        System.out.println(beanInfoFactory);
    }
}
```

通过以上可以证明，SpringFactoriesLoader会寻找jar包中配置META-INF下的spring.factories配置文件相应Key的value,并根据需要实例化。

源码详见：[github](https://github.com/albert-liu435/rookies-spring/blob/master/rookie-spring-io/src/test/java/org/springframework/core/io/support/SpringFactoriesLoaderTest.java)