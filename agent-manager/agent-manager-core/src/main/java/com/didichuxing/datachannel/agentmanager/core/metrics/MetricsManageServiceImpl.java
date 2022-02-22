package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@org.springframework.stereotype.Service
public class MetricsManageServiceImpl implements MetricsManageService {

    @Autowired
    private MetricsSystemPOMapper metricsSystemDAO;

    @Autowired
    private MetricsAgentPOMapper metricsAgentDAO;

    @Autowired
    private MetricsDiskPOMapper metricsDiskDAO;

    @Autowired
    private MetricsNetCardPOMapper metricsNetCardDAO;

    @Autowired
    private MetricsProcessPOMapper metricsProcessDAO;

    @Autowired
    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;

    @Autowired
    private MetricsDiskIOPOMapper metricsDiskIODAO;

    /**
     * top n 默认值
     */
    private static Integer TOP_N_DEFAULT_VALUE = 5;

    /**
     * 指标排序类型 默认值
     */
    private static Integer SORT_METRIC_TYPE_DEFAULT_VALUE = 0;

    @Override
    public MetricNodeVO getMetricsTreeByMetricType(Integer metricTypeCode) {
        MetricTypeEnum rootMetricTypeEnum = MetricTypeEnum.fromCode(metricTypeCode);
        if(null == rootMetricTypeEnum) {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getMetricsTreeByMetricType||msg={%s}", ErrorCodeEnum.METRICS_TYPE_NOT_EXISTS.getMessage()),
                    ErrorCodeEnum.METRICS_TYPE_NOT_EXISTS.getCode()
            );
        }
        return handleGetMetricsTreeByMetricType(rootMetricTypeEnum);
    }

    @Override
    public MetricPanel getMetric(BusinessMetricsQueryDTO metricQueryDTO) {
        Integer metricCode = metricQueryDTO.getMetricCode();
        MetricFieldEnum metricFieldEnum = MetricFieldEnum.fromMetricCode(metricCode);
        if(null == metricFieldEnum) {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getMetric||msg={%s}", ErrorCodeEnum.METRIC_NOT_EXISTS.getMessage()),
                    ErrorCodeEnum.METRIC_NOT_EXISTS.getCode()
            );
        }
        if(isLogCollectTaskMetric(metricFieldEnum)) {//日志采集任务相关指标
            return getLogCollectTaskMetric(metricQueryDTO, metricFieldEnum);
        } else {//agent相关指标
            return getAgentMetric(metricQueryDTO, metricFieldEnum);
        }
    }

    @Override
    public List<MetricsLogCollectTaskPO> getErrorMetrics(Long logCollectTaskId, Long pathId, String hostName, String errorFieldName, Long startHeartbeatTime, Long endHeartbeatTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", pathId);
        params.put("hostName", hostName);
        params.put("errorFieldName", errorFieldName);
        params.put("startHeartbeatTime", startHeartbeatTime);
        params.put("endHeartbeatTime", endHeartbeatTime);
        return metricsLogCollectTaskDAO.getErrorMetrics(params);
    }

    @Override
    public MetricsLogCollectTaskPO getMetricLogCollectTask(Long logCollectTaskMetricId) {
        return metricsLogCollectTaskDAO.selectByPrimaryKey(logCollectTaskMetricId);
    }

    @Override
    public Long getSumMetricAllAgents(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        params.put("fieldName", metricFieldEnum.getFieldName());
        params.put("startTime", metricFieldEnum.getFieldName());
        params.put("endTime", metricFieldEnum.getFieldName());
        Double value = 0d;
        if(isProcessMetric(metricFieldEnum)) {
            value = metricsProcessDAO.getSumMetricAllAgents(params);
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            value = metricsAgentDAO.getSumMetricAllAgents(params);
        } else {
            //TODO：
            throw new RuntimeException();
        }
        if(null == value) {
            return 0L;
        } else {
            return Double.valueOf(Math.ceil(value)).longValue();
        }
    }

    @Override
    public List<List<MetricPoint>> getTopNByMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField, boolean logCollectTaskByServiceNames) {
        if(isSystemMetric(metricFieldEnum)) {
            return handleGetTopNBySystemMetric(metricFieldEnum, startTime, endTime, sortTimeField);
        } else if(isProcessMetric(metricFieldEnum)) {
            return handleGetTopNByProcessMetric(metricFieldEnum, startTime, endTime, sortTimeField);
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            return handleGetTopNByAgentBusinessMetric(metricFieldEnum, startTime, endTime, sortTimeField);
        } else if(isLogCollectTaskMetric(metricFieldEnum)) {
            if(logCollectTaskByServiceNames) {
                return handleGetTopNByLogCollectTaskMetricPerServiceNames(metricFieldEnum, startTime, endTime, sortTimeField);
            } else {
                return handleGetTopNByLogCollectTaskMetricPerLogCollectTaskId(metricFieldEnum, startTime, endTime, sortTimeField);
            }
        } else {
            //TODO：
            throw new RuntimeException();
        }
    }

    @Override
    public void clearExpireMetrics(Integer metricsExpireDays) {
        Long heartBeatTime = DateUtils.getBeforeDays(new Date(), metricsExpireDays).getTime();
        metricsLogCollectTaskDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsAgentDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsProcessDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsSystemDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsNetCardDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsDiskDAO.deleteByLtHeartbeatTime(heartBeatTime);
        metricsDiskIODAO.deleteByLtHeartbeatTime(heartBeatTime);
    }

    @Override
    public Double getAggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(
            Long logCollectTaskId,
            Long fileLogCollectPathId,
            String logCollectTaskHostName,
            Long heartbeatTimeStart,
            Long heartbeatTimeEnd,
            String aggregationFunction,
            String aggregationField
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", fileLogCollectPathId);
        params.put("hostName", logCollectTaskHostName);
        params.put("startTime", heartbeatTimeStart);
        params.put("endTime", heartbeatTimeEnd);
        params.put("function", aggregationFunction);
        params.put("fieldName", aggregationField);
        return metricsLogCollectTaskDAO.aggregationQueryPerLogCollectTskAndPathAndHostNameFromMetricsLogCollectTask(params);
    }

    @Override
    public MetricsLogCollectTaskPO getLastLogCollectTaskMetric(Long logCollectTaskId, Long pathId, String hostName) {
        Map<String, Object> params = new HashMap<>();
        params.put("logCollectTaskId", logCollectTaskId);
        params.put("pathId", pathId);
        params.put("hostName", hostName);
        return metricsLogCollectTaskDAO.getLastRecord(params);
    }

    private List<List<MetricPoint>> handleGetTopNByLogCollectTaskMetricPerServiceNames(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n logcollecttask
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsServiceNamesTopPO> metricsServiceNamesTopPOList = metricsLogCollectTaskDAO.getTopNByMetricPerServiceNames(params);
        /*
         * 2.）根据 top n host name，挨个获取单条线
         */
        List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
        for (MetricsServiceNamesTopPO metricsServiceNamesTopPO : metricsServiceNamesTopPOList) {
            String serviceNames = metricsServiceNamesTopPO.getServiceNames();
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("serviceNames", serviceNames);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatStatisticByServiceNames(params);
                multiLineChatValue.add(result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("serviceNames", serviceNames);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatNonStatisticByServiceNames(params);
                multiLineChatValue.add(result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
            }
        }
        return multiLineChatValue;
    }

    private List<List<MetricPoint>> handleGetTopNByLogCollectTaskMetricPerLogCollectTaskId(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n logcollecttask
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskIdTopPO> metricsLogCollectTaskIdTopPOList = metricsLogCollectTaskDAO.getTopNByMetricPerLogCollectTaskId(params);
        /*
         * 2.）根据 top n host name，挨个获取单条线
         */
        List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
        for (MetricsLogCollectTaskIdTopPO metricsLogCollectTaskIdTopPO : metricsLogCollectTaskIdTopPOList) {
            Long logCollectTaskId = metricsLogCollectTaskIdTopPO.getLogCollectTaskId();
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("logCollectTaskId", logCollectTaskId);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatStatisticByLogCollectTaskId(params);
                multiLineChatValue.add(result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("logCollectTaskId", logCollectTaskId);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatNonStatisticByLogCollectTaskId(params);
                multiLineChatValue.add(result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
            }
        }
        return multiLineChatValue;
    }

    private List<List<MetricPoint>> handleGetTopNByAgentBusinessMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n host name
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsAgentDAO.getTopNByMetricPerHostName(params);
        /*
         * 2.）根据 top n host name，挨个获取单条线
         */
        List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
        for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
            String hostName = metricsLogCollectTaskTopPO.getHostName();
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", hostName);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsAgentDAO.getSingleChatStatisticByHostName(params);
                multiLineChatValue.add(result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", hostName);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsAgentDAO.getSingleChatNonStatisticByHostName(params);
                multiLineChatValue.add(result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
            }
        }
        return multiLineChatValue;
    }

    private List<List<MetricPoint>> handleGetTopNByProcessMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n host name
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsProcessDAO.getTopNByMetricPerHostName(params);
        /*
         * 2.）根据 top n host name，挨个获取单条线
         */
        List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
        for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
            String hostName = metricsLogCollectTaskTopPO.getHostName();
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", hostName);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsProcessDAO.getSingleChatStatisticByHostName(params);
                multiLineChatValue.add(result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", hostName);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsProcessDAO.getSingleChatNonStatisticByHostName(params);
                multiLineChatValue.add(result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
            }
        }
        return multiLineChatValue;
    }

    private List<List<MetricPoint>> handleGetTopNBySystemMetric(MetricFieldEnum metricFieldEnum, Long startTime, Long endTime, String sortTimeField) {
        /*
         * 1.）获取 top n host name
         */
        Map<String, Object> params = new HashMap<>();
        params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
        Integer sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
        params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
        params.put("sortTime", getSortTime(sortTimeField, endTime));
        params.put("topN", TOP_N_DEFAULT_VALUE);
        params.put("sortType", SortTypeEnum.DESC.getType());
        params.put("sortTimeField", sortTimeField);
        List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsSystemDAO.getTopNByMetricPerHostName(params);
        /*
         * 2.）根据 top n host name，挨个获取单条线
         */
        List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
        for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
            String hostName = metricsLogCollectTaskTopPO.getHostName();
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", hostName);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsSystemDAO.getSingleChatStatisticByHostName(params);
                multiLineChatValue.add(result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", hostName);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                List<MetricPoint> result = metricsSystemDAO.getSingleChatNonStatisticByHostName(params);
                multiLineChatValue.add(result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
            }
        }
        return multiLineChatValue;
    }

    private Long getSortTime(String sortTimeField, Long time) {
        if(SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName().equals(sortTimeField)) {
            return DateUtils.getDayUnitTimeStamp(time);
        } else if(SortTimeFieldEnum.HEARTBEAT_TIME_HOUR.getFieldName().equals(sortTimeField)) {
            return DateUtils.getHourUnitTimeStamp(time);
        } else if(SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName().equals(sortTimeField)) {
            return DateUtils.getMinuteUnitTimeStamp(time);
        } else {
            //TODO：
            throw new RuntimeException();
        }
    }

    /**
     * @param metricFieldEnum 指标字段定义枚举对象
     * @return 校验给定指标字段定义枚举对象是否为日志采集任务相关指标 true：是 false：否
     */
    private boolean isLogCollectTaskMetric(MetricFieldEnum metricFieldEnum) {
        return metricFieldEnum.getMetricType().equals(MetricTypeEnum.LOG_COLLECT_TASK_BUSINESS);
    }

    /**
     * 根据给定指标查询条件获取agent相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent相关指标数据
     */
    private MetricPanel getAgentMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 根据系统级、进程级、业务级，进行分别处理
         */
        if(isSystemMetric(metricFieldEnum)) {
            return getAgentSystemMetric(metricQueryDTO, metricFieldEnum);
        } else if(isProcessMetric(metricFieldEnum)) {
            return getAgentProcessMetric(metricQueryDTO, metricFieldEnum);
        } else if(isAgentBusinessMetric(metricFieldEnum)) {
            return getAgentBusinessMetric(metricQueryDTO, metricFieldEnum);
        } else {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getAgentMetric||msg={%s, metricCode=%d非法，必须为系统级、进程级、业务级指标}", ErrorCodeEnum.METRICS_QUERY_ERROR.getMessage(), metricFieldEnum.getCode()),
                    ErrorCodeEnum.METRICS_QUERY_ERROR.getCode()
            );
        }
    }

    /**
     * 根据给定指标查询条件获取agent业务级相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent业务级相关指标数据
     */
    private MetricPanel getAgentBusinessMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：not support
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsAgentDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            //TODO：不支持多条线指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsAgentDAO.getSingleChatStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsAgentDAO.getSingleChatNonStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent进程级相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent进程级相关指标数据
     */
    private MetricPanel getAgentProcessMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：not support
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsProcessDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            //TODO：不支持多条线指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsProcessDAO.getSingleChatStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsProcessDAO.getSingleChatNonStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标数据（集）、指标定义对象获取MetricPanel对象
     * @param metricFieldEnum 待查询指标枚举定义
     * @param lableValue lable值
     * @param multiLineChatValue 多条线指标值
     * @param singleLineChatValue 单条线指标值
     * @return 返回根据给定指标查询条件、数据访问层对象获取MetricPanel对象
     */
    private MetricPanel getMetricsPanel(
            MetricFieldEnum metricFieldEnum,
            Object lableValue,
            List<List<MetricPoint>> multiLineChatValue,
            List<MetricPoint> singleLineChatValue
            ) {
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(metricFieldEnum.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(metricFieldEnum.getDisplayUnit().getCode());
        metricPanel.setName(metricFieldEnum.getMetricName());
        metricPanel.setType(metricFieldEnum.getMetricDisplayType().getCode());
        MetricDisplayTypeEnum metricDisplayTypeEnum = metricFieldEnum.getMetricDisplayType();
        if(metricDisplayTypeEnum.equals(MetricDisplayTypeEnum.LABLE)) {
            metricPanel.setLableValue(lableValue);
        } else if(metricDisplayTypeEnum.equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            metricPanel.setMultiLineChatValue(multiLineChatValue);
        } else {// single line
            metricPanel.setSingleLineChatValue(singleLineChatValue);
        }
        return metricPanel;
    }

    /**
     * 根据给定指标查询条件获取agent系统级相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级相关指标数据
     */
    private MetricPanel getAgentSystemMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 根据普通系统级、disk/io相关、net card相关，进行分别处理
         */
        if(metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_DISK)) {//disk相关
            return getAgentSystemDiskMetric(metricQueryDTO, metricFieldEnum);
        } else if(metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_DISK_IO)) {//disk/io相关
            return getAgentSystemDiskIOMetric(metricQueryDTO, metricFieldEnum);
        } else if(metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_NET_CARD)) {//net card相关
            return getAgentSystemNetCardMetric(metricQueryDTO, metricFieldEnum);
        } else if(metricFieldEnum.getMetricType().getParentMetricType().equals(MetricTypeEnum.SYSTEM)) {//普通系统级
            return getAgentSystemNormalMetric(metricQueryDTO, metricFieldEnum);
        } else {
            throw new ServiceException(
                    String.format("class=MetricsServiceImpl||method=getAgentSystemMetric||msg={%s, metricCode=%d非法，必须为系统disk/io级、系统net card级、普通系统指标}", ErrorCodeEnum.METRICS_QUERY_ERROR.getMessage(), metricFieldEnum.getCode()),
                    ErrorCodeEnum.METRICS_QUERY_ERROR.getCode()
            );
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级disk/io相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级disk/io相关指标数据
     */
    private MetricPanel getAgentSystemDiskIOMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：not support
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            //TODO：不支持lable指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n device
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsDiskIOTopPO> metricsDiskIOTopPOList = metricsDiskIODAO.getTopNDiskDevice(params);
            /*
             * 2.）根据 top n disk，挨个获取单条线
             */
            List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
            for (MetricsDiskIOTopPO metricsDiskIOTopPO : metricsDiskIOTopPOList) {
                String device = metricsDiskIOTopPO.getDevice();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("device", device);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskIODAO.getSingleChatStatisticByDevice(params);
                    multiLineChatValue.add(result);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("device", device);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskIODAO.getSingleChatNonStatisticByDevice(params);
                    multiLineChatValue.add(result);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            //TODO：不支持单条线指标
            throw new RuntimeException();
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级普通指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级普通指标数据
     */
    private MetricPanel getAgentSystemNormalMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：not support
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsSystemDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            //TODO：不支持多条线指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsSystemDAO.getSingleChatStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsSystemDAO.getSingleChatNonStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级网卡相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级网卡相关指标数据
     */
    private MetricPanel getAgentSystemNetCardMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：not support
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            //TODO：不支持lable指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n net card
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsNetCardTopPO> MetricsNetCardTopPOList = metricsNetCardDAO.getTopNMacAddress(params);
            /*
             * 2.）根据 top n net card，挨个获取单条线
             */
            List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
            for (MetricsNetCardTopPO metricsNetCardTopPO : MetricsNetCardTopPOList) {
                String macAddress = metricsNetCardTopPO.getMacAddress();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("macAddress", macAddress);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsNetCardDAO.getSingleChatStatisticByMacAddress(params);
                    multiLineChatValue.add(result);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("macAddress", macAddress);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsNetCardDAO.getSingleChatNonStatisticByMacAddress(params);
                    multiLineChatValue.add(result);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            //TODO：不支持单条线指标
            throw new RuntimeException();
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标查询条件获取agent系统级disk相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取agent系统级disk相关指标数据
     */
    private MetricPanel getAgentSystemDiskMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：not support
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            //TODO：不支持lable指标
            throw new RuntimeException();
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n disk
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsDiskTopPO> metricsDiskTopPOList = metricsDiskDAO.getTopNDiskPath(params);
            /*
             * 2.）根据 top n disk，挨个获取单条线
             */
            List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
            for (MetricsDiskTopPO metricsDiskTopPO : metricsDiskTopPOList) {
                String path = metricsDiskTopPO.getPath();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("path", path);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskDAO.getSingleChatStatisticByPath(params);
                    multiLineChatValue.add(result);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("hostName", metricQueryDTO.getHostName());
                    params.put("path", path);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsDiskDAO.getSingleChatNonStatisticByPath(params);
                    multiLineChatValue.add(result);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            //TODO：不支持单条线指标
            throw new RuntimeException();
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定原始字段名与字段排序类型获取排序字段名
      * @param fieldName 原始字段名
     * @param sortMetricType 字段排序类型
     * @return 返回根据给定原始字段名与字段排序类型获取排序字段名
     */
    private String getSortFieldName(String fieldName, Integer sortMetricType) {
        if(sortMetricType == 0) {
          return fieldName;
        } else if(sortMetricType == 1) {
            return fieldName+"Min";
        } else if(sortMetricType == 2) {
            return fieldName+"Max";
        } else if(sortMetricType == 3) {
            return fieldName+"Mean";
        } else if(sortMetricType == 4) {
            return fieldName+"Std";
        } else if(sortMetricType == 5) {
            return fieldName+"55Quantile";
        } else if(sortMetricType == 6) {
            return fieldName+"75Quantile";
        } else if(sortMetricType == 7) {
            return fieldName+"95Quantile";
        } else if(sortMetricType == 8) {
            return fieldName+"99Quantile";
        } else {
            //TODO：throw exception 未知sortMetricType类型
            throw new RuntimeException();
        }
    }

    /**
     * 校验给定指标定义是否为agent业务级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为agent业务级指标 true：是 false：否
     */
    private boolean isAgentBusinessMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType();
        return metricTypeEnum.equals(MetricTypeEnum.AGENT_BUSINESS);
    }

    /**
     * 校验给定指标定义是否为进程级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为进程级指标 true：是 false：否
     */
    private boolean isProcessMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType().getParentMetricType();
        return metricTypeEnum.equals(MetricTypeEnum.PROCESS);
    }

    /**
     * 校验给定指标定义是否为系统级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为系统级指标 true：是 false：否
     */
    private boolean isSystemMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType().getParentMetricType();
        return metricTypeEnum.equals(MetricTypeEnum.SYSTEM);
    }

    /**
     * 根据给定指标查询条件获取日志采集任务相关指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @return 返回根据给定指标查询条件获取日志采集任务相关指标数据
     */
    private MetricPanel getLogCollectTaskMetric(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum) {
        /*
         * 如下几种情况分别处理：
         *  1.lable
         *  2.多根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *  3.单根折线图：
         *      1.）附带统计值指标
         *      2.）不附带统计值指标
         *
         */
        if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.LABLE)) {
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
            params.put("pathId", metricQueryDTO.getPathId());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsLogCollectTaskDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n host
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            Integer sortMetricType;
            if(null == metricQueryDTO.getSortMetricType()) {
                sortMetricType = SORT_METRIC_TYPE_DEFAULT_VALUE;
            } else {
                sortMetricType = metricQueryDTO.getSortMetricType();
            }
            params.put("fieldName", getSortFieldName(metricFieldEnum.getFieldName(), sortMetricType));
            params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
            params.put("pathId", metricQueryDTO.getPathId());
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", DateUtils.getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", TOP_N_DEFAULT_VALUE);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
            params.put("sortType", metricFieldEnum.getSortTypeEnum().getType());
            List<MetricsLogCollectTaskTopPO> metricsLogCollectTaskTopPOList = metricsLogCollectTaskDAO.getTopNByHostName(params);
            /*
             * 2.）根据 top n disk，挨个获取单条线
             */
            List<List<MetricPoint>> multiLineChatValue = new ArrayList<>();
            for (MetricsLogCollectTaskTopPO metricsLogCollectTaskTopPO : metricsLogCollectTaskTopPOList) {
                String hostName = metricsLogCollectTaskTopPO.getHostName();
                if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                    params.put("pathId", metricQueryDTO.getPathId());
                    params.put("hostName", hostName);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatStatistic(params);
                    multiLineChatValue.add(result);
                } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                    params = new HashMap<>();
                    params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                    params.put("fieldName", metricFieldEnum.getFieldName());
                    params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                    params.put("pathId", metricQueryDTO.getPathId());
                    params.put("hostName", hostName);
                    params.put("startTime", metricQueryDTO.getStartTime());
                    params.put("endTime", metricQueryDTO.getEndTime());
                    List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatNonStatistic(params);
                    multiLineChatValue.add(result);
                } else {
                    //TODO：throw exception 未知MetricValueTypeEnum类型
                }
            }
            return getMetricsPanel(metricFieldEnum, null, multiLineChatValue, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.SINGLE_LINE_CHAT)) {
            if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.STATISTICS)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                params.put("pathId", metricQueryDTO.getPathId());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else if(metricFieldEnum.getMetricValueType().equals(MetricValueTypeEnum.CURRENT)) {
                Map<String, Object> params = new HashMap<>();
                params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
                params.put("fieldName", metricFieldEnum.getFieldName());
                params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
                params.put("pathId", metricQueryDTO.getPathId());
                params.put("hostName", metricQueryDTO.getHostName());
                params.put("startTime", metricQueryDTO.getStartTime());
                params.put("endTime", metricQueryDTO.getEndTime());
                List<MetricPoint> result = metricsLogCollectTaskDAO.getSingleChatNonStatistic(params);
                return getMetricsPanel(metricFieldEnum, null, null, result);
            } else {
                //TODO：throw exception 未知MetricValueTypeEnum类型
                throw new RuntimeException();
            }
        } else {
            //TODO：throw exception 未知MetricDisplayTypeEnum类型
            throw new RuntimeException();
        }
    }

    /**
     * 根据给定指标类型构建对应指标树
     * @param metricTypeEnum 指标类型
     * @return 返回根据给定指标类型构建对应指标树
     */
    private MetricNodeVO handleGetMetricsTreeByMetricType(MetricTypeEnum metricTypeEnum) {
        MetricNodeVO metricNodeVO = buildFromMetricTypeEnum(metricTypeEnum);
        LinkedList<Pair<MetricTypeEnum, MetricNodeVO>> queue = new LinkedList<>();
        queue.addLast(new Pair<>(metricTypeEnum, metricNodeVO));
        while(!queue.isEmpty()) {
            Pair<MetricTypeEnum, MetricNodeVO> firstNode = queue.removeFirst();
            List<MetricTypeEnum> subNodes = MetricTypeEnum.fromParentMetricTypeCode(firstNode.getKey().getCode());
            if(CollectionUtils.isNotEmpty(subNodes)) {
                List<MetricNodeVO> children = new ArrayList<>();
                for (MetricTypeEnum item : subNodes) {
                    MetricNodeVO child = buildFromMetricTypeEnum(item);
                    queue.add(new Pair<>(item, child));
                    children.add(child);
                }
                firstNode.getValue().setChildren(children);
            } else {
                List<MetricFieldEnum> metricFieldEnumList = MetricFieldEnum.fromMetricTypeEnum(firstNode.getKey());
                if(CollectionUtils.isNotEmpty(metricFieldEnumList)) {
                    List<MetricNodeVO> children = new ArrayList<>();
                    for (MetricFieldEnum metricFieldEnum : metricFieldEnumList) {
                        MetricNodeVO metric = buildFromMetricFieldEnum(metricFieldEnum);
                        children.add(metric);
                    }
                    firstNode.getValue().setChildren(children);
                }
            }
        }
        return metricNodeVO;
    }

    /**
     * @param metricFieldEnum 指标字段定义
     * @return 将给定指标字段定义转化为MetricNodeVO对象
     */
    private MetricNodeVO buildFromMetricFieldEnum(MetricFieldEnum metricFieldEnum) {
        MetricNodeVO metricNodeVO = new MetricNodeVO();
        metricNodeVO.setMetricName(metricFieldEnum.getMetricName());
        metricNodeVO.setMetricDesc(metricFieldEnum.getDescription());
        metricNodeVO.setCode(metricFieldEnum.getCode());
        metricNodeVO.setChecked(metricFieldEnum.isChecked());
        metricNodeVO.setIsLeafNode(true);
        return metricNodeVO;
    }

    /**
     * @param metricTypeEnum 指标类型定义
     * @return 将给定指标类型定义转化为MetricNodeVO对象
     */
    private MetricNodeVO buildFromMetricTypeEnum(MetricTypeEnum metricTypeEnum) {
        MetricNodeVO metricNodeVO = new MetricNodeVO();
        metricNodeVO.setMetricName(metricTypeEnum.getType());
        metricNodeVO.setMetricDesc(metricTypeEnum.getDescription());
        metricNodeVO.setCode(metricTypeEnum.getCode());
        metricNodeVO.setIsLeafNode(false);
        return metricNodeVO;
    }

}
