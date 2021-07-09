package com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry.enumeration;

import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentOperationTaskStatusEnum;

public enum N9eTaskStateEnum {
    DONE(0, "done", AgentOperationTaskStatusEnum.FINISHED),
    PAUSE(1, "pause", AgentOperationTaskStatusEnum.BLOCKED),
    START(2, "start", AgentOperationTaskStatusEnum.RUNNING),
            ;

    private Integer code;

    private String message;

    private AgentOperationTaskStatusEnum status;

    N9eTaskStateEnum(Integer code, String message, AgentOperationTaskStatusEnum status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AgentOperationTaskStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AgentOperationTaskStatusEnum status) {
        this.status = status;
    }

    public static N9eTaskStateEnum getByMessage(String message) {
        for (N9eTaskStateEnum elem: N9eTaskStateEnum.values()) {
            if (elem.message.equals(message)) {
                return elem;
            }
        }
        return null;
    }
}
