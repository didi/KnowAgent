package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.DirectoryLogCollectPathPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "directoryLogCollectPathDAO")
public interface DirectoryLogCollectPathMapper {
    int deleteByPrimaryKey(Long id);

    int insert(DirectoryLogCollectPathPO record);

    DirectoryLogCollectPathPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(DirectoryLogCollectPathPO record);

    int deleteByLogCollectTaskId(Long logCollectTaskId);

    List<DirectoryLogCollectPathPO> selectByLogCollectTaskId(Long logCollectTaskId);

}