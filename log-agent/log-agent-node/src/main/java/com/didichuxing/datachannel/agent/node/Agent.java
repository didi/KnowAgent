package com.didichuxing.datachannel.agent.node;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.node.service.http.server.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: Agent 启动类
 */
public class Agent {

    private static final Logger  LOGGER     = LoggerFactory.getLogger(Agent.class.getName());

    /**
     * agent http 服务
     */
    private static HttpService   httpService;
    /**
     * agent 启动服务
     */
    private static LaunchService launchService;
    /**
     * agent 配置服务
     */
    private static ConfigService configService;
    /**
     * agent 是否处于运行状态
     */
    public static boolean        isRunning  = false;
    /**
     * agent 启动时间
     */
    public static Long           START_TIME = null;

    public static void main(String[] args) {
        START_TIME = System.currentTimeMillis();
        LOGGER.info("start time is " + START_TIME);
        if (!init()) {
            System.exit(-1);
        }

        if (!start()) {
            System.exit(-1);
        }
        isRunning = true;

        // 关闭
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                stopAgent();
            }
        });
    }

    /**
     * 初始化
     */
    private static boolean init() {
        try {
            launchService = new LaunchService();
            configService = new ConfigService(launchService);

            // 初始化过程直接启动http服务
            httpService = new HttpService(launchService, configService);
            httpService.init();
            httpService.start();

            configService.init(null);
            launchService.init(null);

            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("Agent error!", "init error! {}", e);
        }
        return false;
    }

    /**
     * 启动
     */
    private static boolean start() {
        try {
            launchService.start();
            configService.start();
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("Agent error!", "start error! {}", e);
        }
        return false;
    }

    /**
     * 停止
     */
    private static void stopAgent() {
        LOGGER.info("begin to stop agent");
        try {
            // 先休眠，等待后续环节准备彻底，再停止
            Thread.sleep(500L);

            launchService.stop(true);
            configService.stop(true);
            httpService.stop(true);
        } catch (Throwable e) {
            LogGather.recordErrorLog("Agent error!", "stop error! {}", e);
        }
        LOGGER.info("stop agent success.");
    }
}
