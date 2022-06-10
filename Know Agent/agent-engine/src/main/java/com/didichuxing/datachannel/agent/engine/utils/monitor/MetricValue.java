package com.didichuxing.datachannel.agent.engine.utils.monitor;

/**
 * 指标值
 */
public class MetricValue<T> {

    /**
     * 当前值
     */
    private T currentValue;
    /**
     * 当前周期均值
     */
    private T avg;
    /**
     * 当前周期标准差值
     */
    private T stdev;
    /**
     *
     */
    private T max;
    private T min;

}
