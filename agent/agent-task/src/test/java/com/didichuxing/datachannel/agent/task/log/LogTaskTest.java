//package com.didichuxing.datachannel.agent.task.log;
//
//import com.didichuxing.datachannel.agent.common.beans.LogPath;
//import com.didichuxing.datachannel.agent.common.configs.v2.component.*;
//import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;
//import com.didichuxing.datachannel.agent.integration.test.basic.BasicUtil;
//import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
//import com.didichuxing.datachannel.agent.source.log.LogSource;
//import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
//import com.didichuxing.datachannel.agent.source.log.utils.FileUtils;
//import com.didichuxing.datachannel.agent.task.log.log2kafak.TestLog2KafkaTask;
//import org.junit.Test;
//
//import java.io.File;
//
//import static org.junit.Assert.assertTrue;
//
///**
// * @description:
// * @author: huangjw
// * @Date: 2019-12-09 16:15
// */
//public class LogTaskTest extends LogFileUtils {
//
//    /**
//     * 文件清空后，重新写入文件，引起文件的状态异常的case
//     */
//    @Test
//    public void runForFileClear() {
//        TestLog2KafkaTask task = getTestTask();
//        task.init(task.getModelConfig());
//        task.start();
//        TaskRunningPool.submit(task);
//
//        try {
//            Thread.sleep(1 * 1000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        overWriteFileForOneLine(baseFilePath_7);
//        try {
//            Thread.sleep(30 * 1000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        task.flush();
//        task.commit();
//
//        File file = new File(baseFilePath_7);
//        String uniqueKey = "0_0_" + FileUtils.getFileKeyByAttrs(file);
//        assertTrue(((LogSource) task.getSource()).getCollectingFileNodeMap().get(uniqueKey)
//            .getFileNode().getOffset() == file.length());
//    }
//
//    private TestLog2KafkaTask getTestTask() {
//        ModelConfig modelConfig = getModelConfig();
//        LogPath logPath = ((LogSourceConfig) modelConfig.getSourceConfig()).getLogPaths().get(0);
//        LogSource logSource = new LogSource(modelConfig, logPath);
//        TestLog2KafkaTask task = new TestLog2KafkaTask(modelConfig, logSource);
//        return task;
//    }
//
//    private ModelConfig getModelConfig() {
//        KafkaTargetConfig kafkaTargetConfig = new KafkaTargetConfig();
//        String bootstrap = BasicUtil.getInstance().getKafkaServerUrl();
//        String propertiesStr = "serializer.class=kafka.serializer.StringEncoder,compression.codec=3,max.request.size=4194304";
//        kafkaTargetConfig.setBootstrap(bootstrap);
//        kafkaTargetConfig.setProperties(propertiesStr);
//        kafkaTargetConfig.setTopic("topicname");
//        kafkaTargetConfig.setFlushBatchSize(1000);
//        kafkaTargetConfig.setFlushBatchTimeThreshold(180000L);
//
//        ModelConfig modelConfig = new ModelConfig("log2kafka");
//        modelConfig.setTargetConfig(kafkaTargetConfig);
//
//        CommonConfig commonConfig = new CommonConfig();
//        commonConfig.setModelId(defaultModelId);
//        modelConfig.setCommonConfig(commonConfig);
//
//        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
//        eventMetricsConfig.setOdinLeaf("hna");
//        eventMetricsConfig.setOriginalAppName("");
//        eventMetricsConfig.setTransName("");
//        modelConfig.setEventMetricsConfig(eventMetricsConfig);
//
//        LogSourceConfig logSourceConfig = getLogSourceConfigWF();
//        modelConfig.setSourceConfig(logSourceConfig);
//
//        ModelLimitConfig modelLimitConfig = new ModelLimitConfig();
//        modelLimitConfig.setRate(10 * 1024 * 1024);
//        modelConfig.setModelLimitConfig(modelLimitConfig);
//
//        ChannelConfig channelConfig = new ChannelConfig();
//        modelConfig.setChannelConfig(channelConfig);
//
//        return modelConfig;
//    }
//}
