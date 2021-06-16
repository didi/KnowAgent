package com.didichuxing.datachannel.swan.agent.node;

import com.didichuxing.datachannel.swan.agent.node.service.http.server.HttpService;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: Agent 启动类
 */
public class SwanAgent {

    private static final ILog    LOGGER     = LogFactory.getLog(SwanAgent.class.getName());

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
        // 初始化
        LOGGER.info("begin to init agent.");
        if (!init()) {
            System.exit(-1);
        }
        LOGGER.info("succes to init agent.");

        // 启动
        LOGGER.info("begin to start swan agent.");
        if (!start()) {
            System.exit(-1);
        }
        LOGGER.info("success to start agent.");

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
            LogGather.recordErrorLog("SwanAgent error!", "init error!", e);
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
            LogGather.recordErrorLog("SwanAgent error!", "init error!", e);
        }
        return false;
    }

    /**
     * 停止
     */
    private static void stopAgent() {
        LOGGER.info("begin to stop swan agent");
        try {
            // 先休眠，等待后续环节准备彻底，再停止
            Thread.sleep(500L);

            launchService.stop(true);
            configService.stop(true);
            httpService.stop(true);
        } catch (Throwable ex) {
            LogGather.recordErrorLog("SwanAgent error!", "stop error!", ex);
        }
        LOGGER.info("stop swan agent success.");
    }
}
