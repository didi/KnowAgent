package com.didichuxing.datachannel.agentmanager.remote.uic;

import com.alibaba.fastjson.JSONObject;

public interface UicInterfaceService {

    Boolean getPermission(Long nid, String username, String ops[]);

    ThreadLocal<JSONObject> userThreadCache = new ThreadLocal<>();

    default ThreadLocal<JSONObject> getUserThreadCache() {
        return userThreadCache;
    }

    default void remove() {
        userThreadCache.remove();
    }
}
