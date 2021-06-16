package com.didichuxing.datachannel.swan.agent.source.log.utils;

import java.util.concurrent.ConcurrentHashMap;

import com.didichuxing.tunnel.util.log.ILog;
import com.didichuxing.tunnel.util.log.LogFactory;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/9 14:18
 */
public class Md5ConfigService {

    private static final ILog                        LOGGER         = LogFactory
                                                                        .getLog(Md5ConfigService.class
                                                                            .getName());

    private static ConcurrentHashMap<String, String> logPath2Md5Map = new ConcurrentHashMap<String, String>();

    public static ConcurrentHashMap<String, String> getLogPath2Md5Map() {
        return logPath2Md5Map;
    }

    public static void setLogPath2Md5Map(String logPathKey, String md5) {
        LOGGER.info("set logPath2Md5Map.logPathKey is " + logPathKey + ", md5 is " + md5);
        logPath2Md5Map.put(logPathKey, md5);
    }
}
