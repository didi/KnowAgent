package com.didichuxing.datachannel.agentmanager.core.k8s.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.k8s.K8sPodDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.k8s.K8sPodPO;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.SpringTool;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.K8sPodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class K8sPodManageServiceImpl implements K8sPodManageService {

    @Autowired
    private K8sPodMapper k8sPodDAO;

    @Override
    public List<K8sPodDO> list() {
        List<K8sPodPO> poList = k8sPodDAO.queryAll();
        return ConvertUtil.list2List(poList, K8sPodDO.class);
    }

    @Override
    @Transactional
    public Long createK8sPod(K8sPodDO k8sPodDO, String operator) {
        k8sPodDO.setOperator(operator);
        K8sPodPO po = ConvertUtil.obj2Obj(k8sPodDO, K8sPodPO.class);
        return (long) k8sPodDAO.insertSelective(po);
    }

    @Override
    @Transactional
    public void updateK8sPod(K8sPodDO k8sPodDO, String operator) {
        k8sPodDO.setOperator(operator);
        K8sPodPO k8sPodPO = ConvertUtil.obj2Obj(k8sPodDO, K8sPodPO.class);
        k8sPodDAO.updateByPrimaryKeySelective(k8sPodPO);
    }

    @Override
    @Transactional
    public void deleteK8sPod(Long id, String operator) {
        k8sPodDAO.deleteByPrimaryKey(id);
    }

    @Override
    public K8sPodDO getByContainerId(Long hostId) {
        K8sPodPO k8sPodPO = k8sPodDAO.selectByContainerId(hostId);
        return ConvertUtil.obj2Obj(k8sPodPO, K8sPodDO.class);
    }

}
