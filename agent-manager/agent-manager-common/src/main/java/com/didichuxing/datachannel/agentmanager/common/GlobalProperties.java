package com.didichuxing.datachannel.agentmanager.common;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard.DashBoardDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.MaintenanceDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.OperatingDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;

import java.util.ArrayList;
import java.util.List;

public class GlobalProperties {

    public static final List<Class<Processor>> LOG_COLLECT_TASK_HEALTH_CHECK_PROCESSOR_CLASS_LIST = new ArrayList<>();
    public static final List<Class<Processor>> AGENT_HEALTH_CHECK_PROCESSOR_CLASS_LIST = new ArrayList<>();

    public static volatile OperatingDashBoardVO operatingDashBoardVO;

    public static volatile MaintenanceDashBoardVO maintenanceDashBoardVO;

}
