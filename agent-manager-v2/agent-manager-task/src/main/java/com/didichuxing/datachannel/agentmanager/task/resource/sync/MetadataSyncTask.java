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
 * 元数据远程同步服务
 */
@Task(name = "MetadataSyncTask", description = "定时同步系统中所有的服务&主机、容器、pod 信息，定时同步周期为 5分钟/次 ",
        cron = "0 0/5 * * * ?", autoRegister = true)
public class MetadataSyncTask implements Job {

    private static final ILog LOGGER = LogFactory.getLog("MetadataSyncTask");

    @Autowired
    private MetadataManageService metadataManageService;

    /**
     * 是否开启远程 k8s 元数据信息同步
     */
    @Value("${metadata.sync.request.k8s.enabled}")
    private boolean enabled;

    @Override
    public Object execute(JobContext jobContext) {
        if(enabled) {
            LOGGER.info("class=MetadataSyncTask||method=execute||msg=start2sync");
            metadataManageService.sync();
            LOGGER.info("class=MetadataSyncTask||method=execute||msg=syncEnd");
        } else {
            LOGGER.info("class=MetadataSyncTask||method=execute||msg=元数据信息远程同步功能被关闭，将不进行同步");
        }
        return "success";
    }

}
