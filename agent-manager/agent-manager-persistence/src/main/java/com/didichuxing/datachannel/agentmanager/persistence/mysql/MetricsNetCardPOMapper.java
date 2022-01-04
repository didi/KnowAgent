package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metrics.MetricsNetCardPO;

public interface MetricsNetCardPOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MetricsNetCardPO record);

    int insertSelective(MetricsNetCardPO record);

    MetricsNetCardPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MetricsNetCardPO record);

    int updateByPrimaryKey(MetricsNetCardPO record);
}