package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostPaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostAgentPO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.host.HostPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "hostDAO")
public interface HostMapper {

    int deleteByPrimaryKey(Long id);

    int insert(HostPO record);

    HostPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(HostPO record);

    HostPO selectByHostName(String hostName);

    List<HostPO> selectContainerListByParentHostName(String hostName);

    List<HostPO> list();

    List<HostPO> selectByServiceId(Long serviceId);

    List<HostAgentPO> paginationQueryByConditon(HostPaginationQueryConditionDO hostPaginationQueryConditionDO);

    Integer queryCountByCondition(HostPaginationQueryConditionDO hostPaginationQueryConditionDO);

    List<String> selectAllMachineZones();

    List<HostPO> selectByIp(String ip);

    Long countByHostType(Integer hostType);

}
