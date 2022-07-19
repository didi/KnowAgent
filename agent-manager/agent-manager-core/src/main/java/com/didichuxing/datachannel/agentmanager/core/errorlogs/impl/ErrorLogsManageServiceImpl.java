package com.didichuxing.datachannel.agentmanager.core.errorlogs.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver.ReceiverDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.ErrorLogPO;
import com.didichuxing.datachannel.agentmanager.common.util.DateUtils;
import com.didichuxing.datachannel.agentmanager.core.errorlogs.ErrorLogsManageService;
import com.didichuxing.datachannel.agentmanager.core.kafkacluster.KafkaClusterManageService;
import com.didichuxing.datachannel.agentmanager.persistence.AgentErrorLogDAO;
import com.didichuxing.datachannel.agentmanager.thirdpart.kafkacluster.extension.KafkaClusterManageServiceExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
public class ErrorLogsManageServiceImpl implements ErrorLogsManageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogsManageServiceImpl.class);

    @Autowired
    @Qualifier(value = "agentErrorLogDAO")
    private AgentErrorLogDAO agentErrorLogDAO;

    @Autowired
    private KafkaClusterManageService kafkaClusterManageService;

    @Autowired
    private KafkaClusterManageServiceExtension kafkaClusterManageServiceExtension;

    /* metrics write 相关 */
    private volatile boolean errorLogsWriteStopTrigger = false;
    private volatile boolean errorLogsWriteStopped = true;
    private static final Long RECEIVER_CLOSE_TIME_OUT_MS = 1 * 60 * 1000l;
    private ReceiverDO lastAgentErrorLogsReceiver = null;
    private static final ExecutorService errorLogsWriteThreadPool = Executors.newSingleThreadExecutor();

    @Override
    @Transactional
    public void insertErrorLogs(String errorLogRecord) {
        handleInsertErrorLogs(errorLogRecord);
    }

    private void handleInsertErrorLogs(String errorLogRecord) {
        if(StringUtils.isNotBlank(errorLogRecord)) {
            ErrorLogPO errorLogPO = JSON.parseObject(errorLogRecord, ErrorLogPO.class);
            if(null != errorLogPO) {
                processErrorLogPOFieldTooLarge(errorLogPO);
                agentErrorLogDAO.insertSelective(errorLogPO);
            }
        }
    }

    private void processErrorLogPOFieldTooLarge(ErrorLogPO errorLogPO) {
        if(errorLogPO.getThrowable().length() > 2000) {
            errorLogPO.setThrowable(errorLogPO.getThrowable().substring(0, 2000));
        }
        if(errorLogPO.getLogMsg().length() > 255) {
            errorLogPO.setLogMsg(errorLogPO.getLogMsg().substring(0, 255));
        }
    }

    @Override
    public void consumeAndWriteErrorLogs() {
        /*
         * 1.）获取 agent metrics & error logs 对应接收端信息、topic
         */
        ReceiverDO agentErrorLogsReceiver  = kafkaClusterManageService.getAgentErrorLogsTopicExistsReceiver();
        /*
         * 2.）校验较上一次获取是否相同，如不同，则立即进行对应变更处理
         */
        if(errorLogsReceiverChanged(lastAgentErrorLogsReceiver, agentErrorLogsReceiver)) {
            LOGGER.info(
                    String.format("ErrorLogs receiver changed, before is %s, after is %s", JSON.toJSONString(lastAgentErrorLogsReceiver), JSON.toJSONString(agentErrorLogsReceiver))
            );
            restartWriteErrorLogs(agentErrorLogsReceiver);
            lastAgentErrorLogsReceiver = agentErrorLogsReceiver;
        }
    }

    private void restartWriteErrorLogs(ReceiverDO agentErrorLogsReceiver) {
        LOGGER.info(
                String.format("restartWriteErrorLogs: Is going to stop receiver %s", JSON.toJSONString(lastAgentErrorLogsReceiver))
        );
        /*
         * stop
         */
        errorLogsWriteStopTrigger = true;
        Long currentTime = System.currentTimeMillis();
        while (
                !errorLogsWriteStopped &&
                        (System.currentTimeMillis() - currentTime) <= RECEIVER_CLOSE_TIME_OUT_MS
        ) {
            try {
                // 等待现有的kafka consumer线程全部关闭
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                LOGGER.error("thread interrupted", e);
            }
        }
        LOGGER.info(
                String.format("restartWriteErrorLogs: Stop receiver %s successful", JSON.toJSONString(lastAgentErrorLogsReceiver))
        );
        LOGGER.info(
                String.format("restartWriteErrorLogs: Is going to start receiver %s", JSON.toJSONString(agentErrorLogsReceiver))
        );
        /*
         * start
         */
        errorLogsWriteStopped = false;
        errorLogsWriteStopTrigger = false;
        errorLogsWriteThreadPool.execute(() -> writeErrorLogs(agentErrorLogsReceiver.getAgentErrorLogsTopic(), agentErrorLogsReceiver.getKafkaClusterBrokerConfiguration()));
        LOGGER.info(
                String.format("restartWriteErrorLogs: Start receiver %s successful", JSON.toJSONString(agentErrorLogsReceiver))
        );
    }

    public void writeErrorLogs(String agentErrorLogsTopic, String kafkaClusterBrokerConfiguration) {
        try {
            LOGGER.info("Thread: {}, cluster: {}, topic: {}", Thread.currentThread().getName(), kafkaClusterBrokerConfiguration, agentErrorLogsTopic);
            Properties properties = kafkaClusterManageServiceExtension.getKafkaConsumerProperties(kafkaClusterBrokerConfiguration);
            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Arrays.asList(agentErrorLogsTopic));
            while (true) {
                ConsumerRecords<String, String> records = null;
                try {
                    records = consumer.poll(Duration.ofSeconds(5));
                } catch (Throwable ex) {
                    LOGGER.warn(
                            String.format("class=%s||method=%s||errorMsg=consumer poll records error, root cause is: %s", this.getClass().getName(), "writeErrorLogs", ex.getMessage()),
                            ex
                    );
                    consumer.close();
                    consumer = new KafkaConsumer<>(properties);
                }
                try {
                    if(null != records) {
                        for (ConsumerRecord<String, String> record : records) {
                            insertErrorLogs(record.value());
                        }
                    }
                } catch (Throwable ex) {
                    LOGGER.warn(
                            String.format("class=%s||method=%s||errorMsg=write errorLogs to store failed, root cause is: %s", this.getClass().getName(), "writeErrorLogs", ex.getMessage()),
                            ex
                    );
                }
                try {
                    if (errorLogsWriteStopTrigger) {
                        consumer.close();
                        break;
                    }
                } catch (Throwable ex) {
                    LOGGER.error(
                            String.format("class=%s||method=%s||errorMsg=close consumer failed, root cause is: %s", this.getClass().getName(), "writeErrorLogs", ex.getMessage()),
                            ex
                    );
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(
                    String.format("class=%s||method=%s||errorMsg=writeErrorLogs error, root cause is: %s", this.getClass().getName(), "writeErrorLogs", ex.getMessage()),
                    ex
            );
        } finally {
            errorLogsWriteStopped = true;
        }
    }

    private boolean errorLogsReceiverChanged(ReceiverDO lastAgentErrorLogsReceiver, ReceiverDO agentErrorLogsReceiver) {
        if(null == lastAgentErrorLogsReceiver && null == agentErrorLogsReceiver) {
            return false;
        }
        if(null == agentErrorLogsReceiver) {
            return false;
        }
        if(null == lastAgentErrorLogsReceiver && null != agentErrorLogsReceiver) {
            return true;
        }
        if(
                !lastAgentErrorLogsReceiver.getAgentErrorLogsTopic().equals(agentErrorLogsReceiver.getAgentErrorLogsTopic()) ||
                        !lastAgentErrorLogsReceiver.getKafkaClusterBrokerConfiguration().equals(agentErrorLogsReceiver.getKafkaClusterBrokerConfiguration())
        ) {
            return true;
        }
        return false;
    }

    @Override
    public List<String> getErrorLogs(String hostName, Long startTime, Long endTime) {
        return agentErrorLogDAO.getErrorLogs(hostName, startTime, endTime);
    }

    @Override
    @Transactional
    public void clearExpireErrorLogs(Integer metricsExpireDays) {
        Long heartBeatTime = DateUtils.getBeforeDays(new Date(), metricsExpireDays).getTime();
        agentErrorLogDAO.deleteBeforeTime(heartBeatTime);
    }

}
