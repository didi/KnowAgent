//package com.didichuxing.datachannel.agentmanager.task.resource.metric;
//
//import com.didiglobal.logi.auvjob.annotation.Task;
//import com.didiglobal.logi.auvjob.core.job.Job;
//import com.didiglobal.logi.auvjob.core.job.JobContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Task(name = "MetricClearTask", description = "定期同步agent配置的metric流、error logs流的消费端，每10分钟执行一次",
//        cron = "0 0/10 * * * ?", autoRegister = true)
//public class MetricConsumerResetTask implements Job {
//    private static final Logger LOGGER = LoggerFactory.getLogger(MetricClearTask.class);
//
//    @Autowired
//    private MetricService metricService;
//
//    @Override
//    public Object execute(JobContext jobContext) throws Exception {
//        LOGGER.info(String.format("class=MetricConsumerResetTask||method=execute||msg=%s", "consumer reset started"));
//
//        /*
//         * TODO：
//         *  1.）检查是否存在变更接收端配置，对于变更接收端配置对应consumer进行对应变更操作
//         *  2.）检查已存在接收端配置对应consumer，是否存在故障，如存在故障，进行对应rebuild操作
//         *  3.）消费线程池需要支持多进程协商，各topic对应partition数为各topic对应最大消费线程数
//         */
//
//        metricService.resetMetricConsumers();
//        LOGGER.info(String.format("class=MetricConsumerResetTask||method=execute||msg=%s", "cleanEnd"));
//        return "success";
//    }
//}
