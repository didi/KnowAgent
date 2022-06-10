package com.didichuxing.datachannel.agent.task.log;

import java.util.List;
import java.util.Map;

import com.didichuxing.datachannel.agent.channel.log.LogChannel;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agentmanager.common.metrics.TaskMetrics;
import com.didichuxing.datachannel.agent.engine.monitor.Monitor;
import com.didichuxing.datachannel.agent.engine.sinker.AbstractSink;
import com.didichuxing.datachannel.agent.source.log.LogSource;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-29 14:26
 */
public class TestTask extends AbstractTask {

    public TestTask(ModelConfig modelConfig, LogSource logSource) {
        this.modelConfig = modelConfig;
        this.source = logSource;
        this.channel = new LogChannel(logSource, modelConfig.getChannelConfig());
        bulidUniqueKey();
    }

    @Override
    public boolean init(ComponentConfig config) {

        source.init(config);

        bulidUniqueKey();
        channel.init(null);

        configure(config);
        for (AbstractSink sinker : sinkers.values()) {
            if (!sinker.init(config)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected List<Monitor> getMonitors() {
        return null;
    }

    @Override
    public void prepare() {

    }

    @Override
    public boolean needToFlush(Event event) {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean flush() {
        return false;
    }

    @Override
    public void rollback() {

    }

    @Override
    public void commit() {

    }

    @Override
    public Map<String, Object> metric() {
        return null;
    }

    @Override
    public void addSink(int orderNum) {
    }

    @Override
    public void bulidUniqueKey() {
        String sourceId = source.getUniqueKey();
        String modelId = modelConfig.getCommonConfig().getModelId() + "";
        String tag = modelConfig.getTag();
        setUniqueKey(modelId + "_" + sourceId + "_" + tag);
    }

    @Override
    public void configure(ComponentConfig config) {

    }

    @Override
    public boolean canStop() {
        return false;
    }

    @Override
    public void setMetrics(TaskMetrics taskMetrics) {

    }
}
