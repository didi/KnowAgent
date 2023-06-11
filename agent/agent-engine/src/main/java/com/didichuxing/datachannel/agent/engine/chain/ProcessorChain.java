package com.didichuxing.datachannel.agent.engine.chain;

import java.util.ArrayList;
import java.util.List;

public class ProcessorChain implements Processor {

    private List<Processor> processorList = new ArrayList<>();
    private int index = 0;

    public ProcessorChain addProcessor(Processor processor) {
        processorList.add(processor);
        return this;
    }

    @Override
    public void process(Context context, ProcessorChain chain) {
        if(index == processorList.size()) {
            this.reset();
            return;
        }
        Processor processor = processorList.get(index);
        index++;
        processor.process(context, chain);
    }

    public void process(Context context) {
        this.process(context, this);
    }

    private void reset() {
        this.index = 0;
    }

}
