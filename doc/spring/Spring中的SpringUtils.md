#### Spring中的SpringUtils

Spring提供的工具类，主要用于框架内部使用，这个类提供了一些简单的方法，并且提供了易于使用的方法在分割字符串，如CSV字符串，以及集合和数组。

StringUtils提供常用的方法如下：

判断对象对象是否为null或者空字符串

```java
public static boolean isEmpty(@Nullable Object str) {
	return (str == null || "".equals(str));
}
```
判断给的序列是否为空或者length为0

	public static boolean hasLength(@Nullable CharSequence str) {
		return (str != null && str.length() > 0);
	}
	
		public static boolean hasLength(@Nullable String str) {
			return (str != null && !str.isEmpty());
		}
判断字符串是否以某个字符串开头

```java
public static boolean startsWithIgnoreCase(@Nullable String str, @Nullable String prefix) {
	return (str != null && prefix != null && str.length() >= prefix.length() &&
			str.regionMatches(true, 0, prefix, 0, prefix.length()));
}
```
判断字符串是否以某个字符串结尾

```java
public static boolean endsWithIgnoreCase(@Nullable String str, @Nullable String suffix) {
	return (str != null && suffix != null && str.length() >= suffix.length() &&
			str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length()));
}
```
用另一个字符串替换字符串中出现的所有子字符串

	public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
			if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
				return inString;
			}
			//oldPattern字符串第一次出现的位置
			int index = inString.indexOf(oldPattern);
			if (index == -1) {
				// no occurrence -> can return input as-is
				return inString;
			}
			//字符串长度
		int capacity = inString.length();
		if (newPattern.length() > oldPattern.length()) {
			capacity += 16;
		}
		StringBuilder sb = new StringBuilder(capacity);
	
		int pos = 0;  // our position in the old string
		int patLen = oldPattern.length();
		while (index >= 0) {
			sb.append(inString, pos, index);
			sb.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
	
		// append any characters to the right of a match
		sb.append(inString, pos, inString.length());
		return sb.toString();
	}
根据给定的路径规范化路径

```java
public static String cleanPath(String path) {
		if (!hasLength(path)) {
			return path;
		}
    //用新字符串替换旧字符串
	String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
	// Shortcut if there is no work to do
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