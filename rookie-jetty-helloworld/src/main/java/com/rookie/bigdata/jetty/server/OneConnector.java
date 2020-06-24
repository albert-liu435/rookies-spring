package com.rookie.bigdata.jetty.server;

/**
 * @ClassName OneConnector
 * @Description OneConnector
 * @Author liuxili
 * @Date 2020/6/18 15:45
 * @Version 1.0
 */

import com.rookie.bigdata.jetty.handler.HelloHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

/**
 * 有一个连接的Server
 */
public class OneConnector {
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        // 创建一个HTTP的连接，配置监听主机，端口，以及超时时间
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);

        // 将此连接添加到Server
        server.addConnector(http);

        // 设置一个处理器
        server.setHandler(new HelloHandler());

        // 启动Server
        server.start();
        server.join();
    }
}
