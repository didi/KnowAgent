package com.didichuxing.datachannel.agent.engine.limit;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelLimitConfig;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.limit.tune.TuneNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitNode {

    private static final Logger LOGGER         = LoggerFactory.getLogger(LimitNode.class);
    private static float        FACTOR         = 0.3f;
    private static long         INTERVAL       = 1000;
    private static long         NUM_INTERVAL   = 1;
    private static long         FLUSH_INTERVAL = 1000L;

    private String              name;
    private LimitService        limiter;
    private ModelLimitConfig    config;

    private TuneNode            tuneNode;                                                 // 限流调整模块
    private TaskLimit           taskLimit;

    // 统计信息
    private long                startTime;                                                // 当前周期的开始时间
    private long                bytesCount;                                               // 当前周期已经发送的字节数

    public LimitNode(String name, LimitService limiter, ModelLimitConfig config, TaskLimit taskLimit) {
        this.limiter = limiter;
        this.name = name;
        this.config = config;

        this.tuneNode = new TuneNode(this);

        this.startTime = System.currentTimeMillis();
        this.bytesCount = 0;
        this.taskLimit = taskLimit;
    }

    public double limit(int bytes) {
        // 判断是否限流
        double result = taskLimit.limit(bytes);
        if (result > 0.0d) {
            tuneNode.setIslimited(true);
        }

        // 是否经过一个周期
        flush();

        bytesCount += bytes;
        return result;
    }

    public synchronized double limit(AbstractTask abstractTask, int bytes) {
        try {
            return limiter.limit(abstractTask, bytes);
        } catch (Exception e) {
            LOGGER.error("LimitNode.limit", "LimitNode limit error, {}", e.getMessage());
        }
        return 0.0d;
    }

    // 计算过去一秒的各个参数
    private void flush() {
        long current = System.currentTimeMillis();

        if ((current - startTime) <= FLUSH_INTERVAL) {
            return;
        }

        // 记录下当前的qps
        if (tuneNode.getAvgQPS() == 0) {
            tuneNode.setAvgQPS(bytesCount / NUM_INTERVAL);
        } else {
            tuneNode.setAvgQPS((long) (tuneNode.getAvgQPS() * (1 - FACTOR) + bytesCount * FACTOR)
                               / NUM_INTERVAL);
        }

        // 重置数据
        bytesCount = 0;
        startTime = current;
    }

    public void reset(ModelLimitConfig config) {
        this.config = config;
    }

    public TuneNode getTuneNode() {
        return tuneNode;
    }

    public long getMinThreshold() {
        return Math.max(limiter.getConfig().getMinThreshold(), config.getMinThreshold());
    }

    public int getLevel() {
        return config.getLevel();
    }

    public long getStartThreshold() {
        return config.getStartThrehold();
    }

    public TaskLimit getTaskLimit() {
        return taskLimit;
    }

    public void setTaskLimit(TaskLimit taskLimit) {
        this.taskLimit = taskLimit;
    }
}
