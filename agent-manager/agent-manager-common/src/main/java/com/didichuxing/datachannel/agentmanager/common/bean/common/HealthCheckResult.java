package com.didichuxing.datachannel.agentmanager.common.bean.common;

import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;

/**
 * @author huqidong
 * @date 2020-09-21
 * 健康度检查结果
 */
public class HealthCheckResult {

    /**
     * 检查是否通过
     */
    private boolean checkResult;
    /**
     * 指标名
     */
    private String metricName;
    /**
     * 指标实际值
     */
    private Object metricActualValuel;
    /**
     * 指标合理阈值
     */
    private Object metricReasonableThreshold;
    /**
     * 潜在风险
     */
    private String potentialRisk;
    /**
     * 可能原因
     */
    private String rootCause;
    /**
     * 处理建议
     */
    private String handlingSuggestions;
    /**
     * 日志采集任务健康等级
     */
    private LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum;

    public boolean getCheckResult() {
        return checkResult;
    }

    public String getMetricName() {
        return metricName;
    }

    public Object getMetricActualValuel() {
        return metricActualValuel;
    }

    public Object getMetricReasonableThreshold() {
        return metricReasonableThreshold;
    }

    public String getPotentialRisk() {
        return potentialRisk;
    }

    public String getRootCause() {
        return rootCause;
    }

    public String getHandlingSuggestions() {
        return handlingSuggestions;
    }

    public LogCollectTaskHealthLevelEnum getLogCollectTaskHealthLevelEnum() {
        return logCollectTaskHealthLevelEnum;
    }
}
