package com.rookie.bigdata.core.io;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/23 22:51
 */
public class MyProtocolResolverMain {

    public static void main(String[] args) throws Exception{

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

    }
}
