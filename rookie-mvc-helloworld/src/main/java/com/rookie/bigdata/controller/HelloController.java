package com.rookie.bigdata.controller;

import com.rookie.bigdata.service.HelloService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/3/22 20:32
 */

public class HelloController implements Controller {
    private static final Log logger = LogFactory
            .getLog(HelloController.class);

    @Autowired
    private HelloService helloService;

    /**
     * handleRequest是Controller接口必须实现的方法。
     * 该方法的参数是对应请求的HttpServletRequest和HttpServletResponse。
     * 该方法必须返回一个包含视图路径或视图路径和模型的ModelAndView对象。
     */
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        logger.info("handleRequest 被调用");
        // 创建准备返回的ModelAndView对象，该对象通常包含了返回视图的路径、模型的名称以及模型对象
        ModelAndView mv = new ModelAndView();
        // 添加模型数据 可以是任意的POJO对象
        mv.addObject("message", "Hello World!");
        // 设置逻辑视图名，视图解析器会根据该名字解析到具体的视图页面
        mv.setViewName("/index.jsp");
        // 返回ModelAndView对象。
        System.out.println(helloService);

        helloService.requestContextHolder();

        return mv;
    }

}
