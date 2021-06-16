package com.didichuxing.datachannel.agentmanager.remote.kafkacluster.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ResultTypeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.KafkaProducerSecurity;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RemoteKafkaClusterServiceImpl implements RemoteKafkaClusterService {

    /**
     * 远程请求kafkacluster信息对应请求 url
     */
    @Value("${metadata.sync.request.kafkacluster.url}")
    private String requestUrl;

    @Override
    public List<ReceiverDO> getKafkaClustersFromRemote() {
        if (StringUtils.isBlank(requestUrl)) {//未配置requestUrl，表示未开启服务节点同步功能
            throw new ServiceException(
                    "未配置请求kafka-manager侧KafkaCluster信息requestUrl，请在配置文件配置metadata.sync.request.kafkacluster.url",
                    ErrorCodeEnum.KAFKA_CLUSTER_REMOTE_REQUEST_URL_NOT_CONFIG.getCode()
            );
        }
        String response = HttpUtils.get(requestUrl, null);
        KafkaClusterResponseResult kafkaClusterResponseResultListResult = JSON.parseObject(response, KafkaClusterResponseResult.class);
        if (null != kafkaClusterResponseResultListResult && kafkaClusterResponseResultListResult.getCode().equals(ResultTypeEnum.SUCCESS.getCode())) {
            if (CollectionUtils.isNotEmpty(kafkaClusterResponseResultListResult.getData())) {
                List<ReceiverDO> receiverDOList = new ArrayList<ReceiverDO>(kafkaClusterResponseResultListResult.getData().size());
                for (KafkaCluster kafkaClusterResponseResult : kafkaClusterResponseResultListResult.getData()) {
                    ReceiverDO receiverDO = new ReceiverDO();
                    receiverDO.setKafkaClusterBrokerConfiguration(kafkaClusterResponseResult.getBootstrapServers());
                    receiverDO.setKafkaClusterName(kafkaClusterResponseResult.getClusterName());
                    receiverDO.setKafkaClusterId(kafkaClusterResponseResult.getClusterId());
                    receiverDOList.add(receiverDO);
                }
                return receiverDOList;
            } else {
                return new ArrayList<>();
            }
        } else {
            throw new ServiceException(
                    String.format("请求kafka-manager侧kafka cluster信息失败, requestUrl={%s}, err={%s}", requestUrl, kafkaClusterResponseResultListResult.getMessage()),
                    ErrorCodeEnum.KAFKA_CLUSTER_REMOTE_REQUEST_FAILED.getCode()
            );
        }

    }

    @Override
    public Set<String> getTopicsByKafkaClusterId(Long kafkaClusterId) {

        return new HashSet<>();

    }

    @Override
    public boolean checkTopicLimitExists(Long kafkaClusterId, String topicName, KafkaProducerSecurity kafkaProducerSecurity) {
//        String requestUrl = "http://10.96.81.201:8180/kafka/api/v1/third-part/logi/6/kmo_test_zq/dkm_admin/throttleInfo";
//
//        // TODO：http://10.96.81.201:8180/swagger-ui.html#!/2432025918255092147545Logi3045620851255092147540REST41/getProjTopicThrottleInfoUsingGET
//
//        String response = HttpUtils.get(requestUrl, null);
//        Result result = JSON.parseObject(response, Result.class);
//        result.getData();
        return false;
    }

    class KafkaClusterResponseResult {

        private List<KafkaCluster> data;
        private String message;
        private String tips;
        private Integer code;

        public List<KafkaCluster> getData() {
            return data;
        }

        public void setData(List<KafkaCluster> data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }
    }

    class KafkaCluster {

        private Long clusterId;
        private String clusterName;
        private String bootstrapServers;

        public Long getClusterId() {
            return clusterId;
        }

        public void setClusterId(Long clusterId) {
            this.clusterId = clusterId;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
        }

        public String getBootstrapServers() {
            return bootstrapServers;
        }

        public void setBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }
    }

}
