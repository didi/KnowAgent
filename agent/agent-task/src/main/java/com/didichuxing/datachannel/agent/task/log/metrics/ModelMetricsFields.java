package com.didichuxing.datachannel.agent.task.log.metrics;

import com.didichuxing.datachannel.agent.engine.metrics.metric.TaskMetricsFields;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-30 17:12
 */
public interface ModelMetricsFields extends TaskMetricsFields {

    String NS_NAME         = PREFIX_METRICS_ + "nsName";
    String NS_LEAF_NAME    = PREFIX_METRICS_ + "leaf";
    String MODEL_ID        = PREFIX_METRICS_ + "logModeId";
    String MODEL_VERSION   = PREFIX_METRICS_ + "logModelVersion";

    String DYNAMIC_LIMITER = PREFIX_LIMIT_ + "dynamicLimiter";
    String LIMIT_RATE      = PREFIX_LIMIT_ + "limitRate";

    String DOCKER_NAME     = PREFIX_LIMIT_ + "dockerName";
    String MODEL_HOST_NAME = PREFIX_METRICS_ + "logModelHostName";

}
