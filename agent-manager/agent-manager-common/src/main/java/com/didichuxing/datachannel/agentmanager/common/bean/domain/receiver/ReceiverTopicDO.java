package com.didichuxing.datachannel.agentmanager.common.bean.domain.receiver;

import java.util.Objects;

public class ReceiverTopicDO {
    private Long receiverId;

    private String topic;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReceiverTopicDO)) {
            return false;
        }
        ReceiverTopicDO dst = (ReceiverTopicDO) obj;
        return this.receiverId.equals(dst.receiverId) && this.topic.equals(dst.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverId, topic);
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
