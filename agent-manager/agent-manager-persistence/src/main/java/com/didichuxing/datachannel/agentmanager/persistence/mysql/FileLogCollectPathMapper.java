package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.FileLogCollectPathPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "fileLogCollectPathDAO")
public interface FileLogCollectPathMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FileLogCollectPathPO record);

    FileLogCollectPathPO selectByPrimaryKey(Long id);

    int updateByPrimaryKey(FileLogCollectPathPO record);

    int deleteByLogCollectTaskId(Long logCollectTaskId);

    List<FileLogCollectPathPO> selectByLogCollectTaskId(Long logCollectTaskId);

    Long countAll();

}
