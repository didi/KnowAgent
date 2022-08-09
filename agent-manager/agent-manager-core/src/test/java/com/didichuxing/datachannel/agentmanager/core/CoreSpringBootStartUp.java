package com.didichuxing.datachannel.agentmanager.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"com.didichuxing.datachannel.agentmanager"})
public class CoreSpringBootStartUp {
    public static void main(String[] args) {
        SpringApplication.run(CoreSpringBootStartUp.class, args);
    }
}
