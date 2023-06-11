package com.didichuxing.datachannel.agent.integration.test.beans;

/**
 * @description: 传输类型
 * @author: huangjw
 * @Date: 19/2/13 14:53
 */
public enum DataTransEnum {
    MQLIST(0, "List<mqLogEvent>"), // mq event list
    MQEVENT(1, "mqLogEvent"), // mq event
    STRING(2, "String"); // String

    private int    tag;

    private String comment;

    private DataTransEnum(int tag, String comment) {
        this.tag = tag;
        this.comment = comment;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
