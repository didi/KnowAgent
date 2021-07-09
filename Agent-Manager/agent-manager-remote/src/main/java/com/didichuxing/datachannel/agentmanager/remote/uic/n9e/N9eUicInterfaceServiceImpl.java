package com.didichuxing.datachannel.agentmanager.remote.uic.n9e;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry.N9eResult;
import com.didichuxing.datachannel.agentmanager.remote.uic.UicInterfaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("n9eUicInterfaceServiceImpl")
public class N9eUicInterfaceServiceImpl implements UicInterfaceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(N9eUicInterfaceServiceImpl.class);

    @Value("${metadata.sync.request.permission.url}")
    private String requestUrl;

    @Value("${metadata.sync.request.permission.srv-token}")
    private String srvToken;

    @Value("${metadata.sync.request.permission.user-token}")
    private String userToken;

    @Override
    public Boolean getPermission(Long nid, String username, String ops[]) {
        String url = requestUrl + "?nid=" + nid + "&username=" + username + "&ops=";
        for (int i = 0; i < ops.length - 1; i++) {
            url += ops[i];
            url += ",";
        }
        url += ops[ops.length - 1];
        try {
            String response = HttpUtils.get(url, null, buildHeaders());
            N9eResult n9eResult = JSON.parseObject(response, N9eResult.class);
            LOGGER.info("{}", n9eResult.getDat());
            List<Boolean> values = new ArrayList(((JSONObject) n9eResult.getDat()).values());
            userThreadCache.set((JSONObject) n9eResult.getDat());

            for (Boolean value : values) {
                if (value) {
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>(2);
        headers.put("content-type", "application/json");
        headers.put("X-Srv-Token", srvToken);
        headers.put("x-user-token", userToken);
        return headers;
    }
}
