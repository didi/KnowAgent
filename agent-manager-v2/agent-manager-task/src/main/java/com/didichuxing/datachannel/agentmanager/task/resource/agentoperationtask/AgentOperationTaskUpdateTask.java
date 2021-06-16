package com.didichuxing.datachannel.agentmanager.task.resource.agentoperationtask;

import com.didichuxing.datachannel.agentmanager.core.agent.operation.task.AgentOperationTaskManageService;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huqidong
 * @date 2020-09-21
 * Agent 运维操作执行任务
 */
@Task(name = "AgentOperationTaskUpdateTask", description = "定时获取所有未完成的Agent运维操作执行任务对应各主机执行结果，并将执行结果更新至表 tb_agent_operation_task & tb_agent_operation_sub_task，周期为 1分钟/次 ",
        cron = "0 0/1 * * * ?", autoRegister = true)
public class AgentOperationTaskUpdateTask implements Job {

    private static final ILog LOGGER = LogFactory.getLog("AgentOperationTaskUpdateTask");

    @Autowired
    private AgentOperationTaskManageService agentOperationTaskManageService;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        LOGGER.info("class=AgentOperationTaskUpdateTask||method=execute||msg=start2UpdateAgentOperationTask");
        /*
         * 同步服务节点信息
         */
        agentOperationTaskManageService.updateAgentOperationTasks();
        LOGGER.info("class=AgentOperationTaskUpdateTask||method=execute||msg=UpdateAgentOperationTaskEnd");
        return "success";
    }

}
