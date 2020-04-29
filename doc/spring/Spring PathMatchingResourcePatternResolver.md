Spring PathMatchingResourcePatternResolver

PathMatchingResourcePatternResolver是ResourcePatternResolver的实现来,用来解析一个或多个匹配资源的指定资源位置路径,资源路径可能是唯一的，也可能是包含classpath*:前缀，或者匹配Ant风格的特殊表达式。
比如 file:C:/context.xml,classpathZ:/context.xml,/WEB-INF/context.xml,将会返回一个resource实例对象

继承体系参考：[资源访问](https://www.cnblogs.com/haizhilangzi/p/11123387.html)

其主要属性如下

```java
//主要用于程序运行状态中，动态获取方法的信息
@Nullable
private static Method equinoxResolveMethod;
//资源加载类
private final ResourceLoader resourceLoader;
//用来匹配Ant风格的匹配规则
private PathMatcher pathMatcher = new AntPathMatcher();
```

```java
//根据给定的资源路径或Ant风格的路径匹配符合的资源文件加载为Resource对象
@Override
public Resource[] getResources(String locationPattern) throws IOException {
   Assert.notNull(locationPattern, "Location pattern must not be null");
    //判断是否以classpath*开头
   if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
      // a class path resource (multiple resources for same name possible)
      if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
         //根据Ant风格给定的字符串路径匹配所有的资源对象
         return findPathMatchingResources(locationPattern);
      }
      else {
         // 查找所classpath路径下的符合规则的资源
         return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
      }
   }
   else {
      // 如果路径资源以war:开头，则执行如下加载资源的方法
      int prefixEnd = (locationPattern.startsWith("war:") ? locationPattern.indexOf("*/") + 1 :
            locationPattern.indexOf(':') + 1);
      if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
         // a file pattern
         return findPathMatchingResources(locationPattern);
      }
      else {
         // a single resource with the given name
         return new Resource[] {getResourceLoader().getResource(locationPattern)};
      }
   }
}
```

测试如下：

```java
PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

@Test
public void testfindPathMatchingResources() throws Exception {

    Resource[] resource = pathMatchingResourcePatternResolver.getResources("classpath*:/config/*.txt");

    for (int i = 0; i < resource.length; i++) {
        String filename = resource[i].getFilename();
        URL url = resource[i].getURL();
        System.out.println(url);
    }
    // System.out.println(resource);
}
```

