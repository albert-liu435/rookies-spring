package com.rookie.bigdata.resource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/8 21:04
 */
public class ResourceMain {

    public static void main(String[] args) throws Exception{

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
    }
}
