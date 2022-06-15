package com.didichuxing.datachannel.agent.sink.utils.serializer;

import java.util.Map;

import com.alibaba.fastjson.serializer.*;
import com.didichuxing.datachannel.agent.engine.loggather.LogGather;

/*
 * @description: event序列化器
 * @author: huangjw
 * @Date: 18/8/20 15:36
 */
public class EventItemSerializer {

    public void write(JSONSerializer serializer, LogEventSerializerObject object) {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }
        try {
            out.write("{");
            ObjectSerializer strWriter = new StringCodec();
            ObjectSerializer intWriter = new IntegerCodec();
            ObjectSerializer longWriter = new LongCodec();
            int i = 0;
            for (Map.Entry<String, String> entry : object.getStrMap().entrySet()) {
                if (i != 0) {
                    out.write(",");
                }
                out.writeFieldName(entry.getKey(), true);
                strWriter.write(serializer, entry.getValue(), entry.getKey(), null, 0);
                i++;
            }

            for (Map.Entry<String, Integer> entry : object.getIntMap().entrySet()) {
                out.write(",");
                out.writeFieldName(entry.getKey(), true);
                intWriter.write(serializer, entry.getValue(), entry.getKey(), null, 0);
            }

            for (Map.Entry<String, Long> entry : object.getLongMap().entrySet()) {
                out.write(",");
                out.writeFieldName(entry.getKey(), true);
                longWriter.write(serializer, entry.getValue(), entry.getKey(), null, 0);
            }

            out.write("}");
        } catch (Exception e) {
            LogGather.recordErrorLog("EventListSerializer error!", "toListJsonString error!", e);
        }
    }

    /**
     *
     * @param object
     * @return
     */
    public String toItemJsonString(LogEventSerializerObject object) {
        if (object == null) {
            return null;
        }

        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        try {
            out.write("{");
            ObjectSerializer strWriter = new StringCodec();
            ObjectSerializer intWriter = new IntegerCodec();
            ObjectSerializer longWriter = new LongCodec();
            int i = 0;
            for (Map.Entry<String, String> entry : object.getStrMap().entrySet()) {
                if (i != 0) {
                    out.write(",");
                }
                out.writeFieldName(entry.getKey(), true);
                strWriter.write(serializer, entry.getValue(), entry.getKey(), null, 0);
                i++;
            }

            for (Map.Entry<String, Integer> entry : object.getIntMap().entrySet()) {
                out.write(",");
                out.writeFieldName(entry.getKey(), true);
                intWriter.write(serializer, entry.getValue(), entry.getKey(), null, 0);
            }

            for (Map.Entry<String, Long> entry : object.getLongMap().entrySet()) {
                out.write(",");
                out.writeFieldName(entry.getKey(), true);
                longWriter.write(serializer, entry.getValue(), entry.getKey(), null, 0);
            }
            serializer.decrementIdent();
            out.write("}");
            return out.toString();
        } catch (Exception e) {

        } finally {
            out.close();
        }
        return null;
    }
}
