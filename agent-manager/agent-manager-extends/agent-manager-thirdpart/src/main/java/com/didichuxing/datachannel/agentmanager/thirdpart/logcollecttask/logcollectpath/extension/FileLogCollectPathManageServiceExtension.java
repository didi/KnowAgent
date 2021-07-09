package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.FileLogCollectPathPO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;

import java.util.List;

public interface FileLogCollectPathManageServiceExtension {

    CheckResult checkCreateParameterFileLogCollectPath(FileLogCollectPathDO fileLogCollectPath);

    FileLogCollectPathPO fileLogCollectPath2FileLogCollectPathPO(FileLogCollectPathDO fileLogCollectPath) throws ServiceException;

    FileLogCollectPathDO fileLogCollectPathPO2FileLogCollectPath(FileLogCollectPathPO fileLogCollectPathPO) throws ServiceException;

    List<FileLogCollectPathDO> fileLogCollectPathPOPOList2FileLogCollectPathDOList(List<FileLogCollectPathPO> fileLogCollectPathPOPOList);

}
