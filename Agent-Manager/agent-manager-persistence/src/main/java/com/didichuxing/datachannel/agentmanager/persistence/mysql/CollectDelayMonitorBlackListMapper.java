package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.collectblacklist.CollectDelayMonitorBlackListPO;

public interface CollectDelayMonitorBlackListMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CollectDelayMonitorBlackListPO record);

    int insertSelective(CollectDelayMonitorBlackListPO record);

    CollectDelayMonitorBlackListPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CollectDelayMonitorBlackListPO record);

    int updateByPrimaryKey(CollectDelayMonitorBlackListPO record);
}