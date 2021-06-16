package com.didichuxing.datachannel.swan.agent.node;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.swan.agent.common.constants.Tags;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.swan.agent.common.beans.LogPath;
import com.didichuxing.datachannel.swan.agent.common.configs.v1.LogConfig;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.*;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.*;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.swan.agent.node.service.http.client.HttpClient;
import com.didichuxing.datachannel.swan.agent.sink.hdfsSink.HdfsTargetConfig;
import com.didichuxing.datachannel.swan.agent.sink.kafkaSink.KafkaTargetConfig;
import com.didichuxing.datachannel.swan.agent.source.log.config.LogSourceConfig;
import com.didichuxing.datachannel.swan.agent.source.log.config.MatchConfig;

/**
 * @description:
 * @author: huangjw
 * @Date: 18/7/23 15:33
 */
public class ConfigServiceTest {

    private String bootstraps  = "10.95.97.20:9092";
    private String errTopic    = "swan-agent-err";
    private String metricTopic = "swan-agent-metrics";
    private String logTopic    = "swan-agent-test";

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
        list.add(getHdfsModelConfig());
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

    protected ModelConfig getHdfsModelConfig() {
        ModelConfig modelConfig = new ModelConfig(Tags.TASK_LOG2HDFS);

        EventMetricsConfig eventMetricsConfig = getEventMetricsConfig();
        CommonConfig commonConfig = getCommonConfig(-1L, "log-hdfs-task");
        ModelLimitConfig modelLimitConfig = new ModelLimitConfig();
        ChannelConfig channelConfig = new ChannelConfig();

        LogPath logPath0 = new LogPath(-1L, 0L, "/home/xiaoju/hdfs/test0.log");
        LogPath logPath1 = new LogPath(-1L, 1L, "/home/xiaoju/hdfs/test1.log");
        List<LogPath> list = new ArrayList<>();
        list.add(logPath0);
        list.add(logPath1);
        LogSourceConfig logSourceConfig = new LogSourceConfig();
        logSourceConfig.setLogPaths(list);
        logSourceConfig.setMatchConfig(new MatchConfig());

        HdfsTargetConfig hdfsTargetConfig = new HdfsTargetConfig();
        hdfsTargetConfig.setUsername("username");
        hdfsTargetConfig.setPassword("password");
        hdfsTargetConfig.setHdfsPath("/huangjiaweihjw/${path}/${yyyy}/${MM}/${dd}/${HH}");

        modelConfig.setEventMetricsConfig(eventMetricsConfig);
        modelConfig.setCommonConfig(commonConfig);
        modelConfig.setModelLimitConfig(modelLimitConfig);
        modelConfig.setChannelConfig(channelConfig);
        modelConfig.setSourceConfig(logSourceConfig);
        modelConfig.setTargetConfig(hdfsTargetConfig);

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
        configService.getParam().put("hostname", "swan-test-05");
        AgentConfig config = configService.getAgentConfig();
        assertTrue(config != null);
    }

    @Test
    public void getConfig2() throws Exception {
        ConfigService configService = new ConfigService(null);
        CommonUtils.setHOSTNAME("agent-manager-pre-sf-3e0ca-0.docker.ys");
        AgentConfig c1 = configService.getAgentConfig();
        configService.curAgentConfig = c1;
        CommonUtils.setHOSTNAME("web-wallet-sf-0c507-10.docker.us01");
        AgentConfig c2 = configService.getAgentConfig();
        if (c2 != null) {
            configService.curAgentConfig = c2;
        }
        CommonUtils.setHOSTNAME("dos-order-sf-3a131-74.docker.us01");
        AgentConfig c3 = configService.getAgentConfig();
        if (c3 != null) {
            configService.curAgentConfig = c3;
        }

        assertTrue(configService.curAgentConfig.getHostname().equals(
            "agent-manager-pre-sf-3e0ca-0.docker.ys"));

        for (int i = 0; i < 10; i++) {
            CommonUtils.setHOSTNAME("agent-manager-sf-3e0ca-0.docker.ys");
            AgentConfig c4 = configService.getAgentConfig();
            if (c4 != null) {
                configService.curAgentConfig = c4;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(
            "agent-manager-sf-3e0ca-0.docker.ys"));

        for (int i = 0; i < 10; i++) {
            CommonUtils.setHOSTNAME("agent-manager-sf-3e0ca-1.docker.ys");
            AgentConfig c5 = configService.getAgentConfig();
            if (c5 != null) {
                configService.curAgentConfig = c5;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(
            "agent-manager-sf-3e0ca-1.docker.ys"));

        for (int i = 0; i < 3; i++) {
            CommonUtils.setHOSTNAME("agent-manager-sf-3e0ca-2.docker.ys");
            AgentConfig c6 = configService.getAgentConfig();
            if (c6 != null) {
                configService.curAgentConfig = c6;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(
            "agent-manager-sf-3e0ca-1.docker.ys"));
    }

    @Test
    public void getConfig3() throws Exception {
        ConfigService configService = new ConfigService(null);
        CommonUtils.setHOSTNAME("agent-manager-pre-sf-3e0ca-0.docker.ys");
        AgentConfig c1 = configService.getAgentConfig();
        configService.curAgentConfig = c1;

        for (int i = 0; i < 6; i++) {
            CommonUtils.setHOSTNAME("mutual-bonus-pre-sf-fc7bf-0.docker.ys");
            AgentConfig c6 = configService.getAgentConfig();
            if (c6 != null) {
                configService.curAgentConfig = c6;
            }
        }
        assertTrue(configService.curAgentConfig.getHostname().equals(
            "mutual-bonus-pre-sf-fc7bf-0.docker.ys"));
    }

    @Ignore
    @Test
    public void getRemoteConfig() throws Exception {
        String managerIp = "100.69.238.11";
        Integer managerPort = 8000;
        String managerUrl = "/agent-manager-gz/api/v1/agents/config/host";
        Map<String, String> param = new HashMap<>();
        param.put("ip", CommonUtils.getHOSTIP());
        param.put("hostName", "tmp-cplat-dos-svr27.gz01");

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
        String input = "{\"code\":0,\"data\":{\"dqualityConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"switchConfig\":0,\"topic\":\"swan-agent-dquality,swan-agent-dquality1\",\"valid\":true},\"errorLogConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"switchConfig\":0,\"topic\":\"swan-agent-error,swan-agent-error1\",\"valid\":true},\"hostname\":\"logagent-stable-zj-02\",\"limitConfig\":{\"cpuThreshold\":200,\"minThreshold\":100,\"startThreshold\":20000},\"metricConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"switchConfig\":0,\"topic\":\"swan-agent-metrics,swan-agent-metrics1\",\"transfer\":true,\"valid\":true},\"modelConfigs\":[{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15435,\"modelName\":\"把脉-elk-工单15000-didi-localVolume-nstar-auditLog2-日志采集-全局化\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"Global\",\"isService\":1,\"location\":\"us01,gz01\",\"originalAppName\":\"Global\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"},\"queryFrom\":\"didi\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15435,\"logPathKey\":\"15435_14191_/home/xiaoju/localVolume/nstar/auditLog2.log\",\"path\":\"/home/xiaoju/localVolume/nstar/auditLog2.log\",\"pathId\":14191}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".2017-01-01\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"\",\"clusterId\":24,\"filterOprType\":0,\"filterRule\":\"create-time:\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"topic-test1\",\"transFormate\":0,\"type\":\"sink\"},\"modelLimitConfig\":{\"level\":9,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":0},{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15880,\"modelName\":\"个人测试-ceph--11111\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":6},\"eventMetricsConfig\":{\"belongToCluster\":\"stable-test\",\"isService\":0,\"location\":\"cn\",\"odinLeaf\":\"\",\"originalAppName\":\"stable-test\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"},\"queryFrom\":\"基础平台部-专家组-日志服务\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15880,\"logPathKey\":\"15880_18924_/home/logger/swan-log-collector/logs/config.log\",\"path\":\"/home/logger/swan-log-collector/logs/config.log\",\"pathId\":18924},{\"logModelId\":15880,\"logPathKey\":\"15880_18925_/home/logger/swan-log-collector/logs/perf.log\",\"path\":\"/home/logger/swan-log-collector/logs/perf.log\",\"pathId\":18925},{\"logModelId\":15880,\"logPathKey\":\"15880_18931_/home/logger/swan-log-collector/logs/system.log\",\"path\":\"/home/logger/swan-log-collector/logs/system.log\",\"pathId\":18931},{\"logModelId\":15880,\"logPathKey\":\"15880_18933_/home/logger/swan-log-collector/logs/swan-agent.log\",\"path\":\"/home/logger/swan-log-collector/logs/swan-agent.log\",\"pathId\":18933}],\"matchConfig\":{\"businessType\":3,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"NoLogTime\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"10.179.162.171:9092,10.179.162.20:9092,10.179.149.194:9092,10.179.149.201:9092,10.179.149.164:9092\",\"clusterId\":44,\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"max.request.size=4194304,compression.codec=1,serializer.class=kafka.serializer.StringEncoder,request.required.acks=all\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"LOG_AGENT_STORAGE_ES\",\"transFormate\":0,\"type\":\"sink\"},\"modelLimitConfig\":{\"level\":5,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":6},{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15439,\"modelName\":\"把脉-elk-工单15000-didi-localVolume-nstar-auditLog2-日志采集-全局化2\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"Global\",\"isService\":1,\"location\":\"cn\",\"originalAppName\":\"Global\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"},\"queryFrom\":\"didi\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15439,\"logPathKey\":\"15439_14197_/home/xiaoju/localVolume/nstar/auditLog2.log\",\"path\":\"/home/xiaoju/localVolume/nstar/auditLog2.log\",\"pathId\":14197}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".2017-01-01\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"\",\"clusterId\":24,\"filterOprType\":0,\"filterRule\":\"create-time:\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"topic-test1\",\"transFormate\":0,\"type\":\"sink\"},\"modelLimitConfig\":{\"level\":9,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":0},{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":1,\"encodeType\":\"UTF-8\",\"modelId\":15912,\"modelName\":\"swan-agent-test-日志采集-hdfs-3\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"stable-test\",\"isService\":0,\"location\":\"cn\",\"odinLeaf\":\"\",\"originalAppName\":\"stable-test\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\",\"metrics1\":\"value1\",\"metrics2\":\"value2\",\"metrics3\":\"\"},\"queryFrom\":\"didi\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15912,\"logPathKey\":\"15912_18962_/home/logger/swan-log-collector/logs/perf.log\",\"path\":\"/home/logger/swan-log-collector/logs/perf.log\",\"pathId\":18962}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2hdfs\",\"targetConfig\":{\"async\":true,\"compression\":0,\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"hdfsFileName\":\"/${hostname}_${filename}_${createTime}_${sourceId}_${sinkId}_${compression}\",\"hdfsPath\":\"/home/xiaoju/${Path}\",\"password\":\"root\",\"retryTimes\":3,\"rootPath\":\"/\",\"sinkNum\":1,\"tag\":\"hdfs\",\"type\":\"sink\",\"username\":\"root\"},\"modelLimitConfig\":{\"level\":9,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":0}],\"offsetConfig\":{\"rootDir\":\"/home/logger/.swan-logOffSet\"},\"status\":0,\"version\":0},\"message\":\"success\"}";
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
                        HdfsTargetConfig targetConfig = JSONObject.parseObject(
                            modelItem.get("targetConfig").toString(), HdfsTargetConfig.class);
                        System.out.println(targetConfig);
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
        String input = "{\"code\":0,\"data\":{\"collectorConfigList\":[{\"businessConfig\":{\"belongToCluster\":\"stable-test\",\"isService\":0,\"location\":\"cn\",\"odinLeaf\":\"\",\"originalAppName\":\"stable-test\",\"queryFrom\":\"基础平台部-专家组-日志服务\"},\"commonConfig\":{\"async\":true,\"collcetLocation\":0,\"collectType\":1,\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"eventAddition\":{\"default\":\"value\",\"default2\":{},\"default3\":{}},\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"isOrderFile\":0,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"logModelId\":15880,\"logModelName\":\"个人测试-ceph--11111\",\"maxContentSize\":4194304,\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"priority\":5,\"readFileType\":0,\"readTimeOut\":3000,\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sequentialCollect\":false,\"stop\":false,\"timeFormat\":\"NoLogTime\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"topic\":\"LOG_AGENT_STORAGE_ES\",\"transFormate\":0,\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"version\":6,\"waitCheckTime\":300000},\"eventMetricsConfig\":{\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"}},\"limitNodeConfig\":{\"level\":5,\"minThreshold\":100,\"startThrehold\":20000},\"matchConfig\":{\"businessType\":3,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"sourceConfig\":{\"logPaths\":[{\"logModelId\":15880,\"logPathKey\":\"15880_18924_/home/logger/swan-log-collector/logs/config.log\",\"path\":\"/home/logger/swan-log-collector/logs/config.log\",\"pathId\":18924},{\"logModelId\":15880,\"logPathKey\":\"15880_18925_/home/logger/swan-log-collector/logs/perf.log\",\"path\":\"/home/logger/swan-log-collector/logs/perf.log\",\"pathId\":18925},{\"logModelId\":15880,\"logPathKey\":\"15880_18931_/home/logger/swan-log-collector/logs/system.log\",\"path\":\"/home/logger/swan-log-collector/logs/system.log\",\"pathId\":18931},{\"logModelId\":15880,\"logPathKey\":\"15880_18933_/home/logger/swan-log-collector/logs/swan-agent.log\",\"path\":\"/home/logger/swan-log-collector/logs/swan-agent.log\",\"pathId\":18933}]},\"targetConfig\":{\"bootstrap\":\"10.179.162.171:9092,10.179.162.20:9092,10.179.149.194:9092,10.179.149.201:9092,10.179.149.164:9092\",\"clusterId\":23,\"properties\":\"max.request.size=4194304,compression.codec=1,serializer.class=kafka.serializer.StringEncoder,request.required.acks=all\"},\"version\":6},{\"businessConfig\":{\"belongToCluster\":\"Global\",\"isService\":1,\"location\":\"us01,gz01\",\"originalAppName\":\"Global\",\"queryFrom\":\"didi\"},\"commonConfig\":{\"async\":true,\"collcetLocation\":0,\"collectType\":1,\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"eventAddition\":{\"default\":\"value\",\"default2\":{},\"default3\":{}},\"filterOprType\":0,\"filterRule\":\"create-time:\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"isOrderFile\":0,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"logModelId\":15435,\"logModelName\":\"把脉-elk-工单15000-didi-localVolume-nstar-auditLog2-日志采集-全局化\",\"maxContentSize\":4194304,\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"priority\":9,\"readFileType\":0,\"readTimeOut\":3000,\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sequentialCollect\":false,\"stop\":false,\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"topic\":\"topic-test1\",\"transFormate\":0,\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"version\":0,\"waitCheckTime\":300000},\"eventMetricsConfig\":{\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"}},\"limitNodeConfig\":{\"level\":9,\"minThreshold\":100,\"startThrehold\":20000},\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".2017-01-01\",\"fileType\":0,\"matchType\":0},\"sourceConfig\":{\"logPaths\":[{\"logModelId\":15435,\"logPathKey\":\"15435_14191_/home/xiaoju/localVolume/nstar/auditLog2.log\",\"path\":\"/home/xiaoju/localVolume/nstar/auditLog2.log\",\"pathId\":14191}]},\"targetConfig\":{\"bootstrap\":\"10.95.136.90:9092,10.95.156.12:9092,10.95.137.184:9092\",\"clusterId\":9,\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1,request.required.acks=all\"},\"version\":0},{\"businessConfig\":{\"belongToCluster\":\"Global\",\"isService\":1,\"location\":\"cn\",\"originalAppName\":\"Global\",\"queryFrom\":\"didi\"},\"commonConfig\":{\"async\":true,\"collcetLocation\":0,\"collectType\":1,\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"eventAddition\":{\"default\":\"value\",\"default2\":{},\"default3\":{}},\"filterOprType\":0,\"filterRule\":\"create-time:\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"isOrderFile\":0,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"logModelId\":15439,\"logModelName\":\"把脉-elk-工单15000-didi-localVolume-nstar-auditLog2-日志采集-全局化2\",\"maxContentSize\":4194304,\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"priority\":9,\"readFileType\":0,\"readTimeOut\":3000,\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sequentialCollect\":false,\"stop\":false,\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"topic\":\"topic-test1\",\"transFormate\":0,\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"version\":0,\"waitCheckTime\":300000},\"eventMetricsConfig\":{\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"}},\"limitNodeConfig\":{\"level\":9,\"minThreshold\":100,\"startThrehold\":20000},\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".2017-01-01\",\"fileType\":0,\"matchType\":0},\"sourceConfig\":{\"logPaths\":[{\"logModelId\":15439,\"logPathKey\":\"15439_14197_/home/xiaoju/localVolume/nstar/auditLog2.log\",\"path\":\"/home/xiaoju/localVolume/nstar/auditLog2.log\",\"pathId\":14197}]},\"targetConfig\":{\"bootstrap\":\"10.95.136.90:9092,10.95.156.12:9092,10.95.137.184:9092\",\"clusterId\":9,\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1,request.required.acks=all\"},\"version\":0}],\"dqualityConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"topic\":\"swan-agent-dquality,swan-agent-dquality1\",\"valid\":true},\"errLogConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"topic\":\"swan-agent-error,swan-agent-error1\",\"valid\":true},\"hostName\":\"logagent-stable-zj-02\",\"limitConfig\":{\"cpuThreshold\":200,\"minThreshold\":100,\"startThreshold\":20000},\"metricConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"topic\":\"swan-agent-metrics,swan-agent-metrics1\",\"valid\":true},\"offsetConfig\":{\"rootDir\":\"/home/logger/.swan-logOffSet\"},\"status\":0,\"version\":0},\"message\":\"success\"}";
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
        String input = "{\"code\":0,\"data\":{\"dqualityConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"switchConfig\":0,\"topic\":\"swan-agent-dquality,swan-agent-dquality1\",\"valid\":true},\"errorLogConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"switchConfig\":0,\"topic\":\"swan-agent-error,swan-agent-error1\",\"valid\":true},\"hostname\":\"logagent-stable-zj-02\",\"limitConfig\":{\"cpuThreshold\":200,\"minThreshold\":100,\"startThreshold\":20000},\"metricConfig\":{\"nameServer\":\"10.95.97.208:9092,10.95.137.177:9092,10.95.97.20:9092\",\"properties\":\"serializer.class=kafka.serializer.StringEncoder,compression.codec=1\",\"switchConfig\":0,\"topic\":\"swan-agent-metrics,swan-agent-metrics1\",\"transfer\":true,\"valid\":true},\"modelConfigs\":[{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15435,\"modelName\":\"把脉-elk-工单15000-didi-localVolume-nstar-auditLog2-日志采集-全局化\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"Global\",\"isService\":1,\"location\":\"us01,gz01\",\"originalAppName\":\"Global\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"},\"queryFrom\":\"didi\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15435,\"logPathKey\":\"15435_14191_/home/xiaoju/localVolume/nstar/auditLog2.log\",\"path\":\"/home/xiaoju/localVolume/nstar/auditLog2.log\",\"pathId\":14191}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".2017-01-01\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"\",\"clusterId\":24,\"filterOprType\":0,\"filterRule\":\"create-time:\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"topic-test1\",\"transFormate\":0,\"type\":\"sink\"},\"modelLimitConfig\":{\"level\":9,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":0},{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15880,\"modelName\":\"个人测试-ceph--11111\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":6},\"eventMetricsConfig\":{\"belongToCluster\":\"stable-test\",\"isService\":0,\"location\":\"cn\",\"odinLeaf\":\"\",\"originalAppName\":\"stable-test\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"},\"queryFrom\":\"基础平台部-专家组-日志服务\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15880,\"logPathKey\":\"15880_18924_/home/logger/swan-log-collector/logs/config.log\",\"path\":\"/home/logger/swan-log-collector/logs/config.log\",\"pathId\":18924},{\"logModelId\":15880,\"logPathKey\":\"15880_18925_/home/logger/swan-log-collector/logs/perf.log\",\"path\":\"/home/logger/swan-log-collector/logs/perf.log\",\"pathId\":18925},{\"logModelId\":15880,\"logPathKey\":\"15880_18931_/home/logger/swan-log-collector/logs/system.log\",\"path\":\"/home/logger/swan-log-collector/logs/system.log\",\"pathId\":18931},{\"logModelId\":15880,\"logPathKey\":\"15880_18933_/home/logger/swan-log-collector/logs/swan-agent.log\",\"path\":\"/home/logger/swan-log-collector/logs/swan-agent.log\",\"pathId\":18933}],\"matchConfig\":{\"businessType\":3,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"NoLogTime\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"10.179.162.171:9092,10.179.162.20:9092,10.179.149.194:9092,10.179.149.201:9092,10.179.149.164:9092\",\"clusterId\":44,\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"max.request.size=4194304,compression.codec=1,serializer.class=kafka.serializer.StringEncoder,request.required.acks=all\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"LOG_AGENT_STORAGE_ES\",\"transFormate\":0,\"type\":\"sink\"},\"modelLimitConfig\":{\"level\":5,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":6},{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15439,\"modelName\":\"把脉-elk-工单15000-didi-localVolume-nstar-auditLog2-日志采集-全局化2\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"Global\",\"isService\":1,\"location\":\"cn\",\"originalAppName\":\"Global\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\"},\"queryFrom\":\"didi\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15439,\"logPathKey\":\"15439_14197_/home/xiaoju/localVolume/nstar/auditLog2.log\",\"path\":\"/home/xiaoju/localVolume/nstar/auditLog2.log\",\"pathId\":14197}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".2017-01-01\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"timestamp=\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"\",\"clusterId\":24,\"filterOprType\":0,\"filterRule\":\"create-time:\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"topic-test1\",\"transFormate\":0,\"type\":\"sink\"},\"modelLimitConfig\":{\"level\":9,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":0},{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":1,\"encodeType\":\"UTF-8\",\"modelId\":15912,\"modelName\":\"swan-agent-test-日志采集-hdfs-3\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"stable-test\",\"isService\":0,\"location\":\"cn\",\"odinLeaf\":\"\",\"originalAppName\":\"stable-test\",\"otherEvents\":{},\"otherMetrics\":{\"default\":\"value\",\"default2\":\"\",\"default3\":\"\",\"metrics1\":\"value1\",\"metrics2\":\"value2\",\"metrics3\":\"\"},\"queryFrom\":\"didi\"},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15912,\"logPathKey\":\"15912_18962_/home/logger/swan-log-collector/logs/perf.log\",\"path\":\"/home/logger/swan-log-collector/logs/perf.log\",\"pathId\":18962}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2hdfs\",\"targetConfig\":{\"async\":true,\"compression\":0,\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"hdfsFileName\":\"/${hostname}_${filename}_${createTime}_${sourceId}_${sinkId}_${compression}\",\"hdfsPath\":\"/home/xiaoju/${Path}\",\"password\":\"root\",\"retryTimes\":3,\"rootPath\":\"/\",\"sinkNum\":1,\"tag\":\"hdfs\",\"type\":\"sink\",\"username\":\"root\"},\"modelLimitConfig\":{\"level\":9,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"type\":\"task\",\"version\":0}],\"offsetConfig\":{\"rootDir\":\"/home/logger/.swan-logOffSet\"},\"status\":0,\"version\":0},\"message\":\"success\"}";
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
        String input1 = "{\"code\":0,\"data\":{\"dqualityConfig\":{\"nameServer\":\"\",\"properties\":\"\",\"switchConfig\":0,\"topic\":\"\",\"valid\":false},\"errorLogConfig\":{\"nameServer\":\"\",\"properties\":\"\",\"switchConfig\":0,\"topic\":\"\",\"valid\":false},\"hostname\":\"cplat-plutus-cratos00.gz01\",\"limitConfig\":{\"cpuThreshold\":200.0,\"minThreshold\":100,\"startThreshold\":20000},\"metricConfig\":{\"nameServer\":\"\",\"properties\":\"\",\"switchConfig\":0,\"topic\":\"\",\"transfer\":true,\"valid\":false},\"modelConfigs\":[{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"collectType\":0,\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15236,\"modelName\":\"swan-monitor-test-cratos.gs.common-plat\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"cratos.gs.common-plat\",\"isService\":1,\"location\":\"cn\",\"odinLeaf\":\"hna\",\"originalAppName\":\"cratos.gs.common-plat\",\"otherEvents\":{},\"otherMetrics\":{},\"queryFrom\":\"didi\"},\"modelConfigKey\":\"15236\",\"modelLimitConfig\":{\"level\":5,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15236,\"logPathKey\":\"15236_17852_/home/xiaoju/swan-monitor/logs/swan-monitor.log\",\"path\":\"/home/xiaoju/swan-monitor/logs/swan-monitor.log\",\"pathId\":17852,\"realPath\":\"/home/xiaoju/swan-monitor/logs/swan-monitor.log\"}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"bigdata-swan-ser20.gz01:9092,bigdata-swan-ser21.gz01:9092,bigdata-swan-ser22.gz01:9092,bigdata-swan-ser23.gz01:9092,bigdata-swan-ser24.gz01:9092,bigdata-swan-ser25.gz01:9092\",\"clusterId\":14,\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"sasl_mechanism=PLAIN,security_protocol=SASL_PLAINTEXT,sasl_jaas_config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"28.appId_000058\\\" password=\\\"obLUytlmNuCP\\\";,compression.codec=1,gateway=10.88.128.149:30372;10.85.128.81:30016,serializer.class=kafka.serializer.StringEncoder\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"huangjiawei_test\",\"transFormate\":0,\"type\":\"sink\"},\"type\":\"task\",\"version\":0}],\"offsetConfig\":{\"rootDir\":\"\"},\"status\":0,\"version\":2},\"message\":\"success\"}";
        String input2 = "{\"code\":0,\"data\":{\"dqualityConfig\":{\"nameServer\":\"\",\"properties\":\"\",\"switchConfig\":0,\"topic\":\"\",\"valid\":false},\"errorLogConfig\":{\"nameServer\":\"\",\"properties\":\"\",\"switchConfig\":0,\"topic\":\"\",\"valid\":false},\"hostname\":\"cplat-plutus-cratos00.gz01\",\"limitConfig\":{\"cpuThreshold\":200.0,\"minThreshold\":100,\"startThreshold\":20000},\"metricConfig\":{\"nameServer\":\"\",\"properties\":\"\",\"switchConfig\":0,\"topic\":\"\",\"transfer\":true,\"valid\":false},\"modelConfigs\":[{\"channelConfig\":{\"maxBytes\":104857600,\"maxNum\":100,\"tag\":\"memory\",\"type\":\"channel\"},\"commonConfig\":{\"dqualitySwitch\":0,\"encodeType\":\"UTF-8\",\"modelId\":15236,\"modelName\":\"swan-monitor-test-cratos.gs.common-plat\",\"modelType\":1,\"priority\":0,\"stop\":false,\"version\":0},\"eventMetricsConfig\":{\"belongToCluster\":\"cratos.gs.common-plat\",\"isService\":1,\"location\":\"cn\",\"odinLeaf\":\"hna\",\"originalAppName\":\"cratos.gs.common-plat\",\"otherEvents\":{},\"otherMetrics\":{},\"queryFrom\":\"didi\"},\"modelLimitConfig\":{\"level\":5,\"minThreshold\":100,\"rate\":0,\"startThrehold\":20000},\"sourceConfig\":{\"collcetLocation\":0,\"isOrderFile\":0,\"logPaths\":[{\"logModelId\":15236,\"logPathKey\":\"15236_17852_/home/xiaoju/swan-monitor/logs/swan-monitor.log\",\"path\":\"/home/xiaoju/swan-monitor/logs/swan-monitor.log\",\"pathId\":17852,\"realPath\":\"/home/xiaoju/swan-monitor/logs/swan-monitor.log\"}],\"matchConfig\":{\"businessType\":0,\"fileFilterType\":0,\"fileSuffix\":\".1\",\"fileType\":0,\"matchType\":0},\"maxErrorLineNum\":100,\"maxModifyTime\":604800000,\"maxThreadNum\":10,\"orderTimeMaxGap\":600000,\"readFileType\":0,\"readTimeOut\":3000,\"sequentialCollect\":false,\"tag\":\"log\",\"timeFormat\":\"yyyy-MM-dd HH:mm:ss\",\"timeStartFlag\":\"\",\"timeStartFlagIndex\":0,\"type\":\"source\",\"vaildLatestFiles\":2,\"vaildTimeConfig\":true,\"waitCheckTime\":300000},\"tag\":\"log2kafka\",\"targetConfig\":{\"async\":true,\"bootstrap\":\"bigdata-swan-ser20.gz01:9092,bigdata-swan-ser21.gz01:9092,bigdata-swan-ser22.gz01:9092,bigdata-swan-ser23.gz01:9092,bigdata-swan-ser24.gz01:9092,bigdata-swan-ser25.gz01:9092\",\"clusterId\":14,\"filterOprType\":0,\"filterRule\":\"\",\"flushBatchSize\":10,\"flushBatchTimeThreshold\":30000,\"keyFormat\":\"\",\"keyStartFlag\":\"\",\"keyStartFlagIndex\":0,\"maxContentSize\":4194304,\"properties\":\"sasl_mechanism=PLAIN,security_protocol=SASL_PLAINTEXT,sasl_jaas_config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\\\"28.appId_000058\\\" password=\\\"obLUytlmNuCP\\\";,compression.codec=1,gateway=10.88.128.149:30372;10.85.128.81:30016,serializer.class=kafka.serializer.StringEncoder\",\"regularPartKey\":\"\",\"sendBatchSize\":50,\"sendBatchTimeThreshold\":1000,\"sinkNum\":1,\"tag\":\"kafka\",\"topic\":\"huangjiawei_test\",\"transFormate\":0,\"type\":\"sink\"},\"type\":\"task\",\"version\":0}],\"offsetConfig\":{\"rootDir\":\"\"},\"status\":0,\"version\":2},\"message\":\"success\"}";
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
