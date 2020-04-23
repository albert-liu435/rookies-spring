package com.rookie.bigdata.core.io;

import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/23 22:45
 */
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
