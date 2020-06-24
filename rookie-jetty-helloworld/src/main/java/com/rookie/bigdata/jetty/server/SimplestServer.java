package com.rookie.bigdata.jetty.server;

import com.rookie.bigdata.jetty.handler.HelloHandler;
import org.eclipse.jetty.server.Server;

/**
 * @ClassName SimplestServer
 * @Description SimplestServer
 * @Author liuxili
 * @Date 2020/6/18 14:43
 * @Version 1.0
 */
public class SimplestServer {


    public static void main(String[] args) throws Exception {
        //在8080端口运行一个http服务
        Server server = new Server(8080);
        server.setHandler(new HelloHandler());
        server.start();
        //server.dumpStdErr();
        server.join();
    }
}
