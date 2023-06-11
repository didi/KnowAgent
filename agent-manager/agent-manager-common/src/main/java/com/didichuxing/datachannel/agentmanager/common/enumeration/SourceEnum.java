package com.didichuxing.datachannel.agentmanager.common.enumeration;

public enum SourceEnum {
    MANUAL(0L, "人为添加"), K8S(1L, "k8s集群获取");

    private long code;
    private String desc;

    SourceEnum(long code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public long getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
