package com.didichuxing.datachannel.agentmanager.common.enumeration.metrics;

public enum SortTimeFieldEnum {

    HEARTBEAT_TIME_MINUTE("heartbeatTimeMinute"),
    HEARTBEAT_TIME_HOUR("heartbeatTimeHour"),
    HEARTBEAT_TIME_DAY("heartbeatTimeDay")

    ;

    private String fieldName;

    SortTimeFieldEnum(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
