package com.didichuxing.datachannel.agentmanager.rest.interceptor;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 直接的api.v1异常会被GlobalExceptionProcessInterceptor处理，不会进入这里，不影响原来的正常使用
@RestControllerAdvice("com.didichuxing.datachannel.agentmanager.rest")
public class InterceptorExceptionProcessInterceptor implements ThrowsAdvice {

    @ExceptionHandler(ServiceException.class)
    public Result handleException(ServiceException e) {
        return Result.build(e.getServiceExceptionCode(), e.getMessage());
    }

}
