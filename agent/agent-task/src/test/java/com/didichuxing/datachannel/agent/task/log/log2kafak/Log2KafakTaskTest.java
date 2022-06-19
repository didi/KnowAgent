//package com.didichuxing.datachannel.agent.task.log.log2kafak;
//
//import static org.junit.Assert.assertTrue;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
//import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;
//import org.apache.commons.lang.StringUtils;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.didichuxing.datachannel.agent.channel.log.FileNodeOffset;
//import com.didichuxing.datachannel.agent.channel.log.LogChannel;
//import com.didichuxing.datachannel.agent.common.beans.LogPath;
//import com.didichuxing.datachannel.agent.engine.bean.LogEvent;
//import com.didichuxing.datachannel.agent.common.configs.v2.component.*;
//import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
//import com.didichuxing.datachannel.agent.integration.test.basic.BasicUtil;
//import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaSink;
//import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
//import com.didichuxing.datachannel.agent.source.log.LogSource;
//import com.didichuxing.datachannel.agent.source.log.beans.WorkingFileNode;
//import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
//import com.didichuxing.datachannel.agent.task.log.LogFileUtils;
//
///**
// * @description:
// * @author: huangjw
// * @Date: 2019-07-26 10:55
// */
//public class Log2KafakTaskTest extends LogFileUtils {
//
//    private static final Long   defaultModelId         = 0L;
//    private static final String defaultTopic           = "test_topic_kafkaSinkTest";
//    private static final String defaultTopic_sync      = "test_topic_kafkaSinkTest_sync";
//    private static final String defaultTopic_run       = "test_topic_kafkaSinkTest_run_"
//                                                         + System.currentTimeMillis();
//    private static final String defaultTopic_run_multi = "test_topic_kafkaSinkTest_run_multi_"
//                                                         + System.currentTimeMillis();
//
//    @BeforeClass
//    public static void beforeClass() {
//        // 启动前先停止
//        // destory();
//        // 启动kafka
//        BasicUtil.getInstance().prepare();
//        BasicUtil.getInstance().createTopic(defaultTopic, 1);
//        BasicUtil.getInstance().createTopic(defaultTopic_sync, 1);
//        BasicUtil.getInstance().createTopic(defaultTopic_run, 1);
//        BasicUtil.getInstance().createTopic(defaultTopic_run_multi, 1);
//
//        List<String> topics = new ArrayList<>();
//        topics.add(defaultTopic);
//        topics.add(defaultTopic_sync);
//        topics.add(defaultTopic_run);
//        topics.add(defaultTopic_run_multi);
//        BasicUtil.getInstance().initTopicList(topics);
//    }
//
//    @AfterClass
//    public static void destory() {
//        // 停止kafka
//        BasicUtil.getInstance().tearDown();
//    }
//
//    @Test
//    public void addSink() {
//        Log2KafkaTask task = getTask();
//        int num = task.getModelConfig().getTargetConfig().getSinkNum();
//        assertTrue(task.getSinkers().size() == num);
//        task.addSink(num);
//        assertTrue(task.getSinkers().size() == num + 1);
//    }
//
//    @Test
//    public void beforeStart() {
//        Log2KafkaTask task = getTask();
//        task.getKafkaTargetConfig().setTopic(defaultTopic_sync);
//        task.init(task.getModelConfig());
//        task.prepare();
//        int max = 30;
//        int result = max;
//        for (int i = 0; i <= max; i++) {
//            ConsumerRecords<String, String> records = BasicUtil.getInstance()
//                .getNextMessageFromConsumer(defaultTopic_sync);
//            for (ConsumerRecord<String, String> record : records) {
//                if (record != null && StringUtils.isNotBlank(record.value())) {
//                    System.out.println(record.key());
//                    System.out.println(record.value());
//                    result = i;
//                    break;
//                }
//            }
//        }
//        assertTrue(result < max);
//    }
//
//    @Test
//    public void needToFlush() {
//        Log2KafkaTask task = getTask();
//        long flushTimeOut = 1000L;
//        task.getKafkaTargetConfig().setFlushBatchTimeThreshold(flushTimeOut);
//        task.init(task.getModelConfig());
//
//        assertTrue(!task.needToFlush(null));
//        try {
//            Thread.sleep(flushTimeOut);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        assertTrue(task.needToFlush(null));
//        assertTrue(!task.needToFlush(null));
//
//        for (WorkingFileNode wfn : ((LogSource) task.getSource()).getCollectingFileNodeMap()
//            .values()) {
//            wfn.setFileEnd(true);
//        }
//        try {
//            Thread.sleep(flushTimeOut);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        assertTrue(task.needToFlush(null));
//    }
//
//    @Test
//    public void rollback() {
//        Log2KafkaTask task = getTask();
//        task.init(task.getModelConfig());
//        task.addSink(1);
//
//        Map<String, WorkingFileNode> workingFileNodeMap = ((LogSource) task.getSource()).getCollectingFileNodeMap();
//
//        KafkaSink sink0 = (KafkaSink) getAbstractSinkByNum(0, task.getSinkers());
//        KafkaSink sink1 = (KafkaSink) getAbstractSinkByNum(1, task.getSinkers());
//
//        List<WorkingFileNode> workingFileNodes = new ArrayList<>(workingFileNodeMap.values());
//        buildFailedRateMap(sink0, workingFileNodes, task.getSinkers().size());
//        buildFailedRateMap(sink1, workingFileNodes, task.getSinkers().size());
//
//        task.rollback();
//        workingFileNodeMap = ((LogSource) task.getSource()).getCollectingFileNodeMap();
//        int result = 0;
//        for (WorkingFileNode wfn : workingFileNodeMap.values()) {
//            if (wfn.getCurOffset() == 2) {
//                result++;
//            }
//        }
//        assertTrue(result == workingFileNodeMap.size());
//    }
//
//    private void buildFailedRateMap(KafkaSink sink, List<WorkingFileNode> workingFileNodes, int sinkNum) {
//        Map<String, Long> rateMap = new HashMap<>();
//        for (int i = 0; i < workingFileNodes.size(); i++) {
//            long rate = 0L;
//            if (i % sinkNum == sink.orderNum) {
//                rate = 2;
//            } else {
//                rate = 4;
//            }
//            rateMap.put(workingFileNodes.get(i).getUniqueKey(), rate);
//        }
//        sink.setFailedRateMapS1(rateMap);
//    }
//
//    private AbstractSink getAbstractSinkByNum(int num, Map<String, AbstractSink> sinkMap) {
//        for (AbstractSink sink : sinkMap.values()) {
//            if (sink.getOrderNum() == num) {
//                return sink;
//            }
//        }
//        return null;
//    }
//
//    @Test
//    public void commit() {
//        Log2KafkaTask task = getTask();
//        task.init(task.getModelConfig());
//
//        long defaultOffset = 2L;
//        Map<String, FileNodeOffset> fileNodeOffsetMap = new ConcurrentHashMap<>();
//        Map<String, WorkingFileNode> workingFileNodeMap = ((LogSource) task.getSource()).getCollectingFileNodeMap();
//        for (WorkingFileNode wfn : workingFileNodeMap.values()) {
//            FileNodeOffset fileNodeOffset = getFileNodeOffset(wfn.getUniqueKey(), defaultOffset);
//            fileNodeOffsetMap.put(fileNodeOffset.getFileNodeKey(), fileNodeOffset);
//        }
//        ((LogChannel) task.getChannel()).setLastestOffsetMap(fileNodeOffsetMap);
//        task.commit();
//
//        workingFileNodeMap = ((LogSource) task.getSource()).getCollectingFileNodeMap();
//        int result = 0;
//        for (WorkingFileNode wfn : workingFileNodeMap.values()) {
//            if (wfn.getFileNode().getOffset() == 2L) {
//                result++;
//            }
//        }
//        assertTrue(result == workingFileNodeMap.size());
//    }
//
//    private FileNodeOffset getFileNodeOffset(String fileNodeKey, Long offset) {
//        LogEvent logEvent = new LogEvent();
//        logEvent.setOffset(offset);
//        logEvent.setFileNodeKey(fileNodeKey);
//        logEvent.setLogTime(System.currentTimeMillis());
//        FileNodeOffset fileNodeOffset = new FileNodeOffset(logEvent);
//        return fileNodeOffset;
//    }
//
//    @Test
//    public void run() {
//        Log2KafkaTask task = getTask();
//        task.getKafkaTargetConfig().setTopic(defaultTopic_run);
//        task.init(task.getModelConfig());
//        task.start();
//        Thread thread = new Thread(task);
//        thread.start();
//
//        Set<String> consumerResult = new HashSet<>();
//        int max = 60;
//        // 等待60s
//        for (int i = 0; i <= max; i++) {
//            ConsumerRecords<String, String> records = BasicUtil.getInstance().getNextMessageFromConsumer(defaultTopic_run);
//            for (ConsumerRecord<String, String> record : records) {
//                if (record != null && StringUtils.isNotBlank(record.value())) {
//                    Set<String> contents = parseContent(record.value());
//                    for (String content : contents) {
//                        if (content.contains("timestamp=") && content.contains("bigline")) {
//                            consumerResult.add(content);
//                        }
//                    }
//                }
//            }
//        }
//
//        task.stop(true);
//        assertTrue(consumerResult.size() == 4 * MAX_LINE);
//
//        task.flush();
//        int successNum = 0;
//        Map<String, WorkingFileNode> workingFileNodeMap = ((LogSource) task.getSource()).getCollectingFileNodeMap();
//        for (WorkingFileNode wfn : workingFileNodeMap.values()) {
//            try {
//                if (wfn.getFileNode().getOffset() == wfn.getIn().length() && wfn.isFileEnd()) {
//                    successNum++;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        assertTrue(successNum == workingFileNodeMap.size());
//
//    }
//
//    @Test
//    public void runForTemporality() {
//        Log2KafkaTask task = getTask();
//        task.getKafkaTargetConfig().setTopic(defaultTopic_run);
//        task.getModelConfig().getCommonConfig().setModelType(LogConfigConstants.COLLECT_TYPE_TEMPORALITY);
//        task.getModelConfig().getCommonConfig().setStartTime(new Date(System.currentTimeMillis() - 1 * 60 * 60 * 1000L) );
//        task.getModelConfig().getCommonConfig().setEndTime(new Date(System.currentTimeMillis()));
//        task.init(task.getModelConfig());
//        task.start();
//        TaskRunningPool.submit(task);
//
//        Set<String> consumerResult = new HashSet<>();
//        int max = 60;
//        // 等待60s
//        for (int i = 0; i <= max; i++) {
//            ConsumerRecords<String, String> records = BasicUtil.getInstance().getNextMessageFromConsumer(defaultTopic_run);
//            for (ConsumerRecord<String, String> record : records) {
//                if (record != null && StringUtils.isNotBlank(record.value())) {
//                    Set<String> contents = parseContent(record.value());
//                    for (String content : contents) {
//                        if (content.contains("timestamp=") && content.contains("bigline")) {
//                            consumerResult.add(content);
//                        }
//                    }
//                }
//            }
//        }
//
//        assertTrue(consumerResult.size() == 4 * MAX_LINE);
//        assertTrue(task.canStop());
//    }
//
//    @Test
//    public void runMultiSink() {
//        Log2KafkaTask task = getTask();
//        task.getKafkaTargetConfig().setTopic(defaultTopic_run_multi);
//        task.getKafkaTargetConfig().setSinkNum(3);
//        task.init(task.getModelConfig());
//        task.start();
//        Thread thread = new Thread(task);
//        thread.start();
//
//        Set<String> consumerResult = new HashSet<>();
//        int max = 60;
//        // 等待60s
//        for (int i = 0; i <= max; i++) {
//            ConsumerRecords<String, String> records = BasicUtil.getInstance().getNextMessageFromConsumer(defaultTopic_run_multi);
//            for (ConsumerRecord<String, String> record : records) {
//                if (record != null && StringUtils.isNotBlank(record.value())) {
//                    Set<String> contents = parseContent(record.value());
//                    for (String content : contents) {
//                        if (content.contains("timestamp=") && content.contains("bigline")) {
//                            consumerResult.add(content);
//                        }
//                    }
//                }
//            }
//        }
//
//        task.stop(true);
//        assertTrue(consumerResult.size() == 4 * MAX_LINE);
//
//        task.flush();
//        int successNum = 0;
//        Map<String, WorkingFileNode> workingFileNodeMap = ((LogSource) task.getSource()).getCollectingFileNodeMap();
//        for (WorkingFileNode wfn : workingFileNodeMap.values()) {
//            try {
//                if (wfn.getFileNode().getOffset() == wfn.getIn().length() && wfn.isFileEnd()) {
//                    successNum++;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        assertTrue(successNum == workingFileNodeMap.size());
//
//    }
//
//    private Set<String> parseContent(String value) {
//        Set<String> contents = new HashSet<>();
//        JSONArray jsonArray = JSON.parseArray(value);
//        for (int i = 0; i < jsonArray.size(); i++) {
//            JSONObject jsonObject = jsonArray.getJSONObject(i);
//            String content = (String) jsonObject.get("content");
//            contents.add(content);
//            System.out.println(content);
//        }
//        return contents;
//    }
//
//    private Log2KafkaTask getTask() {
//        ModelConfig modelConfig = getModelConfig();
//        LogPath logPath = ((LogSourceConfig) modelConfig.getSourceConfig()).getLogPaths().get(0);
//        LogSource logSource = new LogSource(modelConfig, logPath);
//        Log2KafkaTask task = new Log2KafkaTask(modelConfig, logSource);
//        return task;
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
//        kafkaTargetConfig.setTopic(defaultTopic);
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
//        LogSourceConfig logSourceConfig = getLogSourceConfig();
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
