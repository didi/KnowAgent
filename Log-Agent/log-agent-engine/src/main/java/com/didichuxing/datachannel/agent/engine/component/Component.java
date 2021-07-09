package com.didichuxing.datachannel.agent.engine.component;

import com.didichuxing.datachannel.agent.common.api.ComponentStatus;

/**
 * @description: 组件抽象类
 * @author: huangjw
 * @Date: 19/6/28 18:02
 */
public abstract class Component {

    private int status = ComponentStatus.RUNNING.getStatus();

    public abstract boolean start();

    public abstract boolean stop(boolean force);

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
