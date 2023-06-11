package com.didichuxing.datachannel.agent.engine;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.conf.Configurable;
import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;
import com.didichuxing.datachannel.agent.engine.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 抽象模型
 * @author: huangjw
 * @Date: 19/7/2 15:41
 */
public abstract class AbstractModel extends TaskComponent implements Configurable {

    private static final Logger           LOGGER = LoggerFactory.getLogger(AbstractTask.class
                                                     .getName());
    protected Map<String, AbstractSource> sources;
    protected Map<String, AbstractTask>   tasks;
    protected long                        taskId;
    protected String                      modelTag;

    protected ModelConfig                 modelConfig;

    private AtomicBoolean                 isStop = new AtomicBoolean(true);

    public AbstractModel(ModelConfig modelConfig){
        this.modelConfig = modelConfig;
        this.taskId = modelConfig.getCommonConfig().getModelId();
        this.modelTag = modelConfig.getTag();
        this.sources = new ConcurrentHashMap<>();
        this.tasks = new ConcurrentHashMap<>();
        bulidUniqueKey();
    }

    @Override
    public void bulidUniqueKey() {
        setUniqueKey(this.modelConfig.getCommonConfig().getModelId()
                     + "_"
                     + (StringUtils.isNotBlank(this.modelConfig.getHostname()) ? this.modelConfig
                         .getHostname() : ""));
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        ModelConfig newConfig = (ModelConfig) newOne;
        if (newConfig.getVersion() <= this.modelConfig.getVersion()) {
            return false;
        }

        LOGGER.info("begin to change task model. new config is " + newOne);
        if (tasks != null) {
            ModelConfig oldConfig = this.modelConfig;
            this.modelConfig = newConfig;
            if (newConfig.getCommonConfig().isStop()) {
                stop(true);
            } else {
                if (oldConfig.getCommonConfig().isStop() && !newConfig.getCommonConfig().isStop()) {
                    // 表示重启
                    init(newOne);
                    start();
                    return true;
                }

                List<AbstractTask> updatedTask = getUpdatedTasks(oldConfig, newConfig);
                List<AbstractTask> addedTask = getAddedTasks(oldConfig, newConfig);
                List<AbstractTask> deletedTask = getDeletedTasks(oldConfig, newConfig);

                for (AbstractTask task : updatedTask) {
                    tasks.get(task.getUniqueKey()).onChange(newOne);
                }

                for (AbstractTask task : addedTask) {
                    this.sources.put(task.getSource().getUniqueKey(), task.getSource());
                    task.init(this.modelConfig);
                    task.start();
                    tasks.put(task.getUniqueKey(), task);
                    TaskRunningPool.submit(task);
                }

                for (AbstractTask task : deletedTask) {
                    task.delete();
                    tasks.remove(task.getUniqueKey());
                }
            }
        }
        return true;
    }

    /**
     * 修改的tasks
     * @param oldTaskConfig
     * @param newTaskConfig
     * @return
     */
    public abstract List<AbstractTask> getUpdatedTasks(ModelConfig oldTaskConfig,
                                                       ModelConfig newTaskConfig);

    /**
     * 新增的tasks
     * @param oldTaskConfig
     * @param newTaskConfig
     * @return
     */
    public abstract List<AbstractTask> getAddedTasks(ModelConfig oldTaskConfig,
                                                     ModelConfig newTaskConfig);

    /**
     * 删除的tasks
     * @param oldTaskConfig
     * @param newTaskConfig
     * @return
     */
    public abstract List<AbstractTask> getDeletedTasks(ModelConfig oldTaskConfig,
                                                       ModelConfig newTaskConfig);

    @Override
    public boolean start() {
        LOGGER
            .info("begin to start model. modelId is " + this.taskId + ", modelTag is " + modelTag);
        if (canStart()) {
            for (AbstractTask task : tasks.values()) {
                task.start();
                TaskRunningPool.submit(task);
            }
            setStop(false);
            LOGGER.info("success to start mode!l");
            return true;
        } else {
            LOGGER.info("model is not allow to start!");
            return false;
        }
    }

    public boolean canStart() {
        if (!isStop()) {
            LOGGER.info("model is already started.ignore!");
            return false;
        }
        // 状态为已停止的日志模型不需要启动
        if (modelConfig.getCommonConfig().isStop()) {
            LOGGER.warn("model is stopped. ingore!");
            setStop(true);
            return false;
        }
        return true;
    }

    @Override
    public boolean stop(boolean force) {
        if (isStop()) {
            LOGGER.info("model is already stoped. ignore! uniqueKey is " + getUniqueKey());
            return true;
        }
        LOGGER.info("begin to stop model. force is " + force + ",modelId is " + this.taskId
                    + ", modelTag is " + modelTag);
        setStop(true);
        if (this.tasks != null) {
            for (AbstractTask task : tasks.values()) {
                task.stop(force);
            }
        }
        return true;
    }

    /**
     * 删除
     * @return
     */
    @Override
    public boolean delete() {
        LOGGER.info("begin to delete model. modelId is " + this.taskId + ", modelTag is "
                    + modelTag);
        if (this.tasks != null) {
            for (AbstractTask task : tasks.values()) {
                task.delete();
            }
        }
        return true;
    }

    public Map<String, AbstractSource> getSources() {
        return sources;
    }

    public void setSources(Map<String, AbstractSource> sources) {
        this.sources = sources;
    }

    public String getModelTag() {
        return modelTag;
    }

    public void setModelTag(String modelTag) {
        this.modelTag = modelTag;
    }

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public void setModelConfig(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    public Map<String, AbstractTask> getTasks() {
        return tasks;
    }

    public void setTasks(Map<String, AbstractTask> tasks) {
        this.tasks = tasks;
    }

    public boolean isStop() {
        return isStop.get();
    }

    public void setStop(boolean result) {
        this.isStop.set(result);
    }
}
