package com.didichuxing.datachannel.agent.sink.kafkaSink.errorlog;

import com.didichuxing.datachannel.agent.common.configs.v2.ErrorLogConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-16 18:43
 */
public class ErrLogService {

    private static final Logger   LOGGER        = LoggerFactory.getLogger(ErrLogService.class
                                                    .getName());
    private static ErrorLogSinker logSinker     = null;
    private final static String   REGISTER_NAME = "agent-error-log";
    private static boolean        isInit        = false;

    /**
     * 根据 agent 错误日志配置初始化对应错误日志服务
     * @param config
     * @throws Exception
     */
    public static void init(ErrorLogConfig config) throws Exception {
        if (config.isValid()) {
            /*
             * 设置 errorlogs 流对应 topic
             */
            config.setTopic(CommonUtils.selectTopic(config.getTopic()));
            /*
             * 设置 errorlogs 流对应 kafka producer
             */
            logSinker = new ErrorLogSinker(config);
            /*
             * 将 logSinker 注册入日志聚合表
             * GatherLogSinkRegistry：用于对同类型日志进行聚合发送，LogGather 进行 发送
             */
            LogGather.registryErrorSink(REGISTER_NAME, logSinker);
            isInit = true;
        }
    }

    public static void onChange(ErrorLogConfig newConfig) throws Exception {
        LOGGER.info("begin to change errorlog service. config is " + newConfig);
        if (isInit) {
            newConfig.setTopic(CommonUtils.selectTopic(newConfig.getTopic()));
            logSinker.onChange(newConfig);
        } else {
            init(newConfig);
        }
        LOGGER.info("success change to errorlog config. config is " + newConfig);
    }

    public static void stop() {
        logSinker.stop();
        LogGather.unRegistryErrorSink(REGISTER_NAME);
        isInit = false;
    }
}
