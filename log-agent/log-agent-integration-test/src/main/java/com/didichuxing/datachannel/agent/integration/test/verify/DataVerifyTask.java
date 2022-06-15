package com.didichuxing.datachannel.agent.integration.test.verify;

import java.util.List;

import com.didichuxing.datachannel.agent.integration.test.basic.BasicUtil;
import com.didichuxing.datachannel.agent.integration.test.format.LogEventFormat;
import com.didichuxing.datachannel.agent.integration.test.utils.Md5Util;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.didichuxing.datachannel.agent.integration.test.datasource.BasicDataSource;
import com.didichuxing.datachannel.agent.integration.test.format.Format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 数据校验流程
 * @author: huangjw
 * @Date: 19/2/12 17:31
 */
public class DataVerifyTask implements Runnable {

    private static final Logger LOGGER      = LoggerFactory.getLogger(DataVerifyTask.class
                                                .getName());
    private BasicUtil           basicUtil   = BasicUtil.getInstance();
    private DataVerifyConfig    config;

    // 60秒未消费到数据，直接退出
    private int                 maxNoRecord = 60;

    public DataVerifyTask(DataVerifyConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        BasicDataSource basicDataSource = config.getBasicDataSource();
        Format format = config.getFormat();
        String topic = config.getTopic();

        int num = 0;
        while (num < maxNoRecord) {
            ConsumerRecords<String, String> records = basicUtil.getNextMessageFromConsumer(topic);
            if (records != null && !records.isEmpty()) {
                for (ConsumerRecord<String, String> record : records) {
                    if (record != null && StringUtils.isNotBlank(record.value())) {
                        String message = record.value() + System.lineSeparator();
                        LOGGER.info("consumer topic[" + topic + "]:" + message);
                        Object object = format.unFormat(message);
                        if (format instanceof LogEventFormat) {
                            List<String> result = (List<String>) object;
                            for (String item : result) {
                                String md5 = Md5Util.getMd5(item);
                                if (basicDataSource.getMap().containsKey(md5)) {
                                    basicDataSource.getMap().remove(md5);
                                }
                            }
                        } else {
                            String md5 = Md5Util.getMd5((String) object);
                            if (basicDataSource.getMap().containsKey(md5)) {
                                basicDataSource.getMap().remove(md5);
                            }
                        }
                    }
                }
                num = 0;
            } else {
                num++;
                if (num > maxNoRecord / 2) {
                    LOGGER.info("topic[" + topic + "] has no any data for " + num + " times!");
                }
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    LOGGER.error("sleep is interrupted.", e);
                }
            }
        }

        // 判断basicDataSource下的map里的内容是否全部校验通过了
        LOGGER.info("check " + basicDataSource.getClass().getName() + " end. topic is " + topic
                    + ", result is " + (basicDataSource.getMap().size() == 0));
    }
}
