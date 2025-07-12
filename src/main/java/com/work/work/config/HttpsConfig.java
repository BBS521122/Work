package com.work.work.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liumingfu
 * @date 2020/06/08 22:06
 * @description: 支持HTTP和HTTPS同时访问的配置
 */
@Configuration
public class HttpsConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // 添加HTTP连接器，不设置重定向
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    /**
     * 配置HTTP连接器，允许直接访问HTTP协议8080端口
     *
     * @return HTTP连接器
     * @author LiuMingFu
     * @date 2025/2/14
     */
    Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);  // HTTP端口
        connector.setSecure(false);
        // 移除了 setRedirectPort，不进行重定向
        return connector;
    }
}