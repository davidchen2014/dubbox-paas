package com.alibaba.dubbo.container.page;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticResourceFilter implements Filter {

    private static final String CLASSPATH_PREFIX = "classpath:";

    private final long start = System.currentTimeMillis();

    private final List<String> resources = new ArrayList<String>();

    public void init(FilterConfig filterConfig) throws ServletException {
        String config = filterConfig.getInitParameter("resources");
        if (config != null && config.length() > 0) {
            String[] configs = config.split(",");
            for (String c : configs) {
                if (c != null && c.length() > 0) {
                    c = c.replace('\\', '/');
                    if (c.endsWith("/")) {
                        c = c.substring(0, c.length() - 1);
                    }
                    resources.add(c);
                }
            }
        }
        // 加个默认的
        resources.add(filterConfig.getServletContext().getRealPath("/"));
    }

    public void destroy() {
    }
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (response.isCommitted()) {
            return;
        }
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        // 只处理 jpg , png, html, PNG后缀 可以在外面配置
        if (context != null && ! "/".equals(context)) {
            uri = uri.substring(context.length());
        }
        if (! uri.startsWith("/")) {
            uri = "/" + uri;
        }
        // TODO: debug修改头部的 /wiki 变成文件夹的 /md
        
        byte[] data;
        InputStream input = getInputStream(uri);// 显示最新版本
    	if (input == null) {// 如果不是静态资源再去找动态的后续处理
    	    // chain.doFilter(req, res); //这里直接跑错了 也不用处理
            return;
        }
    	try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
            data = output.toByteArray();
        } finally {
            input.close();
        }
        OutputStream output = response.getOutputStream();
        output.write(data);
        output.flush();
    }
    
    private boolean isFile(String path) {
        return path.startsWith("/") || path.indexOf(":") <= 1;
    }
	
	private InputStream getInputStream(String uri) {
        for (String resource : resources) {
            String path = resource + uri;
            try {
                if (isFile(path)) {
                    return new FileInputStream(path);
                } else if (path.startsWith(CLASSPATH_PREFIX)) {
                    return Thread.currentThread().getContextClassLoader().getResourceAsStream(path.substring(CLASSPATH_PREFIX.length()));
                } else {
                    return new URL(path).openStream();
                }
            } catch (IOException e) {
            }
        }
        return null;
	}

}