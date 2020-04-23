package com.rookie.bigdata.core.io;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.InputStream;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/22 22:31
 */
public class DefaultResourceLoaderMain {
    public static void main(String[] args) throws Exception {
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

    }
}
