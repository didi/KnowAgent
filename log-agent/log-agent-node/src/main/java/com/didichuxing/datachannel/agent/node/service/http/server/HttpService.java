package com.didichuxing.datachannel.agent.node.service.http.server;

import java.net.InetSocketAddress;
import java.util.Map;

import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.node.LaunchService;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.node.ConfigService;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: http服务
 * @author: huangjw
 * @Date: 19/7/2 14:54
 */
public class HttpService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpService.class.getName());
    private final static int    PORT   = 2023;

    private HttpServer          httpServer;

    private LaunchService       launchService;
    private ConfigService       configService;

    public HttpService(LaunchService launchService, ConfigService configService) {
        this.launchService = launchService;
        this.configService = configService;
    }

    public boolean init() {
        LOGGER.info("begin to init http server.");
        try {
            Map<String, String> configMap = CommonUtils.readSettings();
            String portStr = configMap.get(LogConfigConstants.HTTP_PORT);
            // 最大连接数为10
            int port = StringUtils.isNotBlank(portStr) ? Integer.parseInt(portStr) : PORT;
            httpServer = HttpServer.create(new InetSocketAddress(port), 10);
            register();
            LOGGER.info("init http server success. port is " + port);
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HttpService error", "init error.", e);
        }
        return false;
    }

    private void register() {
        LOGGER.info("begin to register.");
        httpServer.createContext(Heartbeat.URI, new Heartbeat());
        httpServer.createContext(Pid.URI, new Pid());
        httpServer.createContext(Version.URI, new Version());
        httpServer.createContext(Path.URI, new Path());
        httpServer.createContext(FileContent.URI, new FileContent());
        // 因安全部需求，进行去掉
        //        httpServer.createContext(Command.URI, new Command());
        httpServer.createContext(Status.URI, new Status(launchService, configService));
        LOGGER.info("register success.");
    }

    public boolean start() {
        LOGGER.info("begin to start http server.");
        try {
            httpServer.start();
            LOGGER.info("start http server success.");
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("HttpService error", "start error.", e);
        }

        return false;
    }

    public boolean stop(boolean force) {
        LOGGER.info("begin to stop http server!");
        httpServer.stop(0);
        LOGGER.info("stop http server success!");
        return true;
    }
}
