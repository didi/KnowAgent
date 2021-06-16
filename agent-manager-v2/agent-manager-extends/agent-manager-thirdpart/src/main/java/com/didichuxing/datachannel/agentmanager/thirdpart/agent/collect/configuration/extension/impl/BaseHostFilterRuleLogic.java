package com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension.impl;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机过滤规则逻辑基类
 */
public class BaseHostFilterRuleLogic {

    /**
     * 主机过滤规则类型
     */
    private int hostFilterRuleType;

    public int getHostFilterRuleType() {
        return hostFilterRuleType;
    }

    @Override
    public String toString() {
        return "BaseHostFilterRuleLogic{" +
                "hostFilterRuleType=" + hostFilterRuleType +
                '}';
    }

}
