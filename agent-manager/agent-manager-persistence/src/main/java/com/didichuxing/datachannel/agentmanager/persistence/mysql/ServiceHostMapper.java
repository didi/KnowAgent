package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.service.ServiceHostPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "serviceHostDAO")
public interface ServiceHostMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ServiceHostPO record);

    int insertSelective(ServiceHostPO record);

    ServiceHostPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ServiceHostPO record);

    int updateByPrimaryKey(ServiceHostPO record);

    int deleteByHostId(Long hostId);

    List<Long> selectHostIdsByServiceId(Long serviceId);

    int deleteByServiceId(Long serviceId);

    int selectRelationHostCountByServiceId(Long serviceId);

    List<ServiceHostPO> selectAll();

}
