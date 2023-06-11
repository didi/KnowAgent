package com.didichuxing.datachannel.agent.common.configs.v2.component.targetConfig;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description: 目的地配置
 * @author: huangjw
 * @Date: 19/7/1 14:42
 */
public class TargetConfig extends ComponentConfig {

    /**
     * sink数量
     */
    private int sinkNum = 1;

    public TargetConfig(String tag) {
        super(ComponentType.SINK, tag);
    }

    public int getSinkNum() {
        return sinkNum;
    }

    public void setSinkNum(int sinkNum) {
        this.sinkNum = sinkNum;
    }

    @Override
    public String toString() {
        return "TargetConfig{" + "sinkNum=" + sinkNum + '}';
    }
}
