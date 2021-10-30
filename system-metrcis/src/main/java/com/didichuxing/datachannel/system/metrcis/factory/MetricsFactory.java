package com.didichuxing.datachannel.system.metrcis.factory;

/**
 * 指标工厂
 * @author william.hu
 */
public interface MetricsFactory {

    /**
     * @return 创建系统指标集
     */
    SystemMetrics createSystemMetrics();

    /**
     * @return 创建进程指标集
     */
    ProcMetrics createProcMetrics();

}
