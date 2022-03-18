package com.didichuxing.datachannel.agent.engine.metrics.source;

import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsBuilder;
import com.didichuxing.datachannel.agent.engine.metrics.system.MetricsSource;
import com.didichuxing.datachannel.agent.engine.metrics.system.lib.MetricsRegistry;
import com.didichuxing.datachannel.agent.engine.metrics.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinbinbin
 * @version $Id: ReceiveInstrumentation.java, v 0.1 2017年10月01日 22:54 jinbinbin Exp $
 */
public class AbstractStatistics implements MetricsSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStatistics.class);
    protected String            name;
    protected MetricsRegistry   metricsRegistry;

    public AbstractStatistics(String name) {
        super();
        this.name = MetricService.NAME + "_" + name + "_MBean";
        metricsRegistry = new MetricsRegistry(name);
    }

    public void init() {
        LOGGER.info("init metric " + this.name);
        // 只会根据metricSetName注册一次，第二次无效
        MetricService.registerSrc(name, null, this);
        LOGGER.info("init metric " + this.name + " success!");
    }

    public void destory() {
        LOGGER.info("destory metric " + this.name);
        MetricService.unRegisterSrc(name);
        LOGGER.info("destroy metric " + this.name + " success!");
    }

    @Override
    public void getMetrics(MetricsBuilder builder, boolean all) {
        metricsRegistry.snapshot(builder.addRecord(metricsRegistry.name()), true);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractStatistics other = (AbstractStatistics) obj;
        if (name == null) {
            return other.name == null;
        } else
            return name.equals(other.name);
    }
}
