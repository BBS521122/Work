package com.work.work.fliter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CorsPreflightFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 处理预检请求
        if ("OPTIONS".equals(httpRequest.getMethod())) {
            httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return; // 直接返回，不继续过滤器链
        }

        chain.doFilter(request, response);
    }
}
