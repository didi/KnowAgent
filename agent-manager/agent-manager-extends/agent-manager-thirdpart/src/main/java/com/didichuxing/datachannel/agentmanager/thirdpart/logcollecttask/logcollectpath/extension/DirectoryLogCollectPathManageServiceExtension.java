package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.DirectoryLogCollectPathPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;

import java.util.List;

public interface DirectoryLogCollectPathManageServiceExtension {

    /**
     * 检查待创建directoryLogCollectPath对象是否合法
     * @param directoryLogCollectPath 待创建directoryLogCollectPath对象
     * @return true：合法 false：不合法
     */
    CheckResult checkCreateParameterDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPath);

    /**
     * 将directoryLogCollectPath对象转化为DirectoryLogCollectPathPO对象
     * @param directoryLogCollectPath 目录型日志采集路径对象
     * @return 返回将directoryLogCollectPath对象转化为的DirectoryLogCollectPathPO对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    DirectoryLogCollectPathPO directoryLogCollectPath2DirectoryLogCollectPathPO(DirectoryLogCollectPathDO directoryLogCollectPath) throws ServiceException;

    /**
     * 将directoryLogCollectPathPO对象转化为DirectoryLogCollectPath对象
     * @param directoryLogCollectPathPO 待转化目录型日志采集路径对象
     * @return 返回将directoryLogCollectPathPO对象转化为的DirectoryLogCollectPath对象
     * @throws ServiceException 执行该函数过程中出现的异常
     */
    DirectoryLogCollectPathDO directoryLogCollectPathPO2DirectoryLogCollectPath(DirectoryLogCollectPathPO directoryLogCollectPathPO) throws ServiceException;

    /**
     * 将给定DirectoryLogCollectPathPO对象集转化为DirectoryLogCollectPathDO对象集
     * @param directoryLogCollectPathPOList 待转化DirectoryLogCollectPathPO对象集
     * @return 返回将给定DirectoryLogCollectPathPO对象集转化为的DirectoryLogCollectPathDO对象集
     */
    List<DirectoryLogCollectPathDO> directoryLogCollectPathPOList2DirectoryLogCollectPathDOList(List<DirectoryLogCollectPathPO> directoryLogCollectPathPOList);

}
