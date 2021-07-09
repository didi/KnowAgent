package com.didichuxing.datachannel.agent.node.service.http.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.didichuxing.datachannel.agent.engine.utils.CommonUtils;
import com.didichuxing.datachannel.agent.engine.utils.HttpUtils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-07-29 20:48
 */
public class HttpServiceTest {

    private HttpService httpServer;

    @Before
    public void init() throws Exception {
        httpServer = new HttpService(null, null);
        httpServer.init();
        httpServer.start();
        System.out.println("init ok");
    }

    @Test
    public void version() throws Exception {
        String response = HttpUtils.get("http://127.0.0.1:2023/log-agent/version", null);
        System.out.println(response);
        assertTrue(response.contains("test"));
    }

    @Test
    public void heartbeat() throws Exception {
        String response = HttpUtils.get("http://127.0.0.1:2023/heartbeat", null);
        assertEquals("OK", response);
    }

    @Test
    public void pid() throws Exception {
        String response = HttpUtils.get("http://127.0.0.1:2023/log-agent/pid", null);
        Long.valueOf(response);
    }

    @Test
    public void command() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("command", "hostname | grep " + CommonUtils.getHOSTNAME() + "");
        String response = HttpUtils.post("http://127.0.0.1:2023/log-agent/command", map);
        System.out.println(response);
        if (response.contains(CommonUtils.getHOSTNAME())) {
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

    @After
    public void destory() {
        httpServer.stop(true);
        System.out.println("destory ok");
    }
}
