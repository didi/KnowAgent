package com.didichuxing.datachannel.system.metrcis.factory.linux;

import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.service.*;
import com.didichuxing.datachannel.system.metrcis.service.linux.*;

public class LinuxMetricsServiceFactory implements MetricsServiceFactory {

    @Override
    public SystemMetricsService createSystemMetrics() throws MetricsException {
        return new LinuxSystemMetricsServiceImpl();
    }

    @Override
    public ProcessMetricsService createProcessMetrics() {
        return new LinuxProcessMetricsServiceImpl();
    }

    @Override
    public DiskIOMetricsService createDiskIOMetricsService() {
        return new LinuxDiskIOMetricsServiceImpl();
    }

    @Override
    public DiskMetricsService createDiskMetricsService() {
        return new LinuxDiskMetricsServiceImpl();
    }

    @Override
    public NetCardMetricsService createNetCardMetricsService() {
        return new LinuxNetCardMetricsServiceImpl();
    }

}
