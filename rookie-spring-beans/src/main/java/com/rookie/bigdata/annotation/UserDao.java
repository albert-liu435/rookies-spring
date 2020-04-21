package com.rookie.bigdata.annotation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/14 7:11
 */
@Repository
@Qualifier(value = "userDao")
public class UserDao {

    public UserDao(){
        System.out.println("实例化userDao");
    }
}
