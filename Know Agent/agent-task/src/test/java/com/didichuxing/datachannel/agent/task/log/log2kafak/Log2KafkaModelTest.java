package com.didichuxing.datachannel.agent.task.log.log2kafak;

import static junit.framework.TestCase.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.didichuxing.datachannel.agent.task.log.LogModelTest;
import org.junit.Test;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-29 17:20
 */
public class Log2KafkaModelTest extends LogModelTest {

    @Test
    public void selectTargetTopic() {
        ModelConfig modelConfig = getModelConfig();
        Log2KafkaModel log2KafkaModel = new Log2KafkaModel(modelConfig);

        KafkaTargetConfig kafkaTargetConfig = new KafkaTargetConfig();

        String topics = "topic1,topic2,topic3";
        kafkaTargetConfig.setTopic(topics);

        log2KafkaModel.selectTargetTopic(kafkaTargetConfig);
        String targetTopic = kafkaTargetConfig.getTopic();
        System.out.println(targetTopic);

        Set<String> topicSet = new HashSet<String>(Arrays.asList(topics.split(",")));
        assertTrue(topicSet.contains(targetTopic));

        int time = 30;
        for (int i = 0; i < time; i++) {
            log2KafkaModel.selectTargetTopic(kafkaTargetConfig);
            String topic = kafkaTargetConfig.getTopic();
            assertTrue(topic.equals(targetTopic));
        }
    }
}
