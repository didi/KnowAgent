package com.didichuxing.datachannel.agentmanager.common.constant;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huqidong
 * @date 2020-09-21
 * 常量类
 */
public class CommonConstant {

    public static final String SYSTEM_DEFAULT_OPERATOR = "System";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String EQUAL_SIGN = "=";
    public static final String SEMICOLON = ";";

    private static Map<Integer, ErrorCodeEnum> errorCodeEnumMap = new HashMap<>();

    static {
        ErrorCodeEnum[] errorCodeEnumArray = ErrorCodeEnum.values();
        for (ErrorCodeEnum errorCodeEnum : errorCodeEnumArray) {
            errorCodeEnumMap.put(errorCodeEnum.getCode(), errorCodeEnum);
        }
    }

    /**
     * @param operator 操作人
     * @return 如给定操作人为空，返回系统默认操作人"System"。如给定操作人不为空，返回给定操作人参数值
     */
    public static String getOperator(String operator) {
        if (StringUtils.isBlank(operator)) {
            return SYSTEM_DEFAULT_OPERATOR;
        } else {
            return operator;
        }
    }

    public static ErrorCodeEnum getErrorCodeEnumByCode(Integer code) {
        ErrorCodeEnum errorCodeEnum = errorCodeEnumMap.get(code);
        return errorCodeEnum;
    }

}
