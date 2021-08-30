package com.didichuxing.datachannel.agentmanager.core.k8s;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;

import java.util.List;

public interface K8sPodManageService {

    /**
     * @return 返回系统全量 k8s pod
     */
    List<K8sPodDO> list();

    /**
     * 创建给定 k8spod 对象
     * @param k8sPodDO 待创建 k8spod 对象
     * @param operator 操作者
     * @return 创建成功 k8spod 对象 id
     */
    Long createK8sPod(K8sPodDO k8sPodDO, String operator);

    /**
     * 修改给定 k8spod 对象
     * @param k8sPodDO 待修改 k8spod 对象
     * @param operator 操作者
     */
    void updateK8sPod(K8sPodDO k8sPodDO, String operator);

    /**
     * 删除给定 k8spod 对象
     * @param id 待删除 k8spod 对象 id
     * @param operator 操作者
     */
    void deleteK8sPod(Long id, String operator);

    K8sPodDO getByContainerId(Long hostId);

    /**
     * 通过namespace和name查找pod，不用uuid的原因是pod变更后无法感知
     *
     * @param namespace
     * @param name
     * @return
     */
    K8sPodDO getByNameAndSpace(String namespace, String name);

}
