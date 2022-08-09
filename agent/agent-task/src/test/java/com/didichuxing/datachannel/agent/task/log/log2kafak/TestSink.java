package com.didichuxing.datachannel.agent.task.log.log2kafak;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaEvent;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaProducerContainer;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.agent.sink.utils.StringFilter;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-12-09 15:00
 */
public class TestSink extends AbstractSink<KafkaEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSink.class.getName());
    private static final long MAX_FAILED_SLEEP_TIME = 16000L;

    private static final long INITAL_TO_SLEEP_TIME = 500L;

    // 批量发送时间
    private long lastPutTime;

    private List<KafkaEvent> batch = new ArrayList<>();

    // 记录已发送的批次数
    private AtomicInteger batchSendCounter = new AtomicInteger(0);

    private volatile Boolean isStop = false;

    // 最新的有效kafkaEvent
    private KafkaEvent latestKafkaEvent = null;

    private KafkaTargetConfig kafkaTargetConfig;

    /**
     * 固定partiton key时的延迟量
     */
    private volatile long keyDelay = 10000L;

    private Map<String, Long> failedRateMapS0 = new ConcurrentHashMap<>();
    private Map<String, Long> failedRateMapS1 = new ConcurrentHashMap<>();

    public TestSink(ModelConfig config, AbstractChannel channel, int orderNum) {
        super(config, channel, orderNum);
    }

    @Override
    public String getThreadName() {
        return "test" + "_" + uniqueKey;
    }

    @Override
    public int getSendNum() {
        return batchSendCounter.get();
    }

    @Override
    public KafkaEvent wrap(Event event) {
        if (event != null) {
            if (event instanceof LogEvent) {
                LogEvent logEvent = (LogEvent) event;
                KafkaEvent kafkaEvent = new KafkaEvent(logEvent.getContent(), logEvent.getBytes().length,
                        logEvent.getTimestamp(), logEvent.getCollectTime(),
                        logEvent.getLogTime(), logEvent.getOffset(),
                        logEvent.getUniqueKey(), logEvent.getPreOffset(),
                        logEvent.getLogId(), logEvent.getLogPathId(),
                        logEvent.getFileKey(), logEvent.getFileNodeKey(),
                        logEvent.getDockerParentPath() == null ? logEvent.getFilePath() : logEvent.getDockerParentPath(),
                        logEvent.getFileName(), true);
                kafkaEvent.setNeedToSend(filter(kafkaEvent));
                latestKafkaEvent = kafkaEvent;
                if (kafkaEvent.isNeedToSend()) {
                    if (batch.isEmpty()) {
                        lastPutTime = System.currentTimeMillis();
                    }
                    // 防止 sink线程 和 task同时调用
                    synchronized (lock) {
                        batch.add(kafkaEvent);
                    }
                }
                return kafkaEvent;
            }
        }
        return null;
    }

    private boolean filter(KafkaEvent kafkaEvent) {
        long start = TimeUtils.getNanoTime();
        Boolean result = checkFilterRule(kafkaEvent);
        if (kafkaEvent != null && StringUtils.isNotBlank(kafkaTargetConfig.getFilterRule())) {
            if (taskPatternStatistics != null) {
                taskPatternStatistics.filterOneRecord(TimeUtils.getNanoTime() - start, result);
            }
        }
        return result;
    }

    /**
     * 返回true表示需要发送，返回false表示该条日志被过滤掉，不需要发送
     *
     * @param event
     * @return
     */
    private boolean checkFilterRule(KafkaEvent event) {
        if (event == null) {
            return true;
        }

        boolean canSend = true;
        // 没有过滤规则时，全部需要发送
        if (StringUtils.isBlank(kafkaTargetConfig.getFilterRule())) {
            canSend = true;
        } else {
            if (kafkaTargetConfig.getFilterOprType() == LogConfigConstants.FILTER_TYPE_CONTAINS) {
                canSend = StringFilter.doFilter(kafkaTargetConfig.getFilterRule(), event.getContent());
            } else if (kafkaTargetConfig.getFilterOprType() == LogConfigConstants.FILTER_TYPE_UNCONTAINS) {
                canSend = !StringFilter.doFilter(kafkaTargetConfig.getFilterRule(), event.getContent());
            } else {
                canSend = true;
            }
        }

        if (!canSend) {
            return false;
        }

        if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            if (COLLECT_ALL_WHEN_TEMPORALITY) {
                return true;
            }
            if (modelConfig.getCommonConfig().getStartTime() != null
                    && modelConfig.getCommonConfig().getEndTime() != null) {
                if (modelConfig.getCommonConfig().getStartTime().getTime() >= event.getMsgTime()
                        && modelConfig.getCommonConfig().getEndTime().getTime() <= event.getMsgTime()) {
                }
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void send(KafkaEvent kafkaEvent) {
        if ((batch.size() >= kafkaTargetConfig.getSendBatchSize()
                || (System.currentTimeMillis() - lastPutTime) >= kafkaTargetConfig.getSendBatchTimeThreshold())
                && !batch.isEmpty()) {

            batchSendCounter.incrementAndGet();

            synchronized (lock) {
                // 发送数据时，batch里不能再加入数据
                sendFromBatchLogs();
                // 发送完成后才可以清空
                batch.clear();
            }
        }
    }

    /**
     * 清空batchLogs中内容
     */
    private void sendFromBatchLogs() {
        // 失败则采用退避算法不断的阻塞重试
        if (!sendMessage(batch) && !isStop) {
            long initalSleepTime = INITAL_TO_SLEEP_TIME;
            while (true) {
                try {
                    Thread.sleep(initalSleepTime);
                } catch (Exception e) {
                    LogGather.recordErrorLog("KafkaSink error", "sendMessage sleep was interrupted!", e);
                }

                if (sendMessage(batch) || isStop) {
                    break;
                }
                initalSleepTime *= 2;
                if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                    initalSleepTime = INITAL_TO_SLEEP_TIME;
                }
            }
        }
    }

    /**
     * 发送消息
     *
     * @param events
     * @return
     */
    public boolean sendMessage(List<KafkaEvent> events) {
        return true;
    }

    @Override
    public boolean flush() {
        if (isStop) {
            LOGGER.warn("kafka sink has been closed. these is no need to flush. uniqueKey is " + this.uniqueKey);
            return false;
        }

        boolean result = true;
        synchronized (lock) {
            if (kafkaTargetConfig.getAsync()) {
                // 若batchLogs不为空，先清空
                if (!batch.isEmpty()) {
                    sendFromBatchLogs();
                    batch.clear();

                }

                if (latestKafkaEvent != null) {
                    // 异步发送，flush
                    batchSendCounter.set(0);
                    // flush成功，无任何错误记录
                    copyFailedOffset();
                    if (failedRateMapS1.size() == 0) {
                        result = true;
                    } else {
                        result = false;
                    }
                    latestKafkaEvent = null;
                }
            } else {
                // 同步时，线程阻塞
                if (batch.isEmpty()) {
                    if (latestKafkaEvent != null) {
                        batchSendCounter.set(0);
                        latestKafkaEvent = null;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void setMetrics(TaskMetrics taskMetrics) {

    }

    /**
     * 拷贝失败的rate信息
     */
    private void copyFailedOffset() {
        failedRateMapS1.clear();
        for (Map.Entry<String, Long> entry : failedRateMapS0.entrySet()) {
            failedRateMapS1.put(entry.getKey(), entry.getValue());
        }
        failedRateMapS0.clear();
    }

    @Override
    public boolean delete() {
        if (isRunning) {
            stop(true);
        }
        return true;
    }

    @Override
    public Map<String, Object> metric() {
        return null;
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop kafka sink. key is " + getUniqueKey());
        isRunning = false;
        isStop = true;
        KafkaProducerContainer.delete(this.modelConfig.getCommonConfig().getModelId(), orderNum);
        return true;
    }

    public synchronized void appendFaildOffset(String fileNodeKey, Long offset) {
        Long existOffset = failedRateMapS0.get(fileNodeKey);
        if (existOffset == null || existOffset > offset) {
            failedRateMapS0.put(fileNodeKey, offset);
        }
    }

    @Override
    public void configure(ComponentConfig config) {
        this.kafkaTargetConfig = (KafkaTargetConfig) this.modelConfig.getTargetConfig();
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        return false;
    }
}
