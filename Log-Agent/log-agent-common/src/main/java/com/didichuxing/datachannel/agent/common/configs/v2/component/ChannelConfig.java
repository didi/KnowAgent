package com.didichuxing.datachannel.agent.common.configs.v2.component;

import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-15 17:00
 */
public class ChannelConfig extends ComponentConfig {

    /**
     * 最大的缓存数量
     */
    private Integer maxNum   = 1000;

    /**
     * 队列最大内存
     */
    private Long    maxBytes = 100 * 1024 * 1024L;

    public ChannelConfig() {
        super(ComponentType.CHANNEL, "memory");
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Long getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(Long maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Override
    public String toString() {
        return "ChannelConfig{" + "maxNum=" + maxNum + ", maxBytes=" + maxBytes + '}';
    }
}
