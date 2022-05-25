package com.didichuxing.datachannel.agent.task.log.log2kafak;

import java.util.List;

import com.didichuxing.datachannel.agent.task.log.LogModel;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.api.FileType;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;
import com.didichuxing.datachannel.agent.engine.source.AbstractSource;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaProducerContainer;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: log2kafka模型
 * @author: huangjw
 * @Date: 19/7/2 18:03
 */
public class Log2KafkaModel extends LogModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Log2KafkaModel.class.getName());

    public Log2KafkaModel(ModelConfig config) {
        super(config);
    }

    @Override
    public AbstractTask buildTask(ModelConfig config, LogSource logSource) {
        Log2KafkaTask task = new Log2KafkaTask(config, logSource);
        return task;
    }

    @Override
    public void configure(ComponentConfig config) {
        this.modelConfig = (ModelConfig) config;
        LOGGER.info("begin to init LogModel. config is " + this.modelConfig);
        LogSourceConfig logSourceConfig = (LogSourceConfig) (this.modelConfig.getSourceConfig());
        KafkaTargetConfig kafkaTargetConfig = (KafkaTargetConfig) (this.modelConfig
            .getTargetConfig());

        // replace
        replaceHostName(logSourceConfig);

        // 替换topic
        selectTargetTopic(kafkaTargetConfig);

        List<LogPath> logPaths = logSourceConfig.getLogPaths();
        if (logPaths != null) {
            for (LogPath logPath : logPaths) {
                if (((LogSourceConfig) this.modelConfig.getSourceConfig()).getMatchConfig()
                    .getFileType() == FileType.File.getStatus()) {
                    // 文件
                    LogSource logSource = new LogSource(modelConfig, logPath);
                    addTask(this.modelConfig, logSource);
                } else {
                    // 目录
                    List<LogSource> logSources = getSourcesByDir(logPath.getRealPath(), logPath);
                    for (LogSource logSource : logSources) {
                        addTask(this.modelConfig, logSource);
                    }
                }
            }
        }
    }

    /**
     * 多个topic选择发送
     *
     * @param kafkaTargetConfig collector config
     */
    public void selectTargetTopic(KafkaTargetConfig kafkaTargetConfig) {
        if (kafkaTargetConfig == null) {
            return;
        }
        String topic = kafkaTargetConfig.getTopic();
        kafkaTargetConfig.setTopic(CommonUtils.selectTopic(topic));
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
                selectTargetTopic((KafkaTargetConfig) this.modelConfig.getTargetConfig());

                List<AbstractTask> updatedTask = getUpdatedTasks(oldConfig, newConfig);
                List<AbstractTask> addedTask = getAddedTasks(oldConfig, newConfig);
                List<AbstractTask> deletedTask = getDeletedTasks(oldConfig, newConfig);

                for (AbstractTask task : updatedTask) {
                    /*
                     * 采用删、添加方式
                     */
                    //删
                    deleteTask(task);
                    //添 加
                    LogPath logPath = ((LogSourceConfig) newConfig.getSourceConfig())
                        .getLogPathMap().get(
                            ((LogSource) task.getSource()).getLogPath().getPathId());
                    if (null != logPath) {
                        LogSource logSourceAdd = new LogSource(newConfig, logPath);
                        AbstractTask taskAdd = buildTask(newConfig, logSourceAdd);
                        addTask(taskAdd);
                    } else {
                        //TODO：log it
                    }

                }

                for (AbstractTask task : addedTask) {
                    addTask(task);
                }

                for (AbstractTask task : deletedTask) {
                    deleteTask(task);
                }

                String oldPro = ((KafkaTargetConfig) oldConfig.getTargetConfig()).getProperties();
                String oldBoot = ((KafkaTargetConfig) oldConfig.getTargetConfig()).getBootstrap();
                String newPro = ((KafkaTargetConfig) newConfig.getTargetConfig()).getProperties();
                String newBoot = ((KafkaTargetConfig) newConfig.getTargetConfig()).getBootstrap();

                if (StringUtils.isNotBlank(newPro)
                    && StringUtils.isNotBlank(newBoot)
                    && ((StringUtils.isBlank(oldPro) || StringUtils.isBlank(oldBoot)) || (!newPro
                        .equals(oldPro) || !newBoot.equals(oldBoot)))) {
                    // 新配置不为空
                    // 老配置为空 或者 新配置与老配置不同

                    // 同步offset，防止切换过程中导致的数据丢失
                    for (AbstractSource source : this.sources.values()) {
                        ((LogSource) source).syncOffset();
                    }

                    KafkaProducerContainer.update(this.modelConfig);
                }
            }
        }
        return true;
    }

    private void addTask(AbstractTask task) {
        this.sources.put(task.getSource().getUniqueKey(), task.getSource());
        task.init(this.modelConfig);
        task.start();
        tasks.put(task.getUniqueKey(), task);
        TaskRunningPool.submit(task);
    }

    private void deleteTask(AbstractTask task) {
        task.delete();
        tasks.remove(task.getUniqueKey());
        this.sources.remove(task.getSource().getUniqueKey());
    }
}
