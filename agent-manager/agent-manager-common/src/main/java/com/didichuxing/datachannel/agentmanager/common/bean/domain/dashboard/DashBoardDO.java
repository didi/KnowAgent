package com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashBoardDO {

    /********************************** part 1 **********************************/

    /**
     * 系统现有服务总数
     */
    private Long serviceNum;

    /**
     * 系统现有未关联主机服务数
     */
    private Long nonRelateAnyHostServiceNum;

    /**
     * 系统现有主机总数
     */
    private Long hostNum;

    /**
     * 故障主机数
     */
    private Long faultyHostNum;

    /**
     * 系统现有agent总数
     */
    private Long agentNum;

    /**
     * 未运行采集任务的 Agent 数
     */
    private Long nonRelateAnyLogCollectTaskAgentNum;

    /**
     * Agent 版本数
     */
    private Long agentVersionNumber;

    /**
     * 未运行任何 Agent 实例的 Agent 版本数
     */
    private Long nonRelateAnyAgentAgentVersionNum;

    /**
     * 系统现有日志采集任务总数
     */
    private Long logCollectTaskNum;

    /**
     * 未在 Agent 上运行的采集任务数
     */
    private Long nonRelateAnyHostLogCollectTaskNum;

    /**
     * 系统现有日志采集路径总数
     */
    private Long logCollectPathNum;

    /**
     * 全量主机实时流量 单位：byte/s
     */
    private Long allHostsSendAndReceiveBytesPerSecond;

    /**
     * 全量主机实时上行流量 单位：byte/s
     */
    private Long allHostsSendBytesPerSecond;

    /**
     * 全量主机实时下行流量 单位：byte/s
     */
    private Long allHostsReceiveBytesPerSecond;

    /**
     * 全量 Agent 实时流量 单位：byte/s
     */
    private Long allAgentsSendAndReceiveBytesPerSecond;

    /**
     * 全量 Agent 实时上行流量 单位：byte/s
     */
    private Long allAgentsSendBytesPerSecond;

    /**
     * 全量 Agent 实时下行流量 单位：byte/s
     */
    private Long allAgentsReceiveBytesPerSecond;

    /**
     * 系统全量agent cpu耗费（单位：core）
     */
    private Double agentCpuCoresSpend;

    /**
     * 系统全量agent memory耗费（单位：byte）
     */
    private Long agentMemorySpend;

    /**
     * 系统全量agent近1分钟发送条数
     */
    private Long agentSendLogEventsLast1Minute;

    /**
     * 系统全量agent近1分钟发送流量
     */
    private Long agentSendBytesLast1Minute;

    /**
     * 系统全量agent当日发送流量
     */
    private Long agentSendBytesDay;

    /**
     * 系统全量agent当日发送条数
     */
    private Long agentSendLogEventsDay;

    /********************************** part 2 **********************************/

    /**
     * 主机各操作系统类型 - 数量 key：操作系统类型 value：数量
     */
    private Map<String, Long> osTypeCountMap;

    /**
     * Agent 各版本 - 数量 key：Agent 版本 value：数量
     */
    private Map<String, Long> agentVersionCountMap;

    /**
     * 系统当前处于red状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id
     */
    private List<Pair<String, Long>> redLogCollectTaskNameIdPairList;

    /**
     * 系统当前处于yellow状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id
     */
    private List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList;

    /**
     * 系统当前处于red状态agent列表集 key：hostName value：agent id
     */
    private List<Pair<String, Long>> redAgentHostNameIdPairList;

    /**
     * 系统当前处于yellow状态agent列表集 key：hostName value：agent id
     */
    private List<Pair<String, Long>> yellowAgentHostNameIdPairList;

    /********************************** part 3 **********************************/

    /*************************** agent 视角 ***************************/

    /**
     * 当前主机时间误差时长 top5 Agents（单位：秒）
     */
    private MetricPanel ntpGapTop5Agents;

    /**
     * 当前进程 cpu 使用率 top5 Agents（单位：%）
     */
    private MetricPanel cpuUsageTop5Agents;

    /**
     * 当前进程内存使用量 top5 Agents（单位：MB）
     */
    private MetricPanel memoryUsedTop5Agents;

    /**
     * 当前系统带宽使用量 top5 Agents（单位：MB）
     */
    private MetricPanel bandWidthUsedTop5Agents;

    /**
     * 当前系统带宽使用率 top5 Agents（单位：%）
     */
    private MetricPanel bandWidthUsageTop5Agents;

    /**
     * 当日 Agent 进程 full gc 次数 top5 Agents（单位：次）
     */
    private MetricPanel fullGcTimesDayTop5Agents;

    /**
     * 当前 Agent 进程 fd 使用量 top5 Agents（单位：个）
     */
    private MetricPanel fdUsedTop5Agents;

    /**
     * 当前 Agent 进程上行流量 top5 Agents（单位：MB）
     */
    private MetricPanel uplinkBytesTop5Agents;

    /**
     * 近1分钟 Agent 进程发送日志条数 top5 Agents（单位：条）
     */
    private MetricPanel sendLogEventsLast1MinuteTop5Agents;

    /**
     * 近1分钟 Agent 进程发送日志量 top5 Agents（单位：MB）
     */
    private MetricPanel sendBytesLast1MinuteTop5Agents;

    /**
     * 当日 Agent 进程发送日志量 top5 Agents（单位：GB）
     */
    private MetricPanel sendBytesDayTop5Agents;

    /**
     * 当日 Agent 进程发送日志条数 top5 Agents（单位：条）
     */
    private MetricPanel sendLogEventsDayTop5Agents;

    /**
     * 当前具有运行状态的日志采集任务数 top5 Agents（单位：个）
     */
    private MetricPanel runningLogCollectTasksTop5Agents;

    /**
     * 当前具有运行状态的日志采集路径数 top5 Agents（单位：个）
     */
    private MetricPanel runningLogCollectPathsTop5Agents;

    /*************************** logCollectTask 视角 ***************************/

    /**
     * 当前采集的日志业务时间最大延时 top5 采集任务（单位：秒）
     */
    private MetricPanel logTimeDelayTop5LogCollectTasks;

    /**
     * 当日限流时长 top5 采集任务（单位：秒）
     */
    private MetricPanel limitTimeTop5LogCollectTasks;

    /**
     * 近1分钟发送日志量 top5 采集任务（单位：MB）
     */
    private MetricPanel sendBytesLast1MinuteTop5LogCollectTasks;

    /**
     * 近1分钟发送日志条数 top5 采集任务（单位：条）
     */
    private MetricPanel sendLogEventsLast1MinuteTop5LogCollectTasks;

    /**
     * 当日发送日志量 top5 采集任务（单位：GB）
     */
    private MetricPanel sendBytesDayTop5LogCollectTasks;

    /**
     * 当日发送日志条数 top5 采集任务（单位：条）
     */
    private MetricPanel sendLogEventsDayTop5LogCollectTasks;

    /**
     * 当前关联主机数 top5 采集任务（单位：个）
     */
    private MetricPanel relateHostsTop5LogCollectTasks;

    /**
     * 当前关联 Agent 数 top5 采集任务（单位：个）
     */
    private MetricPanel relateAgentsTop5LogCollectTasks;

    /*************************** Application 视角 ***************************/

    /**
     * 近1分钟发送日志量 top5 应用（单位：MB）
     */
    private MetricPanel sendBytesLast1MinuteTop5Applications;

    /**
     * 近1分钟发送日志条数 top5 应用（单位：条）
     */
    private MetricPanel sendLogEventsLast1MinuteTop5Applications;

    /**
     * 当日发送日志量 top5 应用（单位：GB）
     */
    private MetricPanel sendBytesDayTop5Applications;

    /**
     * 当日发送日志条数 top5 应用（单位：条）
     */
    private MetricPanel sendLogEventsDayTop5Applications;

    /**
     * 当前关联主机数 top5 应用（单位：个）
     */
    private MetricPanel relateHostsTop5Applications;

    /**
     * 当前关联 Agent 数 top5 应用（单位：个）
     */
    private MetricPanel relateAgentsTop5Applications;

    /**
     * 当前关联采集任务数 top5 应用（单位：个）
     */
    private MetricPanel relateLogCollectTaskTop5Applications;

}
