package com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration;

import com.didichuxing.datachannel.agentmanager.common.enumeration.agent.AgentOperationTaskStatusEnum;

/**
 * @author zengqiao
 * @date 20/9/7
 */
public enum AgentOperationTaskSubStateEnum {
    WAITING(AgentOperationTaskStatusEnum.WAITING),
    RUNNING(AgentOperationTaskStatusEnum.RUNNING),
    FAILED(AgentOperationTaskStatusEnum.FAILED),
    SUCCEED(AgentOperationTaskStatusEnum.SUCCEED),
    TIMEOUT(AgentOperationTaskStatusEnum.TIMEOUT),
    CANCELED(AgentOperationTaskStatusEnum.CANCELED),
    IGNORED(AgentOperationTaskStatusEnum.IGNORED),
    KILLING(AgentOperationTaskStatusEnum.KILLING),
    KILL_FAILED(AgentOperationTaskStatusEnum.KILL_FAILED),
    ;

    private Integer code;

    private String message;

    AgentOperationTaskSubStateEnum(AgentOperationTaskStatusEnum statusEnum) {
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
        return "ClusterTaskSubState{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    /**
     * @return 是否已执行完
     */
    public boolean finish() {
        return isFinished(this.getCode());
    }

    /**
     * 指定状态码对应任务是否已执行完
     * @param code 状态码值
     * @return true：执行完 false：未执行完
     */
    public static Boolean isFinished(Integer code) {
        if (
                code.equals(FAILED.getCode()) ||
                        code.equals(SUCCEED.getCode()) ||
                        code.equals(TIMEOUT.getCode()) ||
                        code.equals(CANCELED.getCode()) ||
                        code.equals(IGNORED.getCode()) ||
                        code.equals(KILL_FAILED.getCode())
        ) {
            return true;
        }
        return false;
    }

}
