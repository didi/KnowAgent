package com.didichuxing.datachannel.agentmanager.core.dashboard.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.DashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPointLine;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricDisplayTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.SortTimeFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.dashboard.DashboardManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.host.impl.HostManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Service
public class DashboardManageServiceImpl implements DashboardManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardManageServiceImpl.class);

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private FileLogCollectPathManageService fileLogCollectPathManageService;

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private AgentManageService agentManageService;

    @Autowired
    private MetricsManageService metricsManageService;

    @Override
    public DashBoardVO build() {

        DashBoardVO dashBoardVO = new DashBoardVO();

        Date current = new Date(System.currentTimeMillis());//当前时间
        Long last1Minute = DateUtils.getMinuteUnitTimeStamp(DateUtils.getBeforeSeconds(current, 60 * 1));//当前时间上一分钟时间戳
        Long currentMinute = DateUtils.getMinuteUnitTimeStamp(current);//当前时间戳
        Long currentDayZeroDate = DateUtils.getZeroDate(current).getTime();//当日凌晨00：00：00 时间戳
        Long last1Hour = DateUtils.getMinuteUnitTimeStamp(DateUtils.getBeforeSeconds(current, 60 * 60));//当前时间上一分钟时间戳

        /*********************** part 1：标量 ***********************/
        dashBoardVO.setHostNum(hostManageService.countAllHost());
        dashBoardVO.setContainerNum(hostManageService.countAllContainer());
        dashBoardVO.setAgentNum(agentManageService.countAll());
        dashBoardVO.setNonRelateAnyLogCollectTaskAgentNum(getNonRelateAnyLogCollectTaskAgentNum());
        dashBoardVO.setServiceNum(serviceManageService.countAll());
        dashBoardVO.setNonRelateAnyHostServiceNum(getNonRelateAnyHostServiceNum());
        dashBoardVO.setLogCollectTaskNum(logCollectTaskManageService.countAll());
        dashBoardVO.setNonRelateAnyHostLogCollectTaskNum(getNonRelateAnyHostLogCollectTaskNum());
        dashBoardVO.setLogCollectPathNum(fileLogCollectPathManageService.countAll());
        dashBoardVO.setAgentCpuCoresSpend(getAgentCpuCoresSpend(last1Minute, currentMinute));
        dashBoardVO.setAgentMemorySpend(getAgentMemorySpend(last1Minute, currentMinute));
        dashBoardVO.setAgentUplinkBytes(getAgentUplinkBytes(last1Minute, currentMinute));
        dashBoardVO.setAgentDownLinkBytes(getAgentDownLinkBytes(last1Minute, currentMinute));
        dashBoardVO.setAgentSendBytesLast1Minute(getAgentSendBytesLast1Minute(last1Minute, currentMinute));
        dashBoardVO.setAgentSendLogEventsLast1Minute(getAgentSendLogEventsLast1Minute(last1Minute, currentMinute));
        dashBoardVO.setAgentSendBytesDay(getAgentSendBytesDay(currentDayZeroDate, currentMinute));
        dashBoardVO.setAgentSendLogEventsDay(getAgentSendLogEventsDay(currentDayZeroDate, currentMinute));

        /*********************** part 2：占比 饼图 ***********************/
        List<LogCollectTaskDO> redLogCollectTaskDOList = logCollectTaskManageService.getByHealthLevel(LogCollectTaskHealthLevelEnum.RED.getCode());
        List<LogCollectTaskDO> yellowLogCollectTaskDOList = logCollectTaskManageService.getByHealthLevel(LogCollectTaskHealthLevelEnum.YELLOW.getCode());
        List<Pair<String, Long>> redLogCollectTaskNameIdPairList = new ArrayList<>(redLogCollectTaskDOList.size());
        for (LogCollectTaskDO logCollectTaskDO : redLogCollectTaskDOList) {
            redLogCollectTaskNameIdPairList.add(new Pair<>(logCollectTaskDO.getLogCollectTaskName(), logCollectTaskDO.getId()));
        }
        List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList = new ArrayList<>(yellowLogCollectTaskDOList.size());
        for (LogCollectTaskDO logCollectTaskDO : yellowLogCollectTaskDOList) {
            yellowLogCollectTaskNameIdPairList.add(new Pair<>(logCollectTaskDO.getLogCollectTaskName(), logCollectTaskDO.getId()));
        }
        dashBoardVO.setRedLogCollectTaskNameIdPairList(redLogCollectTaskNameIdPairList);
        dashBoardVO.setYellowLogCollectTaskNameIdPairList(yellowLogCollectTaskNameIdPairList);
        List<AgentDO> redAgentDOList = agentManageService.getByHealthLevel(AgentHealthLevelEnum.RED.getCode());
        List<AgentDO> yellowAgentDOList = agentManageService.getByHealthLevel(AgentHealthLevelEnum.YELLOW.getCode());
        List<Pair<String, Long>> redAgentHostNameIdPairList = new ArrayList<>(redAgentDOList.size());
        for (AgentDO agentDO : redAgentDOList) {
            redAgentHostNameIdPairList.add(new Pair<>(agentDO.getHostName(), agentDO.getId()));
        }
        List<Pair<String, Long>> yellowAgentHostNameIdPairList = new ArrayList<>(yellowAgentDOList.size());
        for (AgentDO agentDO : yellowAgentDOList) {
            yellowAgentHostNameIdPairList.add(new Pair<>(agentDO.getHostName(), agentDO.getId()));
        }
        dashBoardVO.setRedAgentHostNameIdPairList(redAgentHostNameIdPairList);
        dashBoardVO.setYellowAgentHostNameIdPairList(yellowAgentHostNameIdPairList);

        /*********************** part 3：时序 图 指标 ***********************/

        /*************************** agent 视角 ***************************/

        dashBoardVO.setNtpGapTop5Agents(getNtpGapTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setCpuUsageTop5Agents((getCpuUsageTop5Agents(last1Hour, last1Minute)));
        dashBoardVO.setMemoryUsedTop5Agents(getMemoryUsedTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setBandWidthUsedTop5Agents(getBandWidthUsedTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setBandWidthUsageTop5Agents(getBandWidthUsageTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setFullGcTimesDayTop5Agents(getFullGcTimesDayTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setFdUsedTop5Agents(getFdUsedTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setUplinkBytesTop5Agents(getUplinkBytesTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setSendLogEventsLast1MinuteTop5Agents(getSendLogEventsLast1MinuteTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setSendBytesLast1MinuteTop5Agents(getSendBytesLast1MinuteTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setSendBytesDayTop5Agents(getSendBytesDayTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setSendLogEventsDayTop5Agents(getSendLogEventsDayTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setRunningLogCollectTasksTop5Agents(getRunningLogCollectTasksTop5Agents(last1Hour, last1Minute));
        dashBoardVO.setRunningLogCollectPathsTop5Agents(getRunningLogCollectPathsTop5Agents(last1Hour, last1Minute));

        /*************************** logCollectTask 视角 ***************************/
        dashBoardVO.setLogTimeDelayTop5LogCollectTasks(getLogTimeDelayTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardVO.setLimitTimeTop5LogCollectTasks(getLimitTimeTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardVO.setSendBytesLast1MinuteTop5LogCollectTasks(getSendBytesTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardVO.setSendLogEventsLast1MinuteTop5LogCollectTasks(getSendLogEventsLast1MinuteTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardVO.setSendBytesDayTop5LogCollectTasks(getSendBytesDayTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardVO.setSendLogEventsDayTop5LogCollectTasks(getSendLogEventsDayTop5LogCollectTasks(last1Hour, last1Minute));

        /*************************** service 视角 ***************************/
        dashBoardVO.setSendBytesLast1MinuteTop5Services(getSendBytesLast1MinuteTop5Services(last1Hour, last1Minute));
        dashBoardVO.setSendLogEventsLast1MinuteTop5Services(getSendLogEventsLast1MinuteTop5Services(last1Hour, last1Minute));
        dashBoardVO.setSendBytesDayTop5Services(getSendBytesDayTop5Services(last1Hour, last1Minute));
        dashBoardVO.setSendLogEventsDayTop5Services(getSendLogEventsDayTop5Services(last1Hour, last1Minute));

        return dashBoardVO;

    }

    private MetricPanel getSendBytesLast1MinuteTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private Long getAgentSendBytesLast1Minute(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, null);
    }

    private MetricPanel getSendLogEventsDayTop5Services(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendBytesDayTop5Services(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsLast1MinuteTop5Services(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendBytesLast1MinuteTop5Services(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
            metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsDayTop5LogCollectTasks(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendBytesDayTop5LogCollectTasks(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsLast1MinuteTop5LogCollectTasks(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendBytesTop5LogCollectTasks(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getLimitTimeTop5LogCollectTasks(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getLogTimeDelayTop5LogCollectTasks(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getRunningLogCollectPathsTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getRunningLogCollectTasksTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsDayTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendBytesDayTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsLast1MinuteTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getUplinkBytesTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getFdUsedTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getFullGcTimesDayTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getBandWidthUsageTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getBandWidthUsedTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getMemoryUsedTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.PROCESS_MEMORY_USED, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.PROCESS_MEMORY_USED.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.PROCESS_MEMORY_USED.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.PROCESS_MEMORY_USED.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getCpuUsageTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.PROCESS_CPU_UTIL, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.PROCESS_CPU_UTIL.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.PROCESS_CPU_UTIL.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.PROCESS_CPU_UTIL.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private MetricPanel getNtpGapTop5Agents(Long startTime, Long endTime) {
        List<MetricPointLine> multiLineChatValue = metricsManageService.getTopNByMetric(
                MetricFieldEnum.SYSTEM_NTP_OFFSET, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.SYSTEM_NTP_OFFSET.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.SYSTEM_NTP_OFFSET.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.SYSTEM_NTP_OFFSET.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.MULTI_LINE_CHAT.getCode());
        metricPanel.setMultiLineChatValue(multiLineChatValue);
        return metricPanel;
    }

    private Long getAgentSendLogEventsDay(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime, null);
    }

    private Long getAgentSendBytesDay(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, null);
    }

    private Long getAgentSendLogEventsLast1Minute(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime, null);
    }

    private Long getAgentDownLinkBytes(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_NET_WORK_RECEIVE_BYTES_PS, startTime, endTime, MetricFieldEnum.PROCESS_NET_WORK_RECEIVE_BYTES_PS.getFieldName()+"Max");
    }

    private Long getAgentUplinkBytes(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS, startTime, endTime, MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getFieldName()+"Max");
    }

    private Long getAgentMemorySpend(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_MEMORY_USED, startTime, endTime, null);
    }

    private Double getAgentCpuCoresSpend(Long startTime, Long endTime) {
        return MathUtil.divideWith2Digit(
                        metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_CPU_UTIL, startTime, endTime, null), 100l
        );
    }

    private Long getNonRelateAnyHostLogCollectTaskNum() {
        List<Long> logCollectTaskIdList = logCollectTaskManageService.getAllIds();
        Long nonRelateAnyHostLogCollectTaskNum = 0L;
        for (Long logCollectTaskId : logCollectTaskIdList) {
            if(logCollectTaskManageService.checkNotRelateAnyHost(logCollectTaskId)) {
                nonRelateAnyHostLogCollectTaskNum++;
            }
        }
        return nonRelateAnyHostLogCollectTaskNum;
    }

    private Long getNonRelateAnyHostServiceNum() {
        Long nonRelateAnyHostServiceNum = 0L;
        List<ServiceDO> serviceDOList = serviceManageService.list();
        for (ServiceDO serviceDO : serviceDOList) {
            if(CollectionUtils.isEmpty(hostManageService.getHostsByServiceId(serviceDO.getId()))) {
                nonRelateAnyHostServiceNum++;
            }
        }
        return nonRelateAnyHostServiceNum;
    }

    private Long getNonRelateAnyLogCollectTaskAgentNum() {
        Long nonRelateAnyLogCollectTaskAgentNum = 0L;
        List<String> agentHostNameList = agentManageService.getAllHostNames();
        for (String hostName : agentHostNameList) {
            if(CollectionUtils.isEmpty(logCollectTaskManageService.getLogCollectTaskListByAgentHostName(hostName))) {
                nonRelateAnyLogCollectTaskAgentNum++;
            }
        }
        return nonRelateAnyLogCollectTaskAgentNum;
    }

}
