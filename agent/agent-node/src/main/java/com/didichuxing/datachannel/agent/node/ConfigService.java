package com.didichuxing.datachannel.agent.node;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didichuxing.datachannel.agent.node.am.v2.AgentCollectConfigDOImpl;
import com.didichuxing.datachannel.agent.node.service.http.client.HttpClient;
import com.didichuxing.datachannel.agentmanager.common.bean.dto.agent.AgentRegisterDTO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ResultTypeEnum;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.component.AgentComponent;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: config service
 * @author: huangjw
 * @Date: 19/7/5 15:02
 */
// TODO: 19/7/5 增加是否需要定时向管理平台拉取配置的 配置
public class ConfigService extends AgentComponent {

    private static final Logger CONFIG_LOGGER = LoggerFactory.getLogger("config");
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);
    private String                     local                = System.getProperty("user.home") + File.separator + "new.conf.local";

    private static final long          INTERVAL             = 30 * 1000;

    private static final long          START_INTERVAL       = 5 * 1000;

    private static Map<String, String> param                = new HashMap<>();

    private static Thread              configThread         = null;

    public static AgentConfig          curAgentConfig       = null;
    public static AgentConfig          lastAgentConfig      = null;

    private LaunchService              launchService        = null;

    private final int                  MAX_CONFIG_THRESHOLD = 5;
    private static int                 curConfigTryTime     = 0;
    private static boolean             isRunning            = false;

    /**
     * 容器采集路径请求url
     */
    public static String containerPathRequestUrl = "";

    public ConfigService(LaunchService launchService){
        this.launchService = launchService;
    }

    /**
     * 初始化 agent 配置服务，定时从服务端获取配置，并将配置持久化至本地
     */
    @Override
    public boolean init(AgentConfig config) {
        LOGGER.info("begin to init agent config.");
        try {
            long waitTime = (long) (START_INTERVAL * Math.random());
            LOGGER.info("wait " + waitTime + " ms before init");
            isRunning = true;
            Thread.sleep(waitTime);
        } catch (Throwable e) {
            LOGGER.error("ConfigService error!", "wait time sleep error!, {}", e.getMessage());
        }

        // 第一次拉取配置并初始化整个系统
        try {
            curAgentConfig = getAgentConfig();
            if (curAgentConfig == null) {
                // 无配置启动
                LOGGER.info("agent config is null.init agent without config!");
            } else {
                writeLocalConfig(curAgentConfig);
            }
        } catch (Throwable e) {
            LOGGER.error("ConfigService error!", "getAgentConfig error!, {}", e.getMessage());
        }

        // 后台线程
        configThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRunning) {
                    try {
                        curAgentConfig = getAgentConfig();
                        if (curAgentConfig != null) {
                            if (launchService.onChange(curAgentConfig)) {
                                writeLocalConfig(curAgentConfig);
                            }
                        }
                    } catch (Throwable t) {
                        LOGGER.error("ConfigChange", "config service change error, {}", t.getMessage());
                    }

                    try {
                        Thread.sleep(INTERVAL);
                    } catch (Throwable t) {
                        LOGGER.error("ConfigService error! config service sleep error", t);
                    }
                }
                LOGGER.info("config thread exist!");
            }
        });
        configThread.setName("agent config refresh service");
        configThread.setDaemon(false);

        LOGGER.info("success to init agent config.");
        return true;
    }

    @Override
    public boolean start() {
        LOGGER.info("begin to start ConfigService");
        configThread.start();
        LOGGER.info("success to start ConfigService");
        return true;
    }

    @Override
    public boolean stop(boolean force) {
        LOGGER.info("begin to stop configService");
        if (configThread != null) {
            isRunning = false;
            configThread.interrupt();
        }

        LOGGER.info("stop configService success!");
        return true;
    }

    public AgentConfig getAgentConfig() throws Exception {
        CONFIG_LOGGER.info("begin to get config.");

        AgentConfig agentConfig = getRemoteConfig();
        if (agentConfig == null) {
            CONFIG_LOGGER.warn("can not get this agent's remote logConfig.");
            agentConfig = getLocalConfig();
        }

        if (agentConfig == null) {
            CONFIG_LOGGER.warn("can not get this agent's  logConfig.");
        } else {
            CONFIG_LOGGER.info("get config success");
        }

        if (curAgentConfig == null && curConfigTryTime == 0) {
            // 第一次设置
            lastAgentConfig = agentConfig;
            return agentConfig;
        } else {
            // 最近的MAX_CONFIG_THRESHOLD次配置获取稳定
            if (agentConfig != null && checkAgentConfigEqual(agentConfig, lastAgentConfig)) {
                curConfigTryTime++;
                if (curConfigTryTime >= MAX_CONFIG_THRESHOLD) {
                    return agentConfig;
                } else {
                    return null;
                }
            } else {
                lastAgentConfig = agentConfig;
                curConfigTryTime = 0;
                return agentConfig;
            }
        }
    }

    /**
     * 校验配置是否正常
     * @param a
     * @param b
     * @return
     */
    private boolean checkAgentConfigEqual(AgentConfig a, AgentConfig b) {
        if (a == null && b == null) {
            return true;
        }

        try {
            if (a != null && b != null) {
                if (a.toString().equals(b.toString())) {
                    Map<String, ModelConfig> map1 = a.getModelConfigMap();
                    Map<String, ModelConfig> map2 = b.getModelConfigMap();
                    if (CollectionUtils.isEqualCollection(map1.keySet(), map2.keySet())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            CONFIG_LOGGER.error("checkAgentConfigEqual error. a is " + a + ", b is " + b, e);
        }
        return false;
    }

    /**
     * 获取远程配置
     *
     * @return logConfig of remote
     * @throws Exception http service is not available
     */
    private AgentConfig getRemoteConfig() throws Exception {
        Map<String, String> configMap = CommonUtils.readSettings();

        param.put("ip", CommonUtils.getHOSTIP());
        param.put("hostName", CommonUtils.getHOSTNAME());

        List<String> ips = readManagerIPs(configMap);
        List<Integer> ports = readManagerPort(configMap);
        List<String> urls = readManagerUrl(configMap);
        if (ips.size() == 0 || urls.size() == 0 || ports.size() == 0) {
            return null;
        }
        for (int i = 0; i < ips.size(); i++) {
            String ip = ips.get(i);
            Integer port = null;
            if (i >= ports.size()) {
                port = ports.get(0);
            } else {
                port = ports.get(i);
            }

            String url = null;
            if (i >= urls.size()) {
                url = urls.get(0);
            } else {
                url = urls.get(i);
            }

            /*
             * 获取agent注册所需参数 registerurl、agentversion、agentcollecttype
             */
            String agentRegisterUrl = configMap.get(LogConfigConstants.AGENT_REGISTER_URL);
            String agentVersion = configMap.get(LogConfigConstants.AGENT_VERSION);
            String agentCollectType = configMap.get(LogConfigConstants.AGENT_COLLECT_TYPE);
            containerPathRequestUrl = configMap.get(LogConfigConstants.CONTAINER_COLLECT_PATH_REQUEST_URL);
            AgentConfig agentConfig = getConfigItem(ip, port, url, param, agentRegisterUrl, agentVersion, agentCollectType);
            if (agentConfig != null) {
                return agentConfig;
            }
        }
        return null;
    }

    private List<String> readManagerIPs(Map<String, String> configMap) {
        String managerIps = configMap.get(LogConfigConstants.CONFIG_IP);
        List<String> ips = new ArrayList<>();
        if (managerIps.contains(LogConfigConstants.COMMA)) {
            ips = Arrays.asList(managerIps.split(LogConfigConstants.COMMA));
        } else {
            ips.add(managerIps);
        }
        return ips;
    }

    private List<Integer> readManagerPort(Map<String, String> configMap) {
        String managerPorts = configMap.get(LogConfigConstants.CONFIG_PORT);
        List<Integer> result = new ArrayList<>();
        if (managerPorts.contains(LogConfigConstants.COMMA)) {
            String[] ports = managerPorts.split(LogConfigConstants.COMMA);
            for (String port : ports) {
                result.add(parsePort(port));
            }
        } else {
            result.add(parsePort(managerPorts));
        }
        return result;
    }

    private Integer parsePort(String port) {
        try {
            return Integer.parseInt(port);
        } catch (Exception e) {
            LOGGER.error("parsePort error. port is " + port);
        }
        return 0;
    }

    private List<String> readManagerUrl(Map<String, String> configMap) {
        String managerUrls = null;
        managerUrls = configMap.get(LogConfigConstants.CONFIG_URL);
        List<String> urls = new ArrayList<>();
        if (managerUrls.contains(LogConfigConstants.COMMA)) {
            urls = Arrays.asList(managerUrls.split(LogConfigConstants.COMMA));
        } else {
            urls.add(managerUrls);
        }
        return urls;
    }

    private AgentConfig getConfigItem(String ip, int port, String url, Map<String, String> param, String agentRegisterUrl, String agentVersion, String agentCollectType) {
        String rootStr = HttpClient.get(ip, port, url, param);
        AgentConfig agentConfig = null;
        try {
            if (StringUtils.isNotBlank(rootStr)) {
                JSONObject jsonObject = JSONObject.parseObject(rootStr);
                Integer code = jsonObject.getInteger("code");
                if(null != code) {
                    if(code.equals(ErrorCodeEnum.AGENT_NOT_EXISTS.getCode())) {//agent未注册
                        //注册 agent 信息至 agent-manager 端，并等待下一次配置获取
                        agentRegister(ip, port, agentRegisterUrl, agentVersion, agentCollectType);
                    } else if(code.equals(ResultTypeEnum.SUCCESS.getCode())) {//agent 配置获取成功
                        String config = jsonObject.getString("data");
                        if (StringUtils.isNotBlank(config) && !"{}".equals(config)) {
                            agentConfig = buildAgentConfigFromString(config);
                        } else {//agent配置对应data域不可为空
                            CONFIG_LOGGER.warn(
                                    String.format("get agent config from remote agent-manager failed, the response data field is null, the response is: %s, ip={%s},port={%d},url={%s},param={%s}.", rootStr, ip, port, url, JSON.toJSONString(param))
                            );
                        }
                    } else {//unknow code
                        CONFIG_LOGGER.warn(
                                String.format("get agent config from remote agent-manager failed, ip={%s},port={%d},url={%s},param={%s}, response unknown code={%d}.", ip, port, url, JSON.toJSONString(param), code)
                        );
                    }
                } else {//code is null
                    CONFIG_LOGGER.warn(
                            String.format("get agent config from remote agent-manager failed, ip={%s},port={%d},url={%s},param={%s}, response code is null, the response is: %s.", ip, port, url, JSON.toJSONString(param), rootStr)
                    );
                }
            } else {
                CONFIG_LOGGER.warn(
                        String.format("get agent config from remote agent-manager failed, ip={%s},port={%d},url={%s},param={%s}, response is null.", ip, port, url, JSON.toJSONString(param))
                );
            }
        } catch (Exception e) {
            CONFIG_LOGGER.warn(
                    String.format("get agent config from remote agent-manager failed, ip={%s},port={%d},url={%s},param={%s}.", ip, port, url, JSON.toJSONString(param)),
                    e
            );
            agentConfig = null;
        }
        return agentConfig;
    }

    /**
     * 针对当前agent，到agent-manager平台进行注册
     * @param ip agent-manager 宿主机 ip
     * @param port agent-manager 进程端口
     * @param agentRegisterUrl agent-manager 对应 agent 注册 url
     * @param agentVersion 该agent版本号
     * @param agentCollectType agent 采集类型
     */
    private void agentRegister(String ip, int port, String agentRegisterUrl, String agentVersion, String agentCollectType) throws Exception {
        if(StringUtils.isBlank(agentCollectType)) {
            throw new Exception("local config item {collect.type} not be null.");
        }
        Integer collectType;
        try {
            collectType = Integer.valueOf(agentCollectType);
        } catch (Exception ex) {
            throw new Exception("config item {collect.type} must be integer.", ex);
        }
        AgentRegisterDTO agentRegisterDTO = new AgentRegisterDTO();
        agentRegisterDTO.setHostName(CommonUtils.getHOSTNAME());
        agentRegisterDTO.setIp(CommonUtils.getHOSTIP());
        agentRegisterDTO.setCollectType(collectType);
        agentRegisterDTO.setVersion(agentVersion);
        String requestBody = JSON.toJSONString(agentRegisterDTO);
        String responseString = HttpClient.post(ip, port, agentRegisterUrl, requestBody);
        if(StringUtils.isBlank(responseString)) {
            CONFIG_LOGGER.warn(
                    String.format("register agent to agent-manager failed, because response is null, ip={%s},port={%d},agentRegisterUrl={%s},agentVersion={%s},agentCollectType={%s},requestBody={%s}", ip, port, agentRegisterUrl, agentVersion, agentCollectType, requestBody)
            );
        }
        JSONObject jsonObject = JSONObject.parseObject(responseString);
        Integer code = jsonObject.getInteger("code");
        if(null == code || !code.equals(ResultTypeEnum.SUCCESS.getCode())) {//请求失败
            CONFIG_LOGGER.warn(
                    String.format("register agent to agent-manager failed, the response is: %s, ip={%s},port={%d},agentRegisterUrl={%s},agentVersion={%s},agentCollectType={%s},requestBody={%s}", responseString, ip, port, agentRegisterUrl, agentVersion, agentCollectType, requestBody)
            );
            throw new Exception(
                    String.format("register agent to agent-manager failed, the response is: %s, ip={%s},port={%d},agentRegisterUrl={%s},agentVersion={%s},agentCollectType={%s},requestBody={%s}", responseString, ip, port, agentRegisterUrl, agentVersion, agentCollectType, requestBody)
            );
        }
    }

    /**
     * 读取本地的备份配置
     *
     * @return logConfig of local
     */
    public AgentConfig getLocalConfig() {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(local);
        } catch (IOException e) {
            CONFIG_LOGGER.warn("not exists local config file:" + local);
            return null;
        }

        byte[] bytes;
        try {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        } catch (IOException e) {
            CONFIG_LOGGER.warn("can't read local config:" + local, e);
            return null;
        }

        if (bytes.length == 0) {
            return null;
        }

        AgentConfig config;
        try {
            config = buildAgentConfigFromString(new String(bytes));
            if (config.getErrorLogConfig() == null || config.getMetricConfig() == null) {
                // 防止读取旧版本的logAgent的本地配置
                CONFIG_LOGGER.warn("config is old version. ignore!");
                return null;
            }
        } catch (Exception e) {
            CONFIG_LOGGER.warn("config parse error for clazz LogConfig.class", e);
            return null;
        }
        return config;
    }

    public AgentConfig buildAgentConfigFromString(String input) {
        AgentConfig agentConfig = null;
        try {
            /*if (StringUtils.isNotBlank(input) && !"{}".equals(input)) {
                // 未构建出modelConfig
                agentConfig = JSONObject.parseObject(input, AgentConfig.class);

                // modelConfig单独构建
                List<ModelConfig> modelConfigs = new ArrayList<>();
                JSONArray models = JSONObject.parseObject(input).getJSONArray("modelConfigs");
                if (models != null) {
                    for (int i = 0; i < models.size(); i++) {
                        JSONObject object = models.getJSONObject(i);
                        ModelConfig modelConfig = getModelConfigFromJSONObject(object);
                        modelConfigs.add(modelConfig);
                    }
                }
                agentConfig.setModelConfigs(modelConfigs);
            }*/
            AgentCollectConfigDOImpl agentCollectConfigurationImpl = JSONObject.parseObject(input, AgentCollectConfigDOImpl.class);
            return agentCollectConfigurationImpl.convertToAgentConfig();
        } catch (Exception e) {
            CONFIG_LOGGER.error("buildAgentConfigFromString error! input is " + input, e);
        }
        return agentConfig;
    }

    /**
     * 备份配置
     *
     * @param agentConfig latest agentConfig
     * @return result of write agentConfig
     */
    public boolean writeLocalConfig(AgentConfig agentConfig) {
        String json = JSON.toJSONString(agentConfig, SerializerFeature.DisableCircularReferenceDetect);
        CONFIG_LOGGER.info("record local agentConfig : " + json);
        File localFile = new File(local);
        agentConfig.getClass().getCanonicalName();
        try {
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(localFile);
            outputStream.write(json.getBytes("utf-8"));
        } catch (Exception e) {
            CONFIG_LOGGER.warn("write local agentConfig error, filePath is {}.", localFile.getAbsolutePath(), e);
            return false;
        }
        return true;
    }

    public static Map<String, String> getParam() {
        return param;
    }

    public static void setParam(Map<String, String> param) {
        ConfigService.param = param;
    }

    @Override
    public boolean onChange(AgentConfig config) {
        return false;
    }
}
