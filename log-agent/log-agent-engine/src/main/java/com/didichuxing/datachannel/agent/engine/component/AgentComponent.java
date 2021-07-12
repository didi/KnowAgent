package com.didichuxing.datachannel.agent.engine.component;

import com.didichuxing.datachannel.agent.common.configs.v2.AgentConfig;

/**
 * @description: agent级别组件
 * @author: huangjw
 * @Date: 19/7/2 14:57
 */
public abstract class AgentComponent extends Component {

    public abstract boolean init(AgentConfig config);

    public abstract boolean onChange(AgentConfig config);
}
