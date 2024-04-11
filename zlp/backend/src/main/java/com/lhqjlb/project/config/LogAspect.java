package com.lhqjlb.project.config;

import com.lhqjlb.project.entity.Userr;
import com.lhqjlb.project.util.JsonUtil;
import com.lhqjlb.project.util.MultipartUtil;
import com.lhqjlb.project.util.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(public * com.lhqjlb.project..*Controller.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String servletPath = request.getServletPath();
        String method = request.getMethod();
        if (log.isDebugEnabled()) {
            if (MultipartUtil.isMultipart(request)) {
                log.debug("RequestStart 文件上传 {} {}", method, servletPath);
            } else {
                log.debug("RequestStart {} {} params={}", method, servletPath, params(joinPoint));
            }
            Object user = request.getAttribute("user");
            if (user != null) {
                Userr userr = (Userr) user;
                log.debug("UserInfo id={} username={} displayName={}",
                        userr.getId(),
                        userr.getUsername(),
                        userr.getNamee());
            }
        }

        Object obj = joinPoint.proceed();
        long time = System.currentTimeMillis() - start;
        if (time > 3000) {
            if (MultipartUtil.isMultipart(request)) {
                log.warn("Request {} {} 文件上传 {}ms", method, servletPath, time);
            } else {
                log.warn("Request {} {} params={} {}ms", method, servletPath, params(joinPoint), time);
            }
        }
        if (log.isDebugEnabled()) {
            if (obj instanceof R ) {
                log.debug("return={}", JsonUtil.toJsonStr(obj));
            }
            log.debug("RequestEnd {}ms", time);
        }
        return obj;
    }

    private String params(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest
                    || arg instanceof HttpServletResponse) {
                continue;
            }
            sb.append(JsonUtil.toJsonStr(arg)).append(",");
        }
        return sb.toString();
    }

}