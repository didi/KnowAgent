package com.didichuxing.datachannel.agent.sink.utils.serializer;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;

/**
 * @description: eventList序列化器
 * @author: huangjw
 * @Date: 18/8/20 16:34
 */
public class EventListSerializer {

    private SerializeWriter                out;
    private JSONSerializer                 ownSerializer;
    private List<LogEventSerializerObject> list;

    public EventListSerializer(){
        out = new SerializeWriter();
        ownSerializer = new JSONSerializer(out);
        list = new ArrayList<>();
    }

    public void append(LogEventSerializerObject object) {
        list.add(object);
    }

    public String toListJsonString() {
        try {
            out.append('[');
            EventItemSerializer agentMapSerializer;
            for (int i = 0, size = list.size(); i < size; ++i) {
                LogEventSerializerObject item = list.get(i);
                if (i != 0) {
                    out.append(',');
                }

                if (item == null) {
                    out.append("null");
                } else {
                    agentMapSerializer = new EventItemSerializer();
                    agentMapSerializer.write(ownSerializer, item);
                }
            }
            ownSerializer.decrementIdent();
            out.append(']');
            return out.toString();
        } catch (Exception e) {
            LogGather.recordErrorLog("EventListSerializer error!", "toListJsonString error!", e);
        } finally {
            out.close();
        }
        return null;
    }
}
