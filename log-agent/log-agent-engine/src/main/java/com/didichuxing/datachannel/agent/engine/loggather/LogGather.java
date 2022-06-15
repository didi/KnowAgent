package com.didichuxing.datachannel.agent.engine.loggather;

import com.didichuxing.datachannel.agent.engine.bean.GlobalProperties;
import com.didichuxing.datachannel.agent.engine.metrics.source.AgentStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LogGather {
    private static final ConcurrentMap<String, LogDetail> WARN_LOGS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, LogDetail> ERROR_LOGS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, LogDetail> INFO_LOGS = new ConcurrentHashMap<>();
    private static final Logger INFO_LOGGER = LoggerFactory.getLogger("gatherInfoLogger");
    private static final Logger WARN_LOGGER = LoggerFactory.getLogger("gatherWarnLogger");
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("gatherErrorLogger");
    private static final Logger METRICS_LOGGER = LoggerFactory.getLogger("gatherMetricsLogger");

    private static final Map<String, LogSink> METRICS_REGISTRY_MAP = new HashMap<>();
    private static final Map<String, LogSink> INFO_REGISTRY_MAP    = new HashMap<>();
    private static final Map<String, LogSink> WARN_REGISTRY_MAP    = new HashMap<>();
    private static final Map<String, LogSink> ERROR_REGISTRY_MAP   = new HashMap<>();

    public static void recordErrorLog(String logCode, String logMsg) {
        recordErrorLog(logCode, logMsg, null);
        handleErrorLogsStatistics();
    }

    public static void recordErrorLog(String logCode, String logMsg, Throwable e) {
        LogDetail detail = record(ERROR_LOGS, logCode, logMsg, e);
        if (detail != null) {
            if (ERROR_LOGGER.isErrorEnabled()) {
                String slog = detail.toString();
                errorTrigger(slog);
                if (e != null) {
                    ERROR_LOGGER.error(slog, e);
                } else {
                    ERROR_LOGGER.error(slog);
                }
            }
        }
        handleErrorLogsStatistics();
    }

    private static LogDetail record(ConcurrentMap<String, LogDetail> detailMap, String logCode, String logMsg,
                                    Throwable e) {
        boolean first = false;
        LogDetail detail = detailMap.get(logCode);
        if (detail == null) {
            detail = new LogDetail(logCode, logMsg, e);
            LogDetail oDetail = detailMap.putIfAbsent(logCode, detail);
            if (oDetail != null) {
                oDetail.incCount();
            } else {
                first = true;
            }
        } else {
            detail.incCount();
        }
        if (first) {
            return detail;
        }
        return null;
    }

    private static void errorTrigger(String log) {
        List<LogSink> sinkList = new ArrayList<>();
        synchronized (ERROR_REGISTRY_MAP) {
            sinkList.addAll(ERROR_REGISTRY_MAP.values());
        }

        for (LogSink logSink : sinkList) {
            logSink.log(log);
        }
        sinkList.clear();
    }

    public static void registryErrorSink(String name, LogSink logSink) {
        synchronized (ERROR_REGISTRY_MAP) {
            ERROR_REGISTRY_MAP.put(name, logSink);
        }
    }

    public static void unRegistryErrorSink(String name) {
        synchronized (ERROR_REGISTRY_MAP) {
            ERROR_REGISTRY_MAP.remove(name);
        }
    }

    private static void handleErrorLogsStatistics() {
        AgentStatistics agentStatistics = GlobalProperties.getAgentStatistics();
        if(null != agentStatistics) {
            agentStatistics.sendErrorLogsRecord();
        }
    }

}
