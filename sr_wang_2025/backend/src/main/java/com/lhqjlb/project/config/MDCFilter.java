package com.lhqjlb.project.config;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;

public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        MDC.put("traceid", IdUtil.getSnowflakeNextIdStr());
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.clear();
    }
}
