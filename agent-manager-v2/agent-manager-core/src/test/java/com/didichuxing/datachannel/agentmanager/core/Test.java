package com.didichuxing.datachannel.agentmanager.core;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.vo.agent.config.AgentCollectConfiguration;

public class Test {
    public static void main(String[] args) {

        String jsonString = "{\"agentConfiguration\":{\"advancedConfigurationJsonString\":\"\",\"agentConfigurationVersion\":2,\"agentId\":1,\"agentVersion\":\"3.5.2\",\"byteLimitThreshold\":1073741824,\"cpuLimitThreshold\":5,\"errorLogsProducerConfiguration\":{\"nameServer\":\"106.52.29.81:9092\",\"properties\":\"acks=all,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer\",\"receiverId\":2,\"topic\":\"errorlog\"},\"hostName\":\"测试主机-1\",\"ip\":\"192.31.18.256\",\"metricsProducerConfiguration\":{\"nameServer\":\"106.52.29.81:9092\",\"properties\":\"acks=all,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer\",\"receiverId\":2,\"topic\":\"metric\"}},\"hostName2LogCollectTaskConfigurationMap\":{{\"hostName\":\"测试主机-1\",\"hostType\":0}:[{\"advancedConfigurationJsonString\":\"\",\"collectEndTimeBusiness\":1610434007955,\"collectStartTimeBusiness\":1610433407955,\"configurationVersion\":4,\"fileLogCollectPathList\":[{\"charset\":\"utf-8\",\"fdOffsetExpirationTimeMs\":604800000,\"fileNameSuffixMatchRuleLogicJsonString\":\"{\\\"suffixMatchType\\\":0,\\\"suffixSeparationCharacter\\\":\\\"log\\\"}\",\"logCollectTaskId\":2,\"logContentSliceRuleLogicJsonString\":\"{\\\"sliceRegular\\\":\\\"yyyy-MM-dd HH:mm:sss\\\",\\\"sliceTimestampFormat\\\":\\\"yyyy-MM-dd HH:mm:sss\\\",\\\"sliceTimestampPrefixString\\\":\\\"log\\\",\\\"sliceTimestampPrefixStringIndex\\\":0,\\\"sliceType\\\":0}\",\"maxBytesPerLogEvent\":5242880,\"path\":\"/pengzhanglong/test/test.log\",\"pathId\":23}],\"limitPriority\":2,\"logCollectTaskId\":2,\"logCollectTaskStatus\":1,\"logCollectTaskType\":0,\"logContentFilterRuleLogicJsonString\":\"{\\\"logContentFilterExpression\\\":\\\"peng&&zhang||long&&hu\\\",\\\"logContentFilterType\\\":0,\\\"needLogContentFilter\\\":1}\",\"logProducerConfiguration\":{\"nameServer\":\"106.52.29.81:9092\",\"properties\":\"acks=all,key.serializer=org.apache.kafka.common.serialization.StringSerializer,value.serializer=org.apache.kafka.common.serialization.StringSerializer\",\"receiverId\":2,\"topic\":\"log\"},\"oldDataFilterType\":2}]}}";
        AgentCollectConfiguration agentCollectConfiguration = JSON.parseObject(jsonString, AgentCollectConfiguration.class);
        System.err.println(JSON.toJSONString(agentCollectConfiguration));

    }
}
