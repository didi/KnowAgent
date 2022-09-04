package com.didichuxing.datachannel.agentmanager.task;

import com.didichuxing.datachannel.agentmanager.common.GlobalProperties;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.dashboard.DashBoardDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.LogCollectTaskDO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.MaintenanceDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.dashboard.OperatingDashBoardVO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.logcollecttask.LogCollectTaskHealthLevelEnum;
import com.didichuxing.datachannel.agentmanager.core.dashboard.DashboardManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.LogCollectTaskHealthManageServiceImpl;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.impl.LogCollectTaskManageServiceImpl;
import com.didiglobal.logi.job.annotation.Task;
import com.didiglobal.logi.job.common.TaskResult;
import com.didiglobal.logi.job.core.consensual.ConsensualEnum;
import com.didiglobal.logi.job.core.job.Job;
import com.didiglobal.logi.job.core.job.JobContext;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Future;

@Component
@Task(name = "LogCollectTaskHealthCheckTask", description = "LogCollectTaskHealthCheckTask", cron = "0 0/1 * * * ? *", autoRegister = true, timeout = 300, consensual = ConsensualEnum.RANDOM)
public class LogCollectTaskHealthCheckTask implements Job {

    private static final ILog logger = LogFactory.getLog(LogCollectTaskHealthCheckTask.class);

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private LogCollectTaskHealthManageService logCollectTaskHealthManageService;

    @Override
    public TaskResult execute(JobContext jobContext) {
        logger.info("start to run LogCollectTaskHealthCheckTask");
        try {
            List<LogCollectTaskDO> logCollectTaskDOList = logCollectTaskManageService.getAllLogCollectTask2HealthCheck();
            if (CollectionUtils.isEmpty(logCollectTaskDOList)) {
                logger.info("class=LogCollectTaskHealthCheckTask||method=execute||msg=LogCollectTaskDO List task is empty!!");
            }
            for (LogCollectTaskDO logCollectTaskDO : logCollectTaskDOList) {
                LogCollectTaskHealthLevelEnum logCollectTaskHealthLevelEnum = logCollectTaskHealthManageService.checkLogCollectTaskHealth(logCollectTaskDO);
                logger.info("class=LogCollectTaskHealthCheckTask||method=execute||logCollectTaskId={}||"
                        + "logCollectTaskHealthLevel={}", logCollectTaskDO.getId(), logCollectTaskHealthLevelEnum.getDescription());
            }
        } catch (Exception ex) {
            logger.error(String.format(" check logCollectTask health error, root cause is: %s", ex.getMessage()), ex);
        }
        logger.info("run LogCollectTaskHealthCheckTask finish.");
        return TaskResult.buildSuccess();
    }

}