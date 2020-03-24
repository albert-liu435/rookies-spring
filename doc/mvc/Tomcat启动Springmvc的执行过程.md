

Tomcat启动Springmvc的执行过程

tomcat启动时会加载web.xml配置文件，加载顺序如下

a)容器首先读取web.xml中的<context-param>的配置内容和<listener>标签中配置项；
b)紧接着实例化ServletContext对象，并将<context-param>配置的内容转化为键值传递给ServletContext；
c)创建<listener>配置的监听器的类实例，并且启动监听；
d)随后调用listener的contextInitialized(ServletContextEvent args)方法，ServletContext = ServletContextEvent.getServletContext(); 
此时你可以通过ServletContext获取context-param配置的内容并可以加以修改，此时Tomcat还没完全启动完成。
e)后续加载配置的各类filter；
f)最后加载servlet；

以下面的web.xml文件为例，看一下源码

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
    http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         id="WebApp_ID" version="3.1">

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    
    <context-param>
    
        <param-name>contextConfigLocation</param-name>
        <param-value>classpath:applicationContext.xml</param-value>
      <!--  <param-name>contextClass</param-name>
      <param-value>org.springframework.web.context.support.XmlWebApplicationContext</param-value>-->
    </context-param>
    
    <!-- 定义Spring MVC的前端控制器 -->
    <servlet>
        <servlet-name>HelloWord</servlet-name>
        <servlet-class>
            org.springframework.web.servlet.DispatcherServlet
        </servlet-class>
           <init-param>
              <param-name>contextConfigLocation</param-name>
              <param-value>classpath:config.xml</param-value>
            </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <!-- 让Spring MVC的前端控制器拦截所有请求 -->
    <servlet-mapping>
        <servlet-name>HelloWord</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
    <filter>
        <filter-name>characterEncodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>UTF-8</param-value>
        </init-param>
        <init-param>
            <param-name>forceEncoding</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>characterEncodingFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

</web-app>
```

tomcat运行首先加载如下ContextLoader.java中的

```java
static {
	// Load default strategy implementations from properties file.
	// This is currently strictly internal and not meant to be customized
	// by application developers.
	try {
        //默认加载ContextLoader.properties文件org.springframework.web.context.WebApplicationContext=org.springframework.web.context.support.XmlWebApplicationContext
		ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoader.class);
		defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
	}
	catch (IOException ex) {
		throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
	}
}
```
ContextLoaderListener

	/**
	 * 初始化WebApplicationContext
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}
```java
//初始化WebApplicationContext
public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
	if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
		throw new IllegalStateException(
				"Cannot initialize context because there is already a root application context present - " +
				"check whether you have multiple ContextLoader* definitions in your web.xml!");
	}

	servletContext.log("Initializing Spring root WebApplicationContext");
	Log logger = LogFactory.getLog(ContextLoader.class);
	if (logger.isInfoEnabled()) {
		logger.info("Root WebApplicationContext: initialization started");
	}
    //开始时间
	long startTime = System.currentTimeMillis();

	try {
		// Store context in local instance variable, to guarantee that
		// it is available on ServletContext shutdown.
		if (this.context == null) {
            //创建WebApplicationContext
			this.context = createWebApplicationContext(servletContext);
		}
		if (this.context instanceof ConfigurableWebApplicationContext) {
			ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) this.context;
			if (!cwac.isActive()) {
				// The context has not yet been refreshed -> provide services such as
				// setting the parent context, setting the application context id, etc
				if (cwac.getParent() == null) {
					// The context instance was injected without an explicit parent ->
					// determine parent for root web application context, if any.
					ApplicationContext parent = loadParentContext(servletContext);
					cwac.setParent(parent);
				}
                //配置和刷新WebApplicationContext
				configureAndRefreshWebApplicationContext(cwac, servletContext);
			}
		}
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);

		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		if (ccl == ContextLoader.class.getClassLoader()) {
			currentContext = this.context;
		}
		else if (ccl != null) {
			currentContextPerThread.put(ccl, this.context);
		}

		if (logger.isInfoEnabled()) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			logger.info("Root WebApplicationContext initialized in " + elapsedTime + " ms");
		}

		return this.context;
	}
	catch (RuntimeException | Error ex) {
		logger.error("Context initialization failed", ex);
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
		throw ex;
	}
}
```
```java
//创建WebApplicationContext
protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
    //用来决定采用哪种类型的WebApplicationContext
	Class<?> contextClass = determineContextClass(sc);
	if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
		throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
				"] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
	}
    //实例化WebApplicationContext
	return (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
}
```
```java
public static final String CONTEXT_CLASS_PARAM = "contextClass";

protected Class<?> determineContextClass(ServletContext servletContext) {
	//首先读取web 文件中的contextClass。如果参数获取为null的话，就直接采用org.springframework.web.context.support.XmlWebApplicationContext
    String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
	if (contextClassName != null) {
		try {
			return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
		}
		catch (ClassNotFoundException ex) {
			throw new ApplicationContextException(
					"Failed to load custom context class [" + contextClassName + "]", ex);
		}
	}
	else {
        //采用默认的org.springframework.web.context.support.XmlWebApplicationContext
		contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());
		try {
			return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			throw new ApplicationContextException(
					"Failed to load default context class [" + contextClassName + "]", ex);
		}
	}
}
```
```java
public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";	
public static final String CONTEXT_ID_PARAM = "contextId";
protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
	if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
		// The application context id is still set to its original default value
		// -> assign a more useful id based on available information
        //从web.xml获取配置的contextId
		String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
		if (idParam != null) {
			wac.setId(idParam);
		}
		else {
			// Generate default id...
			wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
					ObjectUtils.getDisplayString(sc.getContextPath()));
		}
	}
	//WebApplicationContext设置ServletContext .
	wac.setServletContext(sc);
    //获取web.xml配置contextConfigLocation
	String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM);
	if (configLocationParam != null) {
        //设置spring的xml文件
		wac.setConfigLocation(configLocationParam);
	}

	// The wac environment's #initPropertySources will be called in any case when the context
	// is refreshed; do it eagerly here to ensure servlet property sources are in place for
	// use in any post-processing or initialization that occurs below prior to #refresh
	//获取系统的环境参数
    ConfigurableEnvironment env = wac.getEnvironment();
	if (env instanceof ConfigurableWebEnvironment) {
        //初始化servletProperty
		((ConfigurableWebEnvironment) env).initPropertySources(sc, null);
	}

	customizeContext(sc, wac);
    //加载或刷新配置,调用AbstractApplicationContext.refresh()方法
	wac.refresh();
}
```
refresh()方法

```java
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// 准备刷新应用上下文，设置启动时间和活动标志以及资源
			prepareRefresh();
    // 获取BeanFactory， 默认创建DefaultListableBeanFactory
		ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

		// 准备Bean工厂,对BeanFactory进行一些设置
		prepareBeanFactory(beanFactory);

		try {
			// postProcessBeanFactory后处理beanFactory。时机是在所有的beanDenifition加载完成之后，bean实例化之前执行
			postProcessBeanFactory(beanFactory);

			// 实例化并调用所有注册的beanFactory后置处理器.
			invokeBeanFactoryPostProcessors(beanFactory);

			// 实例化和注册beanFactory中扩展了BeanPostProcessor的bean
			registerBeanPostProcessors(beanFactory);

			// 初始化国际化工具类MessageSource
			initMessageSource();

			// 初始化事件广播器
			initApplicationEventMulticaster();

			// Initialize other special beans in specific context subclasses.
			onRefresh();

			// 注册监听器
			registerListeners();

			// 实例化所有bean
			finishBeanFactoryInitialization(beanFactory);

			// Last step: publish corresponding event.
			finishRefresh();
		}

		catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Exception encountered during context initialization - " +
						"cancelling refresh attempt: " + ex);
			}

			// Destroy already created singletons to avoid dangling resources.
			destroyBeans();

			// Reset 'active' flag.
			cancelRefresh(ex);

			// Propagate exception to caller.
			throw ex;
		}

		finally {
			// Reset common introspection caches in Spring's core, since we
			// might not ever need metadata for singleton beans anymore...
			resetCommonCaches();
		}
	}
}
```
HttpServletBean的init()方法

```java
public final void init() throws ServletException {

	// 将Servlet中配置的参数封装到pvs变量中，requiredProperties为必须参数，如果没有配置将报异常
	PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
	if (!pvs.isEmpty()) {
		try {
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
			ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
			bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
            //模板方法，可以在子类调用，做一些初始化工作，
			initBeanWrapper(bw);
            //将配置的初始化值(如contextConfigLocation)设置到DispatcherServlet
			bw.setPropertyValues(pvs, true);
		}
		catch (BeansException ex) {
			if (logger.isErrorEnabled()) {
				logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
			}
			throw ex;
		}
	}

	// 模板方法，子类初始化的入口方法
	initServletBean();
}
```
FrameworkServlet()方法

```java
@Override
protected final void initServletBean() throws ServletException {
	getServletContext().log("Initializing Spring " + getClass().getSimpleName() + " '" + getServletName() + "'");
	if (logger.isInfoEnabled()) {
		logger.info("Initializing Servlet '" + getServletName() + "'");
	}
	long startTime = System.currentTimeMillis();

	try {
        //初始化WebApplicationContext
		this.webApplicationContext = initWebApplicationContext();
        //初始化FrameworkServlet()
		initFrameworkServlet();
	}
	catch (ServletException | RuntimeException ex) {
		logger.error("Context initialization failed", ex);
		throw ex;
	}

	if (logger.isDebugEnabled()) {
		String value = this.enableLoggingRequestDetails ?
				"shown which may lead to unsafe logging of potentially sensitive data" :
				"masked to prevent unsafe logging of potentially sensitive data";
		logger.debug("enableLoggingRequestDetails='" + this.enableLoggingRequestDetails +
				"': request parameters and headers will be " + value);
	}

	if (logger.isInfoEnabled()) {
		logger.info("Completed initialization in " + (System.currentTimeMillis() - startTime) + " ms");
	}
}
```
初始化WebApplicationContext方法

```java
protected WebApplicationContext initWebApplicationContext() {
	//获取rootContext,默认情况下为XmlWebApplicationContext
    WebApplicationContext rootContext =
			WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	WebApplicationContext wac = null;
//如果已经通过构造方法设置了webApplicationContext
	if (this.webApplicationContext != null) {
		// A context instance was injected at construction time -> use it
		wac = this.webApplicationContext;
		if (wac instanceof ConfigurableWebApplicationContext) {
			ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
			if (!cwac.isActive()) {
				// The context has not yet been refreshed -> provide services such as
				// setting the parent context, setting the application context id, etc
				if (cwac.getParent() == null) {
					// The context instance was injected without an explicit parent -> set
					// the root application context (if any; may be null) as the parent
					cwac.setParent(rootContext);
				}
				configureAndRefreshWebApplicationContext(cwac);
			}
		}
	}
	if (wac == null) {
	//当webApplicationContext已经存在ServletContext中时，通过配置在ServletcontextAttribute参数获取
		wac = findWebApplicationContext();
	}
	if (wac == null) {
		//如果webApplicationContext还没有创建，则创建一个
		wac = createWebApplicationContext(rootContext);
	}

	if (!this.refreshEventReceived) {
		// 当ContextRefreshedEvent事件没有触发时调用此方法
		synchronized (this.onRefreshMonitor) {
			onRefresh(wac);
		}
	}

	if (this.publishContext) {
		// 将ApplicationContext保存到ServletContext中
		String attrName = getServletContextAttributeName();
		getServletContext().setAttribute(attrName, wac);
	}

	return wac;
}
```
FrameworkServlet的createWebApplicationContext(rootContext);

```java
protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
    //获取创建类型
	Class<?> contextClass = getContextClass();
    //检查创建类型
	if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
		throw new ApplicationContextException(
				"Fatal initialization error in servlet with name '" + getServletName() +
				"': custom WebApplicationContext class [" + contextClass.getName() +
				"] is not of type ConfigurableWebApplicationContext");
	}
    //创建ConfigurableWebApplicationContext
	ConfigurableWebApplicationContext wac =
			(ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

	wac.setEnvironment(getEnvironment());
	wac.setParent(parent);
	String configLocation = getContextConfigLocation();
    //将设置的contextConfigLocation参数传给wac,默认传入的是WEB-INFO/[ServletName]-Servlet.xml
	if (configLocation != null) {
		wac.setConfigLocation(configLocation);
	}
	configureAndRefreshWebApplicationContext(wac);

	return wac;
}
```
configureAndRefreshWebApplicationContext(wac);方法



		protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
			if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
				// The application context id is still set to its original default value
				// -> assign a more useful id based on available information
				if (this.contextId != null) {
					wac.setId(this.contextId);
				}
				else {
					// Generate default id...
					wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
							ObjectUtils.getDisplayString(getServletContext().getContextPath()) + '/' + getServletName());
				}
			}
		wac.setServletContext(getServletContext());
		wac.setServletConfig(getServletConfig());
		wac.setNamespace(getNamespace());
		//添加监听ContextRefreshedEvent的监听器，当接收到消息时调用FrameworkServlet的onApplicationEvent()方法，最终调用 DispatcherServlet的onRefresh方法
		wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));
	
		// The wac environment's #initPropertySources will be called in any case when the context
		// is refreshed; do it eagerly here to ensure servlet property sources are in place for
		// use in any post-processing or initialization that occurs below prior to #refresh
		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(getServletContext(), getServletConfig());
		}
	
		postProcessWebApplicationContext(wac);
		applyInitializers(wac);
		wac.refresh();
	}
DispatcherServlet的onRefresh方法

```java
protected void onRefresh(ApplicationContext context) {
	initStrategies(context);
}
protected void initStrategies(ApplicationContext context) {
    //初始化上传文件解析器
	initMultipartResolver(context);
    //初始化本地解析器
	initLocaleResolver(context);
    //初始化主题解析器
	initThemeResolver(context);
    //初始化处理器映射器，将请求映射到处理器上
	initHandlerMappings(context);
    //初始化处理适配器
	initHandlerAdapters(context);
    //初始化异常处理器
	initHandlerExceptionResolvers(context);
    //初始化请求到视图名称解析器
	initRequestToViewNameTranslator(context);
    //初始化视图解析器
	initViewResolvers(context);
    //初始化flash映射管理器
	initFlashMapManager(context);
}
```
 initStrategies方法将在WebApplicationContext初始化后自动执行，自动扫描上下文的Bean,根据名称或类型匹配的机制查找自定义的组件，如果没有找到，则会装配spring默认的组件，org.springframework,web.servlet路径下的DispatcherServlet.properties 

```properties
 # Default implementation classes for DispatcherServlet's strategy interfaces. # Used as fallback when no matching beans are found in the DispatcherServlet context. # Not meant to be customized by application developers. 
 # 本地化解析器 org.springframework.web.servlet.LocaleResolver=org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver 
 # 主题解析器 org.springframework.web.servlet.ThemeResolver=org.springframework.web.servlet.theme.FixedThemeResolver 
 # 处理映射器 org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\    org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping 
 # 处理适配器 org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\    org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\    org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter 
 # 异常处理器 org.springframework.web.servlet.HandlerExceptionResolver=org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver,\    org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver,\    org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver 
 # 视图名称解析器 org.springframework.web.servlet.RequestToViewNameTranslator=org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator 
 # 视图解析器 org.springframework.web.servlet.ViewResolver=org.springframework.web.servlet.view.InternalResourceViewResolver 
 # FlashMap映射器 org.springframework.web.servlet.FlashMapManager=org.springframework.web.servlet.support.SessionFlashMapManager 
```

###### 本地化解析器 只允许一个实例

1、查找名为localeResolver、类型为LocaleResolver的Bean作为该类型组件
2、如果没有找到，则使用默认AcceptHeaderLocaleResolver

###### 主题解析器 只允许一个实例

1、查找名为themeResolver，类型为ThemeResolver的bean作为该类型的组件
2、如果没有找到，则使用默认的FixedThemeResolver

###### 处理器映射器 允许多个实例

1、如果detectAllHandlerMappings的属性为true,则根据类型匹配戒指查找上下文以及Spring容器中的所有类型为HandlerMapping的Bean，将他们作为该类型的组件
2、如果detectAllHandlerMappings的属性为false,则查找名为handlerMappping 类型为HandlerMapping的Bean作为该类型的组件
3、如果以上两种方式都没有，则使用BeanNameUrlHandlerMapping实现类创建该类型的组件

###### 处理器适配器 允许多个实例

1、如果detectAllHandlerAdapters的属性为true,则根据类型匹配机制查找上下文以及Spring容器中所有类型为HandlerAdapter的Bean,将他们作为该类型的组件
2、如果detectAllHandlerAdapters的属性为false,则查找名为handlerAdapter类型为handlerAdapter的bean作为该类型的组件
3、如果以上两种都没有找到，则使用默认配置文件中的三个实现类分别创建适配器，并将其添加到适配器列表中

###### 处理异常解析器 允许多个实例

1、如果detectAllHandlerExceptionResolvers的属性为true,则根据领匹配机制查找上下文以及Spring容器中所有类型为HandlerExceptionResolver的bean，将他们作为该类型的组件
2、如果detectAllHandlerExceptionResolvers的属性为false,则查找名为handlerExceptionResolver，类型为HandlerExceptionResolvers的bean作为该类型的组件
3、如果以上两种都没有找到，则使用默认配置文件中默认实现类

###### 视图名称解析器 只允许一个实例

1、查找名为viewNameTranslator类型为RequestToViewTranslator的bean作为该类型的组件
2、如果没有找到，则使用默认配置文件中的实现类DefaultRequestToViewNameTranslator

###### 视图解析器 允许多个实例

1、如果detectAllViewResolvers的属性为true,则根据类型匹配机制查找上下文以及Spring容器中的所有类型为ViewResolver的bean,将他们作为该类型的组件
2、如果detectAllViewResolvers的属性为false,则查找名为viewResolvers类型为ViewResolver的Bean作为类型的醉娜
3、如果以上两种方式都没有找到的话，就使用配置文件中的默认实现类InternalResourceViewResolver

###### 文件上传解析器 只允许一个实例

1、查找名为muliipartResolver类型为MuliipartResolver的Bean作为该类型组件
2、如果没有找到，则使用配置中加载的组件

###### FlashMap映射管理器

1、查找名为FlashMapManager 类型为SessionFlashMapManager的bean作为该类型的组件，用于管理FlashMqap，即数据默认存储在HttpSession中