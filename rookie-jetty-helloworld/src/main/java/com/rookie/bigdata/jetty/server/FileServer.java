package com.rookie.bigdata.jetty.server;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

/**
 * @ClassName FileServer
 * @Description FileServer
 * @Author liuxili
 * @Date 2020/6/18 15:24
 * @Version 1.0
 */
public class FileServer {
    public static void main(String[] args) throws Exception {
        //创建一个基础的Jetty服务监听8080端口
        //若设置端口为0，则随机一个可用的端口，端口信息可以从日志中获取也可以写测试方法获取
        Server server = new Server(8080);

        //创建一个ResourceHandler，它处理请求的方式是提供一个资源文件
        //这是一个Jetty内置的处理器，所以它非常适合与其他处理器构成一个处理链
        ResourceHandler resource_handler = new ResourceHandler();
        //配置ResourceHandler，设置哪个文件应该被提供给请求方
        //这个例子里，配置的是当前路径下的文件，但是实际上可以配置长任何jvm能访问到的地方
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[] { "index.jsp" });
        resource_handler.setResourceBase(".");

        // 将resource_handler添加到GzipHandler中，然后将GzipHandler提供给Server
        GzipHandler gzip = new GzipHandler();
        server.setHandler(gzip);
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
        gzip.setHandler(handlers);

        server.start();
        server.join();
    }
}
