package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.metatableversion.MetaTableVersionPO;

public interface MetaTableVersionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MetaTableVersionPO record);

    int insertSelective(MetaTableVersionPO record);

    MetaTableVersionPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MetaTableVersionPO record);

    int updateByPrimaryKey(MetaTableVersionPO record);
}