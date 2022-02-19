package com.didichuxing.datachannel.agentmanager.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.metrics.*;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SimpleConsumer {

    private static final Logger LOGGER           = LoggerFactory.getLogger(SimpleConsumer.class);

    private static String topicName = "metric";
    private static final String address = "10.255.1.196:9092";

    private static String group = "d2";

    public static void print() {

        LOGGER.error(" start to print ");

        Properties props = new Properties();
        props.put("bootstrap.servers", address);
        props.put("group.id", group);
        props.put("auto.offset.reset", "latest");
        props.put("compression.type", "lz4");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topicName));

        long count = 0;
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    LOGGER.error(count + "\t" + record.value());
                    JSONObject object = JSON.parseObject(record.value());
                    Object taskMetricsObj = object.get("taskMetrics");
                    Object agentMetricsObj = object.get("agentMetrics");
                    if(taskMetricsObj != null) {
                        String taskMetricsStr = taskMetricsObj.toString();
                        TaskMetrics taskMetrics = JSON.parseObject(taskMetricsStr, TaskMetrics.class);
                        LOGGER.error(String.format("taskMetrics=%s", JSON.toJSONString(taskMetrics)));
                        MetricsLogCollectTaskPO logCollectTaskPO = ConvertUtil.obj2Obj(taskMetrics, MetricsLogCollectTaskPO.class);
                        LOGGER.error(String.format("logCollectTaskPO=%s", JSON.toJSONString(logCollectTaskPO)));
                    } else if(agentMetricsObj != null) {
                        String agentMetricsStr = agentMetricsObj.toString();
                        AgentMetrics agentMetrics = JSON.parseObject(agentMetricsStr, AgentMetrics.class);
                        LOGGER.error(String.format("agentMetrics=%s", JSON.toJSONString(agentMetrics)));
                        AgentBusinessMetrics agentBusinessMetrics = agentMetrics.getAgentBusinessMetrics();
                        SystemMetrics systemMetrics = agentMetrics.getSystemMetrics();
                        ProcessMetrics processMetrics = agentMetrics.getProcessMetrics();
                        List<DiskIOMetrics> diskIOMetricsList = agentMetrics.getDiskIOMetricsList();
                        List<DiskMetrics> diskMetricsList = agentMetrics.getDiskMetricsList();
                        List<NetCardMetrics> netCardMetrics = agentMetrics.getNetCardMetricsList();

                        MetricsAgentPO metricsAgentPO = ConvertUtil.obj2Obj(agentBusinessMetrics, MetricsAgentPO.class);
                        MetricsSystemPO metricsSystemPO = ConvertUtil.obj2Obj(systemMetrics, MetricsSystemPO.class);
                        MetricsProcessPO metricsProcessPO = ConvertUtil.obj2Obj(processMetrics, MetricsProcessPO.class);
                        List<MetricsDiskIOPO> metricsDiskIOPOS = ConvertUtil.list2List(diskIOMetricsList, MetricsDiskIOPO.class);
                        List<MetricsDiskPO> metricsDiskPOList = ConvertUtil.list2List(diskMetricsList, MetricsDiskPO.class);
                        List<MetricsNetCardPO> metricsNetCardPOList = ConvertUtil.list2List(netCardMetrics, MetricsNetCardPO.class);

                        LOGGER.error(String.format("metricsAgentPO=%s", JSON.toJSONString(metricsAgentPO)));
                        LOGGER.error(String.format("metricsSystemPO=%s", JSON.toJSONString(metricsSystemPO)));
                        LOGGER.error(String.format("metricsProcessPO=%s", JSON.toJSONString(metricsProcessPO)));
                        LOGGER.error(String.format("metricsDiskIOPOS=%s", JSON.toJSONString(metricsDiskIOPOS)));
                        LOGGER.error(String.format("metricsDiskPOList=%s", JSON.toJSONString(metricsDiskPOList)));
                        LOGGER.error(String.format("metricsNetCardPOList=%s", JSON.toJSONString(metricsNetCardPOList)));

                    }
                    LOGGER.error(" ==================== ");
//                    LOGGER.error(String.format(
//                            "type=[%d], content=[%s]", metricRecord.getType(), JSON.toJSONString(metricRecord.getData())
//                    ));
//                    LOGGER.error(" ==================== ");
//                    if(metricRecord.getType().equals(1)) {
//                        TaskMetrics taskMetrics = (TaskMetrics) metricRecord.getData();
//                    } else {
//                        AgentMetrics agentMetrics = (AgentMetrics) metricRecord.getData();
//                    }
                    count++;
                }
            } catch (Throwable ignored) {
            }
        }
    }

}
