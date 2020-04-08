SpringMVC接受请求的处理流程



![springmvc](.\pic\springmvc.png)

1. 用户向服务端发送请求，请求被前端控制器DispatcherServlet截获
2. DispatcherServlet对请求的URL进行解析，得到URI,然后根据URI调用HandlerMapper获取该Handler配置的所有相关的对象，包括Handler对象以及Handler对象对应的拦截器，这些对象会被封装到一个 HandlerExecutionChain对象中返回
3. DispatcherServlet根据获得的Handler，选择一个合适的HandlerAdapter，HandlerAdapter会处理多种handler，调用handler实际处理请求的方法
4. 提取请求中的数据模型，开始执行Handler（Controller），在填充Handler的入参过程中根据配置，Spring做了一些如下工作
   1. 消息转换。将请求消息（如JSON,XML等数据）转换成一个对象，将对象转换为指定的响应消息
   2. 数据转换 。 对请求消息进行数据转换，如String转换为Integer，Double等
   3. 数据格式化。对请求消息进行格式化。如将字符串转换为格式化数字或格式化日期
   4. 数据验证。验证数据的有效性，验证结果存储在BindingResult或Error中
5. handler执行完之后，向DispatcherServlet返回一个ModelAndVIew对象,ModelAndVIew对象中应该包含视图名或视图名和模型
6. 根据返回的ModelAndView对象那个，选择一个合适的ViewResolver（视图解析器），返回给DispatcherServlet
7. ViewResolver结合Model和View来渲染视图
8. 将视图渲染结果返回给客户端

Springmvc接收请求源码分析

DispatcherServlet继承体系

![DispatcherServlet](.\pic\DispatcherServlet.png)

关于EnvironmentAware，EnvironmentCapable和ApplicationContextAware的作用可以参考 [https://www.cnblogs.com/haizhilangzi/p/12610152.html]( https://www.cnblogs.com/haizhilangzi/p/12610152.html ) 

首先http请求到达FrameworkServlet中的service方法

```java
@Override
protected void service(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
//获取request请求的请求method
	HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
	if (httpMethod == HttpMethod.PATCH || httpMethod == null) {
		processRequest(request, response);
	}
	else {
		super.service(request, response);
	}
}
```
处理get请求的方法

```java
@Override
protected final void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

	processRequest(request, response);
}
```
processRequest方法：首先获取LocaleContextHolder和RequestContextHolder中原来保存的LocaleContext和RequestAttributes并设置到previousLocaleContext和previousAttributes临时属性，然后调用buildLocaleContext和buildRequestAttributes方法获取当前请求的LocaleContext和RequestAttributes，并通过initContextHolders方法将它们设置到LocaleContextHolder和RequestContextHolder中，接着使用request拿到异步处理管理器并设置了拦截器，然后调用doService方法。

```java
protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
	long startTime = System.currentTimeMillis();
	Throwable failureCause = null;
//获取LocaleContextHolder中原来保存的LocaleContext
	LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
    //获取当前请求的LocaleContext
	LocaleContext localeContext = buildLocaleContext(request);
	//获取RequestContextHolder中原来保存的RequestAttributes
	RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
	//获取当前请求的ServletRequestAttributes
    ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

	WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
	asyncManager.registerCallableInterceptor(FrameworkServlet.class.getName(), new RequestBindingInterceptor());
//将当前请求的LocaleContext和ServletRequestAttributes设置到LocaleContextHolder和RequestContextHolder
	initContextHolders(request, localeContext, requestAttributes);

	try {
        //实际处理请求的方法
		doService(request, response);
	}
	catch (ServletException | IOException ex) {
		failureCause = ex;
		throw ex;
	}
	catch (Throwable ex) {
		failureCause = ex;
		throw new NestedServletException("Request processing failed", ex);
	}

	finally {
        //恢复原来的LocaleContext和ServletRequestAttributes到LocaleContextHolder和RequestContextHolder
		resetContextHolders(request, previousLocaleContext, previousAttributes);
		if (requestAttributes != null) {
			requestAttributes.requestCompleted();
		}
		logResult(request, response, failureCause, asyncManager);
        //发布ServletRequestHandledEvent消息
		publishRequestHandledEvent(request, response, startTime, failureCause);
	}
}
```
DispatcherServlet中的doService()方法

```java
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logRequest(request);	
	// 当include请求时对request的Attribute做快照备份
	Map<String, Object> attributesSnapshot = null;
	if (WebUtils.isIncludeRequest(request)) {
		attributesSnapshot = new HashMap<>();
		Enumeration<?> attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			if (this.cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
				attributesSnapshot.put(attrName, request.getAttribute(attrName));
			}
		}
	}

	// 对request设置一些属性
	request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
	request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
	request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
	request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());

	if (this.flashMapManager != null) {
		FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
		if (inputFlashMap != null) {
			request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
		}
		request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
		request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);
	}

	try {
		doDispatch(request, response);
	}
	finally {
		if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
			// 还原request快照属性
			if (attributesSnapshot != null) {
				restoreAttributesAfterInclude(request, attributesSnapshot);
			}
		}
	}
}
```
doDispatch(request, response)方法

doDispatch的主要任务：

- 根据request找到Handler
- 根据Handler找到对应的HandlerAdapter
- 用HandlerAdapter处理Handler
- 调用processDispatchResult方法处理上面处理之后的结果

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
	HttpServletRequest processedRequest = request;
	HandlerExecutionChain mappedHandler = null;
	boolean multipartRequestParsed = false;

	WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

	try {
		ModelAndView mv = null;
		Exception dispatchException = null;

		try {
            //检查是不是上传请求
			processedRequest = checkMultipart(request);
			multipartRequestParsed = (processedRequest != request);

			// 根据request找到Handler
			mappedHandler = getHandler(processedRequest);
			if (mappedHandler == null) {
				noHandlerFound(processedRequest, response);
				return;
			}

			// 根据Handler找到HandlerAdapter
			HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

			// 处理GET、HEAD请求的Last-Modified
			String method = request.getMethod();
			boolean isGet = "GET".equals(method);
			if (isGet || "HEAD".equals(method)) {
				long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
				if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
					return;
				}
			}
			//执行相应Interceptor的preHandle
			if (!mappedHandler.applyPreHandle(processedRequest, response)) {
				return;
			}

			// HandlerAdapter使用Handler处理请求
			mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
			//如果需要异步，直接返回
			if (asyncManager.isConcurrentHandlingStarted()) {
				return;
			}
			//当view为空时，根据request设置默认的view
			applyDefaultViewName(processedRequest, mv);
            //执行相应Interceptor的postHandle
			mappedHandler.applyPostHandle(processedRequest, response, mv);
		}
		catch (Exception ex) {
			dispatchException = ex;
		}
		catch (Throwable err) {
			// As of 4.3, we're processing Errors thrown from handler methods as well,
			// making them available for @ExceptionHandler methods and other scenarios.
			dispatchException = new NestedServletException("Handler dispatch failed", err);
		}
        //处理返回结果。包括处理异常、渲染页面、发出完成通知触发Interceptor的afterCompletion
		processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
	}
	catch (Exception ex) {
		triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
	}
	catch (Throwable err) {
		triggerAfterCompletion(processedRequest, response, mappedHandler,
				new NestedServletException("Handler processing failed", err));
	}
	finally {
        //判断是否执行异步请求
		if (asyncManager.isConcurrentHandlingStarted()) {
			// 删除上传请求的资源
			if (mappedHandler != null) {
				mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
			}
		}
		else {
			// Clean up any resources used by a multipart request.
			if (multipartRequestParsed) {
				cleanupMultipart(processedRequest);
			}
		}
	}
}
```
防守打法

发送到

防守打法

电风扇

方式