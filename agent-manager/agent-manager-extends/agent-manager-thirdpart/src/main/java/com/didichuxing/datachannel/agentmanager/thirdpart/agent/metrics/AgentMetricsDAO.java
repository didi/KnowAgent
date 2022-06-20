package com.didichuxing.datachannel.agentmanager.thirdpart.agent.metrics;

import org.apache.kafka.clients.consumer.ConsumerRecords;

public interface AgentMetricsDAO {

    void writeMetrics(ConsumerRecords<String, String> records);

    void writeErrors(ConsumerRecords<String, String> records);

}
