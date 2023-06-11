package com.didichuxing.datachannel.agentmanager.common.metrics;

/**
 * 指标记录
 */
public class MetricRecord {

    /**
     * 类型：
     *  0：AgentMetrics
     *  1：TaskMetrics
     */
    private Integer type;

    /**
     * 内容
     */
    private Object  data;

    private MetricRecord(Integer type, Object data) {
        this.type = type;
        this.data = data;
    }

    public MetricRecord() {

    }

    public static MetricRecord buildAgentMetrics(AgentMetrics agentMetrics) {
        return new MetricRecord(0, agentMetrics);
    }

    public static MetricRecord buildTaskMetrics(TaskMetrics taskMetrics) {
        return new MetricRecord(1, taskMetrics);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
