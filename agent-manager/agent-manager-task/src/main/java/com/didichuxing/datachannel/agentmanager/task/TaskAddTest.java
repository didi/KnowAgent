package com.didichuxing.datachannel.agentmanager.task;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.observability.conponent.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class TaskAddTest {
    public static void main(String[] args) throws Exception {

        String url = "http://localhost:9010/v1/logi-job/task";
        Map<String, Object> params = new HashMap<>();
        params.put("name", "带参数的定时任务");
        params.put("description", "带参数的定时任务");
        params.put("cron", "0 0/1 * * * ? *");
        params.put("className", "com.didichuxing.datachannel.agentmanager.task.JobBroadcasWithParamtTest");
        params.put("params", "{\"name\":\"william\", \"age\":30}");
        params.put("consensual", "RANDOM");
        params.put("nodeNameWhiteListString", "[\"node1\"]");
        String content = JSON.toJSONString(params);
        String response = HttpUtils.postForString(url, content, null);
        System.err.println(response);

    }
}
