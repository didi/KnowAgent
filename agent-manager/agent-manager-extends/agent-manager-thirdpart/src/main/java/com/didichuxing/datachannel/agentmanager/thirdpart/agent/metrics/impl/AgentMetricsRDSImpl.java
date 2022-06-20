package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.metrics.DashBoardStatisticsDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.AggregationCalcFunctionEnum;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPoint;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.*;
import com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics.AgentMetricsDAO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AgentMetricsRDSImpl implements AgentMetricsDAO {

    private static final Logger LOGGER           = LoggerFactory.getLogger(AgentMetricsRDSImpl.class);

    @Autowired
    private ErrorLogMapper errorLogMapper;

    @Autowired
    private MetricsDiskIOPOMapper metricsDiskIODAO;

    @Autowired
    private MetricsAgentPOMapper metricsAgentDAO;

    @Autowired
    private MetricsProcessPOMapper metricsProcessDAO;

    @Autowired
    private MetricsSystemPOMapper metricsSystemDAO;

    @Autowired
    private MetricsDiskPOMapper metricsDiskDAO;

    @Autowired
    private MetricsNetCardPOMapper metricsNetCardDAO;

    @Autowired
    private MetricsLogCollectTaskPOMapper metricsLogCollectTaskDAO;

    @Override
    public void writeMetrics(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            handleWriteMetrics(record.value());
        }
    }

    private void handleWriteMetrics(String value) {
        JSONObject object = JSON.parseObject(value);
        Object taskMetricsObj = object.get("taskMetrics");
        Object agentMetricsObj = object.get("agentMetrics");
        if(taskMetricsObj != null) {
            String taskMetricsStr = taskMetricsObj.toString();
            TaskMetrics taskMetrics = JSON.parseObject(taskMetricsStr, TaskMetrics.class);
            MetricsLogCollectTaskPO logCollectTaskPO = ConvertUtil.obj2Obj(taskMetrics, MetricsLogCollectTaskPO.class);
            metricsLogCollectTaskDAO.insertSelective(logCollectTaskPO);
        } else if(agentMetricsObj != null) {
            String agentMetricsStr = agentMetricsObj.toString();
            AgentMetrics agentMetrics = JSON.parseObject(agentMetricsStr, AgentMetrics.class);
            AgentBusinessMetrics agentBusinessMetrics = agentMetrics.getAgentBusinessMetrics();
            SystemMetrics systemMetrics = agentMetrics.getSystemMetrics();
            ProcessMetrics processMetrics = agentMetrics.getProcessMetrics();
            List<DiskIOMetrics> diskIOMetricsList = agentMetrics.getDiskIOMetricsList();
            List<DiskMetrics> diskMetricsList = agentMetrics.getDiskMetricsList();
            List<NetCardMetrics> netCardMetrics = agentMetrics.getNetCardMetricsList();

            MetricsAgentPO metricsAgentPO = ConvertUtil.obj2Obj(agentBusinessMetrics, MetricsAgentPO.class);
            MetricsSystemPO metricsSystemPO = ConvertUtil.obj2Obj(systemMetrics, MetricsSystemPO.class);
            MetricsProcessPO metricsProcessPO = ConvertUtil.obj2Obj(processMetrics, MetricsProcessPO.class);
            List<MetricsDiskIOPO> metricsDiskIOPOS = ConvertUtil.list2List(diskIOMetricsList, MetricsDiskIOPO.class);
            List<MetricsDiskPO> metricsDiskPOList = ConvertUtil.list2List(diskMetricsList, MetricsDiskPO.class);
            List<MetricsNetCardPO> metricsNetCardPOList = ConvertUtil.list2List(netCardMetrics, MetricsNetCardPO.class);

            for (MetricsDiskIOPO metricsDiskIOPO : metricsDiskIOPOS) {
                metricsDiskIODAO.insertSelective(metricsDiskIOPO);
            }
            for (MetricsDiskPO metricsDiskPO : metricsDiskPOList) {
                metricsDiskDAO.insertSelective(metricsDiskPO);
            }
            for (MetricsNetCardPO metricsNetCardPO : metricsNetCardPOList) {
                metricsNetCardDAO.insertSelective(metricsNetCardPO);
            }

            metricsAgentDAO.insertSelective(metricsAgentPO);
            metricsSystemDAO.insertSelective(metricsSystemPO);
            metricsProcessDAO.insertSelective(metricsProcessPO);

        }
    }

    @Override
    public void writeErrors(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            ErrorLogPO errorLogPO = JSON.parseObject(record.value(), ErrorLogPO.class);
            errorLogMapper.insertSelective(errorLogPO);
        }
    }

}
