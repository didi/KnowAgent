package com.didichuxing.datachannel.agent.common.configs.v2;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/7/1 14:55
 */
public class LimitConfig {

    private float cpuThreshold;  // CPU利用率阀值
    private long  startThreshold; // 初始整体的限制阀值

    private long  minThreshold;  // 最小qps阀值

    public long getStartThreshold() {
        return startThreshold;
    }

    public void setStartThreshold(long startThreshold) {
        this.startThreshold = startThreshold;
    }

    public float getCpuThreshold() {
        return cpuThreshold;
    }

    public void setCpuThreshold(float cpuThreshold) {
        this.cpuThreshold = cpuThreshold;
    }

    public long getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(long minThreshold) {
        this.minThreshold = minThreshold;
    }

    @Override
    public String toString() {
        return "LimitConfig{" + "cpuThreshold=" + cpuThreshold + ", startThreshold="
               + startThreshold + ", minThreshold=" + minThreshold + '}';
    }

}
