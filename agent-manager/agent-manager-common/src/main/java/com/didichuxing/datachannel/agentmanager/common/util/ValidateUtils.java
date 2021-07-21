package com.didichuxing.datachannel.agentmanager.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengqiao
 * @date 20/4/16
 */
public class ValidateUtils {
    /**
     * 为空
     */
    public static boolean isNull(Object object) {
        if (object instanceof String) {
            String str = (String) object;
            return isBlank(str);
        }

        return object == null;
    }

    /**
     * 是空字符串或者空
     */
    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

}