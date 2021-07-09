package com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration;

/**
 *
 * agent 操作任务类型：
 *  0：安装 1：卸载 2：升级
 * @author zengqiao
 * @date 20/9/7
 */
public enum AgentOperationTaskTypeEnum {
    INSTALL(0, "安装"),
    UNINSTALL(1, "卸载"),
    UPGRADE(2, "升级")
    ;

    private Integer code;

    private String message;

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

    AgentOperationTaskTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "AgentOperationTaskTypeEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}
