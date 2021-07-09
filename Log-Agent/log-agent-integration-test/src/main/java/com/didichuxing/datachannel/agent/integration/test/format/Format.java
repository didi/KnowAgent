package com.didichuxing.datachannel.agent.integration.test.format;

/**
 * @description: 消息体包装类
 * @author: huangjw
 * @Date: 19/2/12 18:22
 */
public interface Format {
    /**
     * 格式化
     * @param original 原始字符串
     * @return
     */
    String format(String original);

    /**
     * 反解析
     * @param source
     * @return
     */
    Object unFormat(String source);
}
