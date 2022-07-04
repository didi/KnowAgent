package com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard.DashBoardDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MaintenanceDashBoardVO {

    /********************************** part 1 **********************************/

    @ApiModelProperty(value = "系统现有服务总数")
    private Long serviceNum;

    @ApiModelProperty(value = "系统现有未关联主机服务数")
    private Long nonRelateAnyHostServiceNum;

    @ApiModelProperty(value = "系统现有主机总数")
    private Long hostNum;

    @ApiModelProperty(value = "故障主机数")
    private Long faultyHostNum;

    @ApiModelProperty(value = "系统现有agent总数")
    private Long agentNum;

    @ApiModelProperty(value = "未运行采集任务的 Agent 数")
    private Long nonRelateAnyLogCollectTaskAgentNum;

    @ApiModelProperty(value = "Agent 版本数")
    private Long agentVersionNumber;

    @ApiModelProperty(value = "未运行任何 Agent 实例的 Agent 版本数")
    private Long nonRelateAnyAgentAgentVersionNum;

    @ApiModelProperty(value = "系统现有日志采集任务总数")
    private Long logCollectTaskNum;

    @ApiModelProperty(value = "未在 Agent 上运行的采集任务数")
    private Long nonRelateAnyHostLogCollectTaskNum;

    @ApiModelProperty(value = "系统现有日志采集路径总数")
    private Long logCollectPathNum;

    @ApiModelProperty(value = "全量主机实时流量 单位：byte/s")
    private Long allHostsSendAndReceiveBytesPerSecond;

    @ApiModelProperty(value = "全量主机实时上行流量 单位：byte/s")
    private Long allHostsSendBytesPerSecond;

    @ApiModelProperty(value = "全量主机实时下行流量 单位：byte/s")
    private Long allHostsReceiveBytesPerSecond;

    @ApiModelProperty(value = "全量 Agent 实时流量 单位：byte/s")
    private Long allAgentsSendAndReceiveBytesPerSecond;

    @ApiModelProperty(value = "全量 Agent 实时上行流量 单位：byte/s")
    private Long allAgentsSendBytesPerSecond;

    @ApiModelProperty(value = "全量 Agent 实时下行流量 单位：byte/s")
    private Long allAgentsReceiveBytesPerSecond;

    /********************************** part 2 **********************************/

    @ApiModelProperty(value = "主机各操作系统类型 - 数量 key：操作系统类型 value：数量")
    private Map<String, Long> osTypeCountMap;

    @ApiModelProperty(value = "Agent 各版本 - 数量 key：Agent 版本 value：数量")
    private Map<String, Long> agentVersionCountMap;

    @ApiModelProperty(value = "系统当前处于red状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id")
    private List<Pair<String, Long>> redLogCollectTaskNameIdPairList;

    @ApiModelProperty(value = "系统当前处于yellow状态日志采集任务列表集 key：日志采集任务名 value：日志采集任务 id")
    private List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList;

    @ApiModelProperty(value = "系统当前处于red状态agent列表集 key：hostName value：agent id")
    private List<Pair<String, Long>> redAgentHostNameIdPairList;

    @ApiModelProperty(value = "系统当前处于yellow状态agent列表集 key：hostName value：agent id")
    private List<Pair<String, Long>> yellowAgentHostNameIdPairList;

    /********************************** part 3 **********************************/

    @ApiModelProperty(value = "当前主机时间误差时长 top5 Agents（单位：秒）")
    private MetricPanel ntpGapTop5Agents;

    @ApiModelProperty(value = "当前进程 cpu 使用率 top5 Agents（单位：%）")
    private MetricPanel cpuUsageTop5Agents;

    @ApiModelProperty(value = "当前进程内存使用量 top5 Agents（单位：MB）")
    private MetricPanel memoryUsedTop5Agents;

    @ApiModelProperty(value = "当前系统带宽使用量 top5 Agents（单位：MB）")
    private MetricPanel bandWidthUsedTop5Agents;

    @ApiModelProperty(value = "当前系统带宽使用率 top5 Agents（单位：%）")
    private MetricPanel bandWidthUsageTop5Agents;

    @ApiModelProperty(value = "当日 Agent 进程 full gc 次数 top5 Agents（单位：次）")
    private MetricPanel fullGcTimesDayTop5Agents;

    @ApiModelProperty(value = "当前 Agent 进程 fd 使用量 top5 Agents（单位：个）")
    private MetricPanel fdUsedTop5Agents;

    @ApiModelProperty(value = "当前 Agent 进程上行流量 top5 Agents（单位：MB）")
    private MetricPanel uplinkBytesTop5Agents;

    @ApiModelProperty(value = "近1分钟 Agent 进程发送日志条数 top5 Agents（单位：条）")
    private MetricPanel sendLogEventsLast1MinuteTop5Agents;

    @ApiModelProperty(value = "近1分钟 Agent 进程发送日志量 top5 Agents（单位：MB）")
    private MetricPanel sendBytesLast1MinuteTop5Agents;

    @ApiModelProperty(value = "当日 Agent 进程发送日志量 top5 Agents（单位：GB）")
    private MetricPanel sendBytesDayTop5Agents;

    @ApiModelProperty(value = "当日 Agent 进程发送日志条数 top5 Agents（单位：条）")
    private MetricPanel sendLogEventsDayTop5Agents;

    @ApiModelProperty(value = "当前具有运行状态的日志采集任务数 top5 Agents（单位：个）")
    private MetricPanel runningLogCollectTasksTop5Agents;

    @ApiModelProperty(value = "当前具有运行状态的日志采集路径数 top5 Agents（单位：个）")
    private MetricPanel runningLogCollectPathsTop5Agents;

    /*************************** logCollectTask 视角 ***************************/

    @ApiModelProperty(value = "当前采集的日志业务时间最大延时 top5 采集任务（单位：秒）")
    private MetricPanel logTimeDelayTop5LogCollectTasks;

    @ApiModelProperty(value = "当日限流时长 top5 采集任务（单位：秒）")
    private MetricPanel limitTimeTop5LogCollectTasks;

    @ApiModelProperty(value = "近1分钟发送日志量 top5 采集任务（单位：MB）")
    private MetricPanel sendBytesLast1MinuteTop5LogCollectTasks;

    @ApiModelProperty(value = "近1分钟发送日志条数 top5 采集任务（单位：条）")
    private MetricPanel sendLogEventsLast1MinuteTop5LogCollectTasks;

    @ApiModelProperty(value = "当日发送日志量 top5 采集任务（单位：GB）")
    private MetricPanel sendBytesDayTop5LogCollectTasks;

    @ApiModelProperty(value = "当日发送日志条数 top5 采集任务（单位：条）")
    private MetricPanel sendLogEventsDayTop5LogCollectTasks;

    @ApiModelProperty(value = "当前关联主机数 top5 采集任务（单位：个）")
    private MetricPanel relateHostsTop5LogCollectTasks;

    @ApiModelProperty(value = "当前关联 Agent 数 top5 采集任务（单位：个）")
    private MetricPanel relateAgentsTop5LogCollectTasks;

    public static MaintenanceDashBoardVO cast2MaintenanceDashBoardVO(DashBoardDO dashBoardDO) {
        return ConvertUtil.obj2Obj(dashBoardDO, MaintenanceDashBoardVO.class);
    }

}
