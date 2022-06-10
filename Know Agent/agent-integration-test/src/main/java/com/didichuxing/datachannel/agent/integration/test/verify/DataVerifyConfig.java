package com.didichuxing.datachannel.agent.integration.test.verify;

import com.didichuxing.datachannel.agent.integration.test.beans.DataTransEnum;
import com.didichuxing.datachannel.agent.integration.test.format.Format;
import com.didichuxing.datachannel.agent.integration.test.datasource.BasicDataSource;

/**
 * @description: 校验相关配置
 * @author: huangjw
 * @Date: 19/2/13 10:56
 */
public class DataVerifyConfig {

    // 日志源
    BasicDataSource basicDataSource;

    // 格式化
    Format          format;

    // topic
    String          topic;

    // 发送类型
    DataTransEnum   dataTransEnum;

    public DataVerifyConfig(BasicDataSource dataSource, Format format, String topic,
                            DataTransEnum dataTransEnum) {
        this.basicDataSource = dataSource;
        this.format = format;
        this.topic = topic;
        this.dataTransEnum = dataTransEnum;
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public void setBasicDataSource(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public DataTransEnum getDataTransEnum() {
        return dataTransEnum;
    }

    public void setDataTransEnum(DataTransEnum dataTransEnum) {
        this.dataTransEnum = dataTransEnum;
    }
}
