package com.didichuxing.datachannel.agent.engine.channel;

import com.didichuxing.datachannel.agent.engine.bean.Event;
import com.didichuxing.datachannel.agent.engine.conf.Configurable;
import com.didichuxing.datachannel.agent.engine.component.TaskComponent;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/6/18 16:23
 */
public abstract class AbstractChannel extends TaskComponent implements Configurable {

    abstract public void tryAppend(Event event);

    abstract public Event tryGet(long timeout);

    abstract public Event tryGet();

    abstract public void commitEvent(Event event);

    abstract public void commit();

    abstract public int size();
}
