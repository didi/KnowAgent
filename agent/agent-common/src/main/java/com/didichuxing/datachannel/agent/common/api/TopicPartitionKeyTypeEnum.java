package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: partition key 类型
 * @author:
 * @Date: 21/2/24 19:37
 */
public enum TopicPartitionKeyTypeEnum {

    // 0：未初始化、1：当前时间戳为key、2：每隔512毫秒切换不同的topic Partition Key
    UNKNOWN(0, "unknown"), RANDOM_PARTITION_KEY(1, "randomPartitionKey"), FIXED_TIME_PARTITION_KEY(
                                                                                                   2,
                                                                                                   "fixPartitionKey");

    private Integer type;
    private String  comment;

    TopicPartitionKeyTypeEnum(Integer type, String comment) {
        this.type = type;
        this.comment = comment;
    }

    public Integer getType() {
        return type;
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
