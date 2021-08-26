package com.didichuxing.datachannel.agentmanager.core.k8s;

import com.didichuxing.datachannel.agentmanager.common.bean.po.k8s.K8sPodHostPO;
import java.util.List;

public interface K8sPodContainerManageService {

    /**
     * 保存给定k8sPod & 容器关联关系集
     * @param k8sPodContainerPOList 待保存k8sPod & 容器关联关系集
     */
    void createK8sPodContainerList(List<K8sPodHostPO> k8sPodContainerPOList);

    /**
     * 根据给定id删除对应k8sPod & 容器关联关系
     * @param id k8sPod & 容器关联关系对象id
     */
    void deleteById(Long id);

    /**
     * @return 返回系统全量k8sPod & 容器关联关系集
     */
    List<K8sPodHostPO> list();

    int deleteByHostId(Long hostId);

}
