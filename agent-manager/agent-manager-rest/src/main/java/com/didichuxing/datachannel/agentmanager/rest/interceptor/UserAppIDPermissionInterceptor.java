package com.didichuxing.datachannel.agentmanager.rest.interceptor;

import com.didichuxing.datachannel.agentmanager.common.annotation.CheckPermission;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.remote.uic.UicInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class UserAppIDPermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UicInterfaceService n9eUicInterfaceServiceImpl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        CheckPermission checkPermission = ((HandlerMethod)handler).getMethodAnnotation(CheckPermission.class);
        if(checkPermission == null) return true;

        if(n9eUicInterfaceServiceImpl.getPermission(Long.valueOf(SpringTool.getAppId(request)),
                                                    SpringTool.getOperator(request),
                                                    checkPermission.permission())){
            return true;
        }
        throw new ServiceException("没有操作权限", ErrorCodeEnum.CHECK_PERMISSION_REMOTE_FAILED.getCode());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        n9eUicInterfaceServiceImpl.remove();
    }
}
