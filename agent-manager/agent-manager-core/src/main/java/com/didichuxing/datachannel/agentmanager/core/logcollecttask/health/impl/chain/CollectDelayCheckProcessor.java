package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.constant.LogCollectTaskHealthCheckConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthInspectionResultEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.health.impl.chain.AgentGcMetricExceptionExistsCheckProcessor;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context.LogCollectTaskHealthCheckContext;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * 是否存在采集延迟检查
 * @author william.
 */
@HealthCheckProcessorAnnotation(seq = 8, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class CollectDelayCheckProcessor extends BaseProcessor {

    @Override
    protected void process(LogCollectTaskHealthCheckContext context) {
        /*
         * 校验日志采集任务是否为红 黄
         */
        if(
                context.getLogCollectTaskHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.RED) ||
                        context.getLogCollectTaskHealthLevelEnum().equals(LogCollectTaskHealthLevelEnum.YELLOW)
        ) {
            return;
        }
        if(context.getCheckCollectDelay()) {
            boolean collectDelay = checkCollectDelay(
                    context.getLogCollectTaskDO().getCollectDelayThresholdMs(),
                    context.getLastLogCollectTaskMetric()
            );
            if (collectDelay) {// 存在采集延迟
                /*
                 * 进一步判断采集延迟原因
                 */
                diagnosisCollectDelay(context);
            }
        }
    }

    static class CollectFile {
        private Boolean fileEnd;
        private String fileName;
        private Integer isFileOrder;
        private Long lastModifyTime;
        private Long logTime;
        private Long rate;
        private Boolean vaildTimeConfig;

        public Boolean getFileEnd() {
            return fileEnd;
        }

        public void setFileEnd(Boolean fileEnd) {
            this.fileEnd = fileEnd;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getIsFileOrder() {
            return isFileOrder;
        }

        public void setIsFileOrder(Integer isFileOrder) {
            this.isFileOrder = isFileOrder;
        }

        public Long getLastModifyTime() {
            return lastModifyTime;
        }

        public void setLastModifyTime(Long lastModifyTime) {
            this.lastModifyTime = lastModifyTime;
        }

        public Long getLogTime() {
            return logTime;
        }

        public void setLogTime(Long logTime) {
            this.logTime = logTime;
        }

        public Long getRate() {
            return rate;
        }

        public void setRate(Long rate) {
            this.rate = rate;
        }

        public Boolean getVaildTimeConfig() {
            return vaildTimeConfig;
        }

        public void setVaildTimeConfig(Boolean vaildTimeConfig) {
            this.vaildTimeConfig = vaildTimeConfig;
        }
    }

    private void diagnosisCollectDelay(LogCollectTaskHealthCheckContext context) {
        /*
         * 校验待采集文件集是否全部采集完，如是，cause by 日志文件没有内容写入导致延时
         */
        String collectFilesString = context.getLastLogCollectTaskMetric().getCollectfiles();
        List<CollectFile> collectFileList = JSON.parseObject(collectFilesString, new TypeReference<List<CollectFile>>() {});
        if(CollectionUtils.isNotEmpty(collectFileList)) {
            Boolean allFilesCollectFinished = true;
            for (CollectFile collectFile : collectFileList) {
                if(!collectFile.rate.equals(100l)) {
                    allFilesCollectFinished = false;
                }
            }
            if(allFilesCollectFinished) {
                setLogCollectTaskHealthInfo(
                        context,
                        LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED_CAUSE_BY_BUSINESS_DATA_WRITE_SLOW,
                        context.getHostDO().getHostName(),
                        context.getFileLogCollectPathDO().getPath()
                );
                return;
            }
        }

        /*
         * 下游接收端，是否存在写入失败情况
         */
        boolean dataSendFailedExists = DataSendFailedCheckProcessor.checkDataSendFailedExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getMetricsManageService()
        );
        if (dataSendFailedExists) {//存在下游接收端写入失败
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED_CAUSE_BY_DATA_SEND_FAILED,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
            return;
        }
        /*
         * 下游接收端，是否存在被限流
         */

        //TODO：kafka 暂无实现 LogCollectTaskHealthManageServiceImpl.checkTopicLimitExists()

        /*
         * 是否存在 agent 限流，如存在，进一步校验限流原因
         */
        boolean byteLimitOnHostExists = ByteLimitOnHostExistsCheckProcessor.checkByteLimitOnHostExists(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                context.getMetricsManageService()
        );
        if (byteLimitOnHostExists) {//存在采集端出口流量阈值限流
            /*
             * 此时，须进一步判断出口限流是否因 agent fullgc 过频导致，如是，提示用户 fullgc 过频，如不是，提示用户对应 agent cpu 限流阈值
             */
            if(
                    AgentGcMetricExceptionExistsCheckProcessor.checkAgentGcMetricExceptionExists(
                            context.getHostDO().getHostName(),
                            context.getMetricsManageService()
                    )
            ) {//存在 full gc 过频
                setLogCollectTaskHealthInfo(
                        context,
                        LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED_CAUSE_BY_HOST_CPU_USAGE_LIMIT_EXISTS_CAUSE_BY_AGENT_FULL_GC_OVER_FREQUENCY,
                        context.getHostDO().getHostName(),
                        context.getFileLogCollectPathDO().getPath()
                );
            } else {//不存在 full gc 过频
                setLogCollectTaskHealthInfo(
                        context,
                        LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED_CAUSE_BY_HOST_CPU_USAGE_LIMIT_EXISTS,
                        context.getHostDO().getHostName(),
                        context.getFileLogCollectPathDO().getPath()
                );
            }
            return;
        }
        /*
         * channel 使用率最大值是否存在 100%，如是，表示 sink 端（含：下游接收端）瓶颈，否则，表示 source 端瓶颈
         */
        Double channelUsage = getChannelUsage(context);
        if(channelUsage.equals(100d)) {//sink 端瓶颈
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED_CAUSE_BY_SINK_SLOW,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
        } else {//source 端瓶颈
            setLogCollectTaskHealthInfo(
                    context,
                    LogCollectTaskHealthInspectionResultEnum.LOG_PATH_COLLECT_DELAYED_CAUSE_BY_SOURCE_SLOW,
                    context.getHostDO().getHostName(),
                    context.getFileLogCollectPathDO().getPath()
            );
        }
    }

    private Double getChannelUsage(LogCollectTaskHealthCheckContext context) {
        Long currentTime = System.currentTimeMillis();
        Long startTime = currentTime - LogCollectTaskHealthCheckConstant.CHANNEL_USAGE_CHECK_LASTEST_MS_THRESHOLD;
        Object channelUsageObj = context.getMetricsManageService().getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
                context.getLogCollectTaskDO().getId(),
                context.getFileLogCollectPathDO().getId(),
                context.getHostDO().getHostName(),
                startTime,
                currentTime,
                AggregationCalcFunctionEnum.MAX.getValue(),
                MetricFieldEnum.LOG_COLLECT_TASK_CHANNEL_USED_PERCENT.getFieldNameMax()
        );
        Double channelUsage = 0d;
        if(null != channelUsageObj) {
            channelUsage = Double.valueOf(channelUsageObj.toString());
        }
        //主机cpu限流时长 单位：ms
        return channelUsage;
    }

    /**
     * 校验 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟
     * @param collectDelayThresholdMs 日志采集任务业务时间采集延时阈值
     * @param metricsLogCollectTaskPO 最近一次 LogCollectTask 采样指标对象
     * @return 返回 logCollectTaskId+fileLogCollectPathId 在host上是否存在采集延迟 true：存在 采集延时 false：不存在 采集延时
     */
    private boolean checkCollectDelay(
            Long collectDelayThresholdMs,
            MetricsLogCollectTaskPO metricsLogCollectTaskPO
    ) {
        /*
         * System.currentTimeMillis() - currentCollectTime > collectDelayThresholdMs ? true : false
         */
        return System.currentTimeMillis() - metricsLogCollectTaskPO.getBusinesstimestamp() > collectDelayThresholdMs;
    }

}
