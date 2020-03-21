package com.rookie.bigdata.interceptor;

import org.springframework.core.NamedThreadLocal;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/3/21 17:35
 * 实现统计程序应用性能的拦截器
 */
public class TimeSpendInterceptor implements HandlerInterceptor {

    //为了线程安全，统计应用花费的时间
    private NamedThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<>("TimeSpend-Watch");


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        //线程绑定变量，只能被当前线程看见
        startTimeThreadLocal.set(startTime);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("执行postHandle");
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long endTime = System.currentTimeMillis();
        //得到当前线程时间执行方法的开始时间
        Long startTime = startTimeThreadLocal.get();
        //计算总耗时时间
        long consumeTime=endTime=startTime;
        System.out.println(Thread.currentThread().getName()+"当前总耗时: "+consumeTime);

    }
}
