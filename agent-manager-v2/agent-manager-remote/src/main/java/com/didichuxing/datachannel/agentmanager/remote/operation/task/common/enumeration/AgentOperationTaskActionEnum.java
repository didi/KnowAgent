package com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration;

/**
 * 任务动作
 * @author zengqiao
 * @date 20/4/26
 */
public enum AgentOperationTaskActionEnum {
    UNKNOWN(-1, "unknown"),

    START(0, "start"),
    PAUSE(1, "pause"),
    CANCEL(2, "cancel"),
    KILL(3, "kill"),

    IGNORE(4, "ignore"),
    REDO(5, "redo"),
    ;

    private Integer code;

    private String action;

    AgentOperationTaskActionEnum(Integer code, String action) {
        this.code = code;
        this.action = action;
    }

    public Integer getCode() {
        return code;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "AgentOperationTaskActionEnum{" +
                "code=" + code +
                ", action='" + action + '\'' +
                '}';
    }
}
