package com.didichuxing.datachannel.agentmanager.core.dashboard.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.version.AgentVersionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard.DashBoardDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsSystemPO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.metrics.MetricPanel;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricDisplayTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricUnitEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.SortTimeFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.version.AgentVersionManageService;
import com.didichuxing.datachannel.agentmanager.core.dashboard.DashboardManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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

    @Autowired
    private AgentVersionManageService agentVersionManageService;

    @Override
    public DashBoardDO build() {

        DashBoardDO dashBoardDO = new DashBoardDO();

        Date current = new Date(System.currentTimeMillis());//当前时间
        Long last1Minute = DateUtils.getMinuteUnitTimeStamp(DateUtils.getBeforeSeconds(current, 60 * 1));//当前时间上一分钟时间戳
        Long currentMinute = DateUtils.getMinuteUnitTimeStamp(current);//当前时间戳
        Long currentDayZeroDate = DateUtils.getZeroDate(current).getTime();//当日凌晨00：00：00 时间戳
        Long last1Hour = DateUtils.getMinuteUnitTimeStamp(DateUtils.getBeforeSeconds(current, 60 * 60));//当前时间上一分钟时间戳

        /*********************** part 1：标量 ***********************/

        dashBoardDO.setServiceNum(serviceManageService.countAll());
        dashBoardDO.setNonRelateAnyHostServiceNum(getNonRelateAnyHostServiceNum());
        dashBoardDO.setHostNum(hostManageService.countAllHost());
        dashBoardDO.setFaultyHostNum(hostManageService.countAllFaultyHost());
        dashBoardDO.setAgentNum(agentManageService.countAll());
        dashBoardDO.setNonRelateAnyLogCollectTaskAgentNum(getNonRelateAnyLogCollectTaskAgentNum());
        dashBoardDO.setAgentVersionNumber(Long.valueOf(agentVersionManageService.list().size()));
        dashBoardDO.setNonRelateAnyAgentAgentVersionNum(getNonRelateAnyAgentAgentVersionNum());
        dashBoardDO.setLogCollectTaskNum(logCollectTaskManageService.countAll());
        dashBoardDO.setNonRelateAnyHostLogCollectTaskNum(getNonRelateAnyHostLogCollectTaskNum());
        dashBoardDO.setLogCollectPathNum(fileLogCollectPathManageService.countAll());
        dashBoardDO.setAllHostsSendAndReceiveBytesPerSecond(getAllHostsSendAndReceiveBytesPerSecond(last1Minute, currentMinute));
        dashBoardDO.setAllHostsSendBytesPerSecond(getAllHostsSendBytesPerSecond(last1Minute, currentMinute));
        dashBoardDO.setAllHostsReceiveBytesPerSecond(getAllHostsReceiveBytesPerSecond(last1Minute, currentMinute));
        Long allAgentsSendBytesPerSecond = getAllAgentsSendBytesPerSecond(last1Minute, currentMinute);
        Long allAgentsReceiveBytesPerSecond = getAllAgentsReceiveBytesPerSecond(last1Minute, currentMinute);
        dashBoardDO.setAllAgentsSendAndReceiveBytesPerSecond(allAgentsSendBytesPerSecond + allAgentsReceiveBytesPerSecond);
        dashBoardDO.setAllAgentsSendBytesPerSecond(allAgentsSendBytesPerSecond);
        dashBoardDO.setAllAgentsReceiveBytesPerSecond(allAgentsReceiveBytesPerSecond);
        dashBoardDO.setAgentCpuCoresSpend(getAgentCpuCoresSpend(last1Minute, currentMinute));
        dashBoardDO.setAgentMemorySpend(getAgentMemorySpend(last1Minute, currentMinute));
        dashBoardDO.setAgentSendLogEventsLast1Minute(getAgentSendLogEventsLast1Minute(last1Minute, currentMinute));
        dashBoardDO.setAgentSendBytesLast1Minute(getAgentSendBytesLast1Minute(last1Minute, currentMinute));
        dashBoardDO.setAgentSendBytesDay(getAgentSendBytesDay(currentDayZeroDate, currentMinute));
        dashBoardDO.setAgentSendLogEventsDay(getAgentSendLogEventsDay(currentDayZeroDate, currentMinute));

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
        dashBoardDO.setRedLogCollectTaskNameIdPairList(redLogCollectTaskNameIdPairList);
        dashBoardDO.setYellowLogCollectTaskNameIdPairList(yellowLogCollectTaskNameIdPairList);
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
        dashBoardDO.setRedAgentHostNameIdPairList(redAgentHostNameIdPairList);
        dashBoardDO.setYellowAgentHostNameIdPairList(yellowAgentHostNameIdPairList);
        dashBoardDO.setOsTypeCountMap(getOsTypeCountMap());
        dashBoardDO.setAgentVersionCountMap(getAgentVersionCountMap());

        /*********************** part 3：时序 图 指标 ***********************/

        /*************************** agent 视角 ***************************/

        dashBoardDO.setNtpGapTop5Agents(getNtpGapTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setCpuUsageTop5Agents((getCpuUsageTop5Agents(last1Hour, last1Minute)));
        dashBoardDO.setMemoryUsedTop5Agents(getMemoryUsedTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setBandWidthUsedTop5Agents(getBandWidthUsedTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setBandWidthUsageTop5Agents(getBandWidthUsageTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setFullGcTimesDayTop5Agents(getFullGcTimesDayTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setFdUsedTop5Agents(getFdUsedTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setUplinkBytesTop5Agents(getUplinkBytesTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setSendLogEventsLast1MinuteTop5Agents(getSendLogEventsLast1MinuteTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setSendBytesLast1MinuteTop5Agents(getSendBytesLast1MinuteTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setSendBytesDayTop5Agents(getSendBytesDayTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setSendLogEventsDayTop5Agents(getSendLogEventsDayTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setRunningLogCollectTasksTop5Agents(getRunningLogCollectTasksTop5Agents(last1Hour, last1Minute));
        dashBoardDO.setRunningLogCollectPathsTop5Agents(getRunningLogCollectPathsTop5Agents(last1Hour, last1Minute));

        /*************************** logCollectTask 视角 ***************************/
        dashBoardDO.setLogTimeDelayTop5LogCollectTasks(getLogTimeDelayTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardDO.setLimitTimeTop5LogCollectTasks(getLimitTimeTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardDO.setSendBytesLast1MinuteTop5LogCollectTasks(getSendBytesTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardDO.setSendLogEventsLast1MinuteTop5LogCollectTasks(getSendLogEventsLast1MinuteTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardDO.setSendBytesDayTop5LogCollectTasks(getSendBytesDayTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardDO.setSendLogEventsDayTop5LogCollectTasks(getSendLogEventsDayTop5LogCollectTasks(last1Hour, last1Minute));
        dashBoardDO.setRelateHostsTop5LogCollectTasks(getRelateHostsTop5LogCollectTasks());
        dashBoardDO.setRelateAgentsTop5LogCollectTasks(getRelateAgentsTop5LogCollectTasks());

        /*************************** service 视角 ***************************/
        dashBoardDO.setSendBytesLast1MinuteTop5Applications(getSendBytesLast1MinuteTop5Services(last1Hour, last1Minute));
        dashBoardDO.setSendLogEventsLast1MinuteTop5Applications(getSendLogEventsLast1MinuteTop5Services(last1Hour, last1Minute));
        dashBoardDO.setSendBytesDayTop5Applications(getSendBytesDayTop5Services(last1Hour, last1Minute));
        dashBoardDO.setSendLogEventsDayTop5Applications(getSendLogEventsDayTop5Services(last1Hour, last1Minute));
        dashBoardDO.setRelateHostsTop5Applications(getRelateHostsTop5Applications());
        dashBoardDO.setRelateAgentsTop5Applications(getRelateAgentsTop5Applications());
        dashBoardDO.setRelateLogCollectTaskTop5Applications(getRelateLogCollectTaskTop5Applications());

        return dashBoardDO;

    }

    private MetricPanel getRelateLogCollectTaskTop5Applications() {
        List<Pair<Object, Object>> result = new ArrayList<>();
        List<ServiceDO> serviceDOList = serviceManageService.list();
        for(ServiceDO serviceDO : serviceDOList) {
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getLogCollectTaskListByServiceId(serviceDO.getId());
            result.add(new Pair(serviceDO.getServicename(), logCollectTaskDOList.size()));
        }
        Comparator comparator = new Comparator<Pair<Object, Object>>() {
            @Override
            public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                return Integer.valueOf(o2.getValue().toString()) - Integer.valueOf(o1.getValue().toString());
            }
        };
        Collections.sort(result, comparator);
        if(result.size() > 5) {
            result = result.subList(0, 5);
        }
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setName("当前关联采集任务数 top5 应用（单位：个）");
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(result);
        return metricPanel;
    }

    private MetricPanel getRelateAgentsTop5Applications() {
        List<Pair<Object, Object>> result = new ArrayList<>();
        List<ServiceDO> serviceDOList = serviceManageService.list();
        for(ServiceDO serviceDO : serviceDOList) {
            List<HostDO> hostDOList = hostManageService.getHostsByServiceId(serviceDO.getId());
            Integer agentCount = 0;
            for (HostDO hostDO : hostDOList) {
                AgentDO agentDO = agentManageService.getAgentByHostName(hostDO.getHostName());
                if(null != agentDO) {
                    agentCount++;
                }
            }
            result.add(new Pair(serviceDO.getServicename(), agentCount));
        }
        Comparator comparator = new Comparator<Pair<Object, Object>>() {
            @Override
            public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                return Integer.valueOf(o2.getValue().toString()) - Integer.valueOf(o1.getValue().toString());
            }
        };
        Collections.sort(result, comparator);
        if(result.size() > 5) {
            result = result.subList(0, 5);
        }
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setName("当前关联 Agent 数 top5 应用（单位：个）");
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(result);
        return metricPanel;
    }

    private MetricPanel getRelateHostsTop5Applications() {
        List<Pair<Object, Object>> result = new ArrayList<>();
        List<ServiceDO> serviceDOList = serviceManageService.list();
        for(ServiceDO serviceDO : serviceDOList) {
            List<HostDO> hostDOList = hostManageService.getHostsByServiceId(serviceDO.getId());
            result.add(new Pair(serviceDO.getServicename(), hostDOList.size()));
        }
        Comparator comparator = new Comparator<Pair<Object, Object>>() {
            @Override
            public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                return Integer.valueOf(o2.getValue().toString()) - Integer.valueOf(o1.getValue().toString());
            }
        };
        Collections.sort(result, comparator);
        if(result.size() > 5) {
            result = result.subList(0, 5);
        }
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setName("当前关联主机数 top5 应用（单位：个）");
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(result);
        return metricPanel;
    }

    private MetricPanel getRelateAgentsTop5LogCollectTasks() {
        List<Pair<Object, Object>> result = new ArrayList<>();
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getAll();
        for(LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskDO.getId());
            Integer agentCount = 0;
            for (HostDO hostDO : hostDOList) {
                AgentDO agentDO = agentManageService.getAgentByHostName(hostDO.getHostName());
                if(null != agentDO) {
                    agentCount++;
                }
            }
            result.add(new Pair(logCollectTaskDO, agentCount));
        }
        Comparator comparator = new Comparator<Pair<Object, Object>>() {
            @Override
            public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                return Integer.valueOf(o2.getValue().toString()) - Integer.valueOf(o1.getValue().toString());
            }
        };
        Collections.sort(result, comparator);
        if(result.size() > 5) {
            result = result.subList(0, 5);
        }
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setName("当前关联 Agent 数 top5 采集任务（单位：个）");
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(result);
        return metricPanel;
    }

    private MetricPanel getRelateHostsTop5LogCollectTasks() {
        List<Pair<Object, Object>> result = new ArrayList<>();
        List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getAll();
        for(LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
            List<HostDO> hostDOList = hostManageService.getHostListByLogCollectTaskId(logCollectTaskDO.getId());
            result.add(new Pair(logCollectTaskDO, hostDOList.size()));
        }
        Comparator comparator = new Comparator<Pair<Object, Object>>() {
            @Override
            public int compare(Pair<Object, Object> o1, Pair<Object, Object> o2) {
                return Integer.valueOf(o2.getValue().toString()) - Integer.valueOf(o1.getValue().toString());
            }
        };
        Collections.sort(result, comparator);
        if(result.size() > 5) {
            result = result.subList(0, 5);
        }
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.NONE.getCode());
        metricPanel.setName("当前关联主机数 top5 采集任务（单位：个）");
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(result);
        return metricPanel;
    }

    private Map<String, Long> getAgentVersionCountMap() {
        Map<Long, Long> agentVersionIdCountMap = new HashMap<>();
        List<AgentDO> agentDOList = agentManageService.list();
        for (AgentDO agentDO : agentDOList) {
            Long agentVersionId = agentDO.getAgentVersionId();
            Long count = agentVersionIdCountMap.get(agentVersionId);
            if(null == count) {
                agentVersionIdCountMap.put(agentVersionId, 0L);
            } else {
                agentVersionIdCountMap.put(agentVersionId, count + 1L);
            }
        }
        Map<String, Long> agentVersionCountMap = new HashMap<>();
        for (Map.Entry<Long, Long> entry : agentVersionIdCountMap.entrySet()) {
            Long agentVersionId = entry.getKey();
            Long count = entry.getValue();
            AgentVersionDO agentVersionDO = agentVersionManageService.getById(agentVersionId);
            if(null != agentVersionDO) {
                String agentVersion = agentVersionDO.getVersion();
                agentVersionCountMap.put(agentVersion, count);
            }
        }
        return agentVersionCountMap;
    }

    private Map<String, Long> getOsTypeCountMap() {
        Map<String, Long> osTypeCountMap = new HashMap<>();
        List<HostDO> hostDOList = hostManageService.list();
        for (HostDO hostDO : hostDOList) {
            String hostName = hostDO.getHostName();
            MetricsSystemPO metricsSystemPO = metricsManageService.getLastSystemMetric(hostName);
            if(null != metricsSystemPO) {
                String osType = String.format("%s%s", metricsSystemPO.getOstype(), metricsSystemPO.getOsversion());
                Long count = osTypeCountMap.get(osType);
                if(null == count) {
                    osTypeCountMap.put(osType, 0L);
                } else {
                    osTypeCountMap.put(osType, count + 1L);
                }
            }
        }
        return osTypeCountMap;
    }

    private Long getAllAgentsSendBytesPerSecond(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(
                        MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS,
                        startTime,
                        endTime,
                        MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getFieldName()+"Max"
                );
    }

    private Long getAllAgentsReceiveBytesPerSecond(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(
                                MetricFieldEnum.PROCESS_NET_WORK_RECEIVE_BYTES_PS,
                                startTime,
                                endTime,
                                MetricFieldEnum.PROCESS_NET_WORK_RECEIVE_BYTES_PS.getFieldName()+"Max"
                        );
    }

    private Long getAllHostsReceiveBytesPerSecond(Long startTime, Long endTime) {
        return
                metricsManageService.getSumMetricAllAgents(
                        MetricFieldEnum.SYSTEM_NET_WORK_RECEIVE_BYTES_PS,
                        startTime,
                        endTime,
                        MetricFieldEnum.SYSTEM_NET_WORK_RECEIVE_BYTES_PS.getFieldName()+"Max"
                );
    }

    private Long getAllHostsSendBytesPerSecond(Long startTime, Long endTime) {
        return
                metricsManageService.getSumMetricAllAgents(
                        MetricFieldEnum.SYSTEM_NET_WORK_SEND_BYTES_PS,
                        startTime,
                        endTime,
                        MetricFieldEnum.SYSTEM_NET_WORK_SEND_BYTES_PS.getFieldName()+"Max"
                );
    }

    private Long getAllHostsSendAndReceiveBytesPerSecond(Long startTime, Long endTime) {
        return
                metricsManageService.getSumMetricAllAgents(
                        MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS,
                        startTime,
                        endTime,
                        MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getFieldName()+"Max"
                );
    }

    private Long getNonRelateAnyAgentAgentVersionNum() {
        List<AgentVersionDO> agentVersionDOList = agentVersionManageService.list();
        Map<Long, Long> agentVersionId2RelateCountMap = new HashMap<>();
        for (AgentVersionDO agentVersionDO : agentVersionDOList) {
            agentVersionId2RelateCountMap.put(agentVersionDO.getId(), 0L);
        }
        List<AgentDO> agentDOList = agentManageService.list();
        for (AgentDO agentDO : agentDOList) {
            agentVersionId2RelateCountMap.put(agentDO.getAgentVersionId(), agentVersionId2RelateCountMap.get(agentDO.getAgentVersionId()) + 1);
        }
        Long nonRelateAnyAgentAgentVersionNum = 0L;
        for (Long value : agentVersionId2RelateCountMap.values()) {
            if(value.equals(0l)) {
                nonRelateAnyAgentAgentVersionNum++;
            }
        }
        return nonRelateAnyAgentAgentVersionNum;
    }

    private MetricPanel getSendBytesLast1MinuteTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private Long getAgentSendBytesLast1Minute(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, null);
    }

    private MetricPanel getSendLogEventsDayTop5Services(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendBytesDayTop5Services(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsLast1MinuteTop5Services(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendBytesLast1MinuteTop5Services(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),true
        );
        MetricPanel metricPanel = new MetricPanel();
            metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsDayTop5LogCollectTasks(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendBytesDayTop5LogCollectTasks(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.G_BYTE.getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsLast1MinuteTop5LogCollectTasks(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendBytesTop5LogCollectTasks(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_SEND_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getLimitTimeTop5LogCollectTasks(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_LIMIT_TIME.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getLogTimeDelayTop5LogCollectTasks(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.LOG_COLLECT_TASK_MAX_BUSINESS_TIMESTAMP_DELAY.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getRunningLogCollectPathsTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_RUNNING_COLLECT_PATH_NUM.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getRunningLogCollectTasksTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_RUNNING_COLLECT_TASK_NUM.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsDayTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendBytesDayTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_BYTES.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricUnitEnum.G_BYTE.getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_BYTES.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getSendLogEventsLast1MinuteTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.AGENT_WRITE_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.AGENT_WRITE_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getUplinkBytesTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getFdUsedTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.JVM_PROCESS_OPEN_FD_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getFullGcTimesDayTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_DAY.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.JVM_PROCESS_FULL_GC_COUNT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getBandWidthUsageTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.SYSTEM_NET_WORK_BAND_WIDTH_USED_PERCENT.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getBandWidthUsedTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.SYSTEM_NET_WORK_SEND_AND_RECEIVE_BYTES_PS.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getMemoryUsedTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.PROCESS_MEMORY_USED, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.PROCESS_MEMORY_USED.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.PROCESS_MEMORY_USED.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.PROCESS_MEMORY_USED.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getCpuUsageTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> value = metricsManageService.getTopNByMetric(
                MetricFieldEnum.PROCESS_CPU_UTIL, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.PROCESS_CPU_UTIL.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.PROCESS_CPU_UTIL.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.PROCESS_CPU_UTIL.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(value);
        return metricPanel;
    }

    private MetricPanel getNtpGapTop5Agents(Long startTime, Long endTime) {
        List<Pair<Object, Object>> result = metricsManageService.getTopNByMetric(
                MetricFieldEnum.SYSTEM_NTP_OFFSET, startTime, endTime, SortTimeFieldEnum.HEARTBEAT_TIME_MINUTE.getFieldName(),false
        );
        MetricPanel metricPanel = new MetricPanel();
        metricPanel.setBaseUnit(MetricFieldEnum.SYSTEM_NTP_OFFSET.getBaseUnit().getCode());
        metricPanel.setDisplayUnit(MetricFieldEnum.SYSTEM_NTP_OFFSET.getDisplayUnit().getCode());
        metricPanel.setName(MetricFieldEnum.SYSTEM_NTP_OFFSET.getMetricName());
        metricPanel.setType(MetricDisplayTypeEnum.HISTOGRAM_CHAT.getCode());
        metricPanel.setHistogramChatValue(result);
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
