package com.didichuxing.datachannel.agentmanager.task;

import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard.DashBoardDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.MaintenanceDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.OperatingDashBoardVO;
import com.didichuxing.datachannel.agentmanager.core.dashboard.DashboardManageService;
import com.didichuxing.datachannel.agentmanager.core.dashboard.impl.DashboardManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.errorlogs.ErrorLogsManageService;
import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Task(name = "DashboardBuildTask", description = "DashboardBuildTask", cron = "0 0/1 * * * ? *", autoRegister = true, timeout = 300, consensual = ConsensualEnum.RANDOM)
public class DashboardBuildTask implements Job {

    private static final ILog logger = LogFactory.getLog(DashboardBuildTask.class);

    @Autowired
    private DashboardManageService dashboardManageService;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("start to run DashboardBuildTask");
        try {
            DashBoardDO dashBoardDO = dashboardManageService.build();
            GlobalProperties.maintenanceDashBoardVO = MaintenanceDashBoardVO.cast2MaintenanceDashBoardVO(dashBoardDO);
            GlobalProperties.operatingDashBoardVO = OperatingDashBoardVO.cast2OperatingDashBoardVO(dashBoardDO);
        } catch (Exception ex) {
            logger.error(String.format(" build dashboard error, root cause is: %s", ex.getMessage()), ex);
        }
        logger.info("run DashboardBuildTask finish.");
        return TaskResult.buildSuccess();
    }

}