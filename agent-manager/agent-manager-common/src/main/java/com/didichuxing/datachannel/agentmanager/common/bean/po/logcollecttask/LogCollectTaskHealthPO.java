package com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask;

import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;

import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康度信息
 */
@Data
public class LogCollectTaskHealthPO extends BasePO {

    /**
     * 日志采集任务健康度信息唯一标识
     */
    private Long id;
    /**
     * 对应日志采集任务id
     */
    private Long logCollectTaskId;
    /**
     * 采集任务健康等级
     * 0：绿色 表示：采集任务很健康，对业务没有任何影响，且运行该采集任务的 AgentPO 也健康
     * 1：黄色 表示：采集任务存在风险，该采集任务有对应错误日志输出
     * 2：红色 表示：采集任务不健康，对业务有影响，该采集任务需要做采集延迟监控但乱序输出，或该采集任务需要做采集延迟监控但延迟时间超过指定阈值、该采集任务对应 kafka 集群信息不存在 待维护
     */
    private Integer logCollectTaskHealthLevel;
    /**
     * 日志采集任务健康描述信息
     */
    private String logCollectTaskHealthDescription;
    /**
     * 日志采集任务巡检结果类型（详见 LogCollectTaskHealthInspectionResultEnum）
     */
    private Integer logCollectTaskHealthInspectionResultType;

}