package com.didichuxing.datachannel.agentmanager.task.resource.sync;

import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
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
@Task(name = "KafkaClusterSyncTask", description = "定时同步系统中所有的Kafka Cluster信息，周期为 10分钟/次 ",
        cron = "0 0/10 * * * ?", autoRegister = true)
public class KafkaClusterSyncTask implements Job {

    private static final ILog LOGGER = LogFactory.getLog("KafkaClusterSyncTask");

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    /**
     * 是否开启远程服务 & 主机信息同步
     */
    @Value("${metadata.sync.request.kafkacluster.enabled}")
    private boolean syncEnabled;

    @Override
    public Object execute(JobContext jobContext) throws Exception {
        if(syncEnabled) {
            LOGGER.info("class=KafkaClusterSyncTask||method=execute||msg=start2syncKafkaCluster");
            /*
             * 同步Kafka Cluster信息
             */
            kafkaClusterManageService.pullKafkaClusterListFromRemoteAndMergeKafkaClusterInLocal();
            LOGGER.info("class=KafkaClusterSyncTask||method=execute||msg=syncKafkaClusterEnd");
        } else {
            LOGGER.info("class=KafkaClusterSyncTask||method=execute||msg=Kafka集群信息远程同步功能被关闭，将不进行同步");
        }
        return "success";
    }

}
