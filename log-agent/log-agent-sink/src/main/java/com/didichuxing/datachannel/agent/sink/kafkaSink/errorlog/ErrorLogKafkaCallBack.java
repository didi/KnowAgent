package com.didichuxing.datachannel.agent.sink.kafkaSink.errorlog;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.metrics.source.AgentStatistics;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: kafka回调方法
 * @author: huangjw
 * @Date: 19/7/10 16:31
 */
public class ErrorLogKafkaCallBack implements Callback {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogKafkaCallBack.class
                                           .getName());

    private String              topic;

    public ErrorLogKafkaCallBack(String topic) {
        this.topic = topic;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (metadata == null || exception != null) {
            // 记录metrics
            AgentStatistics agentStatistics = GlobalProperties.getAgentStatistics();
            if (null != agentStatistics) {
                agentStatistics.sendErrorLogsFailedRecord();
            } else {
                LOGGER
                    .error(String
                        .format(
                            "ErrorLogKafkaCallBack error, send error log message to kafka error! metadata is %s, topic is %s, because agentStatistics is null",
                            JSON.toJSONString(metadata), topic));
            }
            LOGGER
                .error(
                    String
                        .format(
                            "ErrorLogKafkaCallBack error, send error log message to kafka error! metadata is %s, and exception is %s, topic is %s",
                            JSON.toJSONString(metadata), exception.getMessage(), topic), exception);
        }
    }
}
