package com.didichuxing.datachannel.swan.agent.node.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.swan.agent.common.constants.Tags;
import com.didichuxing.datachannel.swan.agent.common.loggather.LogGather;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.swan.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.AbstractModel;
import com.didichuxing.datachannel.swan.agent.engine.component.AgentComponent;
import com.didichuxing.datachannel.swan.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.swan.agent.engine.metrics.source.AgentStatistics;
import com.didichuxing.datachannel.swan.agent.engine.utils.CollectUtils;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.swan.agent.node.SwanAgent;
import com.didichuxing.datachannel.swan.agent.source.log.offset.OffsetManager;
import com.didichuxing.datachannel.swan.agent.task.log.log2kafak.Log2KafkaModel;

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

    private AgentStatistics            agentStatistics;

    @Override
    public boolean init(AgentConfig config) {
        LOGGER.info("begin to init model manager. config is " + config);
        this.agentConfig = config;

        try {
            if (CommonUtils.getSystemType().equals("linux")) {
                /*
                 * 根据限流配置初始化限流服务
                 */
                LimitService.LIMITER.init(config.getLimitConfig());
            }
            /*
             * 构建 agent 统计信息对象
             */
            agentStatistics = new AgentStatistics("basic", LimitService.LIMITER, SwanAgent.START_TIME);
            /*
             * 初始化 agent 统计信息对象
             */
            agentStatistics.init();
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
            for (ModelConfig mc : modelConfigs) {
                AbstractModel model = getByTag(mc);
                if (model != null) {
                    models.put(model.getModelConfig().getModelConfigKey(), model);
                    if (mc.getCommonConfig().isStop()) {
                        LOGGER.warn("modelConfig is stoped. ignore! logModelId is "
                                    + mc.getCommonConfig().getModelId());
                        continue;
                    }
                    model.init(mc);
                }
            }
        }

        return true;
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

        if (!OffsetManager.isInit()) {
            OffsetManager.init(config.getOffsetConfig(), null);
        }
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

        this.agentStatistics.destory();
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

    public Map<String, AbstractModel> getModels() {
        return models;
    }

    public void setModels(Map<String, AbstractModel> models) {
        this.models = models;
    }
}
