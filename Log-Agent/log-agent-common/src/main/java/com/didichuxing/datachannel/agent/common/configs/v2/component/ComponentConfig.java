package com.didichuxing.datachannel.agent.common.configs.v2.component;

/**
 * @description: 组件基本配置
 * @author: huangjw
 * @Date: 19/7/1 16:11
 */
public class ComponentConfig {

    String tag;

    String type;

    public ComponentConfig(String type, String tag) {
        this.type = type;
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
