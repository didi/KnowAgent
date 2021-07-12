package com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration;

import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentOperationTaskStatusEnum;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public enum AgentOperationTaskStateEnum {
    RUNNING(AgentOperationTaskStatusEnum.RUNNING),
    BLOCKED(AgentOperationTaskStatusEnum.BLOCKED),
    FINISHED(AgentOperationTaskStatusEnum.FINISHED),
    ;

    private Integer code;

    private String message;

    AgentOperationTaskStateEnum(AgentOperationTaskStatusEnum statusEnum) {
        this.code = statusEnum.getCode();
        this.message = statusEnum.getMessage();
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

    @Override
    public String toString() {
        return "AgentOperationTaskStateEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public static AgentOperationTaskStateEnum getByTaskStatusEnum(AgentOperationTaskStatusEnum taskStatusEnum) {
        for (AgentOperationTaskStateEnum agentOperationTaskStateEnum: AgentOperationTaskStateEnum.values()) {
            if (agentOperationTaskStateEnum.getCode().equals(taskStatusEnum.getCode())) {
                return agentOperationTaskStateEnum;
            }
        }
        return null;
    }
}