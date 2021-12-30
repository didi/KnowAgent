package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.agent.MetricsDiskPO;

public interface MetricsDiskPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MetricsDiskPO record);

    int insertSelective(MetricsDiskPO record);

    MetricsDiskPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MetricsDiskPO record);

    int updateByPrimaryKey(MetricsDiskPO record);
}