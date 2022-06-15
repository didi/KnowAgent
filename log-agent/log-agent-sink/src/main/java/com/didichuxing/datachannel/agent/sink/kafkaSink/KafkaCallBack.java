package com.didichuxing.datachannel.agent.sink.kafkaSink;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;

/**
 * @description: kafka回调方法
 * @author: huangjw
 * @Date: 19/7/10 16:31
 */
public class KafkaCallBack implements Callback {

    private KafkaSink kafkaSink;

    private int       size;

    private Long      sendTime;

    private String    topic;

    private Long      modelId;

    private String    fileNodeKey;

    private Long      offset;

    private Long      bytes;

    public KafkaCallBack(KafkaSink kafkaSink, int size, long bytes, Long sendTime, String topic,
                         Long modelId, String fileNodeKey, Long offset) {
        this.kafkaSink = kafkaSink;
        this.size = size;
        this.sendTime = sendTime;
        this.topic = topic;
        this.modelId = modelId;
        this.fileNodeKey = fileNodeKey;
        this.offset = offset;
        this.bytes = bytes;
    }

    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (metadata == null || exception != null) {
            LogGather.recordErrorLog("KafkaCallBack error",
                "send message to kafka error! metadata is " + metadata + ",and exception is "
                        + exception + ",topic is " + topic + ",modelId is " + modelId, exception);
            kafkaSink.appendFaildOffset(fileNodeKey, offset);
        } else {
            // 记录metrics
            if (kafkaSink.getTaskPatternStatistics() != null) {
                Long cost = TimeUtils.getNanoTime() - sendTime;
                kafkaSink.getTaskPatternStatistics().sinkMutilRecord(size, bytes, cost);
                kafkaSink.getAgentStatistics().sinkMutilRecord(size, bytes, cost);
            }
        }
    }
}
