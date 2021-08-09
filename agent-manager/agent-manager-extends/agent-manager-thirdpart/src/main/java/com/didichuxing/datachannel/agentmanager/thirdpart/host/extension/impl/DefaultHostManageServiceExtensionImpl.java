package com.didichuxing.datachannel.agentmanager.thirdpart.host.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostAgentDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.*;
import com.didichuxing.datachannel.agentmanager.thirdpart.host.extension.HostManageServiceExtension;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceManageServiceExtension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@org.springframework.stereotype.Service
public class DefaultHostManageServiceExtensionImpl implements HostManageServiceExtension {

    @Autowired
    private ServiceManageServiceExtension serviceManageServiceExtension;

    @Override
    public CheckResult checkCreateParameterHost(HostDO host) {
        if(null == host) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参host对象为null");
        }
        if(null == host.getContainer() || (host.getContainer() != 0 && host.getContainer() != 1)) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参host对象container属性值不可为空且取值范围须为[0,1]");
        }
        if(StringUtils.isBlank(host.getHostName())) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参host对象hostName属性值不可为空");
        }
//        if(null == host.getMachineZone()) {
//            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "入参host对象machineZone属性值不可为空");
//        }
        //TODO：主机信息校验逻辑
        return new CheckResult(true);
    }

    @Override
    public CheckResult checkModifyParameterHost(HostDO host) {
        if(null == host) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), "给定参数host对象不能为null");
        }
        if(null == host.getId() || 0 >= host.getId()) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(), String.format("给定参数host对象对应id属性值[%d]非法", host.getId()));
        }
        //TODO：主机信息校验逻辑
        return new CheckResult(true);
    }

    @Override
    public HostDO updateHost(HostDO sourceHost, HostDO targetHost) throws ServiceException {
        if(targetHost.getContainer() != null && !sourceHost.getContainer().equals(targetHost.getContainer())) {
            sourceHost.setContainer(targetHost.getContainer());
        }
        if(StringUtils.isNotBlank(targetHost.getHostName()) && !sourceHost.getHostName().equals(targetHost.getHostName())) {
            sourceHost.setHostName(targetHost.getHostName());
        }
        if(StringUtils.isNotBlank(targetHost.getDepartment()) && !sourceHost.getDepartment().equals(targetHost.getDepartment())) {
            sourceHost.setDepartment(targetHost.getDepartment());
        }
        if(null != targetHost.getMachineZone()) {
            sourceHost.setMachineZone(targetHost.getMachineZone());
        }
        if(StringUtils.isNotBlank(targetHost.getIp()) && !sourceHost.getIp().equals(targetHost.getIp())) {
            sourceHost.setIp(targetHost.getIp());
        }
        if(StringUtils.isNotBlank(targetHost.getParentHostName()) && !sourceHost.getParentHostName().equals(targetHost.getParentHostName())) {
            sourceHost.setParentHostName(targetHost.getParentHostName());
        }
        return sourceHost;
    }

    @Override
    public HostDO hostPO2HostDO(HostPO hostPO) throws ServiceException {
        try {
            return ConvertUtil.obj2Obj(hostPO, HostDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultHostManageServiceExtensionImpl||method=hostPO2HostDO||msg={%s}",
                            String.format("HostPO对象{%s}转化为Host对象失败", JSON.toJSONString(hostPO))
                    ),
              ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public HostPO host2HostPO(HostDO host) throws ServiceException {
        try {
            HostPO hostPO = ConvertUtil.obj2Obj(host, HostPO.class);
            hostPO.setContainer(host.getContainer());
            return hostPO;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultHostManageServiceExtensionImpl||method=host2HostPO||msg={%s}",
                            String.format("Host对象{%s}转化为HostPO对象失败", JSON.toJSONString(host))
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public HostAgentDO hostAgentPO2HostAgentDO(HostAgentPO hostAgentPO) throws ServiceException {
        try {
            HostAgentDO hostAgentDO = ConvertUtil.obj2Obj(hostAgentPO, HostAgentDO.class);
            return hostAgentDO;
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format("HostAgentPO对象={%s}转化为HostAgentDO对象失败，原因为：%s", JSON.toJSONString(hostAgentPO), ex.getMessage()),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public List<HostDO> hostPOList2HostDOList(List<HostPO> hostPOList) {
        return ConvertUtil.list2List(hostPOList, HostDO.class);
    }

    @Override
    public List<HostAgentDO> hostAgentPOList2HostAgentDOList(List<HostAgentPO> hostAgentPOList) {
        return ConvertUtil.list2List(hostAgentPOList, HostAgentDO.class);
    }

}
