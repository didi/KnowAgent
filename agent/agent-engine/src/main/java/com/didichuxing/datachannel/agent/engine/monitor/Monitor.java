package com.didichuxing.datachannel.agent.engine.monitor;

import com.didichuxing.datachannel.agent.engine.component.TaskComponent;

/**
 * @description: 监控
 * @author: huangjw
 * @Date: 19/7/4 14:17
 */
public interface Monitor {

    /**
     * 监控注册
     * @param task
     * @return
     */
    boolean register(TaskComponent task);

    /**
     * 监控注销
     * @param task
     * @return
     */
    boolean unregister(TaskComponent task);

    void start();

    void stop();
}
