package com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.FileLogCollectPathPO;
import com.didichuxing.datachannel.agentmanager.common.constant.CommonConstant;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.health.LogCollectTaskHealthDetailManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath.FileLogCollectPathManageService;
import com.didichuxing.datachannel.agentmanager.core.logcollecttask.manage.LogCollectTaskManageService;
import com.didichuxing.datachannel.agentmanager.persistence.mysql.FileLogCollectPathMapper;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension.FileLogCollectPathManageServiceExtension;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class FileLogCollectPathManageServiceImpl implements FileLogCollectPathManageService {

    @Autowired
    private FileLogCollectPathManageServiceExtension fileLogCollectPathManageServiceExtension;

    @Autowired
    private LogCollectTaskManageService logCollectTaskManageService;

    @Autowired
    private FileLogCollectPathMapper fileLogCollectPathDAO;

    @Autowired
    private LogCollectTaskHealthDetailManageService logCollectTaskHealthDetailManageService;

    @Override
    @Transactional
    public Long createFileLogCollectPath(FileLogCollectPathDO fileLogCollectPath, String operator) {
        return this.createFileLogCollectPathProcess(fileLogCollectPath, operator);
    }

    @Override
    public List<FileLogCollectPathDO> getAllFileLogCollectPathByLogCollectTaskId(Long logCollectTaskId) {
        List<FileLogCollectPathPO> fileLogCollectPathPOPOList = fileLogCollectPathDAO.selectByLogCollectTaskId(logCollectTaskId);
        if(CollectionUtils.isEmpty(fileLogCollectPathPOPOList)) {
            return new ArrayList<>();
        }
        return fileLogCollectPathManageServiceExtension.fileLogCollectPathPOPOList2FileLogCollectPathDOList(fileLogCollectPathPOPOList);
    }

    @Override
    @Transactional
    public void deleteFileLogCollectPath(Long id, String operator) {
        fileLogCollectPathDAO.deleteByPrimaryKey(id);
        /*
         * 删除对应表 tb_log_collect_task_health_detail 记录
         */
        logCollectTaskHealthDetailManageService.deleteByLogCollectPathId(id);
    }

    @Override
    @Transactional
    public void updateFileLogCollectPath(FileLogCollectPathDO fileLogCollectPathDO, String operator) {
        FileLogCollectPathPO fileLogCollectPathPO = fileLogCollectPathManageServiceExtension.fileLogCollectPath2FileLogCollectPathPO(fileLogCollectPathDO);
        fileLogCollectPathPO.setOperator(CommonConstant.getOperator(operator));
        fileLogCollectPathDAO.updateByPrimaryKey(fileLogCollectPathPO);
    }

    @Override
    @Transactional
    public void deleteByLogCollectTaskId(Long logCollectTaskId) {
        fileLogCollectPathDAO.deleteByLogCollectTaskId(logCollectTaskId);
    }

    @Override
    public Long countAll() {
        return fileLogCollectPathDAO.countAll();
    }

    @Override
    public FileLogCollectPathDO getById(Long id) {
        FileLogCollectPathPO fileLogCollectPathPO = fileLogCollectPathDAO.selectByPrimaryKey(id);
        return fileLogCollectPathManageServiceExtension.fileLogCollectPathPO2FileLogCollectPath(fileLogCollectPathPO);
    }

    /**
     * 创建文件型日志采集路径对象流程
     * @param fileLogCollectPath 待创建文件型日志采集路径对象
     * @param operator 操作人
     * @return 返回持久化的文件型日志采集路径对象 id 值
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    private Long createFileLogCollectPathProcess(FileLogCollectPathDO fileLogCollectPath, String operator) throws ServiceException {
        CheckResult checkResult = this.fileLogCollectPathManageServiceExtension.checkCreateParameterFileLogCollectPath(fileLogCollectPath);
        if(!checkResult.getCheckResult()) {
            throw new ServiceException(
                    checkResult.getMessage(),
                    checkResult.getCode()
            );
        }
        if(null == logCollectTaskManageService.getById(fileLogCollectPath.getLogCollectTaskId())) {
            throw new ServiceException(
                    String.format("系统中不存在入参id为fileLogCollectPath对象={%s}对应logCollectTaskId属性值的LogCollectTask对象", JSON.toJSONString(fileLogCollectPath)),
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode()
            );
        }
        FileLogCollectPathPO fileLogCollectPathPO = fileLogCollectPathManageServiceExtension.fileLogCollectPath2FileLogCollectPathPO(fileLogCollectPath);
        fileLogCollectPathPO.setOperator(CommonConstant.getOperator(operator));
        fileLogCollectPathDAO.insert(fileLogCollectPathPO);
        return fileLogCollectPathPO.getId();
    }

}
