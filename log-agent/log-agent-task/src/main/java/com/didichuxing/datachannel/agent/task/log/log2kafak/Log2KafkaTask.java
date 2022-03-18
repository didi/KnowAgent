package com.didichuxing.datachannel.agent.task.log.log2kafak;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.agent.channel.log.LogChannel;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.agent.source.log.LogSource;
import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.monitor.FileCloseMonitor;
import com.didichuxing.datachannel.agent.source.log.monitor.RealTimeFileMonitor;
import com.didichuxing.datachannel.agent.source.log.monitor.ScheduleFileMonitor;

import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
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

        long current = 0;
        if (sendNum > getKafkaTargetConfig().getFlushBatchSize()
            || (current = System.currentTimeMillis()) - lastFlushTime > (getKafkaTargetConfig())
                .getFlushBatchTimeThreshold()) {
            lastFlushTime = current == 0 ? System.currentTimeMillis() : current;
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
    public void setMetrics(TaskMetrics taskMetrics) {

        // common metrics
        taskMetrics.setCollecttaskid(modelConfig.getCommonConfig().getModelId());
        taskMetrics.setCollecttaskversion(modelConfig.getVersion());
        String logModelHostName = StringUtils.isNotBlank(modelConfig.getHostname()) ? modelConfig
            .getHostname() : StringUtils.EMPTY;
        taskMetrics.setCollecttaskhostname(logModelHostName);
        taskMetrics.setDynamiclimiterthreshold(taskLimter.getCurrentRate());
        taskMetrics.setServiceNames(modelConfig.getCommonConfig().getServiceNames());
        taskMetrics.setCollecttasktype(modelConfig.getCommonConfig().getModelType());

        // source metrics
        this.source.setMetrics(taskMetrics);

        // channel metrics
        this.channel.setMetrics(taskMetrics);

        // sink metrics
        sinkMetricMerge(taskMetrics);

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
        LOGGER.info("Log2MqTask:begin to rollback !uniqueKey is " + getUniqueKey());
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

        return null;
    }

    private void sinkMetricMerge(TaskMetrics taskMetrics) {
        taskMetrics.setSinknum(sinkers.size());
        int i = 0;
        for (AbstractSink sink : sinkers.values()) {
            KafkaSink kafkaSink = (KafkaSink) sink;
            if (i == 0) {
                kafkaSink.setMetrics(taskMetrics);
                i++;
                continue;
            }
        }
    }

    public LogSourceConfig getLogSourceConfig() {
        return (LogSourceConfig) this.modelConfig.getSourceConfig();
    }

    public KafkaTargetConfig getKafkaTargetConfig() {
        return (KafkaTargetConfig) this.modelConfig.getTargetConfig();
    }

}
