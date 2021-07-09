package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.k8s.K8sPodPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("k8sPodDAO")
public interface K8sPodMapper {
    int deleteByPrimaryKey(Long id);

    int insert(K8sPodPO record);

    int insertSelective(K8sPodPO record);

    K8sPodPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(K8sPodPO record);

    int updateByPrimaryKey(K8sPodPO record);

    List<K8sPodPO> queryAll();

    K8sPodPO selectByContainerId(Long id);
}