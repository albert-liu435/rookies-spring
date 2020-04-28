package org.springframework.core.io.support;

import org.junit.Test;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/28 20:57
 */
public class SpringFactoriesLoaderTest {

    @Test
    public void testLoadFactoryNames() {
        //获取所有META-INF/spring.factories中的value值
        List<String> applicationContextInitializers = SpringFactoriesLoader.loadFactoryNames(BeanInfoFactory.class, ClassUtils.getDefaultClassLoader());
        for (String applicationContextInitializer : applicationContextInitializers) {
            System.out.println(applicationContextInitializer);
        }
    }

    @Test
    public void testLoadFactories() {
        //实例化所有在META-INF/spring.factories配置的且实现BeanInfoFactory接口的类
        List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, ClassUtils.getDefaultClassLoader());

        for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
            System.out.println(beanInfoFactory);
        }
    }

}
