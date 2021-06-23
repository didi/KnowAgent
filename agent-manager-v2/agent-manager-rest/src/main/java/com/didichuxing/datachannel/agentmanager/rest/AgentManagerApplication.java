package com.didichuxing.datachannel.agentmanager.rest;

import com.didichuxing.datachannel.agentmanager.common.util.EnvUtil;
import com.didichuxing.datachannel.agentmanager.rest.swagger.SwaggerConfiguration;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.MetricService;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl.AgentMetricsRDSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by limeng on 2020-04-16
 */
@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager"})
public class AgentManagerApplication {

    private static final Logger LOGGER           = LoggerFactory.getLogger(AgentManagerApplication.class);

    static final String[]     ALL_EXCLUDE_URLS = new String[] {
            "/agent-manager/api/swagger-ui.html",
            "/agent-manager/api/swagger-resources/configuration/ui",
            "/agent-manager/api/null/swagger-resources/configuration/ui",
            "/agent-manager/api/webjars/springfox-swagger-ui/favicon-32x32.png",
            "/agent-manager/api/swagger-resources/configuration/security",
            "/agent-manager/api/swagger-resources",
            "/agent-manager/api/v1/client-docs",
            "/agent-manager/api/",
            "/agent-manager/api/v2/client-docs",
            "/agent-manager/api/csrf",
            "/agentmanager/api/druid/login.html",
            "/agentmanager/api/druid/css/bootstrap.min.css",
            "/agentmanager/api/druid/js/bootstrap.min.js",
            "/agentmanager/api/druid/js/doT.js", "/agentmanager/api/druid/js/jquery.min.js",
            "/agentmanager/api/druid/index.html", "/agentmanager/api/druid/js/client.js",
            "/agentmanager/api/druid/css/style.css", "/agentmanager/api/druid/js/lang.js",
            "/agentmanager/api/druid/header.html", "/agentmanager/api/druid/basic.json",
            "/agentmanager/api/druid/datasource.html", "/agentmanager/api/druid/datasource.json",
            "/agentmanager/api/druid/agentmanagerSql.html", "/agentmanager/api/druid/agentmanagerSql.json",
            "/agentmanager/api/druid/wall.html", "/agentmanager/api/druid/wall.json",
            "/agentmanager/api/druid/webapp.html", "/agentmanager/api/druid/js/doT.js",
            "/agentmanager/api/druid/weburi.html", "/agentmanager/api/druid/webapp.json",
            "/agentmanager/api/druid/weburi.json", "/agentmanager/api/druid/websession.html",
            "/agentmanager/api/druid/websession.json", "/agentmanager/api/druid/spring.html",
            "/agentmanager/api/druid/spring.json", "/agentmanager/api/druid/client.html"};

    @Value(value = "${agentmanager.port.web}")
    private int               port;

    @Value(value = "${agentmanager.contextPath}")
    private String            contextPath;

    /**
     * @param args
     */
    public static void main(String[] args) {
        EnvUtil.setLoadActiveProfiles(args);
        SwaggerConfiguration.initEnv(args);
        ApplicationContext ctx = SpringApplication.run(AgentManagerApplication.class, args);
        EnvUtil.setLoadActiveProfiles(ctx.getEnvironment().getActiveProfiles());
        for (String profile : ctx.getEnvironment().getActiveProfiles()) {
            LOGGER.info("Spring Boot use profile: {}", profile);
        }
        LOGGER.info("agentmanagerApplication started");
        MetricService metricService = ctx.getBean(MetricService.class);
        metricService.run();
    }

    @Bean
    public ConfigurableServletWebServerFactory configurableServletWebServerFactory() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setPort(port);
        factory.setContextPath(contextPath);
        return factory;
    }

    /**
     * todo 临时添加
     *
     * @return 指标流用MySQL做存储
     */
    @Bean
    public AgentMetricsDAO agentMetricsDAO() {
        return new AgentMetricsRDSImpl();
    }
}
