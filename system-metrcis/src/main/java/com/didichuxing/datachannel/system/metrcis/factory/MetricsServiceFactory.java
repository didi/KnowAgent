package com.didichuxing.datachannel.system.metrcis.factory;

import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;

/**
 * 指标服务工厂
 * @author william.hu
 */
public interface MetricsServiceFactory {

    /**
     * @return 创建系统指标服务
     */
    SystemMetricsService createSystemMetrics();

    /**
     * @return 创建进程指标服务
     */
    ProcessMetricsService createProcessMetrics();

}
