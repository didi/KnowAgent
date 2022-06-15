package com.didichuxing.datachannel.agent.common.configs.v2;

/**
 * @description: metrics上报配置
 * @author: huangjw
 * @Date: 19/7/1 14:45
 */
public class MetricConfig extends ProducerConfig {

    /**
     *  是否转换将新版本的metrics转换成老版本metrics
     */
    private boolean isTransfer = true;

    public boolean isTransfer() {
        return isTransfer;
    }

    public void setTransfer(boolean transfer) {
        isTransfer = transfer;
    }

    @Override
    public String toString() {
        return "MetricConfig{" + "isTransfer=" + isTransfer + '}';
    }
}
