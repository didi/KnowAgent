package com.didichuxing.datachannel.agentmanager.rest.config;

import com.didichuxing.datachannel.agentmanager.rest.interceptor.HeaderHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
@Component
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private HeaderHandlerInterceptor headerHandlerInterceptor;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/agent").setViewName("pages/agent");
        registry.addViewController("/").setViewName("pages/agent");

        registry.addViewController("/agent/**").setViewName("pages/agent");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerHandlerInterceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // SWAGGER
        registry.addResourceHandler("/swagger-ui.html", "/swagger/**", "swagger-resources").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        // FE
        registry.addResourceHandler("/**").addResourceLocations("classpath:/templates/");

    }
}