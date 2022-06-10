package com.didichuxing.datachannel.agent.engine;

import com.didichuxing.datachannel.agent.common.api.ComponentStatus;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.conf.Configurable;
import com.didichuxing.datachannel.agent.engine.limit.TaskLimit;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.agent.engine.metrics.source.TaskPatternStatistics;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.agent.engine.source.AbstractSource;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 抽象任务
 * @author: huangjw
 * @Date: 19/6/18 15:44
 */
public abstract class AbstractTask extends TaskComponent implements Runnable, Configurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class.getName());
    protected AbstractSource source = null;
    protected AbstractChannel channel = null;
    protected Map<String, AbstractSink> sinkers = new ConcurrentHashMap<>();
    public ModelConfig modelConfig = null;

    private List<Monitor> monitors = null;
    protected TaskLimit taskLimter = new TaskLimit();

    /**
     * 空闲休息
     */
    private final static Long IDLE_SLEEP_TIME = 500L;

    private TaskPatternStatistics taskPatternStatistics;

    protected abstract List<Monitor> getMonitors();

    @Override
    public boolean init(ComponentConfig config) {
        LOGGER.info("begin to init task's config, config is " + config);

        source.init(config);

        bulidUniqueKey();
        try {
            taskPatternStatistics = new TaskPatternStatistics(getUniqueKey(), this);
        } catch (MetricsException ex) {
            LOGGER.warn("taskPatternStatistics init error. taskPatternStatistics is " + taskPatternStatistics);
            return false;
        }

        source.setTaskPatternStatistics(taskPatternStatistics);
        source.setAgentStatistics(GlobalProperties.getAgentStatistics());

        channel.init(null);

        configure(config);
        for (AbstractSink sinker : sinkers.values()) {
            sinker.setTaskPatternStatistics(taskPatternStatistics);
            sinker.setAgentStatistics(GlobalProperties.getAgentStatistics());
            if (!sinker.init(config)) {
                LOGGER.warn("sinker init error. sink is " + sinker);
                return false;
            }
        }

        this.monitors = getMonitors();
        if (monitors != null) {
            for (Monitor monitor : monitors) {
                monitor.start();
            }
            for (Monitor monitor : monitors) {
                if (!monitor.register(this)) {
                    return false;
                }
            }
        }

        taskPatternStatistics.init();

        return taskLimter.init(this, config);
    }

    @Override
    public void run() {
        // task线程名称：task-Executor-4-logModeId-(path + host)
        String threadNamePrefix = getThreadNamePrefix(Thread.currentThread().getName());
        Thread.currentThread().setName(threadNamePrefix + "-" + modelConfig.getCommonConfig().getModelId() + "-"
                + source.getUniqueKey());
        try {
            prepare();
            source.start();
            for (AbstractSink sinker : sinkers.values()) {
                sinker.start();
            }
            while (true) {
                if (!process()) {
                    break;
                }
            }
        } catch (Throwable t) {
            LogGather.recordErrorLog("AbstractTask error", "unexpected error, task is " + this, t);
        } finally {
            stop(true);
        }
    }

    /**
     * 获取当前task线程名称前缀：默认线程名：task-Executor-3-1-1
     * @param currentThreadName：线程初始名称
     * @return：
     */
    private String getThreadNamePrefix(String currentThreadName) {
        if (StringUtils.ordinalIndexOf(currentThreadName, "-", 4) != -1) {
            return currentThreadName.substring(0, StringUtils.ordinalIndexOf(currentThreadName, "-", 4));
        } else {
            return currentThreadName;
        }
    }

    private boolean process() throws Exception {
        if (getStatus() == ComponentStatus.STOP.getStatus()) {
            LOGGER.info("task is stopped. config is " + modelConfig);
            return false;
        }

        Event event = source.tryGetEvent();
        if (event != null && event.length() > 0) {
            double limitTime = taskLimter.limitRate(event.length());
            if (limitTime > 0.0d) {
                // 放大到毫秒精度
                this.taskPatternStatistics.limitOneRecord(new Double(limitTime * 1000).longValue());
            }
        }
        channel.tryAppend(event);

        if (needToFlush(event)) {
            long start = TimeUtils.getNanoTime();
            if (flush()) {
                commit();
                if (this.taskPatternStatistics != null) {
                    this.taskPatternStatistics.flushOneRecord(TimeUtils.getNanoTime() - start);
                }
            } else {
                rollback();
                if (this.taskPatternStatistics != null) {
                    this.taskPatternStatistics.flushFailedRecord(TimeUtils.getNanoTime() - start);
                }
            }
            reset();
        }

        if (event == null) {
            if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY
                    && canStop()) {
                // 补采完成自动停止
                LOGGER.info("task is temporality.it can be stoped now. uniqueKey is " + getUniqueKey());
                return false;
            }

            // 不需要关闭采集
            try {
                Thread.sleep(IDLE_SLEEP_TIME);
            } catch (InterruptedException e) {
                LogGather.recordErrorLog("AbstractTask error",
                                         "idleSleep is Interrupted. uniqueKey is " + getUniqueKey(), e);
            }
        }
        return true;
    }

    /**
     * 清空队列
     */
    public void clearChannel() {
        AbstractSink sink = null;
        for (AbstractSink s : sinkers.values()) {
            sink = s;
            break;
        }
        while (channel.size() > 0) {
            if (sink != null) {
                sink.sendMsg();
            }
        }
    }

    /**
     * 启动前执行的步骤
     */
    public abstract void prepare();

    /**
     * 是否刷新
     *
     * @return
     */
    public abstract boolean needToFlush(Event event);

    /**
     * 重置
     *
     * @return
     */
    public abstract void reset();

    /**
     * flush
     *
     * @return
     */
    public abstract boolean flush();

    /**
     * 回滚
     */
    public abstract void rollback();

    /**
     * 提交进度
     */
    public abstract void commit();

    /**
     * 获取metrics
     *
     * @return
     */
    @Override
    public abstract Map<String, Object> metric();

    /**
     * 根据编号创建 sink
     *
     * @param orderNum
     */
    public abstract void addSink(int orderNum);

    /**
     * 判断临时采集情况下，是否可以停止
     */
    public abstract boolean canStop();

    /**
     * 根据编号删除 sink
     *
     * @param orderNum
     */
    public void delSink(int orderNum) {
        LOGGER.info("del Sink. orderNum is " + orderNum + ",uniqueKey is " + getUniqueKey());
        String sinkUniqueKey = "";
        for (AbstractSink sink : sinkers.values()) {
            if (sink.getOrderNum() == orderNum) {
                sink.delete();
                sinkUniqueKey = sink.getUniqueKey();
                break;
            }
        }

        sinkers.remove(sinkUniqueKey);
    }

    @Override
    public boolean start() {
        setStatus(ComponentStatus.RUNNING.getStatus());
        return true;
    }

    public void interrupt() {
        LOGGER.info("begin to interrupt.config is " + modelConfig.toString());
        setStatus(ComponentStatus.STOP.getStatus());
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop task. force is " + force + ",uniqueKey is " + this.uniqueKey);
        if (getStatus() == ComponentStatus.STOP.getStatus()) {
            LOGGER.info("task is stopped. ignore!");
            return true;
        }
        if (monitors != null) {
            for (Monitor monitor : monitors) {
                monitor.unregister(this);
            }

            for (Monitor monitor : monitors) {
                monitor.stop();
            }
        }

        interrupt();
        source.stop(force);
        channel.stop(force);

        for (AbstractSink sink : sinkers.values()) {
            sink.stop(force);
        }

        taskLimter.stop();
        taskPatternStatistics.destory();
        LOGGER.info("stop task success. uniqueKey is " + getUniqueKey());
        return true;
    }

    @Override
    public boolean delete() {
        LOGGER.info("begin to delete task. uniqueKey is " + uniqueKey);
        try {
            stop(false);
            source.delete();
            channel.delete();
            for (AbstractSink sink : sinkers.values()) {
                sink.delete();
            }
        } catch (Exception e) {
            LOGGER.error("AbstractTask error",
                    "AbstractTask special delete error! uniqueKey is {}" + uniqueKey, e.getMessage());
        }
        return true;
    }

    /**
     * 特殊删除逻辑
     */
    public void specialDelete(Object object) {
        LOGGER.info("begin to special delete task. uniqueKey is " + uniqueKey + ", object is " + object);
        try {
            stop(false);
            source.specialDelete(object);
            channel.delete();
            for (AbstractSink sink : sinkers.values()) {
                sink.delete();
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("AbstractTask error",
                                     "AbstractTask special delete error! uniqueKey is " + uniqueKey, e);
        }
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        ModelConfig newConfig = (ModelConfig) newOne;

        LOGGER.info("begin to change task's config.newOne is " + newConfig);
        // sink数量变更
        int oldNum = this.modelConfig.getTargetConfig().getSinkNum();
        int newNum = newConfig.getTargetConfig().getSinkNum();

        this.modelConfig = newConfig;
        this.source.onChange(newConfig);
        this.channel.onChange(newConfig);
        if (oldNum != newNum) {
            if (oldNum > newNum) {
                for (int i = oldNum - 1; i >= newNum; i--) {
                    delSink(i);
                }
            } else {
                for (int i = oldNum; i < newNum; i++) {
                    addSink(i);
                }
            }
        }
        for (AbstractSink sink : sinkers.values()) {
            try {
                if (sink.isInited()) {
                    sink.onChange(newConfig);
                } else {
                    LOGGER.info("this sink is new sink. begin to start it. uniqueKey is " + sink.getUniqueKey());
                    sink.init(newConfig);
                }

                if (!sink.isRunning()) {
                    sink.start();
                }
            } catch (Exception e) {
                LogGather.recordErrorLog("AbstractTask error", "AbstractTask change sink error!", e);
            }
        }
        taskLimter.onChange(newConfig);
        LOGGER.info("change task success.");
        return true;
    }

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public void setModelConfig(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    public AbstractSource getSource() {
        return source;
    }

    public void setSource(AbstractSource source) {
        this.source = source;
    }

    public AbstractChannel getChannel() {
        return channel;
    }

    public void setChannel(AbstractChannel channel) {
        this.channel = channel;
    }

    public Map<String, AbstractSink> getSinkers() {
        return sinkers;
    }

    public void setSinkers(Map<String, AbstractSink> sinkers) {
        this.sinkers = sinkers;
    }

    public TaskLimit getTaskLimter() {
        return taskLimter;
    }

    public void setTaskLimter(TaskLimit taskLimter) {
        this.taskLimter = taskLimter;
    }

    public abstract void setMetrics(TaskMetrics taskMetrics);

}
