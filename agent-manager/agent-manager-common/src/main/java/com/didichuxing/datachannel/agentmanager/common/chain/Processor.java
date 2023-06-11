package com.didichuxing.datachannel.agentmanager.common.chain;

public interface Processor {
    void process(Context context, ProcessorChain chain);
}
