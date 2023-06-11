package com.didichuxing.datachannel.agent.engine.metrics.metric;

import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-30 18:07
 */
public interface TaskMetricsFields {

    String PREFIX_         = ComponentType.TASK + ".";
    String PREFIX_METRICS_ = PREFIX_ + "metrics.";
    String PREFIX_LIMIT_   = PREFIX_ + "limit.";
}
