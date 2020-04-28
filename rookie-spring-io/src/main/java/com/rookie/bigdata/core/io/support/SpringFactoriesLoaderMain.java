package com.rookie.bigdata.core.io.support;

import org.springframework.beans.BeanInfoFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * @ClassName SpringFactoriesLoader
 * @Description SpringFactoriesLoader
 * @Author
 * @Date 2020/4/28 18:00
 * @Version 1.0
 */
public class SpringFactoriesLoaderMain {

    public static void main(String[] args) {

       // SpringFactoriesLoader springFactoriesLoader=new SpringFactoriesLoader();

        List<String> applicationContextInitializers = SpringFactoriesLoader.loadFactoryNames(BeanInfoFactory.class, ClassUtils.getDefaultClassLoader());
        for (String applicationContextInitializer : applicationContextInitializers) {
            System.out.println(applicationContextInitializer);
        }


    }
}
