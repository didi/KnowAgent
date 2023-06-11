package com.didichuxing.datachannel.agent.integration.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.integration.test.basic.BasicUtil;
import com.didichuxing.datachannel.agent.integration.test.beans.DataTransEnum;
import com.didichuxing.datachannel.agent.integration.test.datasource.DailyFiveMinDataSource;
import com.didichuxing.datachannel.agent.integration.test.datasource.FileSize10MBDataSouce;
import com.didichuxing.datachannel.agent.integration.test.format.Format;
import com.didichuxing.datachannel.agent.integration.test.format.LogEventFormat;
import com.didichuxing.datachannel.agent.integration.test.format.OriginalFormat;
import com.didichuxing.datachannel.agent.integration.test.verify.DataVerifyConfig;
import com.didichuxing.datachannel.agent.integration.test.verify.DataVerifyTask;
import com.didichuxing.datachannel.agent.integration.test.verify.VerifyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 集成测试入口
 * @author: huangjw
 * @Date: 19/2/11 14:47
 */
public class IntegrationTest {

    private static final Logger LOGGER         = LoggerFactory.getLogger(IntegrationTest.class
                                                   .getName());
    private static final int    partitions     = 1;

    private static final String configFileName = "verify.properties";

    public static void main(String[] args) {
        try {
            init();

            start();
        } catch (Exception e) {
            LOGGER.error("IntegrationTest run error!", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                destroy();
            }
        });
    }

    public static void start() {
        LOGGER.info("start to verify.");
        // 构建校验配置
        List<DataVerifyConfig> configs = buildDataVerifyConfigs();
        if (configs == null) {
            LOGGER.warn("config is null.");
        }

        // 生成日志
        for (DataVerifyConfig config : configs) {
            new Thread(config.getBasicDataSource()).start();
        }

        // 启动订阅关系，校验数据完整性性
        List<String> topics = new ArrayList<>();
        for (DataVerifyConfig config : configs) {
            // 创建topic
            BasicUtil.getInstance().createTopic(config.getTopic(), partitions);
            // 添加订阅
            topics.add(config.getTopic());
        }
        AgentConfig logConfig = new AgentConfig();
        // 只创建，不消费
//        BasicUtil.getInstance().createTopic(logConfig.getErrorLogConfig().getTopic(), partitions);
//        BasicUtil.getInstance().createTopic(logConfig.getMetricConfig().getTopic(), partitions);

//        BasicUtil.getInstance().initTopicList(topics);

        // 启动logAgent
        // Controller.getInstance().start();

        // 启动校验
        for (DataVerifyConfig config : configs) {
            DataVerifyTask task = new DataVerifyTask(config);
            new Thread(task).start();
        }
        LOGGER.info("start completed");
    }

    private static List<DataVerifyConfig> buildDataVerifyConfigs() {
        List<DataVerifyConfig> dataVerifyConfigs = new ArrayList<>();
        String config = readConfig();
        List<VerifyConfig> verifyConfigs = null;
        try {
            verifyConfigs = JSON.parseArray(config, VerifyConfig.class);
        } catch (Exception e) {
            LOGGER.error("parse config string to object error.config is " + config, e);
            return null;
        }
        for (VerifyConfig vConfig : verifyConfigs) {
            String classType = vConfig.getType();

            switch (classType) {
                case "DailyFiveMinDataSource":
                    DailyFiveMinDataSource dataSource1 = new DailyFiveMinDataSource(vConfig.getSourcePath(),
                                                                                    vConfig.getTargetPath());
                    if (vConfig.getLogNum() != 0) {
                        dataSource1.setMaxNum(vConfig.getLogNum());
                    }
                    dataVerifyConfigs.add(new DataVerifyConfig(dataSource1, getFormat(vConfig), vConfig.getName(),
                                                               getDataTransEnum(vConfig)));
                    break;
                case "FileSize10MBDataSouce":
                    FileSize10MBDataSouce dataSource2 = new FileSize10MBDataSouce(vConfig.getSourcePath(),
                                                                                  vConfig.getTargetPath());
                    if (vConfig.getLogNum() != 0) {
                        dataSource2.setMaxNum(vConfig.getLogNum());
                    }
                    dataVerifyConfigs.add(new DataVerifyConfig(dataSource2, getFormat(vConfig), vConfig.getName(),
                                                               getDataTransEnum(vConfig)));
                    break;
                default:
                    break;
            }
        }
        return dataVerifyConfigs;
    }

    public static void init() {
        // 启动前先停止
        // destory();
        // 启动kafka
        BasicUtil.getInstance().prepare();

        LOGGER.info("init completed.");
    }

    public static void destroy() {
        BasicUtil.getInstance().tearDown();
        LOGGER.info("destroy completed");
    }

    private static DataTransEnum getDataTransEnum(VerifyConfig config) {
        int dataTransEnumNum = config.getTransType();
        for (DataTransEnum item : DataTransEnum.values()) {
            if (item.getTag() == dataTransEnumNum) {
                return item;
            }
        }
        return DataTransEnum.MQLIST;
    }

    private static Format getFormat(VerifyConfig config) {
        int formatId = config.getFormat();
        Format result = null;
        switch (formatId) {
            case 0:
                result = new LogEventFormat();
                break;
            case 1:
                result = new OriginalFormat();
                break;
            default:
                result = new LogEventFormat();
                break;
        }
        return result;
    }

    /**
     * 读配置文件
     *
     * @return
     */
    private static String readConfig() {
        // BufferedReader是可以按行读取文件
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Thread
            .currentThread().getContextClassLoader().getResourceAsStream(configFileName)));

        StringBuilder config = new StringBuilder();
        String str = null;
        try {
            while ((str = bufferedReader.readLine()) != null) {
                config.append(str);
            }
            return config.toString();
        } catch (Exception e) {
            LOGGER.error("read config error.", e);
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e1) {

            }
        }
        return null;
    }
}
