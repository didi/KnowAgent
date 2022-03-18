package com.didichuxing.datachannel.agent.node.service.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.didichuxing.datachannel.agent.node.Agent;
import com.didichuxing.datachannel.agent.node.LaunchService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.node.ConfigService;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 启停log-agent
 * @author: huangjw
 * @Date: 18/8/28 14:17
 */
public class Status extends Handler implements HttpHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Status.class.getName());
    public final static String  URI    = "/log-agent/status";

    private LaunchService       launchService;

    private ConfigService       configService;

    public Status(LaunchService launchService, ConfigService configService) {
        this.launchService = launchService;
        this.configService = configService;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // 获得输入流
        InputStream in = httpExchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
        // 将BufferedReader转化为字符串
        String text = IOUtils.toString(reader);
        if (text.contains(LogConfigConstants.STATUS_TAG)) {
            try {
                String status = text.substring(text.indexOf("=") + 1);
                if (StringUtils.isNotBlank(status)) {
                    if (status.equals(LogConfigConstants.START_TAG)) {
                        start();
                    } else if (status.equals(LogConfigConstants.STOP_TAG)) {
                        stop();
                    }
                    writer("success", httpExchange);
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("Status error!", "handle error!", e);
                writer("failed", httpExchange);
            }
        }
    }

    private void stop() {
        LOGGER.info("begin to stop agent");
        try {
            if (Agent.isRunning) {
                configService.stop(true);
                launchService.stop(true);
                Agent.isRunning = false;
            } else {
                LOGGER.info("agent is already stopped. ignore!");
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("Status error!", "stop error!", e);
        }
        LOGGER.info("success to stop agent");
    }

    private void start() {
        LOGGER.info("begin to start agent");
        try {
            if (!Agent.isRunning) {
                configService.init(null);
                launchService.init(null);

                configService.start();
                launchService.start();
                Agent.isRunning = true;
            } else {
                LOGGER.info("agent is already started. ignore!");
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("Status error!", "start error!", e);
        }
        LOGGER.info("success to start agent");
    }
}
