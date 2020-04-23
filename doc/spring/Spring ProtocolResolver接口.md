Spring ProtocolResolver接口

ProtocolResolver是一个策略接口，可以用于自定义协议解析， 比如spring就有一个 “classpath:”开头的特定协议（但是spring并不是自定义ProtocolResolver 实现来完成这个功能的）

```java
@FunctionalInterface
public interface ProtocolResolver {

	@Nullable
	Resource resolve(String location, ResourceLoader resourceLoader);

}
```

@FunctionalInterface:声明该接口是一个函数式接口,主要用于编译级错误检查，加上该注解，该接口中只能定义唯一一个方法。

 spring提供了ProtocolResolver机制，用于匹配自定义的文件schema来加载文件；而且不干扰ResourceLoader的机制，最重要的是它会添加到spring环境下的所有的loader中。我们只需要扩展一个ProtocolResolver类，并将它在合适的实际加入到ResourceLoader即可，此后加载properties文件时我们的ProtocolResolver总会被执行 

具体用法如下：

自定义MyProtocolResolver,加载资源时，当我们输入的资源路径以path:开头时，就会加载相对路径下的config/下相应的资源。

```jaa
public class MyProtocolResolver implements ProtocolResolver {

    public static final String PATH = "path:";
    
    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (!location.startsWith(PATH))
            return null;
        String realPath = location.substring(5);
        String classPath = "classpath:config/" + realPath;
    
        return resourceLoader.getResource(classPath);
    }

}
```

测试：

```java
        DefaultResourceLoader resourceLoader=new DefaultResourceLoader();
        resourceLoader.addProtocolResolver(new MyProtocolResolver());
        Resource resource = resourceLoader.getResource("path:config.txt");
        InputStream inputStream = resource.getInputStream();
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = inputStream.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        System.out.println(out);
```

由此我们可以通过实现ProtocolResolver接口来定义我们自己的加载资源路径，也可以自定义自己加载资源路径的优先级。

参考： [https://www.iteye.com/blog/shift-alt-ctrl-2442047]( https://www.iteye.com/blog/shift-alt-ctrl-2442047 ) 

 [https://blog.csdn.net/yuxiuzhiai/article/details/79080154](https://blog.csdn.net/yuxiuzhiai/article/details/79080154 ) 

代码：[github](https://github.com/albert-liu435/rookies-spring/blob/master/rookie-spring-io/src/main/java/com/rookie/bigdata/core/io/MyProtocolResolverMain.java)

