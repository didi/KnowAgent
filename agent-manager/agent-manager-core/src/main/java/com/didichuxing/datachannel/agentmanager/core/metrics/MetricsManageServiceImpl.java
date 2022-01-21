package com.didichuxing.datachannel.agentmanager.core.metrics;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.metrics.BusinessMetricsQueryDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsDiskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsLogCollectTaskTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardTopPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricNodeVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricDisplayTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricValueTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
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
     * 根据给定指标查询条件获取lable型指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @param dao 数据访问层对象
     * @param params 查询参数集
     * @return 返回根据给定指标查询条件获取lable型指标数据
     */
    private Object getLable(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum, Object dao, Map<Object,Object> params) {
        return null;
    }

    /**
     * 根据给定指标查询条件获取单折线型指标数据
     * @param metricQueryDTO 指标查询条件
     * @param metricFieldEnum 待查询指标枚举定义
     * @param dao 数据访问层对象
     * @param params 查询参数集
     * @return 返回根据给定指标查询条件获取单折线型指标数据
     */
    private List<MetricPoint> getSingleLineChat(BusinessMetricsQueryDTO metricQueryDTO, MetricFieldEnum metricFieldEnum, Object dao, Map<Object,Object> params) {

        //TODO：

        return null;

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
        if(
                metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_DISK_IO) ||
                        metricFieldEnum.getMetricType().equals(MetricTypeEnum.SYSTEM_DISK)
        ) {//disk/io相关
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
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsNetCardDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n net card
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", 0);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
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
            Map<String, Object> params = new HashMap<>();
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            params.put("startTime", metricQueryDTO.getStartTime());
            params.put("endTime", metricQueryDTO.getEndTime());
            Object value = metricsDiskDAO.getLast(params);
            return getMetricsPanel(metricFieldEnum, value, null, null);
        } else if(metricFieldEnum.getMetricDisplayType().equals(MetricDisplayTypeEnum.MULTI_LINE_CHAT)) {
            /*
             * 1.）获取 top n disk
             */
            Map<String, Object> params = new HashMap<>();
            params.put("function", metricFieldEnum.getAggregationCalcFunction().getValue());
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("hostName", metricQueryDTO.getHostName());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", 0);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
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
     * 根据给定时间戳，将其转化为分钟为单位时间戳
     * @param timestamp 毫秒时间戳
     * @return 返回根据给定时间戳，将其转化为分钟为单位时间戳
     */
    private Long getMinuteUnitTimeStamp(Long timestamp) {
        return Math.round(timestamp / 60000.00d) * 60000L;
    }

    /**
     * 校验给定指标定义是否为agent业务级指标
     * @param metricFieldEnum 指标定义枚举对象
     * @return 校验给定指标定义是否为agent业务级指标 true：是 false：否
     */
    private boolean isAgentBusinessMetric(MetricFieldEnum metricFieldEnum) {
        MetricTypeEnum metricTypeEnum = metricFieldEnum.getMetricType().getParentMetricType();
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
            params.put("fieldName", metricFieldEnum.getFieldName());
            params.put("logCollectTaskId", metricQueryDTO.getLogCollectTaskId());
            params.put("pathId", metricQueryDTO.getPathId());
            if(null == metricQueryDTO.getSortTime() || metricQueryDTO.getSortTime().equals(0L)) {//排序时间点未设置值，将采用时间范围最后时间
                params.put("sortTime", getMinuteUnitTimeStamp(metricQueryDTO.getEndTime()));
            } else {
                params.put("sortTime", metricQueryDTO.getSortTime());
            }
            if(null == metricQueryDTO.getTopN()) {
                params.put("topN", 0);
            } else {
                params.put("topN", metricQueryDTO.getTopN());
            }
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
        return metricNodeVO;
    }

}
