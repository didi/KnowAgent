package com.didichuxing.datachannel.agent.engine.metrics.system.lib;

import com.didichuxing.datachannel.agent.engine.metrics.system.Metric;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsVisitor;

/**
 * 
 * 
 * @author liujianhui
 * @version:2015年12月3日 下午5:20:35
 */
public class MetricReference<T> extends Metric {

    private final T value;

    public MetricReference(String name, String description, T value) {
        super(name, description);
        this.value = value;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public void visit(MetricsVisitor visitor) {
        //TODO
    }

}
