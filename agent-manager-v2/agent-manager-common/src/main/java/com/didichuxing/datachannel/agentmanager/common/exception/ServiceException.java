package com.didichuxing.datachannel.agentmanager.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huqidong
 * @date 2020-09-21
 * 服务层异常
 */
public class ServiceException extends RuntimeException {

    /**
     * 服务层异常对象对应的状态码枚举对象
     */
    private Integer serviceExceptionCode;
    /**
     * 服务层异常对象对应上下文信息集
     */
    private Map<String, Object> context = new HashMap<>();

    public ServiceException(String message, Throwable cause, Integer serviceExceptionCode) {
        super(message, cause);
        this.serviceExceptionCode = serviceExceptionCode;
    }

    public ServiceException(String message, Integer serviceExceptionCode) {
        super(message);
        this.serviceExceptionCode = serviceExceptionCode;
    }

    public Integer getServiceExceptionCode() {
        return serviceExceptionCode;
    }

    /**
     * 添加给定服务层异常对应上下文信息
     * @param key 上下文信息 key
     * @param value 上下文信息 value 注：须 json 可序列化类型
     */
    public void addAttribute(String key, Object value) {
        this.context.put(key, value);
    }

}
