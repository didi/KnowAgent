package com.didichuxing.datachannel.system.metrcis.factory.linux;

import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.service.ProcMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxProcMetricsServiceImpl;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxSystemMetricsServiceImpl;

public class LinuxMetricsServiceFactory implements MetricsServiceFactory {

    @Override
    public SystemMetricsService createSystemMetrics() {
        return new LinuxSystemMetricsServiceImpl();
    }

    @Override
    public ProcMetricsService createProcMetrics() {
        return new LinuxProcMetricsServiceImpl();
    }

}
