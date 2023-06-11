package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

public enum SortTypeEnum {

    DESC("desc"),
    ASC("asc")
    ;

    private String type;

    SortTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
