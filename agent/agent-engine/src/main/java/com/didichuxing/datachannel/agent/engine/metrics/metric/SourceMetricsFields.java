package com.didichuxing.datachannel.agent.engine.metrics.metric;

import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-30 17:56
 */
public interface SourceMetricsFields {

    String PREFIX_         = ComponentType.SOURCE + ".";
    String PREFIX_TYPE     = PREFIX_ + "type";
    String PREFIX_METRICS_ = PREFIX_ + "metrics.";
}
