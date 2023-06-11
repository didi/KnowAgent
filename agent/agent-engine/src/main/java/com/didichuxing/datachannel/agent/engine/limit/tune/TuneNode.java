package com.didichuxing.datachannel.agent.engine.limit.tune;

import com.didichuxing.datachannel.agent.engine.limit.LimitNode;

public class TuneNode {

    private LimitNode limitNode;

    private boolean   islimited;    // 是否被限流
    private long      avgQPS;       // 上一个周期的流量

    // 临时变量
    private int       level;        // 优先等级
    private long      minThreshold;
    private long      needThreshold;

    private long      threshold;    // 当前生效的阀值

    public TuneNode(LimitNode limitNode) {
        this.limitNode = limitNode;

        threshold = limitNode.getStartThreshold();
    }

    public void reset() {
        // 获得当前node的最小qps
        this.minThreshold = limitNode.getMinThreshold();

        // 设置level
        this.level = limitNode.getLevel();

        // 设置需要的流量
        if (islimited) {
            needThreshold = (long) (threshold * (2.0f));
        } else {
            if (avgQPS < threshold * 0.6) {
                // 小于0.7时，需要开始调整限流值
                if (avgQPS < threshold * 0.4) {
                    needThreshold = (long) (threshold * 0.6 + avgQPS * 0.3);
                } else {
                    needThreshold = (long) (threshold * 0.8 + avgQPS * 0.3);
                }
            }
        }

        if (needThreshold < minThreshold) {
            needThreshold = minThreshold;
        }

        islimited = false;
    }

    public LimitNode getLimitNode() {
        return limitNode;
    }

    public void setLimitNode(LimitNode limitNode) {
        this.limitNode = limitNode;
    }

    public boolean isIslimited() {
        return islimited;
    }

    public void setIslimited(boolean islimited) {
        this.islimited = islimited;
    }

    public long getAvgQPS() {
        return avgQPS;
    }

    public void setAvgQPS(long avgQPS) {
        this.avgQPS = avgQPS;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(long minThreshold) {
        this.minThreshold = minThreshold;
    }

    public long getNeedThreshold() {
        return needThreshold;
    }

    public void setNeedThreshold(long needThreshold) {
        this.needThreshold = needThreshold;
    }

    public long getThreshold() {
        return threshold;
    }

    public void setThreshold(long threshold) {
        this.threshold = threshold;
        limitNode.getTaskLimit().reset((double) (threshold + 1), true);
    }
}
