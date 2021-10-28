package com.didichuxing.datachannel.agentmanager.common.chain;

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
    public void process(Context context) {
        if(index == processorList.size()) {
            return;
        }
        Processor processor = processorList.get(index);
        index++;
        processor.process(context);
    }

}
