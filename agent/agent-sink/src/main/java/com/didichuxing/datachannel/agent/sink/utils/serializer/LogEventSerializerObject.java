package com.didichuxing.datachannel.agent.sink.utils.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: huangjw
 * @Date: 18/8/20 16:43
 */
public class LogEventSerializerObject {

    Map<String, String>  strMap;
    Map<String, Integer> intMap;
    Map<String, Long>    longMap;

    public LogEventSerializerObject(){
        strMap = new HashMap<>();
        intMap = new HashMap<>();
        longMap = new HashMap<>();
    }

    public void append(String key, String value) {
        strMap.put(key, value);
    }

    public void append(String key, Integer value) {
        intMap.put(key, value);
    }

    public void append(String key, Long value) {
        longMap.put(key, value);
    }

    public Map<String, String> getStrMap() {
        return strMap;
    }

    public void setStrMap(Map<String, String> strMap) {
        this.strMap = strMap;
    }

    public Map<String, Integer> getIntMap() {
        return intMap;
    }

    public void setIntMap(Map<String, Integer> intMap) {
        this.intMap = intMap;
    }

    public Map<String, Long> getLongMap() {
        return longMap;
    }

    public void setLongMap(Map<String, Long> longMap) {
        this.longMap = longMap;
    }
}
