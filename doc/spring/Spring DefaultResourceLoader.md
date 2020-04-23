Spring DefaultResourceLoader

Spring DefaultResourceLoader继承ResourceLoader接口，用来加载资源， 通过ResourceLoader,给定其可以接受的资源路径，我们可以获得对应资源的Resource对象，然后进行进行相应的资源访问 。

Spring提供了一个默认的实现类DefaultResourceLoader，可以用来加载classpath或者文件系统中的某个文件，也可以采用URL的形式加载某个网络的资源。

ResourceLoader接口定义如下：

```java
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:". */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;
	/**
	 * 根据给定的资源路径,返回响应的Resource资源对象
	 * 支持如下三种形式：
	 *"file:C:/test.dat".
	 *"classpath:test.dat".
	 *"WEB-INF/test.dat".
	
	 */
	Resource getResource(String location);
	/**
	 * 获取资源类加载器
	 */
	@Nullable
	ClassLoader getClassLoader();

}
```

DefaultResourceLoader类中定义方法及属性如下：

```java
public class DefaultResourceLoader implements ResourceLoader {
	@Nullable
	private ClassLoader classLoader;
	private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<>(4);
	private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap<>(4);

	//构造方法中获取默认个类加载器
	public DefaultResourceLoader() {
		this.classLoader = ClassUtils.getDefaultClassLoader();
	}

	public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setClassLoader(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	//返回类加载器
	@Override
	@Nullable
	public ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
	}

	/**
	 * 注册资源解析器,会覆盖默认的资源规则
	 */
	public void addProtocolResolver(ProtocolResolver resolver) {
		Assert.notNull(resolver, "ProtocolResolver must not be null");
		this.protocolResolvers.add(resolver);
	}

	/**
	 * Return the collection of currently registered protocol resolvers,
	 * allowing for introspection as well as modification.
	 * @since 4.3
	 */
	public Collection<ProtocolResolver> getProtocolResolvers() {
		return this.protocolResolvers;
	}

	/**
	 * Obtain a cache for the given value type, keyed by {@link Resource}.
	 * @param valueType the value type, e.g. an ASM {@code MetadataReader}
	 * @return the cache {@link Map}, shared at the {@code ResourceLoader} level
	 * @since 5.0
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<Resource, T> getResourceCache(Class<T> valueType) {
		return (Map<Resource, T>) this.resourceCaches.computeIfAbsent(valueType, key -> new ConcurrentHashMap<>());
	}

	/**
	 * 清空资源缓存
	 */
	public void clearResourceCaches() {
		this.resourceCaches.clear();
	}


	//加载资源并返回Resource对象
	@Override
	public Resource getResource(String location) {
		Assert.notNull(location, "Location must not be null");
		//如果有ProtocolResolver,会优先调用
		for (ProtocolResolver protocolResolver : getProtocolResolvers()) {
			Resource resource = protocolResolver.resolve(location, this);
            //根据返回值是否为null来判断是否解决 资源加载的问题
			if (resource != null) {
				return resource;
			}
		}
		//资源路径是否以/开头
		if (location.startsWith("/")) {
			return getResourceByPath(location);
		}//判断资源路径是否以classpath:开头
		else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		}
		else {
			try {
				// T尝试将资源路径转化为URL形式去加载
				URL url = new URL(location);
				return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
			}
			catch (MalformedURLException ex) {
				// 当上面无法转换为 URL的形式事，尝试从相对路径中加载资源
				return getResourceByPath(location);
			}
		}
	}

	//从相对路径中下载资源
	protected Resource getResourceByPath(String path) {
		return new ClassPathContextResource(path, getClassLoader());
	}


	/**
	 * DefaultResourceLoader内部类类，用来加载相对路径的资源，可以参看ClassPathResource
	 https://www.cnblogs.com/haizhilangzi/p/12717368.html
	 */
	protected static class ClassPathContextResource extends ClassPathResource implements ContextResource {

		public ClassPathContextResource(String path, @Nullable ClassLoader classLoader) {
			super(path, classLoader);
		}

		@Override
		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public Resource createRelative(String relativePath) {
			String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
			return new ClassPathContextResource(pathToUse, getClassLoader());
		}
	}

}

```

关于ProtocolResolver接口的详细使用，请参考 [Spring ProtocolResolver](Spring ProtocolResolver接口.md) 

测试代码

```java
        ResourceLoader resourceLoader = new DefaultResourceLoader();


        //获取classpath:上的资源
//        Resource resource = resourceLoader.getResource(
//                "classpath:resource/conf.txt");

        //获取相对路径上的某个资源，相当于classpath:
//        Resource resource = resourceLoader.getResource(
//                "resource/conf.txt");
        //同样是获取classpath:上的资源
//        Resource resource = resourceLoader.getResource(
//                "/resource/conf.txt");

        //获取网络上的资源
        Resource resource = resourceLoader.getResource(
                "https://www.cnblogs.com/haizhilangzi/p/12717368.html");


        System.out.println(resource.getFilename());
        System.out.println(resource.contentLength());
        System.out.println(resource.getURL());
        System.out.println(resource.getURI());

        InputStream inputStream = resource.getInputStream();

        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = inputStream.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        System.out.println(out);
```

