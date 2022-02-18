package com.didichuxing.datachannel.system.metrcis.factory.linux;

import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxProcessMetricsServiceImpl;
import com.didichuxing.datachannel.system.metrcis.service.linux.LinuxSystemMetricsServiceImpl;

public class LinuxMetricsServiceFactory implements MetricsServiceFactory {

    @Override
    public SystemMetricsService createSystemMetrics() {
        return new LinuxSystemMetricsServiceImpl();
    }

    @Override
    public ProcessMetricsService createProcessMetrics() {
        return new LinuxProcessMetricsServiceImpl();
    }

}
