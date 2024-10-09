package com.lhqjlb.project.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MDCFilter> mdcFilter() {
        FilterRegistrationBean<MDCFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new MDCFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Integer.MIN_VALUE + 100);
        return registration;
    }

}
