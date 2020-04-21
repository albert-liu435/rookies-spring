package com.rookie.bigdata.aware;

import org.springframework.beans.factory.BeanNameAware;

/**
 * desc:让Bean获取自己在BeanFactory配置中的名字（根据情况是id或者name）。
 * Spring自动调用。并且会在Spring自身完成Bean配置之后，且在调用任何Bean生命周期回调（初始化或者销毁）方法之前就调用这个方法。
 * 换言之，在程序中使用BeanFactory.getBean(String beanName)之前，Bean的名字就已经设定好了。
 *
 * @author rookie
 * @version 1.0
 * @date 2020/4/17 8:17
 */
public class SpringBeanNameAware implements BeanNameAware {

    /**
     * 将配置文件中配置Bean的ID传入该方法内
     *
     * @param name
     */
    @Override
    public void setBeanName(String name) {
        System.out.println(name);
    }
}
