package com.didichuxing.datachannel.agent.engine.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-08-01 10:43
 */
public class CommonUtilsTest {

    String baseString = System.currentTimeMillis() + "";

    @Test
    public void decodeAndEnCode() {
        String encodeString = CommonUtils.encode(baseString);
        System.out.println(encodeString);

        assertTrue(baseString.equals(CommonUtils.decode(encodeString)));
    }

    @Test
    public void testSelectTopic() {
        String topic = "textdxh_[0,5]";
        for (int i = 0; i < 10; i++) {
            String topicList = CommonUtils.selectTopic(topic);
            System.err.println(topicList);
        }
    }

}
