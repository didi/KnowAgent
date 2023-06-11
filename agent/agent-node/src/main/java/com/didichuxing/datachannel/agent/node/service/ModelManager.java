package com.didichuxing.datachannel.agent.node.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agent.common.constants.Tags;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.metrics.source.*;
import com.didichuxing.datachannel.agent.node.Agent;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.AbstractModel;
import com.didichuxing.datachannel.agent.engine.component.AgentComponent;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.utils.CollectUtils;
import com.didichuxing.datachannel.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.agent.task.log.log2kafak.Log2KafkaModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 模型管理器
 * @author: huangjw
 * @Date: 19/7/2 18:39
 */
public class ModelManager extends AgentComponent {

    private static final Logger        LOGGER = LoggerFactory.getLogger(ModelManager.class
                                                  .getName());
    private AgentConfig                agentConfig;

    private Map<String, AbstractModel> models;

    @Override
    public boolean init(AgentConfig config) {
        LOGGER.info("begin to init model manager. config is " + config);
        this.agentConfig = config;

        try {
            /*
             * 根据限流配置初始化限流服务
             */
            LimitService.LIMITER.init(config.getLimitConfig());
            /*
             * 构建 system，process，agent business，disk/io，net card 相关统计信息对象
             */
            buildAgentStatistics();
        } catch (Exception e) {
            LogGather.recordErrorLog("ModelManager error!", "ModelManager init error!", e);
        }

        /*
         * 准备工作：初始化 offset 管理服务
         */
        prepare(this.agentConfig);

        /*
         * 根据日志模型配置初始化对应日志模型并初始化对应日志模型
         */
        List<ModelConfig> modelConfigs = this.agentConfig.getModelConfigs();
        models = new ConcurrentHashMap<>();
        if (modelConfigs != null && modelConfigs.size() != 0) {
            for (ModelConfig modelConfig : modelConfigs) {
                AbstractModel model = getByTag(modelConfig);
                if (model != null) {
                    models.put(model.getModelConfig().getModelConfigKey(), model);
                    if (modelConfig.getCommonConfig().isStop()) {
                        LOGGER.warn("modelConfig is stoped. ignore! logModelId is "
                                    + modelConfig.getCommonConfig().getModelId());
                        continue;
                    }

                    model.init(modelConfig);
                }
            }
        }

        return true;
    }

    /**
     * 构建 system，process，agent business，disk/io，net card 相关统计信息对象
     */
    private void buildAgentStatistics() {
        /*
         * 构建并初始化agent统计信息对象
         */
        GlobalProperties.setAgentStatistics(new AgentStatistics("agentBasic", LimitService.LIMITER,
            Agent.START_TIME, getRunningCollectTaskNum(), getRunningCollectPathNum(),
            this.agentConfig.getModelConfigs().size(), getCollectPathNum()));
        GlobalProperties.getAgentStatistics().init();
    }

    /**
     * @return 根据当前agent采集配置，获取对应运行状态日志采集任务数
     */
    private Integer getRunningCollectTaskNum() {
        return getRunningCollectTaskList().size();
    }

    /**
     * @return 根据当前agent采集配置，获取对应运行状态日志采集路径数
     */
    private Integer getRunningCollectPathNum() {
        Integer runningCollectPathNum = 0;
        for (ModelConfig modelConfig : getRunningCollectTaskList()) {
            LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
            runningCollectPathNum += logSourceConfig.getLogPaths().size();
        }
        return runningCollectPathNum;
    }

    /**
     * @return 根据当前agent采集配置，获取对应运行状态日志采集任务集
     */
    private List<ModelConfig> getRunningCollectTaskList() {
        List<ModelConfig> runningModelConfigList = new ArrayList<>();
        for(ModelConfig modelConfig : this.agentConfig.getModelConfigs()) {
            if(!modelConfig.getCommonConfig().isStop()) {
                runningModelConfigList.add(modelConfig);
            }
        }
        return runningModelConfigList;
    }

    private AbstractModel getByTag(ModelConfig taskConfig) {
        if (taskConfig != null) {
            String tag = taskConfig.getTag();
            if (StringUtils.isNotBlank(tag)) {
                if (Tags.TASK_LOG2KAFKA.equals(tag)) {
                    return new Log2KafkaModel(taskConfig);
                } else if (Tags.TASK_LOG2HDFS.equals(tag)) {
                    //TODO：TASK_LOG2HDFS not support
                    LOGGER
                        .warn("taskconfig matched TASK_LOG2HDFS model, but not support.taskConfig is "
                              + taskConfig);
                    return null;
                }
            }
        }
        LOGGER.warn("taskconfig has no any model matched.taskConfig is " + taskConfig);
        return null;
    }

    /**
     * 1. 初始化offset
     * @param config
     */
    private void prepare(AgentConfig config) {
        Map<String, ModelConfig> newMap = config.getModelConfigMap();
        if (newMap.size() == 0 || OffsetManager.isInit()) {
            return;
        }

        OffsetManager.init(config.getOffsetConfig(), null);
    }

    @Override
    public boolean start() {
        LOGGER.info("begin to start models.");
        try {
            LimitService.LIMITER.start();
        } catch (Exception e) {
            LogGather.recordErrorLog("ModelManager error!", "limitService start error!", e);
        }
        if (models != null && models.size() != 0) {
            for (AbstractModel model : models.values()) {
                try {
                    if (model.getModelConfig().getCommonConfig().isStop()) {
                        LOGGER.warn("modelConfig is stoped. ignore! logModelId is "
                                    + model.getModelConfig().getCommonConfig().getModelId());
                        continue;
                    }
                    model.start();
                } catch (Exception e) {
                    LogGather.recordErrorLog("ModelManager error!", "ModelManager start error!", e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to modelManager.");
        if (models != null && models.size() != 0) {
            for (AbstractModel model : models.values()) {
                try {
                    model.stop(force);
                } catch (Exception e) {
                    LogGather.recordErrorLog("ModelManager error!", "ModelManager stop error!", e);

                }
            }
        }

        if (OffsetManager.isInit()) {
            OffsetManager.flush();
            OffsetManager.stop();
        }

        LimitService.LIMITER.stop();

        GlobalProperties.getAgentStatistics().destory();

        LOGGER.info("stop modelManager success!");
        return true;
    }

    @Override
    public boolean onChange(AgentConfig newConfig) {
        boolean isConfigChanged = false;
        if (newConfig.getVersion() > this.agentConfig.getVersion()) {
            LOGGER.info("get newAgentConfig. begin to change limiterConfig and offsetConfig");

            // 处理limiter的配置变更
            LimitService.LIMITER.onChange(newConfig.getLimitConfig());

            // 处理offset配置变更
            OffsetManager.onChange(newConfig.getOffsetConfig());

            isConfigChanged = true;
        }

        Map<String, ModelConfig> oldMap = this.agentConfig.getModelConfigMap();
        Map<String, ModelConfig> newMap = newConfig.getModelConfigMap();

        prepare(newConfig);
        this.agentConfig = newConfig;

        //更新 agentStatistics
        updateAgentStatistics();

        LOGGER.info("old model config map size {}, new model config map size {}, models size {}",
            oldMap.size(), newMap.size(), models.size());
        Set<String> updateModIds = CollectUtils.getStrSame(oldMap.keySet(), newMap.keySet());
        Set<String> addModIds = CollectUtils.getStrAdd(oldMap.keySet(), newMap.keySet());
        Set<String> delModIds = CollectUtils.getStrDel(oldMap.keySet(), newMap.keySet());

        for (String modIdKey : updateModIds) {
            try {
                if (!oldMap.get(modIdKey).getTag().equals(newMap.get(modIdKey).getTag())) {
                    // 表示发生了任务类型的转换
                    models.get(modIdKey).stop(false);
                    models.remove(modIdKey);
                    ModelConfig modelConfig = newMap.get(modIdKey);
                    AbstractModel model = getByTag(modelConfig);
                    if (model != null) {
                        if (model.init(modelConfig) && model.start()) {
                            models.put(model.getModelConfig().getModelConfigKey(), model);
                            isConfigChanged = true;
                        }
                    }
                } else {
                    // 正常的change
                    if (models.get(modIdKey) != null && newMap.get(modIdKey) != null
                        && models.get(modIdKey).onChange(newMap.get(modIdKey))) {
                        isConfigChanged = true;
                    }
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("ModelManager error", "update model error. id is  "
                                                               + modIdKey, e);
            }
        }

        for (String modIdKey : addModIds) {
            try {
                ModelConfig modelConfig = newMap.get(modIdKey);
                AbstractModel model = getByTag(modelConfig);
                if (model != null) {
                    if (model.init(modelConfig) && model.start()) {
                        models.put(model.getModelConfig().getModelConfigKey(), model);
                        isConfigChanged = true;
                    }
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("ModelManager error", "add model error. id is  "
                                                               + modIdKey, e);
            }
        }

        for (String modIdKey : delModIds) {
            try {
                if (models.get(modIdKey) != null && models.get(modIdKey).delete()) {
                    models.remove(modIdKey);
                    isConfigChanged = true;
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("ModelManager error", "del model error. id is  "
                                                               + modIdKey, e);
            }
        }
        return isConfigChanged;
    }

    private void updateAgentStatistics() {
        GlobalProperties.getAgentStatistics().setRunningCollectPathNum(getRunningCollectPathNum());
        GlobalProperties.getAgentStatistics().setRunningCollectTaskNum(getRunningCollectTaskNum());
        GlobalProperties.getAgentStatistics().setCollectTaskNum(
            this.agentConfig.getModelConfigs().size());
        GlobalProperties.getAgentStatistics().setCollectPathNum(getCollectPathNum());
    }

    private Integer getCollectPathNum() {
        Integer collectPathNum = 0;
        for (ModelConfig modelConfig : this.agentConfig.getModelConfigs()) {
            LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
            collectPathNum += logSourceConfig.getLogPaths().size();
        }
        return collectPathNum;
    }

    public Map<String, AbstractModel> getModels() {
        return models;
    }

    public void setModels(Map<String, AbstractModel> models) {
        this.models = models;
    }
}
