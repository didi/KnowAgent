package com.didichuxing.datachannel.system.metrcis.factory.linux.mac;

import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.service.*;
import com.didichuxing.datachannel.system.metrcis.service.macos.*;

import java.util.Map;

public class MacOSMetricsServiceFactory implements MetricsServiceFactory {
    @Override
    public SystemMetricsService createSystemMetrics() throws MetricsException {
        return new MacOSSystemMetricsServiceImpl();
    }

    @Override
    public ProcessMetricsService createProcessMetrics() {
        return new MacOSProcessMetricsServiceImpl();
    }

    @Override
    public DiskIOMetricsService createDiskIOMetricsService() {
        return new MacOSDiskIOMetricsServiceImpl();
    }

    @Override
    public DiskMetricsService createDiskMetricsService() {
        return new MacOSDiskMetricsServiceImpl();
    }

    @Override
    public NetCardMetricsService createNetCardMetricsService() {
        return new MacOSNetCardMetricsServiceImpl();
    }

    @Override
    public Map<Class, Object> getMetricsServiceMap() {
        return null;
    }
}
