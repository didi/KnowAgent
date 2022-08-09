package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 文件读取类型
 * @author: huangjw
 * @Date: 18/7/11 11:15
 */
public enum FileReadType {
    MultiRow(0, "multi"), // 多行聚合
    SingleRow(1, "single"); // 单行

    private int    type;

    private String comment;

    private FileReadType(int type, String comment) {
        this.type = type;
        this.comment = comment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
