package com.didichuxing.datachannel.agentmanager.common.util;

public class LogUtil {

    public static String defaultLogFormat() {
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String[] slices = className.split(".");
        String simpleName = slices[slices.length - 1];
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return "class=" + simpleName + "||method=" + methodName + "||errorMsg={%s}";
    }
}
