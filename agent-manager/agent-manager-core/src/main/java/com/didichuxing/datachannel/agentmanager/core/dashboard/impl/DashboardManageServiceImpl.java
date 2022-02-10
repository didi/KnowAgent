package com.didichuxing.datachannel.agentmanager.core.dashboard.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.DashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.metrics.MetricFieldEnum;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.dashboard.DashboardManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.metrics.MetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Service
public class DashboardManageServiceImpl implements DashboardManageService {

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

        Date current = new Date(System.currentTimeMillis());
        Long startTime = DateUtils.getMinuteUnitTimeStamp(DateUtils.getBeforeSeconds(current, 60).getTime());
        Long endTime = DateUtils.getMinuteUnitTimeStamp(current.getTime());

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
        dashBoardVO.setAgentCpuCoresSpend(getAgentCpuCoresSpend(startTime, endTime));
        dashBoardVO.setAgentMemorySpend(getAgentMemorySpend(startTime, endTime));
        dashBoardVO.setAgentUplinkBytes(getAgentUplinkBytes(startTime, endTime));
        dashBoardVO.setAgentDownLinkBytes(getAgentDownLinkBytes(startTime, endTime));
        dashBoardVO.setAgentSendLogEventsLast1Minute(getAgentSendLogEventsLast1Minute(startTime, endTime));
        startTime = DateUtils.getZeroDate(current).getTime();
        dashBoardVO.setAgentSendBytesDay(getAgentSendBytesDay(startTime, endTime));
        dashBoardVO.setAgentSendLogEventsDay(getAgentSendLogEventsDay(startTime, endTime));

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

        return dashBoardVO;

    }

    private Long getAgentSendLogEventsDay(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime);
    }

    private Long getAgentSendBytesDay(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_BYTES, startTime, endTime);
    }

    private Long getAgentSendLogEventsLast1Minute(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.AGENT_WRITE_COUNT, startTime, endTime);
    }

    private Long getAgentDownLinkBytes(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_NET_WORK_RECEIVE_BYTES_PS, startTime, endTime);
    }

    private Long getAgentUplinkBytes(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_NET_WORK_SEND_BYTES_PS, startTime, endTime);
    }

    private Long getAgentMemorySpend(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_MEMORY_USED, startTime, endTime);
    }

    private Long getAgentCpuCoresSpend(Long startTime, Long endTime) {
        return metricsManageService.getSumMetricAllAgents(MetricFieldEnum.PROCESS_CPU_UTIL, startTime, endTime);
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
