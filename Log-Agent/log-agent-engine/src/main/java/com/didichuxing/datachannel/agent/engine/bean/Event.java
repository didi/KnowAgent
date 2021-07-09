package com.didichuxing.datachannel.agent.engine.bean;

import java.io.Serializable;
import java.util.Arrays;

import com.didichuxing.datachannel.agent.engine.utils.TimeUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 19/6/18 16:58
 */
public class Event implements Serializable {

    private static final long serialVersionUID = -5818973952603652710L;

    // string 类型内容
    protected String          content;

    // byte类型内容
    protected byte[]          bytes;

    // 该条event的时间点
    protected Long            transTime;

    // 该条event的纳秒时间点
    private Long              transNanoTime;

    public Event(String content, byte[] bytes) {
        this.content = content;
        this.bytes = bytes;
        this.transTime = System.currentTimeMillis();
        this.transNanoTime = TimeUtils.getNanoTime();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Long getTransTime() {
        return transTime;
    }

    public void setTransTime(Long tranTime) {
        this.transTime = transTime;
    }

    public Long getTransNanoTime() {
        return transNanoTime;
    }

    public void setTransNanoTime(Long transNanoTime) {
        this.transNanoTime = transNanoTime;
    }

    /**
     * 消息体大小
     * @return
     */
    public int length() {
        if (content.length() != 0) {
            return bytes.length;
        } else {
            return bytes.length * 2;
        }
    }

    @Override
    public String toString() {
        return "Event{" + "content='" + content + '\'' + ", bytes=" + Arrays.toString(bytes)
               + ", transTime=" + transTime + ", transNanoTime=" + transNanoTime + '}';
    }
}
