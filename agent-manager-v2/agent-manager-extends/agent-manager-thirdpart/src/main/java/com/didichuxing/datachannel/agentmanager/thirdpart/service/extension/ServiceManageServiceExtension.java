package com.didichuxing.datachannel.agentmanager.thirdpart.service.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServicePO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;

import java.util.List;

public interface ServiceManageServiceExtension {

    /**
     * 根据给定json字符串形式服务名集得到主机名集
     * @param serviceNameListJsonString json字符串形式服务名集
     * @return 根据给定json字符串形式服务名集获取到的主机名集
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    List<String> getServiceNameListByServiceNameListJsonString(String serviceNameListJsonString) throws ServiceException;

    /**
     * 将给定service对象转化为待持久化ServicePO对象
     * @param service 待转化 service 对象
     * @return service转化后得到的ServicePO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    ServicePO serviceDO2Service(ServiceDO service) throws ServiceException;

    /**
     * 将给定servicePO对象转化为ServicePO对象
     * @param servicePO 待转化 hostPO 对象
     * @return servicePO转化后得到的Service对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    ServiceDO service2ServiceDO(ServicePO servicePO) throws ServiceException;

    /**
     * 校验添加service方法对应的参数"service对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param service 待校验service对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkCreateParameterService(ServiceDO service);

    /**
     * 根据指定服务名构建对应服务对象
     * @param serviceName 服务名
     * @param operator 操作人
     * @return 根据指定服务名构建的对应服务对象
     */
    ServiceDO buildServiceByServiceName(String serviceName, String operator);

    /**
     * 校验更新service方法对应的参数"service对象"信息是否合法
     * 注：该操作不应抛出异常，校验过程中出现异常需要对应实现内部处理好
     * @param serviceDO 待校验service对象
     * @return 合法：true 不合法：false
     */
    CheckResult checkUpdateParameterService(ServiceDO serviceDO);

    /**
     * 将ServicePO对象集转化为ServiceDO对象集
     * @param servicePOList 待转化ServicePO对象集
     * @return 返回将ServicePO对象集转化为的ServiceDO对象集
     */
    List<ServiceDO> servicePOList2serviceDOList(List<ServicePO> servicePOList);

}
