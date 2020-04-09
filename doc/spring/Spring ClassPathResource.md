Spring ClassPathResource

 org.springframework.core.io.ClassPathResource位于Spring核心core下，用以表达类路径下的资源 。

其继承实现关系如下图：

![ClassPathResource](.\pic\ClassPathResource.png)

  ClasspathResource类的属性变量和构造方法如下 


```java
//资源文件路径
private final String path;
//通过类加载器加载资源
@Nullable
private ClassLoader classLoader;
//通过Class类加载资源文件
@Nullable
private Class<?> clazz;
//通过类路径创建ClassPathResource对象
public ClassPathResource(String path) {
	this(path, (ClassLoader) null);
}

//通过类路径和classLoader创建ClassPathResource对象
public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
	Assert.notNull(path, "Path must not be null");
	String pathToUse = StringUtils.cleanPath(path);
	if (pathToUse.startsWith("/")) {
		pathToUse = pathToUse.substring(1);
	}
	this.path = pathToUse;
	this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
}

// 通过类路径和给定的Class类创建ClassPathResource对象
public ClassPathResource(String path, @Nullable Class<?> clazz) {
	Assert.notNull(path, "Path must not be null");
	this.path = StringUtils.cleanPath(path);
	this.clazz = clazz;
}

// 通过类路径和给定的ClassLoader或Class创建ClassPathResource对象，这个方法最终会弃用
@Deprecated
protected ClassPathResource(String path, @Nullable ClassLoader classLoader, @Nullable Class<?> clazz) {
	this.path = StringUtils.cleanPath(path);
	this.classLoader = classLoader;
	this.clazz = clazz;
}
```
以下面两段代码来查看ClassPathResource的执行过程

```java
    Resource resource=new ClassPathResource("conf.txt");

    InputStream inputStream = resource.getInputStream();
```
最终调用如下方法

```java
public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
    //路径不能为空
	Assert.notNull(path, "Path must not be null");
	String pathToUse = StringUtils.cleanPath(path);
    //判断是否以/开头
	if (pathToUse.startsWith("/")) {
		pathToUse = pathToUse.substring(1);
	}
    //赋值给path
	this.path = pathToUse;
    //获取默认的类加载器
	this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
}
```
cleanPath方法如下

```java
public static String cleanPath(String path) {
		if (!hasLength(path)) {
			return path;
		}
	//将windows的 //替换为通用的\
    String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);	
	// 文件是否含有.
	if (pathToUse.indexOf('.') == -1) {
		return pathToUse;
	}

	// Strip prefix from path to analyze, to not treat it as part of the
	// first path element. This is necessary to correctly parse paths like
	// "file:core/../core/io/Resource.class", where the ".." should just
	// strip the first "core" directory while keeping the "file:" prefix.
	int prefixIndex = pathToUse.indexOf(':');
	String prefix = "";
	if (prefixIndex != -1) {
		prefix = pathToUse.substring(0, prefixIndex + 1);
		if (prefix.contains(FOLDER_SEPARATOR)) {
			prefix = "";
		}
		else {
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}
	}
	if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
		prefix = prefix + FOLDER_SEPARATOR;
		pathToUse = pathToUse.substring(1);
	}

	String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
	LinkedList<String> pathElements = new LinkedList<>();
	int tops = 0;

	for (int i = pathArray.length - 1; i >= 0; i--) {
		String element = pathArray[i];
		if (CURRENT_PATH.equals(element)) {
			// Points to current directory - drop it.
		}
		else if (TOP_PATH.equals(element)) {
			// Registering top path found.
			tops++;
		}
		else {
			if (tops > 0) {
				// Merging path element with element corresponding to top path.
				tops--;
			}
			else {
				// Normal path element found.
				pathElements.add(0, element);
			}
		}
	}

	// Remaining top paths need to be retained.
	for (int i = 0; i < tops; i++) {
		pathElements.add(0, TOP_PATH);
	}
	// If nothing else left, at least explicitly point to current path.
	if (pathElements.size() == 1 && "".equals(pathElements.getLast()) && !prefix.endsWith(FOLDER_SEPARATOR)) {
		pathElements.add(0, CURRENT_PATH);
	}

	return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
}
```
当调用resource.getInputStream()时，调用如下方法

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

