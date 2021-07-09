package com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry.enumeration;

import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskActionEnum;

/**
 * 任务动作
 * @author zengqiao
 * @date 20/4/26
 */
public enum N9eTaskActionEnum {
    UNKNOWN(AgentOperationTaskActionEnum.UNKNOWN, "unknown"),

    START(AgentOperationTaskActionEnum.START, "start"),
    PAUSE(AgentOperationTaskActionEnum.PAUSE, "pause"),
    CANCEL(AgentOperationTaskActionEnum.CANCEL, "cancel"),
    KILL(AgentOperationTaskActionEnum.KILL, "kill"),

    IGNORE(AgentOperationTaskActionEnum.IGNORE, "ignore"),
    REDO(AgentOperationTaskActionEnum.REDO, "redo"),
    ;

    private AgentOperationTaskActionEnum actionEnum;

    private String action;

    N9eTaskActionEnum(AgentOperationTaskActionEnum actionEnum, String action) {
        this.actionEnum = actionEnum;
        this.action = action;
    }

    public AgentOperationTaskActionEnum getActionEnum() {
        return actionEnum;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "N9eTaskActionEnum{" +
                "actionEnum=" + actionEnum +
                ", action='" + action + '\'' +
                '}';
    }

    public static N9eTaskActionEnum getByAgentOperationTaskActionEnum(AgentOperationTaskActionEnum actionEnum) {
        for (N9eTaskActionEnum n9eTaskActionEnum: N9eTaskActionEnum.values()) {
            if (n9eTaskActionEnum.actionEnum.equals(actionEnum)) {
                return n9eTaskActionEnum;
            }
        }
        return null;
    }
}
