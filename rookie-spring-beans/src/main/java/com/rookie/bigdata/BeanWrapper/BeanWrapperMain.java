package com.rookie.bigdata.BeanWrapper;

import com.rookie.bigdata.domain.User;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/3/22 21:38
 */
public class BeanWrapperMain {
    public static void main(String[] args) {
        User user=new User();
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(user);

        beanWrapper.setPropertyValue("username","zhangsan");
        System.out.println(user.getUsername());



    }

}
