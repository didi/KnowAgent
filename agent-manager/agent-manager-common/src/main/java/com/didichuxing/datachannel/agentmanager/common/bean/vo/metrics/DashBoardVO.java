package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class DashBoardVO {

    @ApiModelProperty(value = "系统现有日志采集任务总数")
    private Integer logCollectTaskNum;

    @ApiModelProperty(value = "系统现有日志采集路径总数")
    private Integer logCollectPathNum;

    @ApiModelProperty(value = "系统现有应用总数")
    private Integer serviceNum;

    @ApiModelProperty(value = "系统现有主机总数")
    private Integer hostNum;

    @ApiModelProperty(value = "系统现有容器总数")
    private Integer containerNum;

    @ApiModelProperty(value = "系统现有agent总数")
    private Integer agentNum;

    @ApiModelProperty(value = "系统当前采集流量总量/s")
    private Long currentCollectBytes;

    @ApiModelProperty(value = "系统当前采集总条数/s")
    private Long currentCollectLogEvents;

    @ApiModelProperty(value = "系统当前处于red状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id")
    private List<Pair<String, Long>> redLogCollectTaskNameIdPairList;

    @ApiModelProperty(value = "系统当前处于yellow状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id")
    private List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList;

    @ApiModelProperty(value = "系统当前处于red状态agent列表集 key：hostName value：agent id")
    private List<Pair<String, Long>> redAgentHostNameIdPairList;

    @ApiModelProperty(value = "系统当前处于yellow状态agent列表集 key：hostName value：agent id")
    private List<Pair<String, Long>> yellowAgentHostNameIdPairList;

    @ApiModelProperty(value = "近1分钟日志采集量最大top5日志采集任务")
    private List<MetricPointList> logCollectTaskListCollectBytesTop5;

    @ApiModelProperty(value = "近1分钟日志采集条数最大top5日志采集任务")
    private List<MetricPointList> logCollectTaskListCollectCountTop5;

    @ApiModelProperty(value = "cpu占用核数最多top5日志采集任务集")
    private List<MetricPointList> logCollectTaskListCpuUsageTop5;

    @ApiModelProperty(value = "memory占用最多top5日志采集任务集")
    private List<MetricPointList> logCollectTaskListMemoryUsageTop5;

    @ApiModelProperty(value = "关联主机数最多top5日志采集任务集")
    private List<MetricPointList> logCollectTaskListRelateHostsTop5;

    @ApiModelProperty(value = "关联agent数最多top5日志采集任务集")
    private List<MetricPointList> logCollectTaskListRelateAgentsTop5;

    @ApiModelProperty(value = "近1分钟日志采集量最大top5 agent")
    private List<MetricPointList> agentListCollectBytesTop5;

    @ApiModelProperty(value = "近1分钟日志采集条数最大top5 agent")
    private List<MetricPointList> agentListCollectCountTop5;

    @ApiModelProperty(value = "cpu占用核数最多top5agent集")
    private List<MetricPointList> agentListCpuUsageTop5;

    @ApiModelProperty(value = "cpu load 最多top5agent集")
    private List<MetricPointList> agentListCpuLoadTop5;

    @ApiModelProperty(value = "内存使用量最多top5agent集")
    private List<MetricPointList> agentListMemoryUsageTop5;

    @ApiModelProperty(value = "full gc 最多top5agent集")
    private List<MetricPointList> agentListFullGcCountTop5;

    @ApiModelProperty(value = "关联日志采集任务数最多top5 agent")
    private List<MetricPointList> agentListRelateLogCollectTasksTop5;

    public Integer getLogCollectTaskNum() {
        return logCollectTaskNum;
    }

    public void setLogCollectTaskNum(Integer logCollectTaskNum) {
        this.logCollectTaskNum = logCollectTaskNum;
    }

    public Integer getLogCollectPathNum() {
        return logCollectPathNum;
    }

    public void setLogCollectPathNum(Integer logCollectPathNum) {
        this.logCollectPathNum = logCollectPathNum;
    }

    public Integer getServiceNum() {
        return serviceNum;
    }

    public void setServiceNum(Integer serviceNum) {
        this.serviceNum = serviceNum;
    }

    public Integer getHostNum() {
        return hostNum;
    }

    public void setHostNum(Integer hostNum) {
        this.hostNum = hostNum;
    }

    public Integer getContainerNum() {
        return containerNum;
    }

    public void setContainerNum(Integer containerNum) {
        this.containerNum = containerNum;
    }

    public Integer getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(Integer agentNum) {
        this.agentNum = agentNum;
    }

    public Long getCurrentCollectBytes() {
        return currentCollectBytes;
    }

    public void setCurrentCollectBytes(Long currentCollectBytes) {
        this.currentCollectBytes = currentCollectBytes;
    }

    public Long getCurrentCollectLogEvents() {
        return currentCollectLogEvents;
    }

    public void setCurrentCollectLogEvents(Long currentCollectLogEvents) {
        this.currentCollectLogEvents = currentCollectLogEvents;
    }

    public List<Pair<String, Long>> getRedLogCollectTaskNameIdPairList() {
        return redLogCollectTaskNameIdPairList;
    }

    public void setRedLogCollectTaskNameIdPairList(List<Pair<String, Long>> redLogCollectTaskNameIdPairList) {
        this.redLogCollectTaskNameIdPairList = redLogCollectTaskNameIdPairList;
    }

    public List<Pair<String, Long>> getYellowLogCollectTaskNameIdPairList() {
        return yellowLogCollectTaskNameIdPairList;
    }

    public void setYellowLogCollectTaskNameIdPairList(List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList) {
        this.yellowLogCollectTaskNameIdPairList = yellowLogCollectTaskNameIdPairList;
    }

    public List<Pair<String, Long>> getRedAgentHostNameIdPairList() {
        return redAgentHostNameIdPairList;
    }

    public void setRedAgentHostNameIdPairList(List<Pair<String, Long>> redAgentHostNameIdPairList) {
        this.redAgentHostNameIdPairList = redAgentHostNameIdPairList;
    }

    public List<Pair<String, Long>> getYellowAgentHostNameIdPairList() {
        return yellowAgentHostNameIdPairList;
    }

    public void setYellowAgentHostNameIdPairList(List<Pair<String, Long>> yellowAgentHostNameIdPairList) {
        this.yellowAgentHostNameIdPairList = yellowAgentHostNameIdPairList;
    }

    public List<MetricPointList> getLogCollectTaskListCollectBytesTop5() {
        return logCollectTaskListCollectBytesTop5;
    }

    public void setLogCollectTaskListCollectBytesTop5(List<MetricPointList> logCollectTaskListCollectBytesTop5) {
        this.logCollectTaskListCollectBytesTop5 = logCollectTaskListCollectBytesTop5;
    }

    public List<MetricPointList> getLogCollectTaskListCollectCountTop5() {
        return logCollectTaskListCollectCountTop5;
    }

    public void setLogCollectTaskListCollectCountTop5(List<MetricPointList> logCollectTaskListCollectCountTop5) {
        this.logCollectTaskListCollectCountTop5 = logCollectTaskListCollectCountTop5;
    }

    public List<MetricPointList> getLogCollectTaskListCpuUsageTop5() {
        return logCollectTaskListCpuUsageTop5;
    }

    public void setLogCollectTaskListCpuUsageTop5(List<MetricPointList> logCollectTaskListCpuUsageTop5) {
        this.logCollectTaskListCpuUsageTop5 = logCollectTaskListCpuUsageTop5;
    }

    public List<MetricPointList> getLogCollectTaskListMemoryUsageTop5() {
        return logCollectTaskListMemoryUsageTop5;
    }

    public void setLogCollectTaskListMemoryUsageTop5(List<MetricPointList> logCollectTaskListMemoryUsageTop5) {
        this.logCollectTaskListMemoryUsageTop5 = logCollectTaskListMemoryUsageTop5;
    }

    public List<MetricPointList> getLogCollectTaskListRelateHostsTop5() {
        return logCollectTaskListRelateHostsTop5;
    }

    public void setLogCollectTaskListRelateHostsTop5(List<MetricPointList> logCollectTaskListRelateHostsTop5) {
        this.logCollectTaskListRelateHostsTop5 = logCollectTaskListRelateHostsTop5;
    }

    public List<MetricPointList> getLogCollectTaskListRelateAgentsTop5() {
        return logCollectTaskListRelateAgentsTop5;
    }

    public void setLogCollectTaskListRelateAgentsTop5(List<MetricPointList> logCollectTaskListRelateAgentsTop5) {
        this.logCollectTaskListRelateAgentsTop5 = logCollectTaskListRelateAgentsTop5;
    }

    public List<MetricPointList> getAgentListCollectBytesTop5() {
        return agentListCollectBytesTop5;
    }

    public void setAgentListCollectBytesTop5(List<MetricPointList> agentListCollectBytesTop5) {
        this.agentListCollectBytesTop5 = agentListCollectBytesTop5;
    }

    public List<MetricPointList> getAgentListCollectCountTop5() {
        return agentListCollectCountTop5;
    }

    public void setAgentListCollectCountTop5(List<MetricPointList> agentListCollectCountTop5) {
        this.agentListCollectCountTop5 = agentListCollectCountTop5;
    }

    public List<MetricPointList> getAgentListCpuUsageTop5() {
        return agentListCpuUsageTop5;
    }

    public void setAgentListCpuUsageTop5(List<MetricPointList> agentListCpuUsageTop5) {
        this.agentListCpuUsageTop5 = agentListCpuUsageTop5;
    }

    public List<MetricPointList> getAgentListCpuLoadTop5() {
        return agentListCpuLoadTop5;
    }

    public void setAgentListCpuLoadTop5(List<MetricPointList> agentListCpuLoadTop5) {
        this.agentListCpuLoadTop5 = agentListCpuLoadTop5;
    }

    public List<MetricPointList> getAgentListMemoryUsageTop5() {
        return agentListMemoryUsageTop5;
    }

    public void setAgentListMemoryUsageTop5(List<MetricPointList> agentListMemoryUsageTop5) {
        this.agentListMemoryUsageTop5 = agentListMemoryUsageTop5;
    }

    public List<MetricPointList> getAgentListFullGcCountTop5() {
        return agentListFullGcCountTop5;
    }

    public void setAgentListFullGcCountTop5(List<MetricPointList> agentListFullGcCountTop5) {
        this.agentListFullGcCountTop5 = agentListFullGcCountTop5;
    }

    public List<MetricPointList> getAgentListRelateLogCollectTasksTop5() {
        return agentListRelateLogCollectTasksTop5;
    }

    public void setAgentListRelateLogCollectTasksTop5(List<MetricPointList> agentListRelateLogCollectTasksTop5) {
        this.agentListRelateLogCollectTasksTop5 = agentListRelateLogCollectTasksTop5;
    }
}
