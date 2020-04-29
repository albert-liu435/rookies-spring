Spring AntPathMatcher

AntPathMatcher是用来对资源路径或者url的字符串做匹配使用的。采用的是Ant风格的格式

Ant风格的资源地址支持3中匹配

- ？：匹配文件名中的一个字符

- *：匹配文件中的任意字符

- **：匹配多层路径

  如下示例：

  classpath:com/con?.xml:匹配com路径下的com/conf.xml、com/cont.xml等文件

  classpath:com/*.xml:匹配com路径下的所有的xml文件

  classpath:com/**/a.xml：匹配com路径下其他文件夹下的所有的a.xml文件

AntPathMatcher的主要属性如下：


```java
//默认路径分隔符
public static final String DEFAULT_PATH_SEPARATOR = "/";

private static final int CACHE_TURNOFF_THRESHOLD = 65536;
//正则表达式
private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?}");
//通配符
private static final char[] WILDCARD_CHARS = { '*', '?', '{' };
private String pathSeparator;

private PathSeparatorPatternCache pathSeparatorPatternCache;

private boolean caseSensitive = true;
//默认不
private boolean trimTokens = false;
//用来定义是否缓存分词
@Nullable
private volatile Boolean cachePatterns;
//用来缓存规则的分词
private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap<>(256);

final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<>(256);
```
其中Spring中用到最多的方法就是match()方法啦，

```java
//用来匹配路径字符串是否满足给定的规则
@Override
public boolean match(String pattern, String path) {
	return doMatch(pattern, path, true, null);
}
```
```java
protected boolean doMatch(String pattern, @Nullable String path, boolean fullMatch,
		@Nullable Map<String, String> uriTemplateVariables) {
	//判断path是null 或者pattern和path的首字符是否同时为设置的分隔符，如果为null或者不一致则直接返回false
	if (path == null || path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
		return false;
	}
	//对规则进行分词
	String[] pattDirs = tokenizePattern(pattern);
	if (fullMatch && this.caseSensitive && !isPotentialMatch(path, pattDirs)) {
		return false;
	}

	String[] pathDirs = tokenizePath(path);
	int pattIdxStart = 0;
	int pattIdxEnd = pattDirs.length - 1;
	int pathIdxStart = 0;
	int pathIdxEnd = pathDirs.length - 1;

	// Match all elements up to the first **
	while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
		String pattDir = pattDirs[pattIdxStart];
		if ("**".equals(pattDir)) {
			break;
		}
		if (!matchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
			return false;
		}
		pattIdxStart++;
		pathIdxStart++;
	}

	if (pathIdxStart > pathIdxEnd) {
		// Path is exhausted, only match if rest of pattern is * or **'s
		if (pattIdxStart > pattIdxEnd) {
			return (pattern.endsWith(this.pathSeparator) == path.endsWith(this.pathSeparator));
		}
		if (!fullMatch) {
			return true;
		}
		if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(this.pathSeparator)) {
			return true;
		}
		for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
			if (!pattDirs[i].equals("**")) {
				return false;
			}
		}
		return true;
	}
	else if (pattIdxStart > pattIdxEnd) {
		// String not exhausted, but pattern is. Failure.
		return false;
	}
	else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
		// Path start definitely matches due to "**" part in pattern.
		return true;
	}

	// up to last '**'
	while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
		String pattDir = pattDirs[pattIdxEnd];
		if (pattDir.equals("**")) {
			break;
		}
		if (!matchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
			return false;
		}
		pattIdxEnd--;
		pathIdxEnd--;
	}
	if (pathIdxStart > pathIdxEnd) {
		// String is exhausted
		for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
			if (!pattDirs[i].equals("**")) {
				return false;
			}
		}
		return true;
	}

	while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
		int patIdxTmp = -1;
		for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
			if (pattDirs[i].equals("**")) {
				patIdxTmp = i;
				break;
			}
		}
		if (patIdxTmp == pattIdxStart + 1) {
			// '**/**' situation, so skip one
			pattIdxStart++;
			continue;
		}
		// Find the pattern between padIdxStart & padIdxTmp in str between
		// strIdxStart & strIdxEnd
		int patLength = (patIdxTmp - pattIdxStart - 1);
		int strLength = (pathIdxEnd - pathIdxStart + 1);
		int foundIdx = -1;

		strLoop:
		for (int i = 0; i <= strLength - patLength; i++) {
			for (int j = 0; j < patLength; j++) {
				String subPat = pattDirs[pattIdxStart + j + 1];
				String subStr = pathDirs[pathIdxStart + i + j];
				if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
					continue strLoop;
				}
			}
			foundIdx = pathIdxStart + i;
			break;
		}

		if (foundIdx == -1) {
			return false;
		}

		pattIdxStart = patIdxTmp;
		pathIdxStart = foundIdx + patLength;
	}

	for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
		if (!pattDirs[i].equals("**")) {
			return false;
		}
	}

	return true;
}
```
对规则进行分词

```java
//对规则进行分词
protected String[] tokenizePattern(String pattern) {
	String[] tokenized = null;
	Boolean cachePatterns = this.cachePatterns;
	//当cachePatterns为null或者为true时，表示缓存分词，需要首先从map对象中获取
	if (cachePatterns == null || cachePatterns.booleanValue()) {
	//从缓存中获取分词
		tokenized = this.tokenizedPatternCache.get(pattern);
	}
	//缓存中不存在该分词是
	if (tokenized == null) {
		tokenized = tokenizePath(pattern);
		//当cachePatterns为null且tokenizedPatternCache对象中缓存的对象大于等于65536时,将缓存对象清空
		if (cachePatterns == null && this.tokenizedPatternCache.size() >= CACHE_TURNOFF_THRESHOLD) {
			// Try to adapt to the runtime situation that we're encountering:
			// There are obviously too many different patterns coming in here...
			// So let's turn off the cache since the patterns are unlikely to be reoccurring.
			//缓存对象清空
			deactivatePatternCache();
			return tokenized;
		}
		//将分词放入缓存中
		if (cachePatterns == null || cachePatterns.booleanValue()) {
			this.tokenizedPatternCache.put(pattern, tokenized);
		}
	}
	return tokenized;
}
```

```java
//匹配字符串path是否以pattern开头
@Override
public boolean matchStart(String pattern, String path) {
   return doMatch(pattern, path, false, null);
}
```

