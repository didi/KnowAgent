package com.didichuxing.datachannel.agentmanager.common.enumeration.receiver;

public enum ReceiverTypeEnum {

    KAFKA(0, "Kafka")
    ;

    ReceiverTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReceiverTypeEnum fromCode(Integer code) {
        for (ReceiverTypeEnum value : ReceiverTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
