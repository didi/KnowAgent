package com.didichuxing.datachannel.agentmanager.core.common;

import com.didichuxing.datachannel.agentmanager.common.util.LogUtil;
import org.junit.jupiter.api.Test;

public class UtilTest {

    @Test
    public void runtimeLogFormatTest() {
        String s = LogUtil.defaultLogFormat();
        System.out.println(s);
    }
}
