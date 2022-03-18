package com.didichuxing.datachannel.agent.engine.sinker;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.conf.Configurable;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.agent.engine.metrics.source.AgentStatistics;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.engine.metrics.source.TaskPatternStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: sink抽象
 * @author: huangjw
 * @Date: 19/6/18 16:30
 */
public abstract class AbstractSink<T extends Event> extends TaskComponent implements Runnable,
                                                                         Configurable {

    private static final Logger     LOGGER                       = LoggerFactory
                                                                     .getLogger(AbstractSink.class
                                                                         .getName());

    AbstractChannel                 channel;

    // 顺序编号
    public int                      orderNum;

    public ModelConfig              modelConfig;

    protected Thread                thread;

    protected volatile boolean      isRunning                    = false;
    protected boolean               isInited                     = false;

    protected final Object          lock                         = new Object();

    private int                     eventIsNullMaxTimes          = 10;
    private int                     eventIsNullTimes             = 0;

    /**
     * 临时采集时，是否采集所有的日志文件
     */
    protected volatile boolean      COLLECT_ALL_WHEN_TEMPORALITY = false;

    protected TaskPatternStatistics taskPatternStatistics;

    protected AgentStatistics       agentStatistics;

    public AbstractSink(ModelConfig modelConfig, AbstractChannel channel, int orderNum) {
        this.channel = channel;
        this.orderNum = orderNum;
        this.modelConfig = modelConfig;
        bulidUniqueKey();
    }

    @Override
    public void bulidUniqueKey() {
        Long modelId = this.modelConfig.getCommonConfig().getModelId();
        String tag = this.modelConfig.getTargetConfig().getTag();
        String channelUk = getChannel().getUniqueKey();
        setUniqueKey(modelId + "_" + tag + "_" + channelUk + "_" + orderNum);
    }

    @Override
    public boolean init(ComponentConfig config) {
        try {
            configure(config);
            thread = new Thread(this, getThreadName());
            isInited = true;
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 线程名
     *
     * @return
     */
    public abstract String getThreadName();

    @Override
    public boolean start() {
        LOGGER.info("begin to start sink. key is " + getUniqueKey());
        isRunning = true;
        if (thread != null) {
            thread.start();
        }
        return true;
    }

    @Override
    public void run() {
        LOGGER.info("begin to sink data from channel. key is " + getUniqueKey());
        while (isRunning) {
            sendMsg();
        }
        LOGGER.info("Sink thread exit！key is " + getUniqueKey() + ";isRunning :" + isRunning);
    }

    public void sendMsg() {
        try {
            // 消费
            Event event = channel.tryGet();
            // 包装
            T result = wrap(event);
            // 发送
            send(result);
            // 消费成功，提交
            channel.commitEvent(event);

            if (result != null) {
                if (taskPatternStatistics != null) {
                    taskPatternStatistics.controlOneRecord(TimeUtils.getNanoTime()
                                                           - event.getTransNanoTime());
                }
            }
            if (event == null) {
                eventIsNullTimes++;
                if (eventIsNullTimes >= eventIsNullMaxTimes) {
                    Thread.sleep(10L);
                    eventIsNullTimes = 0;
                }
            } else {
                eventIsNullTimes = 0;
            }
        } catch (Throwable t) {
            LogGather.recordErrorLog("AbstractSink error", "sendMsg error! key is "
                                                           + getUniqueKey(), t);
        }
    }

    public TaskPatternStatistics getTaskPatternStatistics() {
        return taskPatternStatistics;
    }

    public void setTaskPatternStatistics(TaskPatternStatistics taskPatternStatistics) {
        this.taskPatternStatistics = taskPatternStatistics;
    }

    public AgentStatistics getAgentStatistics() {
        return agentStatistics;
    }

    public void setAgentStatistics(AgentStatistics agentStatistics) {
        this.agentStatistics = agentStatistics;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    abstract public int getSendNum();

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    public void setModelConfig(ModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }

    abstract public T wrap(Event event);

    abstract public void send(T t);

    abstract public boolean flush();

    public AbstractChannel getChannel() {
        return channel;
    }

    public void setChannel(AbstractChannel channel) {
        this.channel = channel;
    }

    public void needToCollectAll(boolean need) {
        COLLECT_ALL_WHEN_TEMPORALITY = need;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isInited() {
        return isInited;
    }

    public void setInited(boolean inited) {
        isInited = inited;
    }

    public abstract void setMetrics(TaskMetrics taskMetrics);

}
