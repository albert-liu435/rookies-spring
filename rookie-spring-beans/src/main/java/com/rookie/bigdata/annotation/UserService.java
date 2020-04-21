package com.rookie.bigdata.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/14 7:11
 */
@Service
public class UserService {

    @Autowired(required =true)
    @Qualifier(value = "userDao")
    @Lazy
    private UserDao userDao;

}
