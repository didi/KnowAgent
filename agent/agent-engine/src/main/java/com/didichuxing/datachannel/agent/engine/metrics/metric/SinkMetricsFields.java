package com.didichuxing.datachannel.agent.engine.metrics.metric;

import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-30 17:57
 */
public interface SinkMetricsFields {

    String PREFIX_         = ComponentType.SINK + ".";
    String PREFIX_TYPE     = PREFIX_ + "type";
    String PREFIX_METRICS_ = PREFIX_ + "metrics.";
}
