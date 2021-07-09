package com.didichuxing.datachannel.agent.common.configs.v1.limit;

public class LimitNodeConfig implements Cloneable {

    private long startThrehold; // 起始阀值
    private long minThreshold; // 最小阀值
    private int  level;        // 优先级 越小优先级越高，范围 [0-9]

    public long getStartThrehold() {
        return startThrehold;
    }

    public void setStartThrehold(long startThrehold) {
        this.startThrehold = startThrehold;
    }

    public long getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(long minThreshold) {
        this.minThreshold = minThreshold;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "LimitNodeConfig{" + "startThrehold=" + startThrehold + ", minThreshold="
               + minThreshold + ", level=" + level + '}';
    }

    @Override
    public LimitNodeConfig clone() {
        LimitNodeConfig limitNodeConfig = null;
        try {
            limitNodeConfig = (LimitNodeConfig) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return limitNodeConfig;
    }
}
