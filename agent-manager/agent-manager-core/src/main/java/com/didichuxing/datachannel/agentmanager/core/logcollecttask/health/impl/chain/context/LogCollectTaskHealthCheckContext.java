package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain.context;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsLogCollectTaskService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.MetricsLogCollectTaskPOMapper;
import lombok.Data;

import java.util.Map;

/**
 * @author Ronaldo
 */
@Data
public class LogCollectTaskHealthCheckContext extends Context {

    /**
     * 待诊断日志采集任务对象
     */
    private LogCollectTaskDO logCollectTaskDO;

    /**
     * 日志采集任务须采集的文件路径
     */
    private FileLogCollectPathDO fileLogCollectPathDO;

    /**
     * 日式采集任务须部署的主机
     */
    private HostDO hostDO;

    /**
     * 日志采集任务健康度检查流程获取agent心跳数据右边界时间
     */
    private Long logCollectTaskHealthCheckTimeEnd;

    /**
     *  各 logpath 对应近一次“文件路径是否存在健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private Map<Long, Long> fileLogCollectPathId2LastestFilePathExistsCheckHealthyTimeMap;

    /**
     *  各 logpath 对应近一次“日志异常截断健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private Map<Long, Long> fileLogCollectPathId2LastestAbnormalTruncationCheckHealthyTimeMap;

    /**
     *  各 logpath 对应近一次“日志切片健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private Map<Long, Long> fileLogCollectPathId2LastestLogSliceCheckHealthyTimeMap;

    /**
     *  各 logpath 对应近一次“文件乱序健康检查”为健康时的时间点，map json 形式：key: logpathid value:timestamp
     */
    private Map<Long, Long> fileLogCollectPathId2LastestFileDisorderCheckHealthyTimeMap;

    /**
     * 文件路径存在的主机个数
     */
    private Integer filePathExistsCheckHealthyCount;

    /**
     * 不存在异常截断主机个数
     */
    private Integer abnormalTruncationCheckHealthyCount;

    /**
     * 文件不乱序的主机个数
     */
    private Integer fileDisorderCheckHealthyCount;

    /**
     * 不存在日志切片配置错误主机个数
     */
    private Integer logSliceCheckHealthyCount;

    /**
     * AgentPO 指标管理服务接口
     */
    private AgentMetricsManageService agentMetricsManageService;

    /**
     * 是否校验日志采集任务采集延时 true：校验 false：不校验
     */
    private Boolean checkCollectDelay;

    /**
     * 日志采集任务健康度检查结果
     */
    private LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum;

    /**
     * 日志采集任务健康检查描述
     */
    private String logCollectTaskHealthDescription;

    /**
     * 采集任我指标查询接口
     */
    private MetricsLogCollectTaskService metricsLogCollectTaskService;

    private LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService;

}
