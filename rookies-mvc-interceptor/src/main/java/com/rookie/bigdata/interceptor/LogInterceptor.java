package com.rookie.bigdata.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/3/21 17:53
 * 记录请求详细信息的日志
 */
public class LogInterceptor implements HandlerInterceptor {
    public static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);
    private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocal StartTime");

    private String getParamString(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            sb.append(e.getKey()).append("=");
            String[] value = e.getValue();
            if (value != null && value.length == 1) {
                sb.append(value[0]).append("\t");
            } else {
                sb.append(Arrays.toString(value)).append("\t");
            }
        }
        return sb.toString();
    }
    /**
     * 将ErrorStack转化为String.
     */
    public static String getStackTraceAsString(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        startTimeThreadLocal.set(startTime); // 线程绑定变量（该数据只有当前请求的线程可见）
        if (HandlerMethod.class.equals(handler.getClass())) {
            StringBuilder sb = new StringBuilder(1000);
            sb.append("-----------------------开始计时:").append(new SimpleDateFormat("hh:mm:ss.SSS").format(startTime))
                    .append("-------------------------------------\n");
            HandlerMethod h = (HandlerMethod) handler;
            sb.append("Controller: ").append(h.getBean().getClass().getName()).append("\n");
            sb.append("Method    : ").append(h.getMethod().getName()).append("\n");
            sb.append("Params    : ").append(getParamString(request.getParameterMap())).append("\n");
            sb.append("URI       : ").append(request.getRequestURI()).append("\n");
            LOGGER.debug(sb.toString());
            //System.out.println("Log"+sb.toString());
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        if (HandlerMethod.class.equals(handler.getClass())) {
            StringBuilder sb = new StringBuilder(1000);
            sb.append("CostTime  : ").append(executeTime).append("ms").append("\n");
            sb.append("-------------------------------------------------------------------------------");
            LOGGER.debug(sb.toString());
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 打印JVM信息。
        if (LOGGER.isDebugEnabled()) {
            long beginTime = startTimeThreadLocal.get();// 得到线程绑定的局部变量（开始时间）
            long endTime = System.currentTimeMillis(); // 2、结束时间

            // 如果controller报错，则记录异常错误
            if (ex != null) {
                LOGGER.debug("Controller异常: " + getStackTraceAsString(ex));
            }

            LOGGER.debug("计时结束：" + new SimpleDateFormat("hh:mm:ss.SSS").format(endTime) + " 耗时：" + (endTime - beginTime)
                    + " URI:" + request.getRequestURI());
            startTimeThreadLocal.remove();
        }
    }
}
