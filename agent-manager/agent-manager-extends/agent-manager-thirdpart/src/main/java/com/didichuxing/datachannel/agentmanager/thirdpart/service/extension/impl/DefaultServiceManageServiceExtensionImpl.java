package com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServicePO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.service.extension.ServiceManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@org.springframework.stereotype.Service
public class DefaultServiceManageServiceExtensionImpl implements ServiceManageServiceExtension {

    public List<String> getServiceNameListByServiceNameListJsonString(String serviceNameListJsonString) throws ServiceException {
        if(StringUtils.isBlank(serviceNameListJsonString)) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=getServiceNameListByServiceNameListJsonString||errMsg={%s}",
                            "入参serviceNameListJsonString值不可为空"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        List<String> serviceNameList = null;
        try {
            serviceNameList = JSON.parseArray(serviceNameListJsonString, String.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=getServiceNameListByServiceNameListJsonString||errMsg={%s}",
                            String.format("入参serviceNameListJsonString值{%s}格式非法，合法格式为[serviceName1, serviceName2, ..... serviceNameN]", serviceNameListJsonString)
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        if(null == serviceNameList || 0 == serviceNameList.size()) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=getServiceNameListByServiceNameListJsonString||errMsg={%s}",
                            "入参[serviceNameListJsonString]不含任何服务名"
                    ),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        return serviceNameList;
    }

    @Override
    public ServicePO serviceDO2Service(ServiceDO service) throws ServiceException {
        try {
            return ConvertUtil.obj2Obj(service, ServicePO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=serviceDO2Service||msg={%s}",
                            String.format("Service对象{%s}转化为ServicePO对象失败", JSON.toJSONString(service))
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public ServiceDO service2ServiceDO(ServicePO servicePO) throws ServiceException {
        try {
            return ConvertUtil.obj2Obj(servicePO, ServiceDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=service2ServiceDO||msg={%s}",
                            String.format("ServicePO对象{%s}转化为Service对象失败", JSON.toJSONString(servicePO))
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
    }

    @Override
    public CheckResult checkCreateParameterService(ServiceDO service) {
        if(null == service) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=checkCreateParameterService||msg={%s}",
                            "入参Service对象不可为空"
                    )
            );
        }
        if(StringUtils.isBlank(service.getServicename())) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=checkCreateParameterService||msg={%s}",
                            String.format("入参Service={%s}对象对应servicename属性值不可为空", JSON.toJSONString(service))
                    )
            );
        }
//        if(CollectionUtils.isEmpty(service.getHostIdList())) {
//            return new CheckResult(
//                    false,
//                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
//                    String.format(
//                            "class=DefaultServiceManageServiceExtensionImpl||method=checkCreateParameterService||msg={%s}",
//                            String.format("入参Service={%s}对象对应hostIdList属性值不可为空集", JSON.toJSONString(service))
//                    )
//            );
//        }
        return new CheckResult(true);
    }

    @Override
    public ServiceDO buildServiceByServiceName(String serviceName, String operator) {
        ServiceDO service = new ServiceDO();
        service.setServicename(serviceName);
        service.setOperator(CommonConstant.getOperator(operator));
        return service;
    }

    @Override
    public CheckResult checkUpdateParameterService(ServiceDO service) {
        if(null == service) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=checkUpdateParameterService||msg={%s}",
                            "入参Service对象不可为空"
                    )
            );
        }
        if(null == service.getId()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=DefaultServiceManageServiceExtensionImpl||method=checkUpdateParameterService||msg={%s}",
                            String.format("入参Service={%s}对象对应id属性值不可为空", JSON.toJSONString(service))
                    )
            );
        }
//        if(CollectionUtils.isEmpty(service.getHostIdList())) {
//            return new CheckResult(
//                    false,
//                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
//                    String.format(
//                            "class=DefaultServiceManageServiceExtensionImpl||method=checkUpdateParameterService||msg={%s}",
//                            String.format("入参Service={%s}对象对应hostIdList属性值不可为空集", JSON.toJSONString(service))
//                    )
//            );
//        }
        return new CheckResult(true);
    }

    @Override
    public List<ServiceDO> servicePOList2serviceDOList(List<ServicePO> servicePOList) {
        return ConvertUtil.list2List(servicePOList, ServiceDO.class);
    }

}
