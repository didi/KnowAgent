package com.didichuxing.datachannel.agentmanager.common.enumeration.operaterecord;

/**
 * 操作枚举
 */
public enum OperationEnum {
    /**
     * 新增
     */
    ADD(1, "新增"),

    DELETE(2, "删除"),

    EDIT(3, "修改"),

    ENABLE(4, "启用"),

    DISABLE(5, "禁用"),

    EXE(6, "执行"),

    UNKNOWN(-1, "unknown"),
    ;

    OperationEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;

    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OperationEnum valueOf(Integer code) {
        if (code == null) {
            return OperationEnum.UNKNOWN;
        }
        for (OperationEnum state : OperationEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return OperationEnum.UNKNOWN;
    }

    public static boolean validate(Integer code) {
        if (code == null) {
            return false;
        }
        for (OperationEnum state : OperationEnum.values()) {
            if (state.getCode() == code) {
                return true;
            }
        }

        return false;
    }
}
