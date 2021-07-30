package com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class DashBoardVO {

    @ApiModelProperty(value = "系统现有日志采集任务总数")
    private Long logCollectTaskNum;

    @ApiModelProperty(value = "系统现有未关联任何主机的日志采集任务数")
    private Long nonRelateAnyHostLogCollectTaskNum;

    @ApiModelProperty(value = "系统现有日志采集路径总数")
    private Long logCollectPathNum;

    @ApiModelProperty(value = "系统现有应用总数")
    private Long serviceNum;

    @ApiModelProperty(value = "系统现有主机总数")
    private Long hostNum;

    @ApiModelProperty(value = "系统现有容器总数")
    private Long containerNum;

    @ApiModelProperty(value = "系统现有agent总数")
    private Long agentNum;

    @ApiModelProperty(value = "系统现有未关联任何日志采集任务的 agent 数")
    private Long nonRelateAnyLogCollectTaskAgentNum;

    @ApiModelProperty(value = "系统当前采集流量总量/s")
    private Long currentCollectBytes;

    @ApiModelProperty(value = "系统当前采集总条数/s")
    private Long currentCollectLogEvents;

    @ApiModelProperty(value = "当日采集流量")
    private Long collectBytesDay;

    @ApiModelProperty(value = "当日采集条数")
    private Long collectLogEventsDay;

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

    //TODO：后续 待 实现
//    @ApiModelProperty(value = "cpu load 最多top5agent集")
//    private List<MetricPointList> agentListCpuLoadTop5;

    @ApiModelProperty(value = "内存使用量最多top5agent集")
    private List<MetricPointList> agentListMemoryUsageTop5;

    @ApiModelProperty(value = " fd 使用量最多top5agent集")
    private List<MetricPointList> agentListFdUsedTop5;

    @ApiModelProperty(value = "full gc 最多top5agent集")
    private List<MetricPointList> agentListFullGcCountTop5;

    @ApiModelProperty(value = "关联日志采集任务数最多top5 agent")
    private List<MetricPointList> agentListRelateLogCollectTasksTop5;

    public Long getLogCollectTaskNum() {
        return logCollectTaskNum;
    }

    public void setLogCollectTaskNum(Long logCollectTaskNum) {
        this.logCollectTaskNum = logCollectTaskNum;
    }

    public Long getLogCollectPathNum() {
        return logCollectPathNum;
    }

    public void setLogCollectPathNum(Long logCollectPathNum) {
        this.logCollectPathNum = logCollectPathNum;
    }

    public Long getServiceNum() {
        return serviceNum;
    }

    public void setServiceNum(Long serviceNum) {
        this.serviceNum = serviceNum;
    }

    public Long getHostNum() {
        return hostNum;
    }

    public void setHostNum(Long hostNum) {
        this.hostNum = hostNum;
    }

    public Long getContainerNum() {
        return containerNum;
    }

    public void setContainerNum(Long containerNum) {
        this.containerNum = containerNum;
    }

    public Long getAgentNum() {
        return agentNum;
    }

    public void setAgentNum(Long agentNum) {
        this.agentNum = agentNum;
    }

    public Long getNonRelateAnyHostLogCollectTaskNum() {
        return nonRelateAnyHostLogCollectTaskNum;
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

    public Long getCollectBytesDay() {
        return collectBytesDay;
    }

    public void setCollectBytesDay(Long collectBytesDay) {
        this.collectBytesDay = collectBytesDay;
    }

    public Long getCollectLogEventsDay() {
        return collectLogEventsDay;
    }

    public void setCollectLogEventsDay(Long collectLogEventsDay) {
        this.collectLogEventsDay = collectLogEventsDay;
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

    public List<MetricPointList> getAgentListFdUsedTop5() {
        return agentListFdUsedTop5;
    }

    public void setAgentListFdUsedTop5(List<MetricPointList> agentListFdUsedTop5) {
        this.agentListFdUsedTop5 = agentListFdUsedTop5;
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

    public void setNonRelateAnyHostLogCollectTaskNum(Long nonRelateAnyHostLogCollectTaskNum) {
        this.nonRelateAnyHostLogCollectTaskNum = nonRelateAnyHostLogCollectTaskNum;
    }

    public Long getNonRelateAnyLogCollectTaskAgentNum() {
        return nonRelateAnyLogCollectTaskAgentNum;
    }

    public void setNonRelateAnyLogCollectTaskAgentNum(Long nonRelateAnyLogCollectTaskAgentNum) {
        this.nonRelateAnyLogCollectTaskAgentNum = nonRelateAnyLogCollectTaskAgentNum;
    }

    public List<MetricPointList> getAgentListRelateLogCollectTasksTop5() {
        return agentListRelateLogCollectTasksTop5;
    }

    public void setAgentListRelateLogCollectTasksTop5(List<MetricPointList> agentListRelateLogCollectTasksTop5) {
        this.agentListRelateLogCollectTasksTop5 = agentListRelateLogCollectTasksTop5;
    }
}
