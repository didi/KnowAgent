package com.didichuxing.datachannel.agent.engine.chain.processors;

import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.chain.Context;
import lombok.Data;

/**
 * @author william.
 */
@Data
public class EventContext extends Context {

    /**
     * 单条日志信息
     */
    private Event event;

    public EventContext(Event event) {
        this.event = event;
    }

    public EventContext() {
    }

}
