package com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.DirectoryLogCollectPathPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.DirectoryLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.DirectoryLogCollectPathMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension.DirectoryLogCollectPathManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class DirectoryLogCollectPathManageServiceImpl implements DirectoryLogCollectPathManageService {

    @Autowired
    private DirectoryLogCollectPathManageServiceExtension directoryLogCollectPathManageServiceExtension;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private DirectoryLogCollectPathMapper directoryLogCollectPathDAO;

    @Override
    @Transactional
    public Long createDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPath, String operator) {
        return this.handleCreateDirectoryLogCollectPath(directoryLogCollectPath, operator);
    }

    @Override
    public List<DirectoryLogCollectPathDO> getAllDirectoryLogCollectPathByLogCollectTaskId(Long logCollectTaskId) {
        List<DirectoryLogCollectPathPO> directoryLogCollectPathPOList = directoryLogCollectPathDAO.selectByLogCollectTaskId(logCollectTaskId);
        if(CollectionUtils.isEmpty(directoryLogCollectPathPOList)) {
            return new ArrayList<>();
        }
        return directoryLogCollectPathManageServiceExtension.directoryLogCollectPathPOList2DirectoryLogCollectPathDOList(directoryLogCollectPathPOList);
    }

    @Override
    @Transactional
    public void deleteDirectoryLogCollectPath(Long id, String operator) {
        directoryLogCollectPathDAO.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public void updateDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPathDO, String operator) {
        DirectoryLogCollectPathPO directoryLogCollectPathPO = directoryLogCollectPathManageServiceExtension.directoryLogCollectPath2DirectoryLogCollectPathPO(directoryLogCollectPathDO);
        directoryLogCollectPathPO.setOperator(CommonConstant.getOperator(operator));
        directoryLogCollectPathDAO.updateByPrimaryKey(directoryLogCollectPathPO);
    }

    @Override
    @Transactional
    public void deleteByLogCollectTaskId(Long logCollectTaskId) {
        directoryLogCollectPathDAO.deleteByLogCollectTaskId(logCollectTaskId);
    }

    /**
     * 创建目录型日志采集路径对象流程
     * @param directoryLogCollectPath 待创建目录型日志采集路径对象
     * @param operator 操作人
     * @return 持久化的目录型日志采集路径对象 id 值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long handleCreateDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPath, String operator) throws ServiceException {
        CheckResult checkResult = directoryLogCollectPathManageServiceExtension.checkCreateParameterDirectoryLogCollectPath(directoryLogCollectPath);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        if(null == logCollectTaskManageService.getById(directoryLogCollectPath.getLogCollectTaskId())) {
            throw new ServiceException(
                    String.format("系统中不存在入参id为directoryLogCollectPath对象={%s}对应logCollectTaskId属性值的LogCollectTask对象", JSON.toJSONString(directoryLogCollectPath)),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        DirectoryLogCollectPathPO directoryLogCollectPathPO = directoryLogCollectPathManageServiceExtension.directoryLogCollectPath2DirectoryLogCollectPathPO(directoryLogCollectPath);
        directoryLogCollectPathPO.setOperator(CommonConstant.getOperator(operator));
        directoryLogCollectPathDAO.insert(directoryLogCollectPathPO);
        return directoryLogCollectPathPO.getId();
    }

}
