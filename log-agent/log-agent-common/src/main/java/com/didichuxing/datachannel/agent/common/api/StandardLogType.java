package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 标准日志类型
 * @author: huangjw
 * @Date: 18/8/9 11:57
 */
public enum StandardLogType {

    // 和AgentManager匹配，0：普通日志，1：public 日志
    Normal(0, "normal"), Public(1, "public");

    private Integer type;

    private String  comment;

    StandardLogType(int type, String comment) {
        this.type = type;
        this.comment = comment;
    }

    public Integer getType() {
        return type;
    }

    public static StandardLogType getByType(Integer type) {
        for (StandardLogType logType : values()) {
            if (logType.getType().equals(type)) {
                return logType;
            }
        }
        return null;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
