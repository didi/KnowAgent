package com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Pair;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.receiver.KafkaClusterPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.Comparator;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
import com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.KafkaClusterManageServiceExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class DefaultKafkaClusterManageServiceExtensionImpl implements KafkaClusterManageServiceExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKafkaClusterManageServiceExtensionImpl.class);

    private static final Integer ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS = 6000;

    private static final String CONSUMER_GROUP_ID = "agentMetricsConsumerGroup";

    @Autowired
    private RemoteKafkaClusterService remoteKafkaClusterService;

    @Override
    public ReceiverDO kafkaClusterPO2KafkaCluster(KafkaClusterPO kafkaClusterPO) throws ServiceException {
        if (null == kafkaClusterPO) {
            throw new ServiceException(
                    String.format(
                            "class=KafkaClusterManageServiceExtensionImpl||method=kafkaClusterPO2KafkaCluster||msg={%s}",
                            "入参kafkaClusterPO对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        ReceiverDO kafkaCluster = null;
        try {
            kafkaCluster = ConvertUtil.obj2Obj(kafkaClusterPO, ReceiverDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=KafkaClusterManageServiceExtensionImpl||method=kafkaClusterPO2KafkaCluster||msg={%s}",
                            String.format("KafkaClusterPO对象={%s}转化为KafkaCluster对象失败，原因为：%s", JSON.toJSONString(kafkaClusterPO), ex.getMessage())
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if (null == kafkaCluster) {
            throw new ServiceException(
                    String.format(
                            "class=KafkaClusterManageServiceExtensionImpl||method=kafkaClusterPO2KafkaCluster||msg={%s}",
                            String.format("KafkaClusterPO对象={%s}转化为KafkaCluster对象失败", JSON.toJSONString(kafkaClusterPO))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return kafkaCluster;
    }

    @Override
    public KafkaClusterPO kafkaCluster2KafkaClusterPO(ReceiverDO kafkaCluster) throws ServiceException {
        if (null == kafkaCluster) {
            throw new ServiceException(
                    String.format(
                            "class=KafkaClusterManageServiceExtensionImpl||method=kafkaCluster2KafkaClusterPO||msg={%s}",
                            "入参kafkaCluster对象不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        KafkaClusterPO kafkaClusterPO = null;
        try {
            kafkaClusterPO = ConvertUtil.obj2Obj(kafkaCluster, KafkaClusterPO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=KafkaClusterManageServiceExtensionImpl||method=kafkaCluster2KafkaClusterPO||msg={%s}",
                            String.format("kafkaCluster对象={%s}转化为KafkaClusterPO对象失败，原因为：%s", JSON.toJSONString(kafkaCluster), ex.getMessage())
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if (null == kafkaClusterPO) {
            throw new ServiceException(
                    String.format(
                            "class=KafkaClusterManageServiceExtensionImpl||method=kafkaCluster2KafkaClusterPO||msg={%s}",
                            String.format("kafkaCluster对象={%s}转化为KafkaClusterPO对象失败", JSON.toJSONString(kafkaCluster))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return kafkaClusterPO;
    }

    @Override
    public CheckResult checkModifyParameterKafkaCluster(ReceiverDO kafkaCluster) {
        if (null == kafkaCluster) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象不可为空");
        }
        if (null == kafkaCluster.getId() || kafkaCluster.getId() <= 0) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象不可为空且必须>0");
        }
        if (StringUtils.isBlank(kafkaCluster.getKafkaClusterBrokerConfiguration())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象的属性[kafkaClusterBrokerConfiguration]值不可为空");
        }
        if (StringUtils.isBlank(kafkaCluster.getKafkaClusterName())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象的属性[kafkaClusterName]值不可为空");
        }
        return new CheckResult(true);
    }

    @Override
    public CheckResult checkCreateParameterKafkaCluster(ReceiverDO kafkaCluster) {
        if (null == kafkaCluster) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象不可为空");
        }
        if (StringUtils.isBlank(kafkaCluster.getKafkaClusterBrokerConfiguration())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象的属性[kafkaClusterBrokerConfiguration]值不可为空");
        }
        if (StringUtils.isBlank(kafkaCluster.getKafkaClusterName())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参kafkaCluster对象的属性[kafkaClusterName]值不可为空");
        }
        return new CheckResult(true);
    }

    @Override
    public Comparator<ReceiverDO, Long> getComparator() {
        return new Comparator<ReceiverDO, Long>() {
            @Override
            public Long getKey(ReceiverDO kafkaCluster) {
                return kafkaCluster.getKafkaClusterId();
            }

            @Override
            public boolean compare(ReceiverDO t1, ReceiverDO t2) {
                return t1.getKafkaClusterName().equals(t2.getKafkaClusterName()) &&
                        t1.getKafkaClusterBrokerConfiguration().equals(t2.getKafkaClusterBrokerConfiguration());
            }

            @Override
            public ReceiverDO getModified(ReceiverDO source, ReceiverDO target) {
                target.setId(source.getId());
                return target;
            }
        };
    }

    @Override
    public ReceiverDO updateKafkaCluster(ReceiverDO sourceReceiverDO, ReceiverDO targetKafkaClusterDO) throws ServiceException {
        if (!sourceReceiverDO.getKafkaClusterBrokerConfiguration().equals(targetKafkaClusterDO.getKafkaClusterBrokerConfiguration())) {
            sourceReceiverDO.setKafkaClusterBrokerConfiguration(targetKafkaClusterDO.getKafkaClusterBrokerConfiguration());
        }
        if (StringUtils.isNotBlank(targetKafkaClusterDO.getKafkaClusterProducerInitConfiguration())) {
            sourceReceiverDO.setKafkaClusterProducerInitConfiguration(targetKafkaClusterDO.getKafkaClusterProducerInitConfiguration());
        }
        if (!sourceReceiverDO.getKafkaClusterName().equals(targetKafkaClusterDO.getKafkaClusterName())) {
            sourceReceiverDO.setKafkaClusterName(targetKafkaClusterDO.getKafkaClusterName());
        }
        if (targetKafkaClusterDO.getKafkaClusterId() != null && !sourceReceiverDO.getKafkaClusterId().equals(targetKafkaClusterDO.getKafkaClusterId())) {
            sourceReceiverDO.setKafkaClusterId(targetKafkaClusterDO.getKafkaClusterId());
        }
        sourceReceiverDO.setAgentErrorLogsTopic(targetKafkaClusterDO.getAgentErrorLogsTopic());
        sourceReceiverDO.setAgentMetricsTopic(targetKafkaClusterDO.getAgentMetricsTopic());
        return sourceReceiverDO;
    }

    @Override
    public List<ReceiverDO> kafkaClusterPOList2ReceiverDOList(List<KafkaClusterPO> kafkaClusterPOList) {
        return ConvertUtil.list2List(kafkaClusterPOList, ReceiverDO.class);
    }

    @Override
    public boolean checkTopicLimitExists(ReceiverDO receiverDO, String topic) {
        Long externalKafkaClusterId = receiverDO.getKafkaClusterId();
        if (null == externalKafkaClusterId || externalKafkaClusterId <= 0) {
            LOGGER.warn(String.format("KafkaCluster={id=%d}非源于kafka-manager，仅支持源于kafka-manager的KafkaCluster限流检测", receiverDO.getId()),
                    ErrorCodeEnum.KAFKA_CLUSTER_NOT_ORIGINATED_FROM_KAFKA_MANAGER.getCode()
            );
            return false;
        }
        /*
         * 调用kafka-manager对应接口获取kafkaClusterId + sendTopic是否被限流
         */
        return remoteKafkaClusterService.checkTopicLimitExists(externalKafkaClusterId, topic);
    }

    @Override
    public List<Pair<String, Integer>> getBrokerIp2PortPairList(ReceiverDO receiverDO) {
        if (null == receiverDO) {
            throw new ServiceException("入参receiverDO对象不可为空", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        String kafkaClusterBrokerConfiguration = receiverDO.getKafkaClusterBrokerConfiguration();
        if (StringUtils.isBlank(kafkaClusterBrokerConfiguration)) {
            throw new ServiceException(
                    String.format("KafkaCluster={id=%d}对应kafkaClusterBrokerConfiguration值为空", receiverDO.getId()),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        String[] brokerConfigList = kafkaClusterBrokerConfiguration.split(CommonConstant.COMMA);
        List<Pair<String, Integer>> result = new ArrayList<>(brokerConfigList.length);
        for (String brokerConfig : brokerConfigList) {
            String[] ip2PortString = brokerConfig.split(CommonConstant.COLON);
            if (2 != ip2PortString.length) {
                throw new ServiceException(
                        String.format("KafkaCluster={id=%d}对应kafkaClusterBrokerConfiguration格式非法，其中%s含带超过1个{%s}的kafka broker对应ip&port配置", receiverDO.getId(), ip2PortString, CommonConstant.COLON),
                        ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
                );
            } else {
                String ip = ip2PortString[0];
                Integer port = Integer.valueOf(ip2PortString[1]);
                Pair<String, Integer> brokerIp2PortPair = new Pair<>(ip, port);
                result.add(brokerIp2PortPair);
            }
        }
        return result;
    }

    @Override
    public Boolean checkProducerConfigurationValid(String brokerConfiguration, String topic, String producerConfiguration) {
        KafkaProducer kafkaProducer = null;
        try {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", brokerConfiguration);
            Map<String, String> producerConfigurationMap = producerConfiguration2Map(producerConfiguration);
            properties.putAll(producerConfigurationMap);
            kafkaProducer = new KafkaProducer<>(properties);
            List<PartitionInfo> partitionInfoList = kafkaProducer.partitionsFor(topic);
            if(CollectionUtils.isNotEmpty(partitionInfoList)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            if(null != kafkaProducer) {
                kafkaProducer.close();
            }
            return false;
        } finally {
            if(null != kafkaProducer) {
                kafkaProducer.close();
            }
        }
    }

    @Override
    public Set<String> listTopics(String kafkaClusterBrokerConfiguration) {
        AdminClient adminClient = null;
        try {
            adminClient = getAdminClient(kafkaClusterBrokerConfiguration);
            ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().timeoutMs(ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            Set<String> topicSet = listTopicsResult.names().get();
            return topicSet;
        } catch (Exception ex) {
            LOGGER.error(
                    String.format(
                            "class=DefaultKafkaClusterManageServiceExtensionImpl||method=listTopics||msg=list topics from kafka{%s} failed, root cause is: %s",
                            kafkaClusterBrokerConfiguration,
                            ex.getMessage()
                    ),
                    ex
            );
            return null;
        } finally {
            if(null != adminClient) {
                adminClient.close();
            }
        }
    }

    @Override
    public Properties getKafkaConsumerProperties(String bootstrapServers) {
            Properties props = new Properties();
            props.put("bootstrap.servers", bootstrapServers);
            props.put("group.id", CONSUMER_GROUP_ID);
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            return props;
    }

    private AdminClient getAdminClient(String kafkaClusterBrokerConfiguration) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaClusterBrokerConfiguration);
        return AdminClient.create(props);
    }

    private Map<String, String> producerConfiguration2Map(String producerConfiguration) {
        Map<String, String> producerConfigurationMap = new HashMap<>();
        String[] configItemArray = producerConfiguration.split(CommonConstant.COMMA);
        if(ArrayUtils.isNotEmpty(configItemArray)) {
            for (String configItem : configItemArray) {
                String[] item = configItem.split(CommonConstant.EQUAL_SIGN);
                if(ArrayUtils.isNotEmpty(item) || item.length == 2) {
                    String key = item[0];
                    String value = item[1];
                    producerConfigurationMap.put(key, value);
                }
            }
        }
        return producerConfigurationMap;
    }

}
