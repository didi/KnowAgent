package com.didichuxing.datachannel.agent.engine.component;

import java.util.Map;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;

/**
 * @description: 任务级别组件
 * @author: huangjw
 * @Date: 19/7/2 18:11
 */
public abstract class TaskComponent extends Component {

    /**
     * 唯一编码
     */
    public String uniqueKey;

    public abstract boolean init(ComponentConfig config);

    public abstract void bulidUniqueKey();

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public abstract boolean delete();

    public abstract Map<String, Object> metric();
}
