package com.rookie.bigdata.factory.InitializingBean;

import org.springframework.beans.factory.InitializingBean;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/6/27 12:12
 */
public class SpringInitializingBean implements InitializingBean {


    public void start() {
        System.out.println("执行start方法");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("执行afterPropertiesSet 方法");
    }
}
