package com.didichuxing.datachannel.agent.engine.chain;

public interface Processor {
    void process(Context context, ProcessorChain chain);
}
