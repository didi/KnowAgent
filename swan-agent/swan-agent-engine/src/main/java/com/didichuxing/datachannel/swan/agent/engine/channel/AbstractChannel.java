package com.didichuxing.datachannel.swan.agent.engine.channel;

import com.didichuxing.datachannel.swan.agent.engine.bean.Event;
import com.didichuxing.datachannel.swan.agent.engine.component.TaskComponent;
import com.didichuxing.datachannel.swan.agent.engine.conf.Configurable;

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
