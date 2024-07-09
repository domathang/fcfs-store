package com.dony.common.passport;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Value("${auth.jwt.passport.key}")
    private String passportKey;

    @Bean
    public FilterRegistrationBean<PassportFilter> passportFilter() {
        FilterRegistrationBean<PassportFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new PassportFilter(passportKey));
        registrationBean.addUrlPatterns("*"); // adjust the URL patterns as needed
        return registrationBean;
    }
}
