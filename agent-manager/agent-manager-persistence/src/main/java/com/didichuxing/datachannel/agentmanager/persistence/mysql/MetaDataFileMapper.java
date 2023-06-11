package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetaDataFilePaginationQueryConditionDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.metadata.MetaDataFilePO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "metaDataFileDAO")
public interface MetaDataFileMapper {

    int insert(MetaDataFilePO metaDataFilePO);

    MetaDataFilePO selectByPrimaryKey(Long id);

    int deleteByPrimaryKey(Long id);

    List<MetaDataFilePO> paginationQueryByCondition(MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO);

    Integer queryCountByCondition(MetaDataFilePaginationQueryConditionDO metaDataFilePaginationQueryConditionDO);

}
