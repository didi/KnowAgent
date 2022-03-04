package com.didichuxing.datachannel.agentmanager.core.service;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServiceDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationRecordDO;

import java.util.List;

public interface ServiceManageService {

    /**
     * 创建给定 ServicePO 对象
     * @param service  待创建 ServicePO 对象
     * @param operator 操作人
     * @return 创建成功的 ServicePO 对象 id
     */
    Long createService(ServiceDO service, String operator);

    /**
     * 根据给定服务名获取对应服务对象
     * @param serviceName 服务名
     * @return 返回系统中服务名为serviceName的服务对象，如系统中不存在，返回Result<Null>
     */
    ServiceDO getServiceByServiceName(String serviceName);

    /**
     * 修改服务，服务仅可修改 Service & Host 关联关系
     * @param serviceDO 待修改服务对象
     * @param operator 操作人
     */
    void updateService(ServiceDO serviceDO, String operator);

    /**
     * 删除服务
     * @param serviceIdList 待删除服务对象id集
     * @param cascadeDeleteHostAndLogCollectTaskRelation 是否级联删除 Service & Host、Service & LogCollectTask 关联关系
     * @param operator 操作人
     */
    void deleteServices(List<Long> serviceIdList, boolean cascadeDeleteHostAndLogCollectTaskRelation, String operator);

    /**
     * 获取系统全量Service集（注：不包括服务关联的主机对象集）
     * @return 返回系统全量Service集（注：不包括服务关联的主机对象集）
     */
    List<ServiceDO> list();

    /**
     * 根据id查询对应服务对象
     * @param id 服务id
     * @return 返回根据id查询到的对应服务对象
     */
    ServiceDO getServiceById(Long id);

    /**
     * 根据主机id查询该主机关联的服务对象集
     * @param hostId 主机id
     * @return 返回根据主机查询到的该主机关联的服务对象集
     */
    List<ServiceDO> getServicesByHostId(Long hostId);

    /**
     * 根据项目id查询该项目关联的服务对象集
     * @param projectId 项目id
     * @return 返回根据项目id查询到的该项目关联的服务对象集
     */
    List<ServiceDO> getServicesByProjectId(Long projectId);

    /**
     * 根据日志采集任务id查询该日志采集任务关联的服务对象集
     * @param logCollectTaskId 日志采集任务id
     * @return 返回根据日志采集任务id查询到的该日志采集任务关联的服务对象集
     */
    List<ServiceDO> getServicesByLogCollectTaskId(Long logCollectTaskId);

    /**
     * 根据给定参数分页查询结果集
     * @param servicePaginationQueryConditionDO 分页查询条件
     * @return 返回根据给定参数分页查询到的结果集
     */
    List<ServicePaginationRecordDO> paginationQueryByConditon(ServicePaginationQueryConditionDO servicePaginationQueryConditionDO);

    /**
     * 根据给定参数查询满足条件的结果集总数量
     * @param servicePaginationQueryConditionDO 查询条件
     * @return 返回根据给定参数查询到的满足条件的结果集总数量
     */
    Integer queryCountByCondition(ServicePaginationQueryConditionDO servicePaginationQueryConditionDO);

    /**
     * @return 返回系统全量服务数
     */
    Long countAll();

}
