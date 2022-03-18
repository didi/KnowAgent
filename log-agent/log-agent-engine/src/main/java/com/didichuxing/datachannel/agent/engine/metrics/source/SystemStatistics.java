package com.didichuxing.datachannel.agent.engine.metrics.source;

import com.didichuxing.datachannel.agent.common.api.MetricsFields;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsBuilder;
import com.didichuxing.datachannel.agent.engine.utils.SystemUtils;

public class SystemStatistics extends AbstractStatistics {

    public SystemStatistics(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {

        metricsRegistry.tag(MetricsFields.SYSTEM_STARTUP_TIME, null,
            String.valueOf(SystemUtils.getInstance().getSystemStartupTime()), true);

        /*********************************** about cpu ***********************************/
        metricsRegistry.tag(MetricsFields.SYSTEM_CPU_USAGE, null,
            String.valueOf(SystemUtils.getInstance().getCurrentSystemCpuUsage()), true);
        metricsRegistry.tag(MetricsFields.TOTAL_SYSTEM_CPU_USAGE, null,
            String.valueOf(SystemUtils.getInstance().getCurrentSystemCpuUsageTotalPercent()), true);

        /*********************************** about memory ***********************************/
        metricsRegistry.tag(MetricsFields.SYSTEM_MEMORY_FREE, null,
            String.valueOf(SystemUtils.getInstance().getCurrentSystemMemoryFree()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_MEMORY_USED, null,
            String.valueOf(SystemUtils.getInstance().getSystemMemoryUsed()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_MEMORY_TOTAL, null,
            String.valueOf(SystemUtils.getInstance().getSystemMemoryTotal()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_MEMORY_SWAP_FREE, null,
            String.valueOf(SystemUtils.getInstance().getSystemMemorySwapFree()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_MEMORY_SWAP_USED, null,
            String.valueOf(SystemUtils.getInstance().getSystemMemorySwapUsed()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_MEMORY_SWAP_SIZE, null,
            String.valueOf(SystemUtils.getInstance().getSystemMemorySwapSize()), true);

        metricsRegistry.tag(MetricsFields.SYSTEM_TCP_ACTIVE_OPENS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkTcpActiveOpens()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_TCP_PASSIVE_OPENS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkTcpPassiveOpens()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_TCP_ATTEMPT_FAILS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkTcpAttemptFails()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_TCP_ESTAB_RESETS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkTcpEstabResets()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_TCP_RETRANS_SEGS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkTcpRetransSegs()), true);
        metricsRegistry
            .tag(MetricsFields.SYSTEM_TCP_EXT_LISTEN_OVERFLOWS, null,
                String.valueOf(SystemUtils.getInstance().getSystemNetworkTcpExtListenOverflows()),
                true);

        /************************** about network udp **************************/
        metricsRegistry.tag(MetricsFields.SYSTEM_UDP_IN_DATAGRAMS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkUdpInDatagrams()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_UDP_OUT_DATAGRAMS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkUdpOutDatagrams()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_UDP_IN_ERRORS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkUdpInErrors()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_UDP_NO_PORTS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkUdpNoPorts()), true);
        metricsRegistry.tag(MetricsFields.SYSTEM_UDP_SEND_BUFFER_ERRORS, null,
            String.valueOf(SystemUtils.getInstance().getSystemNetworkUdpSendBufferErrors()), true);

        super.getMetrics(builder, all);
    }

}
