package com.cjy.doubleblindserver.common.baseclass.webconfig;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Jiyang Chen
 * @Date: 2022/11/22 21:29
 */
@Component
@Order(1)
public class CORSFilterConfig implements Filter {

    private final static String[] allowOrigin = {
            "http://localhost:8041",
            "http://127.0.0.1:8041",
    };

    private final static Set<String> allowOrigins = new HashSet<>(Arrays.asList(allowOrigin));


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String originHeader = request.getHeader("Origin");
        if (allowOrigins.contains(originHeader)) {
            //这里填写你允许进行跨域的主机ip
            response.addHeader("Access-Control-Allow-Origin", originHeader);
            //允许的访问方法
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            //Access-Control-Max-Age 用于 CORS 相关配置的缓存
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,authentication,Authorization,cookies,token");
            //是否支持cookie跨域
            response.setHeader("Access-Control-Allow-Credentials", "true");
            //允许的访问方法
            response.setHeader("Content-Type", "application/json, application/x-www-form-urlencoded");
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
