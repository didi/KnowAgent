package com.didichuxing.datachannel.agent.integration.test.format;

/**
 * @description: 原始类型
 * @author: huangjw
 * @Date: 19/2/12 18:32
 */
public class OriginalFormat implements Format {

    @Override
    public String format(String original) {
        return original;
    }

    @Override
    public Object unFormat(String source) {
        return source;
    }
}
