package com.didichuxing.datachannel.swan.agent.task.log;

import com.didichuxing.datachannel.swan.agent.engine.AbstractTask;
import com.didichuxing.datachannel.swan.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.swan.agent.source.log.LogSource;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-29 15:28
 */
public class TestLogModel extends LogModel {

    public TestLogModel(ModelConfig config) {
        super(config);
    }

    @Override
    public AbstractTask buildTask(ModelConfig config, LogSource logSource) {
        return new TestTask(modelConfig, logSource);
    }
}
