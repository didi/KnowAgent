package com.didichuxing.datachannel.agent.engine.source;

import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.conf.Configurable;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.agent.common.configs.v2.component.sourceConfig.SourceConfig;
import com.didichuxing.datachannel.agent.engine.metrics.source.TaskPatternStatistics;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/6/18 15:55
 */
public abstract class AbstractSource extends TaskComponent implements Configurable {

    protected TaskPatternStatistics taskPatternStatistics;

    protected SourceConfig          sourceConfig;

    abstract public Event tryGetEvent();

    public AbstractSource(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    public TaskPatternStatistics getTaskPatternStatistics() {
        return taskPatternStatistics;
    }

    public void setTaskPatternStatistics(TaskPatternStatistics taskPatternStatistics) {
        this.taskPatternStatistics = taskPatternStatistics;
    }

    public abstract boolean specialDelete(Object object);
}
