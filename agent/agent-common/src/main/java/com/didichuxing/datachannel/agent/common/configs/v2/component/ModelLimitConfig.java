package com.didichuxing.datachannel.agent.common.configs.v2.component;

/**
 * @description: 任务级别的limit配置
 * @author: huangjw
 * @Date: 19/7/1 14:41
 */
public class ModelLimitConfig implements Cloneable {

    // 可动态调整
    private long startThrehold = 20 * 1024L; // 起始阀值 k/s
    private long minThreshold  = 1L;        // 最小阀值 k/s
    private int  level         = 5;         // 优先级 越小优先级越高，范围 [0-9]

    // 设置该值时，表示无法动态调整
    private int  rate;                      // 固定采集速度，单位byte/s

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "TaskLimitConfig{" + "startThrehold=" + startThrehold + ", minThreshold="
               + minThreshold + ", level=" + level + ", rate=" + rate + '}';
    }

    @Override
    public ModelLimitConfig clone() {
        ModelLimitConfig limitConfig = null;
        try {
            limitConfig = (ModelLimitConfig) super.clone();
        } catch (CloneNotSupportedException e) {

        }
        return limitConfig;
    }
}
