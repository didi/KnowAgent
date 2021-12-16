package com.didichuxing.datachannel.agent.engine.metrics.source;

import java.util.Map;

import com.didichuxing.datachannel.agent.common.metrics.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.metrics.metric.MetricsField;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
    jvmMetrics	jvm参数：gc次数和耗时
    cpuMetrics	周期内cpu的最高消耗
    limitRate	该采集任务的当前限流比例
    limitCount	当前周期内的限流阈值
*/
public class AgentStatistics extends AbstractStatistics {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentStatistics.class);
    private LimitService        limiter;

    private Long                startTime;

    public AgentStatistics(String name, LimitService limiter, Long startTime) {
        super(name);
        this.limiter = limiter;
        this.startTime = startTime;
    }

    @Override
    public void init() {
        Map<String, String> settings = null;
        try {
            settings = CommonUtils.readSettings();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (settings == null) {
            LOGGER.error("setting is null");
            LogGather.recordErrorLog("AgentStatistics error", "get local settings error.");
            throw new NullPointerException();
        }
        String messageVersion = settings.get(LogConfigConstants.MESSSAGE_VERSION);
        metricsRegistry.tag(MetricsField.MESSAGE_VERSION.getAgentFieldName(), null,
            messageVersion != null ? messageVersion : "-1");
        metricsRegistry.tag(
            MetricsField.START_TIME.getAgentFieldName(),
            null,
            startTime != null ? String.valueOf(startTime) : String.valueOf(System
                .currentTimeMillis()));
        super.init();
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {
        metricsRegistry.tag(MetricsField.CPU_LIMIT.getAgentFieldName(), null,
            String.valueOf(limiter.getCpuThreshold()), true);
        metricsRegistry.tag(MetricsField.CPU_USAGE.getAgentFieldName(), null,
            String.valueOf(limiter.getCurrentCpuUsage()), true);
        metricsRegistry.tag(MetricsField.LIMIT_TPS.getAgentFieldName(), null,
            String.valueOf(limiter.getAllQps()), true);
        super.getMetrics(builder, all);
    }
}
