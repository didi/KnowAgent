package com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OperatingDashBoardVO {

    /********************************** part 1 **********************************/

    @ApiModelProperty(value = "系统现有服务总数")
    private Long serviceNum;

    @ApiModelProperty(value = "系统现有主机总数")
    private Long hostNum;

    @ApiModelProperty(value = "系统现有agent总数")
    private Long agentNum;

    @ApiModelProperty(value = "系统现有日志采集任务总数")
    private Long logCollectTaskNum;

    @ApiModelProperty(value = "系统全量agent cpu耗费（单位：core）")
    private Double agentCpuCoresSpend;

    @ApiModelProperty(value = "系统全量agent memory耗费（单位：byte）")
    private Long agentMemorySpend;

    @ApiModelProperty(value = "全量主机实时流量 单位：byte/s")
    private Long allHostsSendAndReceiveBytesPerSecond;

    @ApiModelProperty(value = "全量 Agent 实时流量 单位：byte/s")
    private Long allAgentsSendAndReceiveBytesPerSecond;

    @ApiModelProperty(value = "系统全量agent近1分钟发送条数")
    private Long agentSendLogEventsLast1Minute;

    @ApiModelProperty(value = "系统全量agent近1分钟发送流量")
    private Long agentSendBytesLast1Minute;

    @ApiModelProperty(value = "系统全量agent当日发送流量")
    private Long agentSendBytesDay;

    @ApiModelProperty(value = "系统全量agent当日发送条数")
    private Long agentSendLogEventsDay;

    /********************************** part 2 **********************************/

    /*************************** Application 视角 ***************************/

    @ApiModelProperty(value = "近1分钟发送日志量 top5 应用（单位：MB）")
    private List<Pair<String, Double>> sendBytesLast1MinuteTop5Applications;

    @ApiModelProperty(value = "近1分钟发送日志条数 top5 应用（单位：条）")
    private List<Pair<String, Double>> sendLogEventsLast1MinuteTop5Applications;

    @ApiModelProperty(value = "当日发送日志量 top5 应用（单位：GB）")
    private List<Pair<String, Double>> sendBytesDayTop5Applications;

    @ApiModelProperty(value = "当日发送日志条数 top5 应用（单位：条）")
    private List<Pair<String, Double>> sendLogEventsDayTop5Applications;

    @ApiModelProperty(value = "当前关联主机数 top5 应用（单位：个）")
    private List<Pair<String, Double>> relateHostsTop5Applications;

    @ApiModelProperty(value = "当前关联 Agent 数 top5 应用（单位：个）")
    private List<Pair<String, Double>> relateAgentsTop5Applications;

    @ApiModelProperty(value = "当前关联采集任务数 top5 应用（单位：个）")
    private List<Pair<String, Double>> relateLogCollectTaskTop5Applications;

}
