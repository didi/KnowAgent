package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.globalconfig.GlobalConfigPO;

public interface GlobalConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(GlobalConfigPO record);

    int insertSelective(GlobalConfigPO record);

    GlobalConfigPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GlobalConfigPO record);

    int updateByPrimaryKey(GlobalConfigPO record);
}