package com.didichuxing.datachannel.agent.sink.kafkaSink;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.didichuxing.datachannel.agent.common.api.TopicPartitionKeyTypeEnum;
import com.didichuxing.datachannel.agent.common.constants.Tags;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.agent.sink.utils.EventUtils;
import com.didichuxing.datachannel.agent.sink.utils.StringFilter;
import com.didichuxing.datachannel.agent.sink.utils.serializer.JsonMqEventSerializer;
import com.didichuxing.datachannel.agent.sink.utils.serializer.MqEventSerializer;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.api.TransFormate;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: kafka sink
 * @author: huangjw
 * @Date: 19/6/18 16:27
 */
public class KafkaSink extends AbstractSink<KafkaEvent> {

private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSink.class.getName());private static final long    MAX_FAILED_SLEEP_TIME = 16000L;

    private static final long    INITAL_TO_SLEEP_TIME  = 500L;

    // 批量发送时间
    private long                 lastPutTime;

    private List<KafkaEvent>     batch                 = new ArrayList<>();

    private KafkaMessageProducer producer              = null;
    private volatile boolean     producerHasBeenSet    = false;

    // 记录已发送的批次数
    private AtomicInteger        batchSendCounter      = new AtomicInteger(0);

    private volatile Boolean     isStop                = false;

    // 最新的有效kafkaEvent
    private KafkaEvent           latestKafkaEvent      = null;

    private KafkaTargetConfig    kafkaTargetConfig;

    /**
     * 固定partiton key时的延迟量
     */
    private volatile long        keyDelay              = 10000L;

    private Map<String, Long>    failedRateMapS0       = new ConcurrentHashMap<>();
    private Map<String, Long>    failedRateMapS1       = new ConcurrentHashMap<>();

    // topic轮转值，兼容logx场景
    private int                  topicRobinIndex       = 0;

    // 0：代表普通日志、1：public日志
    private Integer              businessType;

    // 根据partition数量自动判断是否采用固定时间轮转partition key发送模式
    private int                topicPartitionKeyType = TopicPartitionKeyTypeEnum.UNKNOWN.getType();

    private MqEventSerializer eventSerializer;

    public KafkaSink(ModelConfig config, AbstractChannel channel, int orderNum){
        super(config, channel, orderNum);
    }

    @Override
    public void configure(ComponentConfig config) {
        this.kafkaTargetConfig = (KafkaTargetConfig) this.modelConfig.getTargetConfig();
        businessType = ((LogSourceConfig) (this.modelConfig.getSourceConfig())).getMatchConfig().getBusinessType();

        this.eventSerializer = new JsonMqEventSerializer(this);

        setKeyDelay();
    }

    private void setKeyDelay() {
        try {
            String regularPartKey = kafkaTargetConfig.getRegularPartKey();
            if (StringUtils.isNotBlank(regularPartKey) && regularPartKey.startsWith(LogConfigConstants.TIME_FLAG)) {
                // regularPartKey类似为${time}-1000，符号-后的表示delay的毫秒数
                String time = regularPartKey.substring(LogConfigConstants.TIME_FLAG.length() + 1);
                if (StringUtils.isNotBlank(time)) {
                    Long delay = Long.parseLong(time);
                    if (delay > 0) {
                        keyDelay = delay;
                    }
                }
            }
        } catch (Exception e) {
            keyDelay = 10000L;
            LogGather.recordErrorLog("Log2KafkaModel error", "setKeyDelay error!", e);
        }
        LOGGER.info("current keyDelay is " + keyDelay + ", modeId is "
                    + this.modelConfig.getCommonConfig().getModelId());
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
                                                       logEvent.getFilePath(), logEvent.getFileName(), logEvent.isNeedToSend());
                //已经过滤掉了预估日志
                if(kafkaEvent.isNeedToSend()){
                    kafkaEvent.setNeedToSend(filter(kafkaEvent));
                }
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
                && modelConfig.getCommonConfig().getEndTime() != null
                && modelConfig.getCommonConfig().getStartTime().getTime() <= event.getMsgTime()
                && modelConfig.getCommonConfig().getEndTime().getTime() >= event.getMsgTime()) {
                return true;

            }
            return false;
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
        long startTime = System.currentTimeMillis();
        try {
            setProducer();
            return sendEvents(events, kafkaTargetConfig.getAsync());
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaSink error",
                                     "sendMessage error!timecost is " + (System.currentTimeMillis() - startTime)
                                                        + ",batchKafkaEvent size is " + batch.size(),
                                     e);
        }
        return false;
    }

    /**
     * 发送kafkaEvents
     *
     * @param events
     * @param isAsync
     * @return
     */
    private boolean sendEvents(List<KafkaEvent> events, Boolean isAsync) {
        if (events == null || events.size() == 0) {
            return true;
        }
        boolean result = false;
        List<KafkaEvent> copys = new ArrayList<>();
        copys.addAll(events);

        if (StringUtils.isNotBlank(kafkaTargetConfig.getKeyFormat())
            && StringUtils.isNotBlank(kafkaTargetConfig.getKeyStartFlag())) {
            // 按照指定key发送
            result = sendItemOrContent(copys, isAsync);
        } else {
            int transFormate = kafkaTargetConfig.getTransFormate();
            if (transFormate == TransFormate.MQList.getStatus()) {
                // 按照list发送
                result = sendBatch(copys, isAsync);
            } else if (transFormate == TransFormate.MQItem.getStatus()) {
                // 单个发送
                result = sendItemOrContent(copys, isAsync);
            } else {
                // 纯内容发送
                result = sendItemOrContent(copys, isAsync);
            }
        }
        return result;
    }

    /**
     * 批量发送
     *
     * @param mqEvents
     * @param isAsync
     * @return
     */
    private boolean sendBatch(List<KafkaEvent> mqEvents, Boolean isAsync) {

        byte[] content = eventSerializer.serializeArray(mqEvents);
        if (content == null) {
            return false;
        }
        if (eventSerializer.checkIsTooLarge(content)) {
            KafkaEvent one = mqEvents.get(0);
            LOGGER.warn("content is too large, so it be send one by one.modelId is "
                    + modelConfig.getCommonConfig().getModelId() + ", sourceItem is "
                    + one.getSourceItemHeaderName() + one.getSourceItemName() + ", rate is " + one.getRate());
            // 消息过大
            for (KafkaEvent mqEvent : mqEvents) {
                byte[] contentToSend = eventSerializer.getValidEvent(mqEvent);
                if (contentToSend != null) {
                    if (!sendReally(Collections.singletonList(mqEvent), contentToSend, isAsync)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return sendReally(mqEvents, content, isAsync);
        }
    }

    /**
     * 单条发送 mq + content
     *
     * @param mqEvents
     * @param isAsync
     * @return
     */
    private boolean sendItemOrContent(List<KafkaEvent> mqEvents, Boolean isAsync) {
        for (KafkaEvent mqEvent : mqEvents) {
            if (StringUtils.isNotBlank(kafkaTargetConfig.getKeyFormat())
                    && StringUtils.isNotBlank(kafkaTargetConfig.getKeyStartFlag())) {
                // 按照key发送
                if (!sendByKey(mqEvent, isAsync)) {
                    return false;
                }
            } else {
                // 正常发送
                byte[] contentToSend = eventSerializer.getValidEvent(mqEvent);
                if (contentToSend != null) {
                    if (!sendReally(Collections.singletonList(mqEvent), contentToSend, isAsync)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 实际发送消息
     *
     * @param content
     * @param isAsync
     * @return
     */
    private boolean sendReally(List<KafkaEvent> kafkaEvents, byte[] content, boolean isAsync) {
        try {
            Long start = TimeUtils.getNanoTime();
            String key = EventUtils.getPartitionKey(this,
                                                    kafkaEvents.size() > 0 ? kafkaEvents.get(0) : new KafkaEvent(), topicPartitionKeyType);
            String sourceItemKey = null;
            long rate = -1L;

            int bytes = 0;
            for (KafkaEvent kafkaEvent : kafkaEvents) {
                bytes += kafkaEvent.length();
                if (StringUtils.isBlank(sourceItemKey)) {
                    sourceItemKey = kafkaEvent.getSourceItemKey();
                }

                if (rate == -1 || (rate != -1 && rate > kafkaEvent.getPreRate())) {
                    // 移动到这条数据开头的offset
                    rate = kafkaEvent.getPreRate();
                }
            }

            if (sourceItemKey == null) {
                return true;
            }

            if (!isAsync) {
                long initalSleepTime = INITAL_TO_SLEEP_TIME;
                // 保证单条重试
                while (!isStop) {
                    try {
                        setProducer();
                        boolean result = producer.sendSync(getTargetTopic(kafkaTargetConfig.getTopic()), key, content);
                        if (result) {
                            break;
                        } else {
                            Thread.sleep(initalSleepTime);
                            initalSleepTime *= 2;
                            if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                                initalSleepTime = INITAL_TO_SLEEP_TIME;
                            }
                        }
                    } catch (Exception e) {
                        LogGather.recordErrorLog("KafkaSink error", "send to Kafka error.topic is "
                                                                    + getTargetTopic(kafkaTargetConfig.getTopic()),
                                                 e);
                    }
                }
                if (taskPatternStatistics != null) {
                    Long cost = TimeUtils.getNanoTime() - start;
                    taskPatternStatistics.sinkMutilRecord(kafkaEvents.size(), bytes, cost);
                    agentStatistics.sinkMutilRecord(kafkaEvents.size(), bytes, cost);
                }
            } else {
                return producer.send(getTargetTopic(kafkaTargetConfig.getTopic()), key, content,
                                     new KafkaCallBack(this, kafkaEvents.size(), bytes, start,
                                                       getTargetTopic(kafkaTargetConfig.getTopic()),
                                                       modelConfig.getCommonConfig().getModelId(), sourceItemKey, rate));
            }
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaSink error", "send to Kafka error.modelId is "
                                                        + this.modelConfig.getCommonConfig().getModelId(),
                                     e);
        }
        return false;
    }

    /**
     * 按照指定的key发送
     *
     * @param mqEvent
     * @param isAsync
     * @return
     */
    private boolean sendByKey(KafkaEvent mqEvent, boolean isAsync) {
        Long start = TimeUtils.getNanoTime();
        byte[] contentToSend = eventSerializer.getValidEvent(mqEvent);
        if (contentToSend == null) {
            return true;
        }
        String key = EventUtils.getPartitionKey(this, mqEvent, topicPartitionKeyType);
        int bytes = mqEvent.length();
        String sourceItemKey = mqEvent.getSourceItemKey();
        long rate = mqEvent.getPreRate();
        try {
            if (!isAsync) {
                long initalSleepTime = INITAL_TO_SLEEP_TIME;
                // 保证单条重试
                while (!isStop) {
                    try {
                        setProducer();
                        boolean result = producer.sendSync(kafkaTargetConfig.getTopic(), key, contentToSend);
                        if (result) {
                            break;
                        } else {
                            Thread.sleep(initalSleepTime);
                            initalSleepTime *= 2;
                            if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                                initalSleepTime = INITAL_TO_SLEEP_TIME;
                            }
                        }
                    } catch (Exception e) {
                        LogGather.recordErrorLog("MqSink error",
                                "send to Mq error.topic is " + kafkaTargetConfig.getTopic(), e);
                    }
                }

                if (taskPatternStatistics != null) {
                    Long cost = TimeUtils.getNanoTime() - start;
                    taskPatternStatistics.sinkOneRecord(bytes, cost);
                    agentStatistics.sinkOneRecord(bytes, cost);
                }
            } else {
                if (modelConfig.getTag().equals(Tags.TASK_LOG2KAFKA)) {
                    return producer.send(kafkaTargetConfig.getTopic(), key, contentToSend,
                            new KafkaCallBack(this, 1, bytes, start, kafkaTargetConfig.getTopic(),
                                    modelConfig.getCommonConfig().getModelId(), sourceItemKey,
                                    rate));
                } else {
                    //TODO：不支持其他处理方式

                }
            }
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("MqSink error", "send to mq by Key error.modelId is "
                            + this.modelConfig.getCommonConfig().getModelId(),
                    e);
        }
        return false;
    }

    /**
     * 同步发送单条消息
     *
     * @param content
     * @param sourceItemKey
     */
    public void syncSend(String content, String sourceItemKey) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        try {
            long initalSleepTime = INITAL_TO_SLEEP_TIME;
            // 保证单条重试
            while (!isStop) {
                try {
                    setProducer();
                    KafkaEvent event = new KafkaEvent();
                    event.setSourceItemKey(sourceItemKey);
                    String key = EventUtils.getPartitionKey(this, event, topicPartitionKeyType);
                    boolean result = producer.sendSync(getTargetTopic(kafkaTargetConfig.getTopic()), key, content.getBytes(StandardCharsets.UTF_8));
                    if (result || isStop) {
                        break;
                    } else {
                        Thread.sleep(initalSleepTime);
                        initalSleepTime *= 2;
                        if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                            initalSleepTime = INITAL_TO_SLEEP_TIME;
                        }
                    }
                } catch (Exception e) {
                    LogGather.recordErrorLog("KafkaSink error", "send to Kafka error.topic is "
                                                                + getTargetTopic(kafkaTargetConfig.getTopic()),
                                             e);
                }
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaSink error", "send content error!", e);
        }
    }

    /**
     * 初始化producer
     */
    private void setProducer() {
        producer = KafkaProducerContainer.getMessageProducer(this);
        producerHasBeenSet = true;
        if (producer == null) {
            LogGather.recordErrorLog("EventSender error",
                                     "producer is null! begin to retry to get producer! modelId is "
                                                          + modelConfig.getCommonConfig().getModelId() + ",orderNum is "
                                                          + orderNum);
            long initalSleepTime = INITAL_TO_SLEEP_TIME;
            while (!isStop) {
                try {
                    Thread.sleep(initalSleepTime);
                } catch (Exception e) {
                    LogGather.recordErrorLog("KafkaSink error", "getProducer sleep was interrupted!", e);
                }
                producer = KafkaProducerContainer.getMessageProducer(this);
                if (producer != null) {
                    break;
                }
                LogGather.recordErrorLog("EventSender error",
                                         "producer is null! retry! modelId is "
                                                              + modelConfig.getCommonConfig().getModelId()
                                                              + ",orderNum is " + orderNum);
                initalSleepTime *= 2;
                if (initalSleepTime > MAX_FAILED_SLEEP_TIME) {
                    initalSleepTime = INITAL_TO_SLEEP_TIME;
                }
            }
        }
    }

    /**
     * 只要执行flush均表明 latestKafkaEvent可使用
     * @return
     */
    @Override
    public boolean flush() {
        if (isStop) {
            LOGGER.warn("kafka sink has been closed. these is no need to flush. uniqueKey is " + this.uniqueKey);
            return false;
        }

        if (producer == null) {
            /**
             * 用于区分从未初始化过producer与producer初始化失败的case
             * 从未初始化过producer：直接返回true
             * 初始化了producer，但是初始化失败：返回false
             */
            return !producerHasBeenSet;
        }

        boolean result = true;
        synchronized (lock) {
            if (kafkaTargetConfig.getAsync()) {
                // 若batchLogs不为空，先清空
                if (!batch.isEmpty()) {
                    sendFromBatchLogs();
                    batch.clear();

                }

                if (latestKafkaEvent != null && !producer.isStop()) {
                    // 异步发送，flush
                    batchSendCounter.set(0);
                    if (producer.flush()) {
                        // flush成功，无任何错误记录
                        copyFailedOffset();
                        if (failedRateMapS1.size() == 0) {
                            result = true;
                        } else {
                            result = false;
                        }
                        latestKafkaEvent = null;
                    }
                }
            } else {
                // 同步时，线程阻塞
                if (batch.isEmpty()) {
                    if (latestKafkaEvent != null && !producer.isStop()) {
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
        taskMetrics.setReceiverclusterid(kafkaTargetConfig.getClusterId().longValue());
        taskMetrics.setReceiverclustertopic((kafkaTargetConfig.getTopic()));
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
    public void bulidUniqueKey() {
        Long modelId = this.modelConfig.getCommonConfig().getModelId();
        String topic = ((KafkaTargetConfig) (this.modelConfig.getTargetConfig())).getTopic();
        String tag = this.modelConfig.getTargetConfig().getTag();
        String channelUk = getChannel().getUniqueKey();
        setUniqueKey(modelId + "_" + topic + "_" + tag + "_" + channelUk + "_" + orderNum);
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop kafka sink. key is " + getUniqueKey());
        isRunning = false;
        isStop = true;
        KafkaProducerContainer.delete(this.modelConfig.getCommonConfig().getModelId(), orderNum);
        return true;
    }

    @Override
    public boolean delete() {
        if (isRunning) {
            stop(true);
        }
        return true;
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        LOGGER.info("begin to change kafka sink config. config is " + newOne);
        this.modelConfig = (ModelConfig) newOne;
        configure(newOne);
        return false;
    }

    public synchronized void appendFaildOffset(String fileNodeKey, Long offset) {
        Long existOffset = failedRateMapS0.get(fileNodeKey);
        if (existOffset == null || existOffset > offset) {
            failedRateMapS0.put(fileNodeKey, offset);
        }
    }

    @Override
    public int getSendNum() {
        return batchSendCounter.get();
    }

    public KafkaTargetConfig getKafkaTargetConfig() {
        return kafkaTargetConfig;
    }

    public void setKafkaTargetConfig(KafkaTargetConfig kafkaTargetConfig) {
        this.kafkaTargetConfig = kafkaTargetConfig;
    }

    public long getKeyDelay() {
        return keyDelay;
    }

    public void setKeyDelay(long keyDelay) {
        this.keyDelay = keyDelay;
    }

    public KafkaEvent getLatestKafkaEvent() {
        return latestKafkaEvent;
    }

    public void setLatestKafkaEvent(KafkaEvent latestKafkaEvent) {
        this.latestKafkaEvent = latestKafkaEvent;
    }

    public Map<String, Long> getFailedRateMapS0() {
        return failedRateMapS0;
    }

    public void setFailedRateMapS0(Map<String, Long> failedRateMapS0) {
        this.failedRateMapS0 = failedRateMapS0;
    }

    public Map<String, Long> getFailedRateMapS1() {
        return failedRateMapS1;
    }

    public void setFailedRateMapS1(Map<String, Long> failedRateMapS1) {
        this.failedRateMapS1 = failedRateMapS1;
    }

    public List<KafkaEvent> getBatch() {
        return batch;
    }

    public void setBatch(List<KafkaEvent> batch) {
        this.batch = batch;
    }

    @Override
    public Map<String, Object> metric() {

        return null;
    }

    @Override
    public String getThreadName() {
        return Tags.TARGET_KAFKA + "_" + uniqueKey;
    }

    /**
     * 根据topicModel获取到指定topic
     * @param topicModel
     * @return
     */
    private String getTargetTopic(String topicModel) {
        try {
            if (topicModel.contains("[")) {
                String topicPrefix = topicModel.substring(0, topicModel.indexOf("["));
                int endIndex = Integer.parseInt(topicModel.substring(topicModel.indexOf(",") + 1,
                                                                     topicModel.indexOf("]")));
                if (topicRobinIndex > endIndex) {
                    topicRobinIndex = 0;
                }

                String topic = topicPrefix + "" + topicRobinIndex;
                topicRobinIndex++;
                return topic;
            } else {
                return topicModel;
            }
        } catch (Exception e) {
            LOGGER.warn(String.format("Parse topicModel failed, the input param topicModel is [%s].", topicModel), e);
            topicRobinIndex = 0;
            return topicModel;
        }
    }
}
