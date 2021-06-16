package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import com.didichuxing.datachannel.agentmanager.common.bean.po.k8s.K8sPodHostPO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "k8sPodHostDAO")
public interface K8sPodHostMapper {
    int deleteByPrimaryKey(Long id);

    int insert(K8sPodHostPO record);

    K8sPodHostPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(K8sPodHostPO record);

    int updateByPrimaryKey(K8sPodHostPO record);

    int batchInsertHost(List<K8sPodHostPO> records);

    List<K8sPodHostPO> queryAll();
}