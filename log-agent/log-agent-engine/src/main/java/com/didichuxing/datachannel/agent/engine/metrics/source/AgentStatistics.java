package com.didichuxing.datachannel.agent.engine.metrics.source;

import java.util.Map;

import com.didichuxing.datachannel.agent.common.metrics.MetricsBuilder;
import com.didichuxing.datachannel.agent.common.api.LogConfigConstants;
import com.didichuxing.datachannel.agent.common.api.MetricsFields;
import com.didichuxing.datachannel.agent.common.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.limit.LimitService;
import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.utils.ProcessUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.PackageElement;

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
        metricsRegistry.tag(MetricsFields.MESSAGE_VERSION, null,
            messageVersion != null ? messageVersion : "-1");
        metricsRegistry.tag(
            MetricsFields.START_TIME,
            null,
            startTime != null ? String.valueOf(startTime) : String.valueOf(System
                .currentTimeMillis()));
        super.init();
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {
        metricsRegistry.tag(MetricsFields.CPU_LIMIT, null,
            String.valueOf(limiter.getCpuThreshold()), true);
        metricsRegistry.tag(MetricsFields.CPU_USAGE, null,
            String.valueOf(limiter.getCurrentCpuUsage()), true);
        metricsRegistry.tag(MetricsFields.TOTAL_CPU_USAGE, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentCpuUsageTotalPercent()), true);
        metricsRegistry.tag(MetricsFields.LIMIT_TPS, null, String.valueOf(limiter.getAllQps()),
            true);

        /*********************************** about agent process gc ***********************************/
        metricsRegistry.tag(MetricsFields.FULL_GC_COUNT, null,
            String.valueOf(ProcessUtils.getInstance().getFullGcCount()), true);
        metricsRegistry.tag(MetricsFields.FULL_GC_TIME, null,
            String.valueOf(ProcessUtils.getInstance().getFullGcTime()), true);
        metricsRegistry.tag(MetricsFields.YOUNG_GC_COUNT, null,
            String.valueOf(ProcessUtils.getInstance().getYoungGcCount()), true);
        metricsRegistry.tag(MetricsFields.YOUNG_GC_TIME, null,
            String.valueOf(ProcessUtils.getInstance().getYoungGcTime()), true);

        metricsRegistry.tag(MetricsFields.FD_COUNT, null,
            String.valueOf(ProcessUtils.getInstance().getFdCount()), true);

        /*********************************** about memory ***********************************/
        metricsRegistry.tag(MetricsFields.MEMORY_USAGE, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentMemoryUsage()), true);
        metricsRegistry.tag(MetricsFields.HEAP_MEMORY_USAGE, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessHeapMemoryUsed()), true);
        metricsRegistry.tag(MetricsFields.NON_HEAP_MEMORY_USAGE, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessNonHeapMemoryUsed()), true);
        metricsRegistry.tag(MetricsFields.MAX_HEAP_SIZE, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessMaxHeapSize()), true);
        metricsRegistry.tag(MetricsFields.MEMORY_USED_PEAK, null,
            String.valueOf(ProcessUtils.getInstance().getProcessMemoryUsedPeak()), true);

        /*********************************** about thread ***********************************/
        metricsRegistry.tag(MetricsFields.THREAD_NUM, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessThreadNum()), true);
        metricsRegistry.tag(MetricsFields.THREAD_NUM_PEAK, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessThreadNumPeak()), true);

        /*********************************** about io ***********************************/
        metricsRegistry.tag(MetricsFields.DISK_IO_READ_BYTES_PS, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessDiskIOReadBytesPS()), true);
        metricsRegistry.tag(MetricsFields.DISK_IO_WRITE_BYTES_PS, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessDiskIOWriteBytesPS()), true);
        metricsRegistry.tag(MetricsFields.DISK_IO_AWAIT_TIME, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessDiskIOAwaitTimePercent()),
            true);

        /*********************************** about network ***********************************/
        metricsRegistry.tag(MetricsFields.NETWORK_RX_BYTES_PS, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessNetworkReceiveBytesPS()),
            true);
        metricsRegistry.tag(MetricsFields.NETWORK_TX_BYTES_PS, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessNetworkSendBytesPS()), true);

        /************************** about network tcp **************************/
        metricsRegistry.tag(MetricsFields.TCP_CONNECTION_NUM, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessNetworkTcpConnectionNum()),
            true);
        metricsRegistry.tag(MetricsFields.TCP_TIME_WAIT_NUM, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessNetworkTcpTimeWaitNum()),
            true);
        metricsRegistry.tag(MetricsFields.TCP_CLOSE_WAIT_NUM, null,
            String.valueOf(ProcessUtils.getInstance().getCurrentProcessNetworkTcpCloseWaitNum()),
            true);

        super.getMetrics(builder, all);
    }
}
