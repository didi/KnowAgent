package com.didichuxing.datachannel.agentmanager.rest.api.v1.normal;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.agent.AgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.dashboard.DashboardRequestDTO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.DashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.constant.ApiPrefix;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.dashboard.DashboardMetricEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.agent.manage.AgentManageService;
import com.didichuxing.datachannel.agentmanager.core.agent.metrics.AgentMetricsManageService;
import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "Normal-Dashboard 相关接口")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX + "dashboard")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

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
    private AgentMetricsManageService agentMetricsManageService;

    @ApiOperation(value = "获取dashboard全量指标", notes = "")
    @RequestMapping(value = "/{startTime}/{endTime}", method = RequestMethod.GET)
    @ResponseBody
    public Result<DashBoardVO> dashboard(@PathVariable Long startTime, @PathVariable Long endTime) {

        if (null == startTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参startTime不可为空");
        }
        if (null == endTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参endTime不可为空");
        }

        DashBoardVO dashBoardVO = new DashBoardVO();

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
//        dashBoardVO.setAgentCpuCoresSpend();














//        dashBoardVO.setCollectBytesDay(logCollectTaskManageService.getCollectBytesToday());
//        dashBoardVO.setCurrentCollectBytes(logCollectTaskManageService.getCurrentCollectBytes());
//        dashBoardVO.setCollectLogEventsDay(logCollectTaskManageService.getCollectCountToday());
//        dashBoardVO.setCurrentCollectLogEvents(logCollectTaskManageService.getCurrentCollectCount());

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

//        dashBoardVO.setLogCollectTaskListCollectBytesTop5(agentMetricsManageService.getLogCollectTaskListCollectBytesLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setLogCollectTaskListCollectCountTop5(agentMetricsManageService.getLogCollectTaskListCollectCountLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setLogCollectTaskListRelateHostsTop5(logCollectTaskManageService.getTop5HostCount(startTime, endTime));
//        dashBoardVO.setLogCollectTaskListRelateAgentsTop5(logCollectTaskManageService.getTop5AgentCount(startTime, endTime));
//
//        dashBoardVO.setAgentListCollectBytesTop5(agentMetricsManageService.getAgentListCollectBytesLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListCollectCountTop5(agentMetricsManageService.getAgentListCollectCountLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListCpuUsageTop5(agentMetricsManageService.getAgentListCpuUsageLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListFdUsedTop5(agentMetricsManageService.getAgentListFdUsedLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListMemoryUsageTop5(agentMetricsManageService.getAgentListMemoryUsedLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListFullGcCountTop5(agentMetricsManageService.getAgentListFullGcCountLastest1MinTop5(startTime, endTime));
//        dashBoardVO.setAgentListRelateLogCollectTasksTop5(agentManageService.getTop5LogCollectTaskCount(startTime, endTime));

        return Result.buildSucc(dashBoardVO);

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

    @ApiOperation(value = "根据给定指标code集获取对应指标数据", notes = "")
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Result<DashBoardVO> getMetrcisByCodes(@RequestBody DashboardRequestDTO dashboardRequestDTO) {
        Long startTime = dashboardRequestDTO.getStartTime();
        Long endTime = dashboardRequestDTO.getEndTime();
        if (null == startTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参startTime不可为空");
        }
        if (null == endTime) {
            return Result.build(ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参endTime不可为空");
        }
        DashBoardVO dashBoardVO = new DashBoardVO();
        if(CollectionUtils.isNotEmpty(dashboardRequestDTO.getDashboardMetricsCodes())) {
            for(Integer metricCode : dashboardRequestDTO.getDashboardMetricsCodes()) {
                DashboardMetricEnum dashboardMetricEnum = DashboardMetricEnum.valueOf(metricCode);
                if(null == dashboardMetricEnum) {
                    throw new ServiceException(
                            String.format("dashboard指标code错误，系统不存在code={%d}的dashboard指标", metricCode),
                            ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
                    );
                } else {
                    setDashBoardMetric(dashboardMetricEnum, startTime, endTime, dashBoardVO);
                }
            }
        }
        return Result.buildSucc(dashBoardVO);
    }

    private void setDashBoardMetric(DashboardMetricEnum dashboardMetricEnum, Long startTime, Long endTime, DashBoardVO dashBoardVO) {
        switch (dashboardMetricEnum) {
            case LOG_COLLECT_TASK_NUM:
                dashBoardVO.setLogCollectTaskNum(logCollectTaskManageService.countAll());
                break;
            case NON_RELATE_ANY_HOST_LOG_COLLECT_TASK_NUM:
                List<Long> logCollectTaskIdList = logCollectTaskManageService.getAllIds();
                Long nonRelateAnyHostLogCollectTaskNum = 0L;
                for (Long logCollectTaskId : logCollectTaskIdList) {
                    if(logCollectTaskManageService.checkNotRelateAnyHost(logCollectTaskId)) {
                        nonRelateAnyHostLogCollectTaskNum++;
                    }
                }
                dashBoardVO.setNonRelateAnyHostLogCollectTaskNum(nonRelateAnyHostLogCollectTaskNum);
                break;
            case LOG_COLLECT_PATH_NUM:
                dashBoardVO.setLogCollectPathNum(fileLogCollectPathManageService.countAll());
                break;
            case SERVICE_NUM:
                dashBoardVO.setServiceNum(serviceManageService.countAll());
                break;
            case HOST_NUM:
                dashBoardVO.setHostNum(hostManageService.countAllHost());
                break;
            case CONTAINER_NUM:
                dashBoardVO.setContainerNum(hostManageService.countAllContainer());
                break;
            case AGENT_NUM:
                dashBoardVO.setAgentNum(agentManageService.countAll());
                break;
            case NON_RELATE_ANY_LOG_COLLECT_TASK_AGENT_NUM:
                List<String> agentHostNameList = agentManageService.getAllHostNames();
                Long nonRelateAnyLogCollectTaskAgentNum = 0L;
                for (String hostName : agentHostNameList) {
                    if(CollectionUtils.isEmpty(logCollectTaskManageService.getLogCollectTaskListByAgentHostName(hostName))) {
                        nonRelateAnyLogCollectTaskAgentNum++;
                    }
                }
                dashBoardVO.setNonRelateAnyLogCollectTaskAgentNum(nonRelateAnyLogCollectTaskAgentNum);
                break;
            case CURRENT_COLLECT_BYTES:
//                dashBoardVO.setCurrentCollectBytes(logCollectTaskManageService.getCurrentCollectBytes());
                break;
            case CURRENT_COLLECT_LOG_EVENTS:
//                dashBoardVO.setCurrentCollectLogEvents(logCollectTaskManageService.getCurrentCollectCount());
                break;
            case COLLECT_BYTES_DAY:
//                dashBoardVO.setCollectBytesDay(logCollectTaskManageService.getCollectBytesToday());
                break;
            case COLLECT_LOG_EVENTS_DAY:
//                dashBoardVO.setCollectLogEventsDay(logCollectTaskManageService.getCollectCountToday());
                break;
            case RED_LOG_COLLECT_TASK_NAME_ID_PAIR_LIST:
                List<LogCollectTaskDO> redLogCollectTaskDOList = logCollectTaskManageService.getByHealthLevel(LogCollectTaskHealthLevelEnum.RED.getCode());
                List<Pair<String, Long>> redLogCollectTaskNameIdPairList = new ArrayList<>(redLogCollectTaskDOList.size());
                for (LogCollectTaskDO logCollectTaskDO : redLogCollectTaskDOList) {
                    redLogCollectTaskNameIdPairList.add(new Pair<>(logCollectTaskDO.getLogCollectTaskName(), logCollectTaskDO.getId()));
                }
                dashBoardVO.setRedLogCollectTaskNameIdPairList(redLogCollectTaskNameIdPairList);
                break;
            case YELLOW_LOG_COLLECT_TASK_NAME_ID_PAIR_LIST:
                List<LogCollectTaskDO> yellowLogCollectTaskDOList = logCollectTaskManageService.getByHealthLevel(LogCollectTaskHealthLevelEnum.YELLOW.getCode());
                List<Pair<String, Long>> yellowLogCollectTaskNameIdPairList = new ArrayList<>(yellowLogCollectTaskDOList.size());
                for (LogCollectTaskDO logCollectTaskDO : yellowLogCollectTaskDOList) {
                    yellowLogCollectTaskNameIdPairList.add(new Pair<>(logCollectTaskDO.getLogCollectTaskName(), logCollectTaskDO.getId()));
                }
                dashBoardVO.setYellowLogCollectTaskNameIdPairList(yellowLogCollectTaskNameIdPairList);
                break;
            case RED_AGENT_HOST_NAME_ID_PAIR_LIST:
                List<AgentDO> redAgentDOList = agentManageService.getByHealthLevel(AgentHealthLevelEnum.RED.getCode());
                List<Pair<String, Long>> redAgentHostNameIdPairList = new ArrayList<>(redAgentDOList.size());
                for (AgentDO agentDO : redAgentDOList) {
                    redAgentHostNameIdPairList.add(new Pair<>(agentDO.getHostName(), agentDO.getId()));
                }
                dashBoardVO.setRedAgentHostNameIdPairList(redAgentHostNameIdPairList);
                break;
            case YELLOW_AGENT_HOST_NAME_ID_PAIR_LIST:
                List<AgentDO> yellowAgentDOList = agentManageService.getByHealthLevel(AgentHealthLevelEnum.YELLOW.getCode());
                List<Pair<String, Long>> yellowAgentHostNameIdPairList = new ArrayList<>(yellowAgentDOList.size());
                for (AgentDO agentDO : yellowAgentDOList) {
                    yellowAgentHostNameIdPairList.add(new Pair<>(agentDO.getHostName(), agentDO.getId()));
                }
                dashBoardVO.setYellowAgentHostNameIdPairList(yellowAgentHostNameIdPairList);
                break;
            case LOG_COLLECT_TASK_LIST_COLLECT_BYTES_TOP5:
//                dashBoardVO.setLogCollectTaskListCollectBytesTop5(agentMetricsManageService.getLogCollectTaskListCollectBytesLastest1MinTop5(startTime, endTime));
                break;
            case LOG_COLLECT_TASK_LIST_COLLECT_COUNT_TOP5:
//                dashBoardVO.setLogCollectTaskListCollectCountTop5(agentMetricsManageService.getLogCollectTaskListCollectCountLastest1MinTop5(startTime, endTime));
                break;
            case LOG_COLLECT_TASK_LIST_RELATE_HOSTS_TOP5:
//                dashBoardVO.setLogCollectTaskListRelateHostsTop5(logCollectTaskManageService.getTop5HostCount(startTime, endTime));
                break;
            case LOG_COLLECT_TASK_LIST_RELATE_AGENTS_TOP5:
//                dashBoardVO.setLogCollectTaskListRelateAgentsTop5(logCollectTaskManageService.getTop5AgentCount(startTime, endTime));
                break;
            case AGENT_LIST_COLLECT_BYTES_TOP5:
//                dashBoardVO.setAgentListCollectBytesTop5(agentMetricsManageService.getAgentListCollectBytesLastest1MinTop5(startTime, endTime));
                break;
            case AGENT_LIST_COLLECT_COUNT_TOP5:
//                dashBoardVO.setAgentListCollectCountTop5(agentMetricsManageService.getAgentListCollectCountLastest1MinTop5(startTime, endTime));
                break;
            case AGENT_LIST_CPU_USAGE_TOP5:
//                dashBoardVO.setAgentListCpuUsageTop5(agentMetricsManageService.getAgentListCpuUsageLastest1MinTop5(startTime, endTime));
                break;
            case AGENT_LIST_MEMORY_USAGE_TOP5:
//                dashBoardVO.setAgentListMemoryUsageTop5(agentMetricsManageService.getAgentListMemoryUsedLastest1MinTop5(startTime, endTime));
                break;
            case AGENT_LIST_FD_USED_TOP5:
//                dashBoardVO.setAgentListFdUsedTop5(agentMetricsManageService.getAgentListFdUsedLastest1MinTop5(startTime, endTime));
                break;
            case AGENT_LIST_FULL_GC_COUNT_TOP5:
//                dashBoardVO.setAgentListFullGcCountTop5(agentMetricsManageService.getAgentListFullGcCountLastest1MinTop5(startTime, endTime));
                break;
            case AGENT_LIST_RELATE_LOG_COLLECT_TASKS_TOP5:
//                dashBoardVO.setAgentListRelateLogCollectTasksTop5(agentManageService.getTop5LogCollectTaskCount(startTime, endTime));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dashboardMetricEnum);
        }
    }

}
