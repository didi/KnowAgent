package com.didichuxing.datachannel.agent.engine.loggather;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class LogDetail {

    private final String        logCode;
    private final AtomicInteger count;
    private final String        logMsg;
    private final Throwable     throwable;

    boolean restrain() {
        return count.get() == 1;
    }

    public int getCount() {
        return count.get();
    }

    public String getLogCode() {
        return logCode;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public LogDetail(String logCode, String logMsg, Throwable throwable) {
        this.logCode = logCode;
        this.logMsg = logMsg;
        this.throwable = throwable;
        this.count = new AtomicInteger(1);
    }

    public void incCount() {
        count.getAndIncrement();
    }

    public void incCount(int delta) {
        count.getAndAdd(delta);
    }

    @Override
    public String toString() {
        Map<String, Object> tempMsgMap = new HashMap<>();
        tempMsgMap.put("logCode", logCode);
        tempMsgMap.put("count", count);
        tempMsgMap.put("logMsg", logMsg);
        // 直接传入exception时，反序列化时会报autoType is not supported错误。log gather都是日志输出，可以只支持字符串
        tempMsgMap.put("throwable", JSON.toJSONString(throwable));

        return JSON.toJSONString(tempMsgMap);
    }
}
