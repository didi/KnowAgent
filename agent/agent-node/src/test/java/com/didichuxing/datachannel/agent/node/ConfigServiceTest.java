package com.didichuxing.datachannel.agent.node;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.ErrorLogConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.LimitConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.MetricConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.OffsetConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ChannelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.CommonConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.EventMetricsConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelLimitConfig;
import com.didichuxing.datachannel.agent.common.constants.Tags;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agent.common.beans.LogPath;
import com.didichuxing.datachannel.agent.common.configs.v1.LogConfig;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.node.service.http.client.HttpClient;
import com.didichuxing.datachannel.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.agent.source.log.config.MatchConfig;

/**
 * @description:
 * @author: huangjw
 * @Date: 18/7/23 15:33
 */
public class ConfigServiceTest {

    private String bootstraps  = "";
    private String errTopic    = "log-agent-err";
    private String metricTopic = "log-agent-metrics";
    private String logTopic    = "log-agent-test";

    protected long time        = 0;

    @Test
    public void writeLocalConfig() throws Exception {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setVersion(0);
        agentConfig.setHostname(CommonUtils.getHOSTNAME());
        agentConfig.setErrorLogConfig(getErrLogConfig());
        agentConfig.setMetricConfig(getMetricConfig());
        agentConfig.setLimitConfig(getLimitConfig());
        agentConfig.setOffsetConfig(getOffsetConfig());

        List<ModelConfig> list = new ArrayList<>();
        list.add(getKafkaModelConfig());
        agentConfig.setModelConfigs(list);
        ConfigService configService = new ConfigService(null);
        configService.writeLocalConfig(agentConfig);

        String localFile = System.getProperty("user.home") + File.separator + "new.conf.local";
        File file = new File(localFile);
        assertTrue(file.exists() && file.length() > 0);
    }

    protected ErrorLogConfig getErrLogConfig() {
        ErrorLogConfig errorLogConfig = new ErrorLogConfig();
        errorLogConfig.setNameServer(bootstraps);
        errorLogConfig.setTopic(errTopic);
        return errorLogConfig;
    }

    protected MetricConfig getMetricConfig() {
        MetricConfig metricConfig = new MetricConfig();
        metricConfig.setNameServer(bootstraps);
        metricConfig.setTopic(metricTopic);
        return metricConfig;
    }

    protected LimitConfig getLimitConfig() {
        LimitConfig limitConfig = new LimitConfig();
        limitConfig.setCpuThreshold(200.0f);
        limitConfig.setStartThreshold(10000);
        limitConfig.setMinThreshold(10000);
        return limitConfig;
    }

    protected OffsetConfig getOffsetConfig() {
        OffsetConfig offsetConfig = new OffsetConfig();
        return offsetConfig;
    }

    protected ModelConfig getKafkaModelConfig() {
        ModelConfig modelConfig = new ModelConfig(Tags.TASK_LOG2KAFKA);

        EventMetricsConfig eventMetricsConfig = getEventMetricsConfig();
        CommonConfig commonConfig = getCommonConfig(time, "log-kafka-task");
        ModelLimitConfig modelLimitConfig = new ModelLimitConfig();
        ChannelConfig channelConfig = new ChannelConfig();

        LogPath logPath0 = new LogPath(time, time, "/home/xiaoju/kafka/test" + time + ".log");
        LogPath logPath1 = new LogPath(time, time + 1, "/home/xiaoju/kafka/test" + (time + 1) + ".log");
        List<LogPath> list = new ArrayList<>();
        list.add(logPath0);
        list.add(logPath1);
        LogSourceConfig logSourceConfig = new LogSourceConfig();
        logSourceConfig.setLogPaths(list);
        logSourceConfig.setMatchConfig(new MatchConfig());

        KafkaTargetConfig targetConfig = new KafkaTargetConfig();
        targetConfig.setTopic("topic");
        targetConfig.setClusterId(-1);
        targetConfig.setBootstrap("bootstrap");
        targetConfig.setProperties("properties");

        modelConfig.setEventMetricsConfig(eventMetricsConfig);
        modelConfig.setCommonConfig(commonConfig);
        modelConfig.setModelLimitConfig(modelLimitConfig);
        modelConfig.setChannelConfig(channelConfig);
        modelConfig.setSourceConfig(logSourceConfig);
        modelConfig.setTargetConfig(targetConfig);
        time++;

        return modelConfig;
    }

    EventMetricsConfig getEventMetricsConfig() {
        EventMetricsConfig eventMetricsConfig = new EventMetricsConfig();
        eventMetricsConfig.setOdinLeaf("hna");
        eventMetricsConfig.setOriginalAppName("originalAppName");
        eventMetricsConfig.setTransName("transName");
        return eventMetricsConfig;
    }

    CommonConfig getCommonConfig(Long id, String name) {
        CommonConfig commonConfig = new CommonConfig();
        commonConfig.setModelId(id);
        commonConfig.setModelName(name);
        return commonConfig;
    }

    @Test
    public void readLocalConfig() throws Exception {
        ConfigService configService = new ConfigService(null);
        assertTrue(configService.getLocalConfig() != null);
    }

    @Test
    public void getConfig() throws Exception {
        ConfigService configService = new ConfigService(null);
        configService.getParam().put("hostname", "test-05");
        AgentConfig config = configService.getAgentConfig();
        assertTrue(config != null);
    }

    @Test
    public void getConfig2() throws Exception {
        ConfigService configService = new ConfigService(null);
        CommonUtils.setHOSTNAME("agent-manager-pre-sf-3e0ca-0.docker.ys");
        AgentConfig c1 = configService.getAgentConfig();
        configService.curAgentConfig = c1;
        CommonUtils.setHOSTNAME("");
        AgentConfig c2 = configService.getAgentConfig();
        if (c2 != null) {
            configService.curAgentConfig = c2;
        }
        CommonUtils.setHOSTNAME("");
        AgentConfig c3 = configService.getAgentConfig();
        if (c3 != null) {
            configService.curAgentConfig = c3;
        }

        assertTrue(configService.curAgentConfig.getHostname().equals(""));

        for (int i = 0; i < 10; i++) {
            CommonUtils.setHOSTNAME("");
            AgentConfig c4 = configService.getAgentConfig();
            if (c4 != null) {
                configService.curAgentConfig = c4;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(""));

        for (int i = 0; i < 10; i++) {
            CommonUtils.setHOSTNAME("");
            AgentConfig c5 = configService.getAgentConfig();
            if (c5 != null) {
                configService.curAgentConfig = c5;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(""));

        for (int i = 0; i < 3; i++) {
            CommonUtils.setHOSTNAME("");
            AgentConfig c6 = configService.getAgentConfig();
            if (c6 != null) {
                configService.curAgentConfig = c6;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(""));
    }

    @Test
    public void getConfig3() throws Exception {
        ConfigService configService = new ConfigService(null);
        CommonUtils.setHOSTNAME("");
        AgentConfig c1 = configService.getAgentConfig();
        configService.curAgentConfig = c1;

        for (int i = 0; i < 6; i++) {
            CommonUtils.setHOSTNAME("");
            AgentConfig c6 = configService.getAgentConfig();
            if (c6 != null) {
                configService.curAgentConfig = c6;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(""));
    }

    @Ignore
    @Test
    public void getRemoteConfig() throws Exception {
        String managerIp = "100.69.238.11";
        Integer managerPort = 8000;
        String managerUrl = "/agent-manager-gz/api/v1/agents/config/host";
        Map<String, String> param = new HashMap<>();
        param.put("ip", CommonUtils.getHOSTIP());
        param.put("hostName", "");

        String rootStr = HttpClient.get(managerIp, managerPort, managerUrl, null);

        AgentConfig logConfig = null;
        try {
            if (StringUtils.isNotBlank(rootStr)) {
                JSONObject jsonObject = JSONObject.parseObject(rootStr);
                String config = jsonObject.getString("data");
                if (StringUtils.isNotBlank(config) && !"{}".equals(config)) {
                    logConfig = JSONObject.parseObject(config, AgentConfig.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logConfig = null;
        }
        System.out.println(logConfig);
    }

    @Test
    public void parseTest() {
        String input = "";
        // ModelConfig logConfig = null;
        try {
            if (StringUtils.isNotBlank(input)) {
                JSONObject jsonObject = JSONObject.parseObject(input);
                String config = jsonObject.getString("data");
                if (StringUtils.isNotBlank(config) && !"{}".equals(config)) {
                    JSONArray jsonArray = JSONObject.parseObject(input).getJSONObject("data")
                        .getJSONArray("modelConfigs");

                    JSONObject modelItem = jsonArray.getJSONObject(0);
                    ModelConfig modelConfig = JSONObject.parseObject(modelItem.toString(),
                        ModelConfig.class);
                    System.out.println(modelConfig);
                    String sourceTag = null;
                    String targetTag = null;
                    if (modelConfig.getTag().equals(Tags.TASK_LOG2HDFS)) {
                        sourceTag = Tags.SOURCE_LOG;
                        targetTag = Tags.TARGET_HDFS;
                    } else if (modelConfig.getTag().equals(Tags.TASK_LOG2KAFKA)) {
                        sourceTag = Tags.SOURCE_LOG;
                        targetTag = Tags.TARGET_KAFKA;
                    }

                    CommonConfig commonConfig = JSONObject.parseObject(modelItem
                        .get("commonConfig").toString(), CommonConfig.class);
                    System.out.println(commonConfig);

                    EventMetricsConfig eventMetricsConfig = JSONObject.parseObject(
                        modelItem.get("eventMetricsConfig").toString(), EventMetricsConfig.class);
                    System.out.println(eventMetricsConfig);

                    ModelLimitConfig modelLimitConfig = JSONObject.parseObject(
                        modelItem.get("modelLimitConfig").toString(), ModelLimitConfig.class);
                    System.out.println(modelLimitConfig);

                    if (targetTag.equals(Tags.TARGET_KAFKA)) {
                        KafkaTargetConfig targetConfig = JSONObject.parseObject(
                            modelItem.get("targetConfig").toString(), KafkaTargetConfig.class);
                        System.out.println(targetConfig);
                    } else if (targetTag.equals(Tags.TARGET_HDFS)) {
                        //TODO：TASK_LOG2HDFS not support
                    }

                    if (sourceTag.equals(Tags.SOURCE_LOG)) {
                        LogSourceConfig sourceConfig = JSONObject.parseObject(
                            modelItem.get("sourceConfig").toString(), LogSourceConfig.class);
                        System.out.println(sourceConfig);
                    }

                    ChannelConfig channelConfig = JSONObject.parseObject(
                        modelItem.get("channelConfig").toString(), ChannelConfig.class);
                    System.out.println(channelConfig);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseTestForOldOne() {
        String input = "";
        LogConfig logConfig = null;
        try {
            if (StringUtils.isNotBlank(input)) {
                JSONObject jsonObject = JSONObject.parseObject(input);
                String config = jsonObject.getString("data");
                if (StringUtils.isNotBlank(config) && !"{}".equals(config)) {
                    logConfig = JSONObject.parseObject(config, LogConfig.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logConfig = null;
        }
        System.out.println(logConfig);
    }

    @Test
    public void parseTestForNewOne() {
        String input = "";
        AgentConfig logConfig = null;
        try {
            if (StringUtils.isNotBlank(input)) {
                JSONObject jsonObject = JSONObject.parseObject(input);
                String config = jsonObject.getString("data");
                if (StringUtils.isNotBlank(config) && !"{}".equals(config)) {
                    ConfigService configService = new ConfigService(null);
                    logConfig = configService.buildAgentConfigFromString(config);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logConfig = null;
        }
        System.out.println(logConfig);
    }

    @Test
    public void parseTestForNewOne2() {
        String input1 = "";
        String input2 = "";
        AgentConfig logConfig1 = null;
        AgentConfig logConfig2 = null;
        try {
            if (StringUtils.isNotBlank(input1) && StringUtils.isNotBlank(input2)) {
                JSONObject jsonObject1 = JSONObject.parseObject(input1);
                JSONObject jsonObject2 = JSONObject.parseObject(input2);
                String config1 = jsonObject1.getString("data");
                String config2 = jsonObject2.getString("data");
                if (StringUtils.isNotBlank(config1) && !"{}".equals(config1)
                    && StringUtils.isNotBlank(config2) && !"{}".equals(config2)) {
                    ConfigService configService = new ConfigService(null);
                    logConfig1 = configService.buildAgentConfigFromString(config1);
                    logConfig2 = configService.buildAgentConfigFromString(config2);
                    assertTrue(logConfig1.toString().equals(logConfig2.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
