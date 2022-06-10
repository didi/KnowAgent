package com.didichuxing.datachannel.agent.engine.limit;

import com.didichuxing.datachannel.agent.common.configs.v2.component.ComponentConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelConfig;
import com.didichuxing.datachannel.agent.common.configs.v2.component.ModelLimitConfig;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;
import com.didichuxing.datachannel.agent.engine.AbstractTask;
import com.didichuxing.datachannel.agent.engine.conf.Configurable;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 令牌桶限流
 * @author: huangjw
 * @Date: 19/7/2 11:37
 */
public class TaskLimit implements Configurable {

    private static final Logger LOGGER    = LoggerFactory.getLogger(TaskLimit.class.getName());
    private RateLimiter         limiter   = null;

    private volatile boolean    isRegular = false;

    private AbstractTask        abstractTask;

    private LimitNode           limitNode;

    public boolean init(AbstractTask abstractTask, ComponentConfig config) {
        try {
            this.abstractTask = abstractTask;
            configure(config);
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("TaskLimit error", "unexpected error, fileCollectTask is "
                                                        + this, e);
            return false;
        }
    }

    public boolean stop() {
        try {
            LimitService.LIMITER.unregister(this.abstractTask);
            return true;
        } catch (Exception e) {
            LogGather.recordErrorLog("TaskLimit error", "unexpected error, fileCollectTask is "
                                                        + this, e);
            return false;
        }
    }

    @Override
    public void configure(ComponentConfig config) {
        ModelLimitConfig taskLimitConfig = ((ModelConfig) config).getModelLimitConfig();
        LOGGER.info("begin to init limit. config is " + taskLimitConfig);
        if (taskLimitConfig.getRate() != 0) {
            // 表示固定
            limiter = RateLimiter.create(taskLimitConfig.getRate());
            isRegular = true;
        } else {
            // 非固定，可调整
            limiter = RateLimiter.create(taskLimitConfig.getStartThrehold());
            isRegular = false;
        }
        limitNode = new LimitNode(abstractTask.getUniqueKey(), LimitService.LIMITER,
            taskLimitConfig, this);
        LimitService.LIMITER.register(this.abstractTask, limitNode);
    }

    @Override
    public boolean onChange(ComponentConfig newOne) {
        if (newOne != null) {
            ModelLimitConfig taskLimitConfig = ((ModelConfig) newOne).getModelLimitConfig();
            LOGGER.info("change limit. new config is " + taskLimitConfig);
            if (taskLimitConfig.getRate() != 0) {
                // 表示固定
                reset(taskLimitConfig.getRate(), false);
                isRegular = true;
            } else {
                // 非固定，可调整
                reset(taskLimitConfig.getStartThrehold(), true);
                isRegular = false;
            }
            LimitService.LIMITER.onChangeNode(this.abstractTask, taskLimitConfig);
            return true;
        } else {
            LOGGER.warn("change limit. new config is null!");
            return false;
        }
    }

    public long getCurrentRate() {
        if (limitNode != null && limitNode.getTuneNode() != null) {
            return limitNode.getTuneNode().getThreshold();
        } else {
            return 0L;
        }
    }

    public double limitRate(int bytes) {
        return limitNode.limit(abstractTask, bytes);
    }

    public double limit(int bytes) {
        return limiter.acquire(bytes);
    }

    public void reset(double newRate, boolean isAuto) {
        if (!isAuto) {
            limiter.setRate(newRate);
        } else {
            if (!isRegular) {
                limiter.setRate(newRate);
            }
        }
    }
}
