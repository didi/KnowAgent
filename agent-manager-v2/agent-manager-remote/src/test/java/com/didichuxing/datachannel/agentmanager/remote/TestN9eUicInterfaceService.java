package com.didichuxing.datachannel.agentmanager.remote;


import com.didichuxing.datachannel.agentmanager.remote.uic.UicInterfaceService;
import com.didichuxing.datachannel.agentmanager.remote.uic.n9e.N9eUicInterfaceServiceImpl;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = N9eUicInterfaceServiceImpl.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class TestN9eUicInterfaceService {

    @Autowired
    private UicInterfaceService n9eUicInterfaceServiceImpl;

//    // 单元测试本身跑不起来，直接发请求测试吧
//    @Test
//    public void testGetPermission() {
//        Boolean res = n9eUicInterfaceServiceImpl.getPermission(1L, "root", new String[]{AGENT_MACHINE_LIST});
//    }
}
