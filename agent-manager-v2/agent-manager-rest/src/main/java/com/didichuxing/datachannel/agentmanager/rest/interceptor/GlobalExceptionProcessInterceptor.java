package com.didichuxing.datachannel.agentmanager.rest.interceptor;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 全局异常处理拦截器
 * @date 20/1/11
 */
@Aspect
@Component
public class GlobalExceptionProcessInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionProcessInterceptor.class);

    /**
     * 切入点
     */
    private static final String PointCut = "execution(* com.didichuxing.datachannel.agentmanager.rest.api..*.*(..))";

    @Pointcut(value = PointCut)
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 获取方法的api level
        MethodSignature msig = (MethodSignature) proceedingJoinPoint.getSignature();
        String methodName = msig.getName();
        Object target = proceedingJoinPoint.getTarget();
        Object methodResult = null;
        try {
            methodResult = proceedingJoinPoint.proceed();
        } catch (ServiceException ex) {
            ErrorCodeEnum errorCodeEnum = CommonConstant.getErrorCodeEnumByCode(ex.getServiceExceptionCode());
            return Result.build(ex.getServiceExceptionCode(), ex.getMessage());
        } catch (Throwable t) {
            LOGGER.error("error occurred when proceed method:{}.", methodName, t);
            return Result.buildFail(t.getMessage());
        } finally {

        }
        return methodResult;
    }

}
