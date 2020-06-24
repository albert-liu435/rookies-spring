package com.rookie.bigdata.jetty.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @ClassName HelloHandler
 * @Description HelloHandler
 * @Author liuxili
 * @Date 2020/6/18 14:46
 * @Version 1.0
 */

public class HelloHandler extends AbstractHandler {
    final String greeting;
    final String body;

    public HelloHandler() {
        this("Hello World");
    }

    public HelloHandler(String greeting) {
        this(greeting, null);
    }

    public HelloHandler(String greeting, String body) {
        this.greeting = greeting;
        this.body = body;
    }

    /**
     * 传入到处理程序方法handle的参数
     *
     * @param target 目标请求，可以是一个URI或者是一个转发到这的处理器的名字
     * @param baseRequest Jetty自己的没有被包装的请求，一个可变的Jetty请求对象
     * @param request 被filter或者servlet包装的请求，一个不可变的Jetty请求对象
     * @param response 响应，可能被filter或者servlet包装过
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
        //处理程序会设置状态码，content-type，并调用write向response输出内容。
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();

        out.println("<h1>" + greeting + "</h1>");
        if (body != null) {
            out.println(body);
        }

        baseRequest.setHandled(true);
    }
}
