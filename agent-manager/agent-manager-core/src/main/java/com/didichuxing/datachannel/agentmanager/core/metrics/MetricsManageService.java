package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;

import java.util.List;

public interface MetricsManageService {

    /**
     * 根据给定指标类型获取该指标类型下的指标集（以树形式组织）
     * @param metricTypeCode 指标类型 对应MetricTypeEnum.code
     * @return 返回根据给定指标类型获取该指标类型下的指标集（以树形式组织）
     */
    MetricNodeVO getMetricsTreeByMetricType(Integer metricTypeCode);

    /**
     * 根据给定指标查询条件获取对应的指标数据信息
     * @param metricQueryDTO 指标查询条件
     * @return 返回根据给定指标查询条件获取对应的指标数据信息
     */
    MetricPanel getMetric(BusinessMetricsQueryDTO metricQueryDTO);

    /**
     * @param logCollectTaskId 日志采集任务id
     * @param pathId 日志采集路径id
     * @param hostName 主机名
     * @param errorFieldName 待查错误对应字段名
     * @param startHeartbeatTime 开始时间戳（不含）
     * @param endHeartbeatTime 结束时间戳（含）
     * @return 返回根据给定参数获取到的心跳信息集
     */
    List<MetricsLogCollectTaskPO> getErrorMetrics(Long logCollectTaskId, Long pathId, String hostName, String errorFieldName, Long startHeartbeatTime, Long endHeartbeatTime);

    /**
     * @param logCollectTaskMetricId 日志采集任务指标id
     * @return 根据给定日志采集任务指标id获取对应日志采集任务指标记录
     */
    MetricsLogCollectTaskPO getMetricLogCollectTask(Long logCollectTaskMetricId);

    /**
     * @param metricFieldEnum 指标对应枚举对象
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 返回给定指标在给定时间范围内在所有agent上汇总统计值
     */
    Long getSumMetricAllAgents(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime);

    /**
     * @param metricFieldEnum 指标对应枚举对象
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @param sortTimeField 排序字段名
     * @param logCollectTaskByServiceNames 日志采集任务相关指标是否按 serviceNames 进行分组统计 true：service names 分组统计 false：logCollectTask id 分组统计，非日志采集任务相关指标忽略该属性
     * @return 返回给定指标 topN agent 对应指标时序
     */
    List<List<MetricPoint>> getTopNByMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField, boolean logCollectTaskByServiceNames);

    /**
     * 清除给定时间前的指标数据
     * @param metricsExpireDays 指标过期时间
     */
    void clearExpireMetrics(Integer metricsExpireDays);

    /**
     *
     * @param logCollectTaskId 日志采集任务 id
     * @param fileLogCollectPathId 日志采集路径 id
     * @param logCollectTaskHostName 日志采集任务运行主机名
     * @param heartbeatTimeStart 心跳开始时间戳（>）
     * @param heartbeatTimeEnd 心跳结束时间戳（<=）
     * @param aggregationFunction 聚合函数名
     * @param aggregationField 聚合字段名
     * @return SELECT #{aggregationFunction}(#{aggregationFunction})
     *          FROM tb_metrics_log_collect_task
     *          WHERE collectTaskId = #{logCollectTaskId} AND
     *                pathId = #{fileLogCollectPathId} AND
     *                collectTaskHostName = #{logCollectTaskHostName} AND
     *                heartbeatTime > #{heartbeatTimeStart} AND heartbeatTime <= #{heartbeatTimeEnd}
     */
    Object getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String logCollectTaskHostName,
            Long heartbeatTimeStart,
            Long heartbeatTimeEnd,
            String aggregationFunction,
            String aggregationField
    );

    /**
     * @param logCollectTaskId 日志采集任务 id
     * @param pathId 日志采集路径 id
     * @param hostName 日志采集任务运行主机名
     * @return 返回最后一个日志采集任务指标数据
     */
    MetricsLogCollectTaskPO getLastLogCollectTaskMetric(Long logCollectTaskId, Long pathId, String hostName);

}
