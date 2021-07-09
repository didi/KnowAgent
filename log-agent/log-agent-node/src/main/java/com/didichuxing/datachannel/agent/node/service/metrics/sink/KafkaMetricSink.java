package com.didichuxing.datachannel.agent.node.service.metrics.sink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.common.metrics.Metric;
import com.didichuxing.datachannel.agent.common.metrics.MetricsRecord;
import com.didichuxing.datachannel.agent.common.metrics.MetricsSink;
import com.didichuxing.datachannel.agent.common.metrics.MetricsTag;
import com.didichuxing.datachannel.agent.common.api.MetricsFields;
import com.didichuxing.datachannel.agent.common.configs.v2.MetricConfig;
import com.didichuxing.datachannel.agent.common.constants.ComponentType;
import com.didichuxing.datachannel.agent.engine.metrics.metric.ChannelMetricsFields;
import com.didichuxing.datachannel.agent.engine.metrics.metric.SinkMetricsFields;
import com.didichuxing.datachannel.agent.engine.metrics.metric.SourceMetricsFields;
import com.didichuxing.datachannel.agent.engine.metrics.metric.TaskMetricsFields;
import com.didichuxing.datachannel.agent.engine.metrics.sink.AbstractMetricSink;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.source.log.metrics.FileMetricsFields;
import com.didichuxing.datachannel.agent.source.log.metrics.FileStatistic;
import com.didichuxing.datachannel.agent.task.log.metrics.ModelMetricsFields;

import com.didichuxing.datachannel.agent.common.loggather.LogGather;
import org.apache.commons.configuration.SubsetConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMetricSink extends AbstractMetricSink implements MetricsSink {

    private static final Logger LOGGER             = LoggerFactory.getLogger("perfLogger");
    private KafkaTopicSink      sink;

    private static final String LIMIT_TIME_TAG     = "limitTime";
    private static final String NEW_LIMIT_TIME_TAG = TaskMetricsFields.PREFIX_METRICS_
                                                     + LIMIT_TIME_TAG;

    @Override
    public void init(SubsetConfiguration conf) {
    }

    public KafkaMetricSink(MetricConfig metricConfig) {
        super(metricConfig);
        try {
            sink = new KafkaTopicSink(metricConfig);
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaMetricSink error", "KafkaTopicSink init error!", e);
        }
    }

    @Override
    public void stop() {
        sink.stop();
    }

    @Override
    public void flush() {
        // sink.flush();
    }

    @Override
    public void sendMetrics(String content) {
        sink.send(content);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(content);
        }
    }

    @Override
    public void onChange(MetricConfig newConfig) {
        try {
            if (!sink.onChange(newConfig)) {
                return;
            }
            KafkaTopicSink s = new KafkaTopicSink(newConfig);
            KafkaTopicSink oldSink = sink;

            this.sink = s;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LogGather.recordErrorLog("KafkaMetricSink error", "on change sleep error", e);
            }

            oldSink.stop();
        } catch (Exception e) {
            LogGather.recordErrorLog("KafkaMetricSink error", "on change error", e);
        }
    }

    @Override
    public void putMetrics(MetricsRecord record) {
        Iterable<Metric> metrics = record.metrics();
        Iterable<MetricsTag> tags = record.tags();
        Map<String, Object> result = new HashMap<>();

        // tag
        if (tags != null) {
            for (MetricsTag loopTag : record.tags()) {
                if (loopTag.value() != null) {
                    if (loopTag.name().equals(FileMetricsFields.COLLECT_FILE_NAMES_STR)) {
                        List<FileStatistic> list = JSON.parseArray(loopTag.value().replace("\\", ""),
                                FileStatistic.class);
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, list);
                    } else if (loopTag.name().equals(FileMetricsFields.RELATED_FILES)
                            && StringUtils.isNotBlank(loopTag.value())) {
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, Long.parseLong(loopTag.value()));
                    } else if (loopTag.name().equals(FileMetricsFields.MAX_TIME_GAP_STR)
                            && StringUtils.isNotBlank(loopTag.value())) {
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, Long.parseLong(loopTag.value()));
                    } else if (loopTag.name().equals(FileMetricsFields.LATEST_LOG_TIME)
                            && StringUtils.isNotBlank(loopTag.value())) {
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, Long.parseLong(loopTag.value()));
                    } else if (loopTag.name().equals(ModelMetricsFields.MODEL_VERSION)
                            && StringUtils.isNotBlank(loopTag.value())) {
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, Long.parseLong(loopTag.value()));
                    } else if (loopTag.name().equals(FileMetricsFields.LATEST_MODIFY_TIME)
                            && StringUtils.isNotBlank(loopTag.value())) {
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, Long.parseLong(loopTag.value()));
                    } else {
                        String name = metricConfig.isTransfer() ? getOldName(loopTag.name()) : loopTag.name();
                        result.put(name, loopTag.value());
                    }
                }
            }
            result.put(MetricsFields.HEARTBEAT_TIME, System.currentTimeMillis());
            result.put(MetricsFields.HOST_NAME, CommonUtils.getHOSTNAME());
            result.put(MetricsFields.HOST_IP, CommonUtils.getHOSTIP());

            if (!result.containsKey(ModelMetricsFields.MODEL_VERSION)
                    && !record.name().equals(MetricsFields.KEY_BASIC)) {
                String name = metricConfig.isTransfer() ? getOldName(ModelMetricsFields.MODEL_VERSION) : ModelMetricsFields.MODEL_VERSION;
                if (!result.containsKey(name)) {
                    result.put(name, -1);
                }
            }

            if (!result.containsKey(MetricsFields.LOG_MODEL_ID_STR)) {
                String name = metricConfig.isTransfer() ? getOldName(FileMetricsFields.LOG_ID_STR) : FileMetricsFields.LOG_ID_STR;
                if (!result.containsKey(name)) {
                    result.put(name, -1);
                }
            }

            if (!result.containsKey(FileMetricsFields.PATH_ID_STR)) {
                String name = metricConfig.isTransfer() ? getOldName(FileMetricsFields.PATH_ID_STR) : FileMetricsFields.PATH_ID_STR;
                if (!result.containsKey(name)) {
                    result.put(name, -1);
                }
            }

            if (!result.containsKey(FileMetricsFields.LOG_PATH_KEY)) {
                String name = metricConfig.isTransfer() ? getOldName(FileMetricsFields.LOG_PATH_KEY) : FileMetricsFields.LOG_PATH_KEY;
                if (!result.containsKey(name)) {
                    result.put(name, "-1");
                }
            }
        }

        // metrics
        if (metrics != null) {
            for (Metric loopMetric : metrics) {
                if (loopMetric.value() != null) {
                    String name = metricConfig.isTransfer() ? getOldName(loopMetric.name()) : loopMetric.name();
                    if (name.equals(LIMIT_TIME_TAG) || name.equals(NEW_LIMIT_TIME_TAG)) {
                        result.put(name, (Long) loopMetric.value() / 1000);
                    } else {
                        result.put(name, loopMetric.value());
                    }
                }
            }
        }

        // 统一hostName
        if (result.containsKey(MetricsFields.HOST_NAME_TO_DEL)) {
            result.remove(MetricsFields.HOST_NAME_TO_DEL);
        }

        sendMetrics(JSON.toJSONString(result));
    }

    private String getOldName(String newName) {
        if (StringUtils.isNotBlank(newName)) {
            if (newName.startsWith(SourceMetricsFields.PREFIX_METRICS_)) {
                return newName.substring(SourceMetricsFields.PREFIX_METRICS_.length()).replace(
                    ComponentType.SOURCE, "read");
            } else if (newName.startsWith(SinkMetricsFields.PREFIX_METRICS_)) {
                return newName.substring(SinkMetricsFields.PREFIX_METRICS_.length()).replace(
                    ComponentType.SINK, "send");
            } else if (newName.startsWith(TaskMetricsFields.PREFIX_METRICS_)) {
                return newName.substring(TaskMetricsFields.PREFIX_METRICS_.length());
            } else if (newName.startsWith(TaskMetricsFields.PREFIX_LIMIT_)) {
                return newName.substring(TaskMetricsFields.PREFIX_LIMIT_.length());
            } else if (newName.startsWith(ChannelMetricsFields.PREFIX_METRICS_)) {
                return newName.substring(ChannelMetricsFields.PREFIX_METRICS_.length());
            } else if (newName.startsWith(SinkMetricsFields.PREFIX_)) {
                return newName.substring(SinkMetricsFields.PREFIX_.length());
            } else {
                return newName;
            }
        }
        return null;
    }
}
