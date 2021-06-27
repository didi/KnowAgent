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
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.KafkaProducerSecurity;
import com.didichuxing.datachannel.agentmanager.remote.kafkacluster.RemoteKafkaClusterService;
import com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.KafkaClusterManageServiceExtension;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class DefaultKafkaClusterManageServiceExtensionImpl implements KafkaClusterManageServiceExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKafkaClusterManageServiceExtensionImpl.class);

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
        return sourceReceiverDO;
    }

    @Override
    public List<ReceiverDO> kafkaClusterPOList2ReceiverDOList(List<KafkaClusterPO> kafkaClusterPOList) {
        return ConvertUtil.list2List(kafkaClusterPOList, ReceiverDO.class);
    }

    /**
     * 根据kafkaClusterProducerInitConfiguration获取appId
     *
     * @param kafkaClusterProducerInitConfiguration KafkaCluster对象对应kafkaClusterProducerInitConfiguration属性值
     * @return 返回根据kafkaClusterProducerInitConfiguration获取到的appId
     * <p>
     * TODO：
     */
    private KafkaProducerSecurity getKafkaProducerSecurityByKafkaClusterProducerInitConfiguration(String kafkaClusterProducerInitConfiguration) {
        if (StringUtils.isBlank(kafkaClusterProducerInitConfiguration)) {
            throw new ServiceException(
                    "kafkaClusterProducerInitConfiguration不可为空",
                    ErrorCodeEnum.KAFKA_CLUSTER_PRODUCER_INIT_CONFIGURATION_IS_NULL.getCode()
            );
        }
        String clusterId = "";
        String appId = "";
        String password = "";
        String[] configs = kafkaClusterProducerInitConfiguration.split(",");
        for (String config : configs) {
            if (config.startsWith("sasl.jaas.config")) {
                Pattern pattern = Pattern.compile("username=\"(.*?)\\.(.*?)\"");
                Matcher matcher = pattern.matcher(config);
                if (matcher.find()) {
                    clusterId = matcher.group(1);
                    appId = matcher.group(2);
                }
                Pattern pattern1 = Pattern.compile("password=\"(.*)\";");
                Matcher matcher1 = pattern1.matcher(config);
                if (matcher1.find()) {
                    password = matcher1.group(1);
                }
                break;
            }
        }
        KafkaProducerSecurity kafkaProducerSecurity = new KafkaProducerSecurity();
        kafkaProducerSecurity.setAppId(appId);
        kafkaProducerSecurity.setPassword(password);
        kafkaProducerSecurity.setClusterId(clusterId);
        return kafkaProducerSecurity;
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
        return remoteKafkaClusterService.checkTopicLimitExists(externalKafkaClusterId, topic, getKafkaProducerSecurityByKafkaClusterProducerInitConfiguration(receiverDO.getKafkaClusterProducerInitConfiguration()));
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

}
