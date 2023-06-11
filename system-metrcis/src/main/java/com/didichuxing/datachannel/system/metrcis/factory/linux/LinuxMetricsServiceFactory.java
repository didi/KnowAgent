package com.didichuxing.datachannel.system.metrcis.factory.linux;

import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.factory.MetricsServiceFactory;
import com.didichuxing.datachannel.system.metrcis.service.*;
import com.didichuxing.datachannel.system.metrcis.service.linux.*;

import java.util.HashMap;
import java.util.Map;

public class LinuxMetricsServiceFactory implements MetricsServiceFactory {

    private LinuxSystemMetricsServiceImpl linuxSystemMetricsService = LinuxSystemMetricsServiceImpl.getInstance();
    private LinuxProcessMetricsServiceImpl linuxProcessMetricsService = LinuxProcessMetricsServiceImpl.getInstance();
    private LinuxDiskIOMetricsServiceImpl linuxDiskIOMetricsService = LinuxDiskIOMetricsServiceImpl.getInstance();
    private LinuxDiskMetricsServiceImpl linuxDiskMetricsService = LinuxDiskMetricsServiceImpl.getInstance();
    private LinuxNetCardMetricsServiceImpl linuxNetCardMetricsService = LinuxNetCardMetricsServiceImpl.getInstance();

    private Map<Class, Object> metricsServiceClass2MetricsServiceInstanceMap;

    private static LinuxMetricsServiceFactory instance;

    public static synchronized LinuxMetricsServiceFactory getInstance() {
        if(null == instance) {
            instance = new LinuxMetricsServiceFactory();
        }
        return instance;
    }

    private LinuxMetricsServiceFactory() {
        this.metricsServiceClass2MetricsServiceInstanceMap = new HashMap<>();
        metricsServiceClass2MetricsServiceInstanceMap.put(LinuxSystemMetricsServiceImpl.class, linuxSystemMetricsService);
        metricsServiceClass2MetricsServiceInstanceMap.put(LinuxProcessMetricsServiceImpl.class, linuxProcessMetricsService);
        metricsServiceClass2MetricsServiceInstanceMap.put(LinuxDiskIOMetricsServiceImpl.class, linuxDiskIOMetricsService);
        metricsServiceClass2MetricsServiceInstanceMap.put(LinuxDiskMetricsServiceImpl.class, linuxDiskMetricsService);
        metricsServiceClass2MetricsServiceInstanceMap.put(LinuxNetCardMetricsServiceImpl.class, linuxNetCardMetricsService);
    }

    @Override
    public Map<Class, Object> getMetricsServiceMap() {
        return metricsServiceClass2MetricsServiceInstanceMap;
    }

    @Override
    public SystemMetricsService createSystemMetrics() throws MetricsException {
        return linuxSystemMetricsService;
    }

    @Override
    public ProcessMetricsService createProcessMetrics() {
        return linuxProcessMetricsService;
    }

    @Override
    public DiskIOMetricsService createDiskIOMetricsService() {
        return linuxDiskIOMetricsService;
    }

    @Override
    public DiskMetricsService createDiskMetricsService() {
        return linuxDiskMetricsService;
    }

    @Override
    public NetCardMetricsService createNetCardMetricsService() {
        return linuxNetCardMetricsService;
    }

}
