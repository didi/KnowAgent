package com.didichuxing.datachannel.swan.agent.task.log.log2hdfs;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.util.ShutdownHookManager;

import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.AbstractTask;
import com.didichuxing.datachannel.swan.agent.engine.service.TaskRunningPool;
import com.didichuxing.datachannel.swan.agent.engine.source.AbstractSource;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsFileSystem;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsTargetConfig;
import com.didichuxing.datachannel.swan.agent.source.log.LogSource;
import com.didichuxing.datachannel.swan.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.swan.agent.task.log.LogModel;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-12 15:10
 */
public class Log2HdfsModel extends LogModel {

    private static final ILog LOGGER   = LogFactory.getLog(Log2HdfsModel.class.getName());
    private HdfsFileSystem    hdfsFileSystem;

    // 停止中
    private volatile boolean  stopping = false;

    public Log2HdfsModel(ModelConfig config) {
        super(config);
    }

    @Override
    public AbstractTask buildTask(ModelConfig config, LogSource logSource) {
        Log2HdfsTask task = new Log2HdfsTask(this, config, logSource);
        return task;
    }

    public void setHdfsFileSystem(HdfsFileSystem hdfsFileSystem) {
        this.hdfsFileSystem = hdfsFileSystem;
    }

    public HdfsFileSystem getHdfsFileSystem() {
        return HdfsFileSystemContainer.getHdfsFileSystem(this);
    }

    @Override
    public boolean start() {
        LOGGER
            .info("begin to start model. modelId is " + this.taskId + ", modelTag is " + modelTag);
        if (canStart()) {
            HdfsFileSystemContainer.start();
            // 设置hdfs file system
            HdfsFileSystemContainer.register(this);

            for (AbstractTask task : tasks.values()) {
                task.start();
                TaskRunningPool.submit(task);
            }
            addHock();
            setStop(false);
            LOGGER.info("success to start mode!l");
            return true;
        } else {
            LOGGER.info("model is not allow to start!");
            return false;
        }
    }

    /**
     * 启动时，直接加入hock，防止出现hdfs相关hock优先与agent的hock执行，导致数据未刷新到hdfs提前退出
     */
    private void addHock() {
        ShutdownHookManager.get().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                stop(true);
            }
        }), 100);
    }

    @Override
    public boolean stop(boolean force) {
        if (isStop()) {
            LOGGER.info("model is already stoped. ignore! uniqueKey is " + getUniqueKey());
            return true;
        }
        while (stopping) {
            try {
                Thread.sleep(50L);
            } catch (Exception e) {
                LOGGER.error("sleep is intrrupted when stoping hdfs task. task's key is "
                             + this.uniqueKey);
                break;
            }
        }
        if (isStop()) {
            LOGGER.info("second check. model is already stoped. ignore! uniqueKey is "
                        + getUniqueKey());
            return true;
        }
        try {
            stopping = true;
            LOGGER.info("begin to stop model. force is " + force + ",modelId is " + this.taskId
                        + ", modelTag is " + modelTag);
            if (this.tasks != null) {
                for (AbstractTask task : tasks.values()) {
                    task.stop(force);
                }
            }

            HdfsFileSystemContainer.unregister(this);
            HdfsFileSystemContainer.stop();
        } catch (Exception e) {
            LogGather.recordErrorLog("Log2HdfsModel error", "stop model error!", e);
        } finally {
            stopping = false;
            setStop(true);
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

        HdfsFileSystemContainer.unregister(this);
        HdfsFileSystemContainer.stop();
        return true;
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        ModelConfig newConfig = (ModelConfig) newOne;
        if (newConfig.getVersion() <= this.modelConfig.getVersion()) {
            return false;
        }

        if (!((LogSourceConfig) this.modelConfig.getSourceConfig())
            .getMatchConfig()
            .getFileType()
            .equals(
                ((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig()
                    .getFileType())) {
            LOGGER.warn("matchConfig's filetype is not allowed to changed. ignore!");
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

                replaceHostName((LogSourceConfig) this.modelConfig.getSourceConfig());

                List<AbstractTask> updatedTask = getUpdatedTasks(oldConfig, newConfig);
                List<AbstractTask> addedTask = getAddedTasks(oldConfig, newConfig);
                List<AbstractTask> deletedTask = getDeletedTasks(oldConfig, newConfig);

                String oldUserName = ((HdfsTargetConfig) oldConfig.getTargetConfig()).getUsername();
                String oldPassword = ((HdfsTargetConfig) oldConfig.getTargetConfig()).getPassword();
                String oldRootPath = ((HdfsTargetConfig) oldConfig.getTargetConfig()).getRootPath();

                String newUserName = ((HdfsTargetConfig) newConfig.getTargetConfig()).getUsername();
                String newPassword = ((HdfsTargetConfig) newConfig.getTargetConfig()).getPassword();
                String newRootPath = ((HdfsTargetConfig) newConfig.getTargetConfig()).getRootPath();

                boolean needToChangeFileSystem = false;
                if (StringUtils.isNotBlank(newUserName)
                    && StringUtils.isNotBlank(newPassword)
                    && StringUtils.isNotBlank(newRootPath)
                    && ((StringUtils.isBlank(oldUserName) || StringUtils.isBlank(oldPassword))
                        || StringUtils.isBlank(newRootPath) || (!oldUserName.equals(newUserName) || !oldPassword
                        .equals(newPassword))) || !oldRootPath.equals(newRootPath)) {
                    // 新配置不为空
                    // 老配置为空 或者 新配置与老配置不同

                    // 同步offset，防止切换过程中导致的数据丢失
                    for (AbstractSource source : this.sources.values()) {
                        ((LogSource) source).syncOffset();
                    }

                    needToChangeFileSystem = true;
                    HdfsFileSystemContainer.changeFileSystem(this, newConfig, oldConfig);
                }

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
                    sources.remove(task.getSource().getUniqueKey());
                    tasks.remove(task.getUniqueKey());
                }

                if (needToChangeFileSystem) {
                    HdfsFileSystemContainer.release(this, oldConfig);
                }
            }
        }
        return true;
    }
}
