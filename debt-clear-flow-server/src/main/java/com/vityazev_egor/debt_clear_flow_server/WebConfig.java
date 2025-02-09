package com.vityazev_egor.debt_clear_flow_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.vityazev_egor.debt_clear_flow_server.Controllers.Filters.AuthFilter;
import com.vityazev_egor.debt_clear_flow_server.Controllers.Filters.ReceptionAccessFilter;

import java.util.*;

import jakarta.servlet.SessionTrackingMode;

@Configuration
public class WebConfig {

    // Устанавливает режим отслеживания сессии на COOKIE.
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        HashSet<SessionTrackingMode> set = new HashSet<SessionTrackingMode>();
        set.add(SessionTrackingMode.COOKIE);
        return servletContext -> {
            servletContext.setSessionTrackingModes(set);
        };
    }

    // регистрирует фильтр для ограничение не авторизованных пользователей.
    @Bean
    @Autowired
    public FilterRegistrationBean<AuthFilter> regAuthFilter(AuthFilter authFilter) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authFilter);
        registrationBean.addUrlPatterns("/panel/*");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    @Autowired
    public FilterRegistrationBean<ReceptionAccessFilter> regReceptionFilter(ReceptionAccessFilter receptionAccessFilter) {
        FilterRegistrationBean<ReceptionAccessFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(receptionAccessFilter);
        registrationBean.addUrlPatterns("/panel/reception/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
