package com.didichuxing.datachannel.agent.node;

import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.component.AgentComponent;
import com.didichuxing.datachannel.agent.engine.metrics.MetricService;
import com.didichuxing.datachannel.agent.node.service.ModelManager;
import com.didichuxing.datachannel.agent.sink.kafkaSink.errorlog.ErrLogService;
import com.didichuxing.datachannel.agent.node.service.metrics.sink.KafkaMetricSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 负责启动和实时拉取配置信息
 * @author: huangjw
 * @Date: 19/7/5 15:20
 */
public class LaunchService extends AgentComponent {

    private static final Logger LOGGER          = LoggerFactory.getLogger(LaunchService.class
                                                    .getName());
    /**
    * agent当前配置
    */
    private static AgentConfig  lastAgentConfig = null;
    /**
     * agent日志模型管理器
     */
    private ModelManager        modelManager;

    @Override
    public boolean init(AgentConfig config) {
        LOGGER.info("begin to init AgentLaunchService!");
        if (ConfigService.curAgentConfig != null) {
            lastAgentConfig = ConfigService.curAgentConfig;
        } else {
            return true;
        }

        LOGGER.info("init agent launch. config is " + lastAgentConfig);
        try {
            /*
             * errorlogs 服务可不具备
             */
            if (lastAgentConfig.getErrorLogConfig() != null
                && lastAgentConfig.getErrorLogConfig().isValid()) {
                ErrLogService.init(lastAgentConfig.getErrorLogConfig());
            }
            /*
             * metrics能力必须具备
             */
            if (lastAgentConfig.getMetricConfig() != null
                && lastAgentConfig.getMetricConfig().isValid()) {
                MetricService.init(lastAgentConfig.getMetricConfig(), new KafkaMetricSink(
                    lastAgentConfig.getMetricConfig()));
            } else {
                return false;
            }
            /*
             * 初始化日志模型管理器
             */
            modelManager = new ModelManager();
            modelManager.init(lastAgentConfig);
        } catch (Exception e) {
            LOGGER.error("AgentLaunchService init error.", e);
        }

        LOGGER.info("init agent launch success");
        return true;
    }

    @Override
    public boolean start() {
        LOGGER.info("begin to start agent to work");

        if (lastAgentConfig == null) {
            return true;
        }

        if (modelManager != null) {
            // 启动任务
            modelManager.start();
            LOGGER.info("success to start agent.");
            return true;
        } else {
            LOGGER.error("failed to start agent for no vaild config!");
            return false;
        }
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("stop agentLaunchService. force is " + force);
        if (modelManager != null) {
            try {
                modelManager.stop(force);
                ErrLogService.stop();
                MetricService.stop();

            } catch (Exception e) {
                LogGather.recordErrorLog("AgentLaunchService error!",
                    "AgentLaunchService stop error!", e);
            }
        }

        LOGGER.info("stop agentLaunchService success!");
        return true;
    }

    @Override
    public boolean onChange(AgentConfig newOne) {
        if (lastAgentConfig == null || lastAgentConfig.getMetricConfig() == null
            || !lastAgentConfig.getMetricConfig().isValid()) {
            if (newOne != null && newOne.getMetricConfig() != null
                && newOne.getMetricConfig().isValid()) {
                lastAgentConfig = newOne;

                // 初始化
                init(newOne);

                // 启动
                start();
                return true;
            } else {
                return false;
            }
        }

        if (lastAgentConfig.getVersion() > newOne.getVersion()) {
            LOGGER.warn("lastVersion>newVersion, ignore, last:" + lastAgentConfig.getVersion()
                        + ", new:" + newOne.getVersion());
            return false;
        }

        boolean isChanged = false;
        if (newOne.getVersion() > lastAgentConfig.getVersion()) {
            // agent本身配置发生变化
            LOGGER.info("use new agent config: " + newOne);

            try {
                // 错误信息提交配置变更
                ErrLogService.onChange(newOne.getErrorLogConfig());

                // 处理metric配置变更
                MetricService.onChange(newOne.getMetricConfig());

                isChanged = true;
            } catch (Exception e) {
                LogGather.recordErrorLog("AgentLaunchService error", "onChange error! newOne is "
                                                                     + newOne, e);
            }

            lastAgentConfig = newOne;
        }

        // 日志模型配置变更
        // 更新本地配置
        return this.modelManager.onChange(newOne) || isChanged;
    }
}
