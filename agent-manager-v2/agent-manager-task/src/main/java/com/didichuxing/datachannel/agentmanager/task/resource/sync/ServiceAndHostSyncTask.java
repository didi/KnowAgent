package com.didichuxing.datachannel.agentmanager.task.resource.sync;

import com.didichuxing.datachannel.agentmanager.core.host.HostManageService;
import com.didichuxing.datachannel.agentmanager.core.metadata.MetadataManageService;
import com.didichuxing.datachannel.agentmanager.core.service.ServiceManageService;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didiglobal.logi.auvjob.annotation.Task;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.job.JobContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author huqidong
 * @date 2020-09-21
 * 日志采集任务健康监控任务
 */
@Task(name = "ServiceAndHostSyncTask", description = "定时同步系统中所有的服务&主机、容器信息检查周期为 10分钟/次 ",
        cron = "0 0/3 * * * ?", autoRegister = true)
public class ServiceAndHostSyncTask implements Job {

    private static final ILog LOGGER = LogFactory.getLog("LogCollectTaskHealthCheckTask");

    @Autowired
    private ServiceManageService serviceManageService;

    @Autowired
    private HostManageService hostManageService;

    @Autowired
    private MetadataManageService metadataManageService;

    /**
     * 是否开启远程服务 & 主机信息同步
     */
    @Value("${metadata.sync.request.service.enabled}")
    private boolean syncEnabled;

    @Override
    public Object execute(JobContext jobContext) {
        if(syncEnabled) {
            LOGGER.info("class=ServiceAndHostSyncTask||method=execute||msg=start2sync");
            metadataManageService.sync();
            LOGGER.info("class=ServiceAndHostSyncTask||method=execute||msg=syncEnd");
        } else {
            LOGGER.info("class=ServiceAndHostSyncTask||method=execute||msg=服务&主机信息远程同步功能被关闭，将不进行同步");
        }
        return "success";
    }

}
