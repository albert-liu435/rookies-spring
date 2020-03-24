package com.rookie.bigdata.service;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Locale;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/3/22 20:32
 * LocaleContextHolder
 * RequestContextHolder 用法，即可以在service层直接获取request的信息，而不需要从Controller中传递过来
 */
@Service
public class HelloService {

    public HelloService() {
        System.out.println("实例化service");
    }


    public void requestContextHolder() {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            System.out.println(s);
        }

        Locale locale = LocaleContextHolder.getLocale();
        String language = locale.getLanguage();
        System.out.println(language);
    }
}