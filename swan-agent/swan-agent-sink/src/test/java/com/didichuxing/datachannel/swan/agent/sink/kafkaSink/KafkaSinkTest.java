package com.didichuxing.datachannel.swan.agent.sink.kafkaSink;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.swan.agent.common.api.StandardLogType;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Test;

import com.didichuxing.datachannel.swan.agent.channel.log.LogChannel;
import com.didichuxing.datachannel.swan.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.EventMetricsConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.engine.bean.LogEvent;
import com.didichuxing.datachannel.swan.agent.engine.channel.AbstractChannel;
import com.didichuxing.datachannel.swan.agent.integration.test.basic.BasicUtil;
import com.didichuxing.datachannel.swan.agent.sink.utils.EventUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-25 13:57
 */
public class KafkaSinkTest {

    private static final Long   defaultModelId = 0L;
    private static final String defaultTopic   = "test_topic_kafkaSinkTest";

    private static long         line_num       = 0L;

    // @BeforeClass
    // public static void before() {
    // // 启动前先停止
    // // destory();
    // // 启动kafka
    // BasicUtil.getInstance().prepare();
    // BasicUtil.getInstance().createTopic(defaultTopic, 1);
    //
    // List<String> topics = new ArrayList<>();
    // topics.add(defaultTopic);
    // BasicUtil.getInstance().initTopicList(topics);
    // }
    //
    // @AfterClass
    // public static void destory() {
    // // 停止kafka
    // BasicUtil.getInstance().tearDown();
    // }

    @Test
    public void wrap() {
        KafkaSink kafkaSink = getSink();
        kafkaSink.getKafkaTargetConfig().setFilterOprType(LogConfigConstants.FILTER_TYPE_CONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        LogEvent logEvent0 = getLogEvent();
        kafkaSink.wrap(logEvent0);
        assertTrue(kafkaSink.getBatch().size() == 1);

        kafkaSink.getBatch().clear();
        kafkaSink.getKafkaTargetConfig()
            .setFilterOprType(LogConfigConstants.FILTER_TYPE_UNCONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        LogEvent logEvent1 = getLogEvent();
        kafkaSink.wrap(logEvent1);
        assertTrue(kafkaSink.getBatch().size() == 0);
    }

    @Test
    public void send() {
        KafkaSink kafkaSink = getSink();
        kafkaSink.getKafkaTargetConfig().setFilterOprType(LogConfigConstants.FILTER_TYPE_CONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        kafkaSink.getKafkaTargetConfig().setSendBatchSize(1);
        LogEvent logEvent0 = getLogEvent();
        KafkaEvent kafkaEvent = kafkaSink.wrap(logEvent0);
        kafkaSink.send(kafkaEvent);

        assertTrue(kafkaSink.getBatch().size() == 0);
        assertTrue(kafkaSink.getSendNum() == 1);

        int max = 30;
        int result = max;
        for (int i = 0; i <= max; i++) {
            ConsumerRecords<String, String> records = BasicUtil.getInstance()
                .getNextMessageFromConsumer(defaultTopic);
            for (ConsumerRecord<String, String> record : records) {
                if (record != null && StringUtils.isNotBlank(record.value())
                    && record.value().contains(logEvent0.getContent())) {
                    System.out.println(record.key());
                    System.out.println(record.value());
                    result = i;
                    return;
                }
            }
        }
        assertTrue(result < max);
    }

    @Test
    public void syncSend() {
        KafkaSink kafkaSink = getSink();
        kafkaSink.getKafkaTargetConfig().setFilterOprType(LogConfigConstants.FILTER_TYPE_CONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        kafkaSink.getKafkaTargetConfig().setSendBatchSize(1);
        LogEvent logEvent0 = getLogEvent();
        kafkaSink.syncSend(logEvent0.getContent(), "");

        int max = 30;
        int result = max;
        for (int i = 0; i <= max; i++) {
            ConsumerRecords<String, String> records = BasicUtil.getInstance()
                .getNextMessageFromConsumer(defaultTopic);
            for (ConsumerRecord<String, String> record : records) {
                if (record != null && StringUtils.isNotBlank(record.value())
                    && record.value().equals(logEvent0.getContent())) {
                    System.out.println(record.key());
                    System.out.println(record.value());
                    result = i;
                    return;
                }
            }
        }
        assertTrue(result < max);
    }

    @Test
    public void flush() {
        KafkaSink kafkaSink = getSink_LonglingerMs();
        kafkaSink.getKafkaTargetConfig().setFilterOprType(LogConfigConstants.FILTER_TYPE_CONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        kafkaSink.getKafkaTargetConfig().setSendBatchSize(1);
        LogEvent logEvent0 = getLogEvent();
        KafkaEvent kafkaEvent = kafkaSink.wrap(logEvent0);
        kafkaSink.send(kafkaEvent);

        assertTrue(kafkaSink.getBatch().size() == 0);
        assertTrue(kafkaSink.getSendNum() == 1);

        int max = 30;
        int result = max;
        for (int i = 0; i <= max; i++) {
            ConsumerRecords<String, String> records = BasicUtil.getInstance()
                .getNextMessageFromConsumer(defaultTopic);
            for (ConsumerRecord<String, String> record : records) {
                if (record != null && StringUtils.isNotBlank(record.value())
                    && record.value().contains(logEvent0.getContent())) {
                    System.out.println(record.key());
                    System.out.println(record.value());
                    result = i;
                    return;
                }
            }
        }
        assertTrue(result == max);

        kafkaSink.flush();
        result = max;
        for (int i = 0; i <= max; i++) {
            ConsumerRecords<String, String> records = BasicUtil.getInstance()
                .getNextMessageFromConsumer(defaultTopic);
            for (ConsumerRecord<String, String> record : records) {
                if (record != null && StringUtils.isNotBlank(record.value())
                    && record.value().contains(logEvent0.getContent())) {
                    System.out.println(record.key());
                    System.out.println(record.value());
                    result = i;
                    return;
                }
            }
        }
        assertTrue(result < max);
    }

    @Test
    public void toJson() {
        KafkaSink kafkaSink = getSink();
        kafkaSink.getKafkaTargetConfig().setFilterOprType(LogConfigConstants.FILTER_TYPE_CONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        kafkaSink.getKafkaTargetConfig().setSendBatchSize(1);
        LogEvent logEvent0 = getLogEvent2();
        KafkaEvent kafkaEvent = kafkaSink.wrap(logEvent0);
        List<KafkaEvent> kafkaEvents = new ArrayList<>();
        kafkaEvents.add(kafkaEvent);
        System.out.println(EventUtils.toNewListJson(kafkaSink, kafkaEvents, StandardLogType.Normal.getType()));
        System.out.println(EventUtils.toListJson(kafkaSink, kafkaEvents));
    }

    @Test
    public void compare() {
        int size = 10;
        int times = 1000;
        KafkaSink kafkaSink = getSink();
        kafkaSink.getKafkaTargetConfig().setFilterOprType(LogConfigConstants.FILTER_TYPE_CONTAINS);
        kafkaSink.getKafkaTargetConfig().setFilterRule("test content");
        kafkaSink.getKafkaTargetConfig().setSendBatchSize(1);
        LogEvent logEvent0 = getLogEvent2();
        KafkaEvent kafkaEvent = kafkaSink.wrap(logEvent0);
        List<KafkaEvent> kafkaEvents = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            kafkaEvents.add(kafkaEvent);
        }

        long start1 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            EventUtils.toNewListJson(kafkaSink, kafkaEvents, StandardLogType.Normal.getType());
        }
        System.out.println("old cost " + (System.currentTimeMillis() - start1));

        long start2 = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            EventUtils.toListJson(kafkaSink, kafkaEvents);
        }
        System.out.println("new cost " + (System.currentTimeMillis() - start2));
    }

    private LogEvent getLogEvent() {
        Long time = System.currentTimeMillis();
        String content = "test content.time is " + time;
        LogEvent logEvent = new LogEvent(content, content.getBytes(), line_num + content.length(),
            time, time + "", line_num, "fileNodeKey", "fileKey", "/tmp/test/", "test.log.didi.log",
            "test.log", null);
        line_num += content.length();
        return logEvent;
    }

    private LogEvent getLogEvent2() {
        Long time = System.currentTimeMillis();
        String content = "[INFO][2019-07-25T00:01:43.724][CouponModel.cpp:81] _undef||traceid=6bd3ea5136084913500e804110520303||spanid=28707e9545272837||[D]_msg=fc coupon info||SmartCouponExp {\n"
                         + "  01: UserID (i64) = 369435969289669,\n"
                         + "  02: CityID (i64) = 52140500,\n"
                         + "  03: CouponBatchID (i64) = 55152043,\n"
                         + "  04: StartTime (i64) = 1563944400,\n"
                         + "  05: EndTime (i64) = 1564030799,\n"
                         + "  06: ExpID (string) = \"activity_level_exp_treshold_8\",\n"
                         + "  07: ActivityID (i64) = 5619631,\n"
                         + "  08: ProductID (i64) = 247,\n"
                         + "}";
        LogEvent logEvent = new LogEvent(content, content.getBytes(), line_num + content.length(),
            time, time + "", line_num, "fileNodeKey", "fileKey", "/tmp/test/", "test.log.didi.log",
            "test.log", null);
        line_num += content.length();
        return logEvent;
    }

    private KafkaSink getSink() {
        KafkaTargetConfig kafkaTargetConfig = new KafkaTargetConfig();
        String bootstrap = BasicUtil.getInstance().getKafkaServerUrl();
        String propertiesStr = "serializer.class=kafka.serializer.StringEncoder,compression.codec=3,max.request.size=4194304";
        kafkaTargetConfig.setBootstrap(bootstrap);
        kafkaTargetConfig.setProperties(propertiesStr);
        kafkaTargetConfig.setTopic(defaultTopic);

        ModelConfig modelConfig = new ModelConfig("log2kafka");
        modelConfig.setTargetConfig(kafkaTargetConfig);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(defaultModelId);
        modelConfig.setCommonConfig(commonConfig);

        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("");
        eventMetricsConfig.setTransName("");
        modelConfig.setEventMetricsConfig(eventMetricsConfig);

        AbstractChannel channel = new LogChannel(null, null);
        channel.setUniqueKey("channel");

        KafkaSink kafkaSink = new KafkaSink(modelConfig, channel, 0);
        kafkaSink.configure(kafkaSink.getModelConfig());
        return kafkaSink;
    }

    private KafkaSink getSink_LonglingerMs() {
        KafkaTargetConfig kafkaTargetConfig = new KafkaTargetConfig();
        String bootstrap = BasicUtil.getInstance().getKafkaServerUrl();
        String propertiesStr = "serializer.class=kafka.serializer.StringEncoder,compression.codec=3,max.request.size=4194304,linger.ms=10000000";
        kafkaTargetConfig.setBootstrap(bootstrap);
        kafkaTargetConfig.setProperties(propertiesStr);
        kafkaTargetConfig.setTopic(defaultTopic);

        ModelConfig modelConfig = new ModelConfig("log2kafka");
        modelConfig.setTargetConfig(kafkaTargetConfig);

        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(defaultModelId);
        modelConfig.setCommonConfig(commonConfig);

        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("");
        eventMetricsConfig.setTransName("");
        modelConfig.setEventMetricsConfig(eventMetricsConfig);

        KafkaSink kafkaSink = new KafkaSink(modelConfig, null, 0);
        kafkaSink.configure(kafkaSink.getModelConfig());
        return kafkaSink;
    }

    @Test
    public void getTargetTopic() throws Exception {
        Class<KafkaSink> ownClass = KafkaSink.class;
        KafkaSink sink = getSink();

        Method method = ownClass.getDeclaredMethod("getTargetTopic", new Class[] { String.class });
        method.setAccessible(true);

        int maxNum = 10000000;
        String topicModel1 = "topic_[0,11]";
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < maxNum; i++) {
            Object result = method.invoke(sink, new Object[] { topicModel1 });
        }
        System.out.println("cost:" + (System.currentTimeMillis() - start1));

        String topicModel2 = "topic";
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < maxNum; i++) {
            Object result = method.invoke(sink, new Object[] { topicModel2 });
        }
        System.out.println("cost:" + (System.currentTimeMillis() - start2));
    }

    @Test
    public void getTargetTopic0() throws Exception {
        Class<KafkaSink> ownClass = KafkaSink.class;
        KafkaSink sink = getSink();

        Method method = ownClass.getDeclaredMethod("getTargetTopic", new Class[] { String.class });
        method.setAccessible(true);

        String topicModel = "topic_[0,0]";
        String topic = "topic_0";
        int maxNum = 10;
        for (int i = 0; i < maxNum; i++) {
            Object result = method.invoke(sink, new Object[] { topicModel });
            assertTrue(topic.equals(result));
        }
    }
}
