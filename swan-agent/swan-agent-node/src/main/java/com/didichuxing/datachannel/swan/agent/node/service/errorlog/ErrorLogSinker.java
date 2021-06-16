package com.didichuxing.datachannel.swan.agent.node.service.errorlog;

import com.didichuxing.datachannel.swan.agent.common.configs.v2.ErrorLogConfig;
import com.didichuxing.datachannel.swan.agent.engine.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.swan.agent.common.api.MetricsFields;
import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.ILogSink;
import com.didichuxing.tunnel.util.log.LogFactory;
import com.didichuxing.tunnel.util.log.LogGather;

/**
 * @description: 错误日志
 * @author: huangjw
 * @Date: 2019-07-17 11:46
 */
public class ErrorLogSinker implements ILogSink {

    private static final ILog LOGGER = LogFactory.getLog(ErrorLogSinker.class.getName());

    private KafkaSender       sender;

    public ErrorLogSinker(ErrorLogConfig config) throws Exception {
        sender = new KafkaSender(config);
        LOGGER.info("success init ErrorLogSinker by config:" + config);
    }

    public void stop() {
        LOGGER.info("begin to stop ErrorLogSinker.");
        sender.stop();
        LOGGER.info("stop ErrorLogSinker success");
    }

    @Override
    public void log(String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        try {
            JSONObject object = JSON.parseObject(content);
            object.put(MetricsFields.HOST_NAME, CommonUtils.getHOSTNAME());
            object.put(MetricsFields.HOST_IP, CommonUtils.getHOSTIP());
            object.put(MetricsFields.HEARTBEAT_TIME, System.currentTimeMillis());

            String c = JSON.toJSONString(object);

            sender.send(c);
        } catch (Exception e) {
            LOGGER.error("error log send error!" + "content is " + content, e);
        }
    }

    public void onChange(ErrorLogConfig newConfig) throws Exception {
        if (!sender.onChange(newConfig)) {
            return;
        }

        KafkaSender s = new KafkaSender(newConfig);
        KafkaSender oldSender = sender;

        this.sender = s;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LogGather.recordErrorLog("ErrorLogSinker error", "on change sleep error", e);
        }

        oldSender.stop();
    }

}
