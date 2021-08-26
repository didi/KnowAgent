package com.didichuxing.datachannel.agentmanager.core.k8s.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.po.k8s.K8sPodHostPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.util.LogUtil;
import com.didichuxing.datachannel.agentmanager.core.k8s.K8sPodContainerManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.K8sPodHostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
public class K8sPodContainerManageServiceImpl implements K8sPodContainerManageService {

    @Autowired
    private K8sPodHostMapper k8sPodhostDAO;

    @Override
    @Transactional
    public void createK8sPodContainerList(List<K8sPodHostPO> k8sPodContainerPOList) {
        handleCreateK8sPodContainerList(k8sPodContainerPOList);
    }

    private void handleCreateK8sPodContainerList(List<K8sPodHostPO> k8sPodContainerPOList) {
        if (k8sPodContainerPOList == null) {
            throw new ServiceException("输入的k8sPodContainerPOList为空", ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        }
        for (K8sPodHostPO k8sPodContainerPO : k8sPodContainerPOList) {
            CheckResult result = checkPodContainer(k8sPodContainerPO);
            if (result.getCheckResult()) {
                k8sPodhostDAO.batchInsertHost(k8sPodContainerPOList);
            }
        }

    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        k8sPodhostDAO.deleteByPrimaryKey(id);
    }

    @Override
    public List<K8sPodHostPO> list() {
        return k8sPodhostDAO.queryAll();
    }

    @Override
    public int deleteByHostId(Long hostId) {
        return k8sPodhostDAO.deleteByHostId(hostId);
    }

    private CheckResult checkPodContainer(K8sPodHostPO k8sPodContainerPO) {
        if (k8sPodContainerPO == null) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(LogUtil.defaultLogFormat(), "入参k8sPodContainer为空"));
        }
        if (k8sPodContainerPO.getHostId() == null || k8sPodContainerPO.getHostId() <= 0) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(LogUtil.defaultLogFormat(), "入参k8sPodContainer中hostId为空"));
        }
        if (k8sPodContainerPO.getK8sPodId() == null || k8sPodContainerPO.getK8sPodId() <= 0) {
            return new CheckResult(false, ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(LogUtil.defaultLogFormat(), "入参k8sPodContainer中podId为空"));
        }
        return new CheckResult(true);
    }

}
