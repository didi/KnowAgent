package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.service.ServicePaginationRecordDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServicePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "serviceDAO")
public interface ServiceMapper {
    int deleteByPrimaryKey(Long id);

    //已测
    int insert(ServicePO record);

    ServicePO selectByPrimaryKey(Long id);

    //已测
    ServicePO selectByServiceName(String serviceName);

    List<ServicePO> list();

    List<ServicePO> selectByHostId(Long hostId);

    List<ServicePO> selectByLogCollectTaskId(Long logCollectTaskId);

    List<ServicePaginationRecordDO> paginationQueryByConditon(ServicePaginationQueryConditionDO servicePaginationQueryConditionDO);

    Integer queryCountByCondition(ServicePaginationQueryConditionDO servicePaginationQueryConditionDO);

    ServicePO selectByExtenalServiceId(Long extenalServiceId);

    List<ServicePO> selectByProjectId(@Param(value = "projectId") Long projectId);

    Long countAll();

}