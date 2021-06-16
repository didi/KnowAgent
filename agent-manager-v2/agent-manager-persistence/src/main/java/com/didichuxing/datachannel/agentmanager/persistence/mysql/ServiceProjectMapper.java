package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceProjectPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "serviceProjectDAO")
public interface ServiceProjectMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ServiceProjectPO record);

    ServiceProjectPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(ServiceProjectPO record);

    int deleteByProjectId(Long projectId);

    int deleteByServiceId(Long serviceId);

    List<ServiceProjectPO> selectAll();

}
