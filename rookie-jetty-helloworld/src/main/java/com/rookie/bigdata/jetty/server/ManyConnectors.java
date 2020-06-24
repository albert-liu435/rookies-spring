package com.rookie.bigdata.jetty.server;

import com.rookie.bigdata.jetty.handler.HelloHandler;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @ClassName ManyConnectors
 * @Description ManyConnectors
 * @Author liuxili
 * @Date 2020/6/18 15:46
 * @Version 1.0
 */
public class ManyConnectors {
    public static void main(String[] args) throws Exception {

        //这个例子会展示如何配置SSL，我们需要一个秘钥库，会在jetty.home下面找
        String jettyDistKeystore = "../../jetty-distribution/target/distribution/demo-base/etc/keystore";
        String keystorePath = System.getProperty("example.keystore", jettyDistKeystore);
        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists()) {
            throw new FileNotFoundException(keystoreFile.getAbsolutePath());
        }

        //创建一个不指定端口的Server，随后将直接配置连接和端口
        Server server = new Server();

        //HTTP配置
        //HttpConfiguration是一个配置http和https属性的集合，默认的配置是http的
        //带secured的ui配置https的，
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);

        //HTTP连接
        //第一个创建的连接是http连接，传入刚才创建的配置信息，也可以重新设置新的配置，如端口，超时等
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);

        //使用SslContextFactory来创建http
        //SSL需要一个证书，所以我们配置一个工厂来获得需要的东西
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

        //HTTPS的配置类
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setStsMaxAge(2000);
        src.setStsIncludeSubDomains(true);
        https_config.addCustomizer(src);

        //HTTPS连接
        //创建第二个连接，
        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(8443);
        https.setIdleTimeout(500000);

        // 设置一个连接的集合
        server.setConnectors(new Connector[] { http, https });

        // 设置一个处理器
        server.setHandler(new HelloHandler());

        // 启动服务
        server.start();
        server.join();
    }
}