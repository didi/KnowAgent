package com.didichuxing.datachannel.agent.task.log.log2kafak;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agent.task.log.metrics.ModelMetricsFields;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.channel.log.LogChannel;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.EventMetricsConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.agent.sink.kafkaSink.metrics.KafkaMetricsFields;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.monitor.FileCloseMonitor;
import com.didichuxing.datachannel.agent.source.log.monitor.RealTimeFileMonitor;
import com.didichuxing.datachannel.agent.source.log.monitor.ScheduleFileMonitor;

import com.didichuxing.datachannel.agent.common.loggather.LogGather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 日志采集入kafka
 * @author: huangjw
 * @Date: 19/7/2 15:14
 */
public class Log2KafkaTask extends AbstractTask {

    private static final Logger LOGGER        = LoggerFactory.getLogger(Log2KafkaTask.class
                                                  .getName());
    private long                lastFlushTime = 0;

    /**
     * ceph采集时标记是否已经完成所有相关文件的同步
     */
    private volatile boolean    isSynced      = false;

    public Log2KafkaTask(ComponentConfig config, LogSource logSource) {
        this.source = logSource;
        this.modelConfig = (ModelConfig) config;
        this.channel = new LogChannel(logSource, modelConfig.getChannelConfig());
    }

    @Override
    public void bulidUniqueKey() {
        String sourceId = source.getUniqueKey();
        String modelId = modelConfig.getCommonConfig().getModelId() + "";
        String tag = modelConfig.getTag();
        String logModelHostName = modelConfig.getHostname();
        setUniqueKey(logModelHostName + "_" + modelId + "_" + sourceId + "_" + tag);
    }

    @Override
    protected List<Monitor> getMonitors() {
        List<Monitor> list = new ArrayList<>();
        if (modelConfig.getCommonConfig().getModelType() != LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            list.add(RealTimeFileMonitor.INSTANCE);
            list.add(ScheduleFileMonitor.INSTANCE);
        }
        list.add(FileCloseMonitor.INSTANCE);
        return list;
    }

    @Override
    public void configure(ComponentConfig config) {
        int sinkNum = this.modelConfig.getTargetConfig().getSinkNum();
        for (int i = 0; i < sinkNum; i++) {
            addSink(i);
        }
    }

    @Override
    public void addSink(int orderNum) {
        LOGGER.info("add kafkaSink. orderNum is " + orderNum + ",uniqueKey is " + getUniqueKey());
        KafkaSink sink = new KafkaSink(this.modelConfig, this.channel, orderNum);
        sink.needToCollectAll(checkNeedToCollectAll(this.modelConfig));
        sinkers.put(sink.getUniqueKey(), sink);
    }

    private boolean checkNeedToCollectAll(ModelConfig modelConfig) {
        try {
            LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
            if (modelConfig.getCommonConfig().getModelType() == LogConfigConstants.COLLECT_TYPE_TEMPORALITY
                && LogConfigConstants.NO_LOG_TIME.equals(logSourceConfig.getTimeFormat())) {
                return true;
            }
        } catch (Exception e) {
            LogGather.recordErrorLog("Log2KafkaTask error", "checkNeedToCollectAll error", e);
        }
        return false;
    }

    @Override
    public void prepare() {
        //去除任务启动前ceph依赖，保留任务启动前准备接口
    }

    @Override
    public boolean needToFlush(Event event) {
        if (lastFlushTime == 0L) {
            lastFlushTime = System.currentTimeMillis();
        }

        int sendNum = 0;
        for (AbstractSink sink : sinkers.values()) {
            sendNum += sink.getSendNum();
        }

        if (sendNum > getKafkaTargetConfig().getFlushBatchSize()
            || System.currentTimeMillis() - lastFlushTime > (getKafkaTargetConfig())
                .getFlushBatchTimeThreshold()) {
            lastFlushTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Override
    public boolean canStop() {
        List<WorkingFileNode> collectingWFN = ((LogSource) source).getCollectingFileNodeList();
        if (collectingWFN == null || collectingWFN.size() == 0) {
            return true;
        }

        for (WorkingFileNode wfn : collectingWFN) {
            if (!wfn.isFileEnd() || !wfn.checkFileCollectEnd()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean flush() {
        clearChannel();
        boolean result = true;
        for (AbstractSink sink : sinkers.values()) {
            if (!sink.flush()) {
                result = false;
            }
        }
        lastFlushTime = System.currentTimeMillis();
        return result;
    }

    @Override
    public void rollback() {
        // flush失败, 同步发送时，需等待flush完成；异步时，需要移动offset，实现重放，只有异步过程才存在失败的case
        Map<String, Long> failedOffsetMap = new ConcurrentHashMap<>();
        for (AbstractSink sink : sinkers.values()) {
            Map<String, Long> offsetMap = ((KafkaSink) sink).getFailedRateMapS1();
            for (String key : offsetMap.keySet()) {
                Long offset = failedOffsetMap.get(key);
                if (offset == null || offset > offsetMap.get(key)) {
                    failedOffsetMap.put(key, offsetMap.get(key));
                }
            }
        }

        for (String key : failedOffsetMap.keySet()) {
            WorkingFileNode wfn = ((LogSource) source).getCollectingFileNodeMap().get(key);
            if (wfn != null) {
                wfn.seek(failedOffsetMap.get(key));
            }
        }

        ((LogChannel) channel).getLastestOffsetMap().clear();
    }

    @Override
    public void commit() {
        channel.commit();
    }

    @Override
    public void reset() {
        for (AbstractSink sink : sinkers.values()) {
            ((KafkaSink) sink).getFailedRateMapS1().clear();
        }
    }

    @Override
    public Map<String, Object> metric() {
        Map<String, Object> ret = new HashMap<>();
        // common metrics
        ret.put(ModelMetricsFields.NS_NAME, modelConfig.getEventMetricsConfig().getOriginalAppName());
        ret.put(ModelMetricsFields.NS_LEAF_NAME, modelConfig.getEventMetricsConfig().getOdinLeaf());
        ret.put(ModelMetricsFields.MODEL_ID, modelConfig.getCommonConfig().getModelId());
        ret.put(ModelMetricsFields.MODEL_VERSION, modelConfig.getVersion());

        //TODO：添加日志模型采集的主机名指标 注：该主机名不一定为 agent 对应宿主机名，如采集宿主机上对应容器场景
        String logModelHostName = StringUtils.isNotBlank(modelConfig.getHostname()) ? modelConfig.getHostname() : StringUtils.EMPTY;
        ret.put(ModelMetricsFields.MODEL_HOST_NAME, logModelHostName);

        EventMetricsConfig eventMetricsConfig = modelConfig.getEventMetricsConfig();
        if (eventMetricsConfig != null && eventMetricsConfig.getOtherMetrics() != null
                && eventMetricsConfig.getOtherMetrics().size() != 0) {
            for (Map.Entry<String, String> entry : eventMetricsConfig.getOtherMetrics().entrySet()) {
                if (StringUtils.isNotBlank(entry.getValue()) && StringUtils.isNotBlank(entry.getKey())) {
                    ret.put(entry.getKey(), entry.getValue());
                }
            }
        }

        ret.put(ModelMetricsFields.DYNAMIC_LIMITER, taskLimter.getCurrentRate());
        ret.put(ModelMetricsFields.LIMIT_RATE, modelConfig.getModelLimitConfig().getRate());

        // source metrics
        ret.putAll(this.source.metric());

        // channel metrics
        ret.putAll(this.channel.metric());

        // sink metrics
        ret.putAll(sinkMetricMerge());

        return ret;
    }

    private Map<String, Object> sinkMetricMerge() {
        Map<String, Object> ret = new HashMap<>();
        ret.put(KafkaMetricsFields.PREFIX_SINK_NUM, sinkers.size());
        int i = 0;
        for (AbstractSink sink : sinkers.values()) {
            KafkaSink kafkaSink = (KafkaSink) sink;
            if (i == 0) {
                ret.putAll(kafkaSink.metric());
                i++;
                continue;
            }
            Map<String, Object> map = kafkaSink.metric();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.equals(KafkaMetricsFields.FILTER_REMAINED)) {
                    ret.put(KafkaMetricsFields.FILTER_REMAINED,
                            (int) ret.get(KafkaMetricsFields.FILTER_REMAINED) + (int) value);
                    continue;
                }

                if (key.equals(KafkaMetricsFields.LOSS_COUNT)) {
                    ret.put(KafkaMetricsFields.LOSS_COUNT, (int) ret.get(KafkaMetricsFields.LOSS_COUNT) + (int) value);
                    continue;
                }

                if (key.equals(KafkaMetricsFields.FILTER_TOO_LARGE_COUNT)) {
                    ret.put(KafkaMetricsFields.FILTER_TOO_LARGE_COUNT,
                            (int) ret.get(KafkaMetricsFields.FILTER_TOO_LARGE_COUNT) + (int) value);
                    continue;
                }

                if (key.equals(KafkaMetricsFields.FILTER_TOTAL_TOO_LARGE_COUNT)) {
                    ret.put(KafkaMetricsFields.FILTER_TOTAL_TOO_LARGE_COUNT,
                            (int) ret.get(KafkaMetricsFields.FILTER_TOTAL_TOO_LARGE_COUNT + (int) value));
                    continue;
                }

                if (key.equals(KafkaMetricsFields.SINK_STAT_TIME)) {
                    if ((int) ret.get(KafkaMetricsFields.SINK_STAT_TIME) > (int) value) {
                        ret.put(KafkaMetricsFields.SINK_STAT_TIME, value);
                    }
                    continue;
                }

                if (key.equals(KafkaMetricsFields.CONTROL_STAT_TIME)) {
                    if ((int) ret.get(KafkaMetricsFields.CONTROL_STAT_TIME) > (int) value) {
                        ret.put(KafkaMetricsFields.CONTROL_STAT_TIME, value);
                    }
                }
            }
        }
        return ret;
    }

    public LogSourceConfig getLogSourceConfig() {
        return (LogSourceConfig) this.modelConfig.getSourceConfig();
    }

    public KafkaTargetConfig getKafkaTargetConfig() {
        return (KafkaTargetConfig) this.modelConfig.getTargetConfig();
    }

}
