package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsProcessPO;

public interface MetricsProcessPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MetricsProcessPO record);

    int insertSelective(MetricsProcessPO record);

    MetricsProcessPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MetricsProcessPO record);

    int updateByPrimaryKey(MetricsProcessPO record);
}