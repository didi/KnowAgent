package com.didichuxing.datachannel.agent.common.configs.v2.component.sourceConfig;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description: source抽象配置
 * @author: huangjw
 * @Date: 19/7/1 14:41
 */
public class SourceConfig extends ComponentConfig {

    public SourceConfig(String tag) {
        super(ComponentType.SOURCE, tag);
    }
}
