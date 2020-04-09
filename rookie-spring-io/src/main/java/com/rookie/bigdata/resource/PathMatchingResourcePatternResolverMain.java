package com.rookie.bigdata.resource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/9 21:54
 */
public class PathMatchingResourcePatternResolverMain {

    public static void main(String[] args) throws Exception{

        ResourcePatternResolver resolver=new PathMatchingResourcePatternResolver();

        Resource resource [] = resolver.getResources("classpath*:**/*.txt");
        for (Resource resource1 : resource) {
            System.out.println(resource1.getDescription());
        }


    }
}
