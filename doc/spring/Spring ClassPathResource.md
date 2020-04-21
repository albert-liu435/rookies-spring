### Spring ClassPathResource

 ClassPathResource用于加载资源文件，如果类路径资源文件位于文件系统中，支持解析为File,但是不用于JAR中的资源。

org.springframework.core.io.ClassPathResource位于Spring核心core下，用以表达类路径下的资源 。

其继承实现关系如下图：

![ClassPathResource](.\pic\ClassPathResource.png)

  ClasspathResource类的主要属性变量和构造方法如下 

```java
//资源文件路径
private final String path;
//通过类加载器加载资源
@Nullable
private ClassLoader classLoader;
//通过Class类加载资源文件
@Nullable
private Class<?> clazz;
```
通过资源路径和classLoader创建ClassPathResource对象，classLoader默认为null

```java
public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
	Assert.notNull(path, "Path must not be null");
    //规范会配置文件路径
	String pathToUse = StringUtils.cleanPath(path);
	if (pathToUse.startsWith("/")) {
		pathToUse = pathToUse.substring(1);
	}
	this.path = pathToUse;
    //获取类加载器
	this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
}
```
关于StringUtils，可以参考： [Spring中的SpringUtils](Spring中的SpringUtils.md) 

```java

// 通过类路径和给定的Class类创建ClassPathResource对象
public ClassPathResource(String path, @Nullable Class<?> clazz) {
	Assert.notNull(path, "Path must not be null");
    //规范化资源文件路径
	this.path = StringUtils.cleanPath(path);
	this.clazz = clazz;
}


```

getInputStream()方法,为给定的类路径资源打开一个InputStream

```java
public InputStream getInputStream() throws IOException {
		InputStream is;
		//判断clazz对象是否为null,不为null的话,获取InputStream对象
		if (this.clazz != null) {
			is = this.clazz.getResourceAsStream(this.path);
		}
		//判断classLoader对象是否为null,不为null的话,获取InputStream对象
		else if (this.classLoader != null) {
			is = this.classLoader.getResourceAsStream(this.path);
		}
		//获取InputStream对象
		else {
			is = ClassLoader.getSystemResourceAsStream(this.path);
		}
		//抛出异常
		if (is == null) {
			throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
		}
		return is;
	}
```

getURL()：返回底层类路径资源的URL

```java
@Override
public URL getURL() throws IOException {
	URL url = resolveURL();
	if (url == null) {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
	}
	return url;
}
```
如下为测试代码：

```java

        //Resource resource=new ClassPathResource("resource/conf.txt",Thread.currentThread().getContextClassLoader());

       // Resource resource=new ClassPathResource("resource/conf.txt",ResourceMain.class.getClassLoader());

        Resource resource=new ClassPathResource("resource/conf.txt");

        InputStream inputStream = resource.getInputStream();
        ByteArrayOutputStream bts=new ByteArrayOutputStream();
        int i;
        while ((i=inputStream.read())!=-1){
            bts.write(i);
        }

        System.out.println(bts.toString());
        System.out.println(resource);
        System.out.println(resource.getURI());
        System.out.println(resource.getURL());
        System.out.println(resource.getDescription());
        System.out.println(resource.getFile());
        System.out.println(resource.getFilename());
```
相关源码参考： [github](https://github.com/albert-liu435/rookies-spring/blob/master/rookie-spring-io/src/main/java/com/rookie/bigdata/resource/ResourceMain.java ) 

