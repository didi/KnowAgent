package com.didichuxing.datachannel.swan.agent.task.log.log2hdfs;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import com.didichuxing.datachannel.swan.agent.channel.log.LogChannel;
import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.EventMetricsConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.AbstractTask;
import com.didichuxing.datachannel.swan.agent.engine.bean.Event;
import com.didichuxing.datachannel.swan.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.swan.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsSink;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsTargetConfig;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.metrics.HdfsMetricsFields;
import com.didichuxing.datachannel.swan.agent.source.log.LogSource;
import com.didichuxing.datachannel.swan.agent.source.log.beans.WorkingFileNode;
import com.didichuxing.datachannel.swan.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.swan.agent.source.log.monitor.FileCloseMonitor;
import com.didichuxing.datachannel.swan.agent.source.log.monitor.RealTimeFileMonitor;
import com.didichuxing.datachannel.swan.agent.source.log.monitor.ScheduleFileMonitor;
import com.didichuxing.datachannel.swan.agent.task.log.log2hdfs.monitor.HdfsDataFileCloseMonitor;
import com.didichuxing.datachannel.swan.agent.task.log.metrics.ModelMetricsFields;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: 日志采集入kafka
 * @author: huangjw
 * @Date: 2019-07-12 14:57
 */
public class Log2HdfsTask extends AbstractTask {

    private static final ILog LOGGER        = LogFactory.getLog(Log2HdfsTask.class.getName());

    private long              lastFlushTime = 0;

    private Log2HdfsModel     log2HdfsModel;

    public Log2HdfsTask(Log2HdfsModel log2HdfsModel, ComponentConfig config, LogSource logSource) {
        this.log2HdfsModel = log2HdfsModel;
        this.source = logSource;
        this.modelConfig = (ModelConfig) config;
        this.channel = new LogChannel(logSource, modelConfig.getChannelConfig());
    }

    @Override
    public void bulidUniqueKey() {
        String sourceId = source.getUniqueKey();
        String modelId = modelConfig.getCommonConfig().getModelId() + "";
        String tag = modelConfig.getTag();
        setUniqueKey(modelId + "_" + sourceId + "_" + tag);
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
        LOGGER.info("add hdfsSink. orderNum is " + orderNum + ",uniqueKey is " + getUniqueKey());
        String dirPathRule = getDirPathRule();
        String fileNameRule = getFileNameRule();
        HdfsSink sink = new HdfsSink(this.modelConfig, this.channel, orderNum, dirPathRule,
            fileNameRule, log2HdfsModel.getHdfsFileSystem());
        sink.needToCollectAll(checkNeedToCollectAll(this.modelConfig));
        sink.setFileSplit(needHdfsFileToSpilt(this.modelConfig));
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
            LogGather.recordErrorLog("Log2HdfsTask error", "checkNeedToCollectAll error", e);
        }
        return false;
    }

    private String getDirPathRule() {
        String hdfsPath = getHdfsTargetConfig().getHdfsPath();
        if (source instanceof LogSource) {
            String parentPath = ((LogSource) source).getParentPath();
            if (parentPath.startsWith("/")) {
                parentPath = parentPath.substring(1);
            }

            if (parentPath.endsWith("/")) {
                parentPath = parentPath.substring(0, parentPath.length() - 1);
            }
            hdfsPath = hdfsPath.replace(LogConfigConstants.PATH_FLAG, parentPath);

            String nsName = modelConfig.getEventMetricsConfig().getOriginalAppName();
            if (hdfsPath.contains(LogConfigConstants.SERVICE_FLAG)
                && StringUtils.isNotBlank(nsName)) {
                hdfsPath = hdfsPath.replace(LogConfigConstants.SERVICE_FLAG, nsName);
            }

            String leaf = modelConfig.getEventMetricsConfig().getOdinLeaf();
            if (hdfsPath.contains(LogConfigConstants.LEAF_FLAG) && StringUtils.isNotBlank(leaf)) {
                hdfsPath = hdfsPath.replace(LogConfigConstants.LEAF_FLAG, leaf);
            }

        }
        return hdfsPath;
    }

    private String getFileNameRule() {
        String hdfsFileName = getHdfsTargetConfig().getHdfsFileName();
        if (source instanceof LogSource) {
            String masterFileName = ((LogSource) source).getMasterFileName();
            hdfsFileName = hdfsFileName.replace(LogConfigConstants.HOSTNAME_FLAG,
                CommonUtils.getHOSTNAME());
            hdfsFileName = hdfsFileName.replace(LogConfigConstants.FILENAME_FLAG, masterFileName);
        }
        return hdfsFileName;
    }

    @Override
    protected List<Monitor> getMonitors() {
        List<Monitor> list = new ArrayList<>();
        if (modelConfig.getCommonConfig().getModelType() != LogConfigConstants.COLLECT_TYPE_TEMPORALITY) {
            list.add(RealTimeFileMonitor.INSTANCE);
            list.add(ScheduleFileMonitor.INSTANCE);
        }
        list.add(FileCloseMonitor.INSTANCE);
        list.add(HdfsDataFileCloseMonitor.INSTANCE);
        return list;
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

        if (sendNum > getHdfsTargetConfig().getFlushBatchSize()
            || System.currentTimeMillis() - lastFlushTime > getHdfsTargetConfig()
                .getFlushBatchTimeThreshold()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean flush() {
        clearChannel();
        boolean result = true;
        for (AbstractSink sink : sinkers.values()) {
            if (!sink.flush()) {
                result = false;
                break;
            }
        }
        lastFlushTime = System.currentTimeMillis();
        return result;
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
    public void rollback() {
        // flush失败, 同步发送时，需等待flush完成；异步时，需要移动offset，实现重放，只有异步过程才存在失败的case
        Map<String, Long> failedOffsetMap = new ConcurrentHashMap<>();
        for (AbstractSink sink : sinkers.values()) {
            Map<String, Long> offsetMap = ((HdfsSink) sink).getFailedRateMapBack();
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

    public void sync() {
        Set<String> keys = ((LogSource) source).getCollectingFileNodeMap().keySet();
        for (AbstractSink sink : this.sinkers.values()) {
            ((HdfsSink) sink).sync(keys);
        }
    }

    @Override
    public void reset() {
        for (AbstractSink sink : sinkers.values()) {
            ((HdfsSink) sink).getFailedRateMapBack().clear();
        }
    }

    public HdfsTargetConfig getHdfsTargetConfig() {
        return (HdfsTargetConfig) modelConfig.getTargetConfig();
    }

    public String getModelUniqueKey() {
        return this.log2HdfsModel.getUniqueKey();
    }

    @Override
    public Map<String, Object> metric() {
        Map<String, Object> ret = new HashMap<>();
        // common metrics
        ret.put(ModelMetricsFields.NS_NAME, modelConfig.getEventMetricsConfig().getOriginalAppName());
        ret.put(ModelMetricsFields.NS_LEAF_NAME, modelConfig.getEventMetricsConfig().getOdinLeaf());
        ret.put(ModelMetricsFields.MODEL_ID, modelConfig.getCommonConfig().getModelId());
        ret.put(ModelMetricsFields.MODEL_VERSION, modelConfig.getVersion());

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
        ret.put(HdfsMetricsFields.PREFIX_SINK_NUM, sinkers.size());
        int i = 0;
        for (AbstractSink sink : sinkers.values()) {
            if (i == 0) {
                ret.putAll(sink.metric());
                i++;
                continue;
            }
            Map<String, Object> map = sink.metric();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (key.equals(HdfsMetricsFields.FILTER_REMAINED)) {
                    ret.put(HdfsMetricsFields.FILTER_REMAINED,
                            (int) ret.get(HdfsMetricsFields.FILTER_REMAINED) + (int) value);
                    continue;
                }

                if (key.equals(HdfsMetricsFields.LOSS_COUNT)) {
                    ret.put(HdfsMetricsFields.LOSS_COUNT, (int) ret.get(HdfsMetricsFields.LOSS_COUNT) + (int) value);
                    continue;
                }

                if (key.equals(HdfsMetricsFields.FILTER_TOO_LARGE_COUNT)) {
                    ret.put(HdfsMetricsFields.FILTER_TOO_LARGE_COUNT,
                            (int) ret.get(HdfsMetricsFields.FILTER_TOO_LARGE_COUNT) + (int) value);
                    continue;
                }

                if (key.equals(HdfsMetricsFields.FILTER_TOTAL_TOO_LARGE_COUNT)) {
                    ret.put(HdfsMetricsFields.FILTER_TOTAL_TOO_LARGE_COUNT,
                            (int) ret.get(HdfsMetricsFields.FILTER_TOTAL_TOO_LARGE_COUNT + (int) value));
                    continue;
                }

                if (key.equals(HdfsMetricsFields.SINK_STAT_TIME)) {
                    if ((int) ret.get(HdfsMetricsFields.SINK_STAT_TIME) > (int) value) {
                        ret.put(HdfsMetricsFields.SINK_STAT_TIME, value);
                    }
                    continue;
                }

                if (key.equals(HdfsMetricsFields.CONTROL_STAT_TIME)) {
                    if ((int) ret.get(HdfsMetricsFields.CONTROL_STAT_TIME) > (int) value) {
                        ret.put(HdfsMetricsFields.CONTROL_STAT_TIME, value);
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public void prepare() {
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        ModelConfig newConfig = (ModelConfig) newOne;

        LOGGER.info("begin to change log2hdfs task's config.newOne is " + newConfig);
        // sink数量变更
        int oldNum = this.modelConfig.getTargetConfig().getSinkNum();
        int newNum = newConfig.getTargetConfig().getSinkNum();

        LOGGER.info("begin to change log2hdfs task's config.oldSinkNum is " + oldNum
                    + ", newSinkNum is " + newNum);

        this.modelConfig = newConfig;
        this.source.onChange(newConfig);
        this.channel.onChange(newConfig);
        String dirPathRule = getDirPathRule();
        String fileNameRule = getFileNameRule();

        // 更改filesystem
        for (AbstractSink sink : sinkers.values()) {
            ((HdfsSink) sink).setHdfsFileSystem(log2HdfsModel.getHdfsFileSystem());
            ((HdfsSink) sink).setDirPathRule(dirPathRule);
            ((HdfsSink) sink).setFileNameRule(fileNameRule);
        }

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
                    ((HdfsSink) sink).setFileSplit(needHdfsFileToSpilt(this.modelConfig));
                } else {
                    LOGGER.info("this sink is new sink. begin to init it. uniqueKey is "
                                + sink.getUniqueKey());
                    sink.init(newConfig);
                    ((HdfsSink) sink).setFileSplit(needHdfsFileToSpilt(this.modelConfig));
                }

                if (!sink.isRunning()) {
                    sink.start();
                }
            } catch (Exception e) {
                LogGather
                    .recordErrorLog("Log2HdfsTask error", "log2HdfsTask change sink error!", e);
            }
        }
        taskLimter.onChange(newConfig);
        LOGGER.info("change log2hdfs task success.");
        return true;
    }

    public Log2HdfsModel getLog2HdfsModel() {
        return log2HdfsModel;
    }

    public void setLog2HdfsModel(Log2HdfsModel log2HdfsModel) {
        this.log2HdfsModel = log2HdfsModel;
    }

    private boolean needHdfsFileToSpilt(ModelConfig modelConfig) {
        LogSourceConfig logSourceConfig = (LogSourceConfig) modelConfig.getSourceConfig();
        return LogConfigConstants.NO_LOG_TIME.equals(logSourceConfig.getTimeFormat());
    }
}
