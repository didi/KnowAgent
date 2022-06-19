package com.didichuxing.datachannel.agentmanager.remote.kafkacluster.impl;

import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
import org.springframework.stereotype.Service;

@Service
public class RemoteKafkaClusterServiceImpl implements RemoteKafkaClusterService {

    @Override
    public boolean checkTopicLimitExists(Long kafkaClusterId, String topicName) {
//        String requestUrl = "http://localhost:8180/kafka/api/v1/third-part/logi/6/kmo_test_zq/dkm_admin/throttleInfo";
//        // TODO：http://localhost:8180/swagger-ui.html#!/2432025918255092147545Logi3045620851255092147540REST41/getProjTopicThrottleInfoUsingGET
//
//        String response = HttpUtils.get(requestUrl, null);
//        Result result = JSON.parseObject(response, Result.class);
//        result.getData();
        return false;
    }

}
