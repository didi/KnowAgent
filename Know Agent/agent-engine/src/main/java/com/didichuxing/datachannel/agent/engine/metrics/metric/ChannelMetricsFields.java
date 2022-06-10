package com.didichuxing.datachannel.agent.engine.metrics.metric;

import com.didichuxing.datachannel.agent.common.constants.ComponentType;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-30 17:58
 */
public interface ChannelMetricsFields {

    String PREFIX_         = ComponentType.CHANNEL + ".";
    String PREFIX_TYPE     = PREFIX_ + "type";
    String PREFIX_METRICS_ = PREFIX_ + "metrics.";

    String PREFIX_SIZE     = PREFIX_METRICS_ + "channelSize";
    String PREFIX_CAPACITY = PREFIX_METRICS_ + "channelCapacity";
}
