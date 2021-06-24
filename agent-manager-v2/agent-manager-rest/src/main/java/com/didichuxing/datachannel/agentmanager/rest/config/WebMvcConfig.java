package com.didichuxing.datachannel.agentmanager.rest.config;

import com.didichuxing.datachannel.agentmanager.rest.interceptor.UserAppIDPermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootConfiguration
@Component
//@DependsOn({"permissionInterceptor"})
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UserAppIDPermissionInterceptor userAppIDPermissionInterceptor;

    @Value("${metadata.sync.request.permission.enabled}")
    private Boolean activatePermission;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("agent/index");
        registry.addViewController("/index.html").setViewName("agent/index");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (activatePermission) {
            registry.addInterceptor(userAppIDPermissionInterceptor).addPathPatterns("/**");
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // SWAGGER
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

        // FE
        registry.addResourceHandler("agent/index.html", "/**").addResourceLocations("classpath:/templates/","classpath:/static/", "classpath:/templates/agent/");
    }
}