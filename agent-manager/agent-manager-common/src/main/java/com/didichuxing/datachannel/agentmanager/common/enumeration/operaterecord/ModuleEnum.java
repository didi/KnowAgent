package com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 操作记录模块枚举
 */
public enum ModuleEnum {

    SERVICE(1, "应用管理"),
    LOG_COLLECT_TASK(2,"采集任务"),
    RECEIVER(3, "Kafka集群"),
    AGENT(4, "Agent管理"),
//    HOST(5, "主机"),
//    AGENT_VERSION(6, "AgentVersion"),
    UNKNOWN(-1, "unknown");

    ModuleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int    code;

    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        map.put("desc", desc);
        return map;
    }

    public static ModuleEnum valueOf(Integer code) {
        if (code == null) {
            return ModuleEnum.UNKNOWN;
        }
        for (ModuleEnum state : ModuleEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return ModuleEnum.UNKNOWN;
    }

    public static boolean validate(Integer code) {
        if (code == null) {
            return false;
        }
        for (ModuleEnum state : ModuleEnum.values()) {
            if (state.getCode() == code) {
                return true;
            }
        }

        return false;
    }

}
