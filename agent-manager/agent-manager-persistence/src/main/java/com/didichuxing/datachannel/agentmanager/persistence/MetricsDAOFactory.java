package com.didichuxing.datachannel.agentmanager.persistence;

/**
 * 指标数据访问接口抽象工厂类
 * @author william.
 */
public interface MetricsDAOFactory {

    /**
     * @return 返回创建的系统指标数据访问对象实例
     */
    MetricsSystemDAO createMetricsSystemDAO();

    /**
     * @return 返回创建的 Agent 业务指标数据访问对象实例
     */
    MetricsAgentDAO createMetricsAgentDAO();

    /**
     * @return 返回创建的 Disk 相关指标数据访问对象实例
     */
    MetricsDiskDAO createMetricsDiskDAO();

    /**
     * @return 返回创建的 Net Card 相关指标数据访问对象实例
     */
    MetricsNetCardDAO createMetricsNetCardDAO();

    /**
     * @return 返回创建的 Agent 进程指标数据访问对象实例
     */
    MetricsProcessDAO createMetricsProcessDAO();

    /**
     * @return 返回创建的采集任务指标数据访问对象实例
     */
    MetricsLogCollectTaskDAO createMetricsLogCollectTaskDAO();

    /**
     * @return 返回创建的 Disk IO 相关指标数据访问对象实例
     */
    MetricsDiskIODAO createMetricsDiskIODAO();

}
