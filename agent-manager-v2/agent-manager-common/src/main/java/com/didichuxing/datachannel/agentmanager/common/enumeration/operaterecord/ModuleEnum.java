package com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 操作记录模块枚举
 */
public enum ModuleEnum {

    SERVICE(1, "服务"),
    LOG_COLLECT_TASK(2,"日志采集任务"),
    RECEIVER(3, "接收端"),
    HOST(4, "主机"),
    AGENT(5, "Agent"),
    AGENT_VERSION(6, "AgentVersion"),
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
