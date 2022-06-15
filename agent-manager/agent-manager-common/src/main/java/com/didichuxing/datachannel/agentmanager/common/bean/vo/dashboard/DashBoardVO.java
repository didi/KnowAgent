package com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DashBoardVO {

    /********************************** part 1 **********************************/

    @ApiModelProperty(value = "系统现有主机总数")
    private Long hostNum;

    @ApiModelProperty(value = "系统现有容器总数")
    private Long containerNum;

    @ApiModelProperty(value = "系统现有agent总数")
    private Long agentNum;

    @ApiModelProperty(value = "系统现有未关联任何日志采集任务的agent数")
    private Long nonRelateAnyLogCollectTaskAgentNum;

    @ApiModelProperty(value = "系统现有服务总数")
    private Long serviceNum;

    @ApiModelProperty(value = "系统现有未关联主机服务数")
    private Long nonRelateAnyHostServiceNum;

    @ApiModelProperty(value = "系统现有日志采集任务总数")
    private Long logCollectTaskNum;

    @ApiModelProperty(value = "系统现有未关联任何主机的日志采集任务数")
    private Long nonRelateAnyHostLogCollectTaskNum;

    @ApiModelProperty(value = "系统现有日志采集路径总数")
    private Long logCollectPathNum;

    @ApiModelProperty(value = "系统全量agent cpu耗费（单位：core）")
    private Double agentCpuCoresSpend;

    @ApiModelProperty(value = "系统全量agent memory耗费（单位：byte）")
    private Long agentMemorySpend;

    @ApiModelProperty(value = "系统全量agent实时上行流量（单位：byte/s）")
    private Long agentUplinkBytes;

    @ApiModelProperty(value = "系统全量agent实时下行流量（单位：byte/s）")
    private Long agentDownLinkBytes;

    @ApiModelProperty(value = "系统全量agent近1分钟发送条数")
    private Long agentSendLogEventsLast1Minute;

    @ApiModelProperty(value = "系统全量agent近1分钟发送流量")
    private Long agentSendBytesLast1Minute;

    @ApiModelProperty(value = "系统全量agent当日发送流量")
    private Long agentSendBytesDay;

    @ApiModelProperty(value = "系统全量agent当日发送条数")
    private Long agentSendLogEventsDay;

    /********************************** part 2 **********************************/

    @ApiModelProperty(value = "系统当前处于red状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id")
    private List<Pair<String, Long>> redLogCollectTaskNameIdPairList;

    @ApiModelProperty(value = "系统当前处于yellow状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id")
    private List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList;

    @ApiModelProperty(value = "系统当前处于red状态agent列表集 key：hostName value：agent id")
    private List<Pair<String, Long>> redAgentHostNameIdPairList;

    @ApiModelProperty(value = "系统当前处于yellow状态agent列表集 key：hostName value：agent id")
    private List<Pair<String, Long>> yellowAgentHostNameIdPairList;

    /********************************** part 3 **********************************/

    /*************************** agent 视角 ***************************/

    @ApiModelProperty(value = "主机时间误差时长 top5 Agents（单位：秒）")
    private MetricPanel ntpGapTop5Agents;

    @ApiModelProperty(value = "进程 cpu 使用率 top5 Agents（单位：%）")
    private MetricPanel cpuUsageTop5Agents;

    @ApiModelProperty(value = "进程内存使用量 top5 Agents（单位：MB）")
    private MetricPanel memoryUsedTop5Agents;

    @ApiModelProperty(value = "系统带宽使用量 top5 Agents（单位：MB）")
    private MetricPanel bandWidthUsedTop5Agents;

    @ApiModelProperty(value = "系统带宽使用率 top5 Agents（单位：%）")
    private MetricPanel bandWidthUsageTop5Agents;

    @ApiModelProperty(value = "进程最近一分钟内 full gc 次数 top5 Agents（单位：次）")
    private MetricPanel fullGcTimesDayTop5Agents;

    @ApiModelProperty(value = "进程 fd 使用量 top5 Agents（单位：个）")
    private MetricPanel fdUsedTop5Agents;

    @ApiModelProperty(value = "进程上行流量 top5 Agents（单位：MB）")
    private MetricPanel uplinkBytesTop5Agents;

    @ApiModelProperty(value = "进程近1分钟发送日志条数 top5 Agents（单位：条）")
    private MetricPanel sendLogEventsLast1MinuteTop5Agents;

    @ApiModelProperty(value = "进程近1分钟发送日志量 top5 Agents（单位：MB）")
    private MetricPanel sendBytesLast1MinuteTop5Agents;

    @ApiModelProperty(value = "进程当日发送日志量 top5 Agents（单位：MB）")
    private MetricPanel sendBytesDayTop5Agents;

    @ApiModelProperty(value = "进程当日发送日志条数 top5 Agents（单位：条）")
    private MetricPanel sendLogEventsDayTop5Agents;

    @ApiModelProperty(value = "具有运行状态的日志采集任务数 top5 Agents（单位：个）")
    private MetricPanel runningLogCollectTasksTop5Agents;

    @ApiModelProperty(value = "具有运行状态的日志采集路径数 top5 Agents（单位：个）")
    private MetricPanel runningLogCollectPathsTop5Agents;

    /*************************** logCollectTask 视角 ***************************/

    @ApiModelProperty(value = "采集的日志业务时间延时最大 top5 采集任务（单位：秒）")
    private MetricPanel logTimeDelayTop5LogCollectTasks;

    @ApiModelProperty(value = "限流时长 top5 采集任务（单位：秒）")
    private MetricPanel limitTimeTop5LogCollectTasks;

    @ApiModelProperty(value = "近1分钟发送日志量 top5 采集任务（单位：MB）")
    private MetricPanel sendBytesLast1MinuteTop5LogCollectTasks;

    @ApiModelProperty(value = "近1分钟发送日志条数 top5 采集任务（单位：条）")
    private MetricPanel sendLogEventsLast1MinuteTop5LogCollectTasks;

    @ApiModelProperty(value = "当日发送日志量 top5 采集任务（单位：MB）")
    private MetricPanel sendBytesDayTop5LogCollectTasks;

    @ApiModelProperty(value = "当日发送日志条数 top5 采集任务（单位：条）")
    private MetricPanel sendLogEventsDayTop5LogCollectTasks;

//    @ApiModelProperty(value = "关联主机数 top5 logCollectTasks，key：logCollectTaskId value：关联主机数")
//    private MetricPanel relateHostsTop5LogCollectTasks;

//    @ApiModelProperty(value = "关联agent数 top5 logCollectTasks，key：logCollectTaskId value：关联agent数")
//    private MetricPanel relateAgentsTop5LogCollectTasks;

    /*************************** service 视角 ***************************/

    @ApiModelProperty(value = "近1分钟发送日志量 top5 应用（单位：MB）")
    private MetricPanel sendBytesLast1MinuteTop5Services;

    @ApiModelProperty(value = "近1分钟发送日志条数 top5 应用（单位：条）")
    private MetricPanel sendLogEventsLast1MinuteTop5Services;

    @ApiModelProperty(value = "当日发送日志量 top5 应用（单位：MB）")
    private MetricPanel sendBytesDayTop5Services;

    @ApiModelProperty(value = "当日发送日志条数 top5 应用（单位：条）")
    private MetricPanel sendLogEventsDayTop5Services;

//    @ApiModelProperty(value = "关联主机数 top5 services，key：serviceName value：关联主机数")
//    private MetricPanel relateHostsTop5Services;

//    @ApiModelProperty(value = "关联agent数 top5 services，key：serviceName value：关联agent数")
//    private MetricPanel relateAgentsTop5Services;

}
