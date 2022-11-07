package com.didichuxing.datachannel.agent.engine.chain.processors;

import com.didichuxing.datachannel.agent.engine.chain.Context;
import com.didichuxing.datachannel.agent.engine.chain.Processor;
import com.didichuxing.datachannel.agent.engine.chain.ProcessorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author william.
 */
public abstract class BaseProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseProcessor.class);

    @Override
    public void process(Context context, ProcessorChain chain) {
        EventContext eventContext = (EventContext) context;
        /*
         * 执行该处理器逻辑
         */
        try {
            process(eventContext);
        } catch (Exception ex) {
            LOGGER.error(
                String.format("class=BaseProcessor|method=process|errMsg=%s", ex.getMessage()), ex);
        }
        /*
         * 执行下一个处理器对应处理逻辑
         */
        chain.process(context, chain);
    }

    /**
     * 执行该处理器逻辑
     * @param context 执行器上下文对象
     */
    protected abstract void process(EventContext context);

}
