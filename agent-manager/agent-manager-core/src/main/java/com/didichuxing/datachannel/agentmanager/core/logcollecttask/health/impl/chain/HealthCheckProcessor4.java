package com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.impl.chain;

import com.didichuxing.datachannel.agentmanager.common.chain.Context;
import com.didichuxing.datachannel.agentmanager.common.chain.HealthCheckProcessorAnnotation;
import com.didichuxing.datachannel.agentmanager.common.chain.Processor;
import com.didichuxing.datachannel.agentmanager.common.enumeration.HealthCheckProcessorEnum;

@HealthCheckProcessorAnnotation(seq = 4, type = HealthCheckProcessorEnum.LOGCOLLECTTASK)
public class HealthCheckProcessor4 implements Processor {

    @Override
    public void process(Context context) {

    }

}
