package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.FileLogCollectPathPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension.FileLogCollectPathManageServiceExtension;

import java.util.List;

@org.springframework.stereotype.Service
public class DefaultFileLogCollectPathManageServiceExtensionImpl implements FileLogCollectPathManageServiceExtension {

    @Override
    public CheckResult checkCreateParameterFileLogCollectPath(FileLogCollectPathDO fileLogCollectPath) {
        if(null == fileLogCollectPath) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=FileLogCollectPathManageServiceExtensionImpl||method=checkCreateParameterFileLogCollectPath||msg={%s}",
                            "入参fileLogCollectPath对象不可为空"
                    )
            );
        }
        if(null == fileLogCollectPath.getLogCollectTaskId()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=FileLogCollectPathManageServiceExtensionImpl||method=checkCreateParameterFileLogCollectPath||msg={%s}",
                            String.format("入参fileLogCollectPath对象={%s}对应logCollectTaskId属性值不可为空", JSON.toJSONString(fileLogCollectPath))
                    )
            );
        }

        //TODO：

        return new CheckResult(true);
    }

    @Override
    public FileLogCollectPathPO fileLogCollectPath2FileLogCollectPathPO(FileLogCollectPathDO fileLogCollectPath) throws ServiceException {
        FileLogCollectPathPO fileLogCollectPathPO = null;
        try {
            fileLogCollectPathPO = ConvertUtil.obj2Obj(fileLogCollectPath, FileLogCollectPathPO.class, "realPath");
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=FileLogCollectPathManageServiceExtensionImpl||method=fileLogCollectPath2FileLogCollectPathPO||msg={%s}",
                            String.format("FileLogCollectPath对象={%s}转化为FileLogCollectPathPO对象失败，原因为：%s", JSON.toJSONString(fileLogCollectPath), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if(null == fileLogCollectPathPO) {
            throw new ServiceException(
                    String.format(
                            "class=FileLogCollectPathManageServiceExtensionImpl||method=fileLogCollectPath2FileLogCollectPathPO||msg={%s}",
                            String.format("FileLogCollectPath对象={%s}转化为FileLogCollectPathPO对象失败", JSON.toJSONString(fileLogCollectPath))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return fileLogCollectPathPO;
    }

    @Override
    public FileLogCollectPathDO fileLogCollectPathPO2FileLogCollectPath(FileLogCollectPathPO fileLogCollectPathPO) throws ServiceException {
        FileLogCollectPathDO fileLogCollectPath = null;
        try {
            fileLogCollectPath = ConvertUtil.obj2Obj(fileLogCollectPathPO, FileLogCollectPathDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=FileLogCollectPathManageServiceExtensionImpl||method=fileLogCollectPathPO2FileLogCollectPath||msg={%s}",
                            String.format("FileLogCollectPathPO对象={%s}转化为FileLogCollectPath对象失败，原因为：%s", JSON.toJSONString(fileLogCollectPathPO), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if(null == fileLogCollectPath) {
            throw new ServiceException(
                    String.format(
                            "class=FileLogCollectPathManageServiceExtensionImpl||method=fileLogCollectPathPO2FileLogCollectPath||msg={%s}",
                            String.format("FileLogCollectPathPO对象={%s}转化为FileLogCollectPath对象失败", JSON.toJSONString(fileLogCollectPathPO))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return fileLogCollectPath;
    }

    @Override
    public List<FileLogCollectPathDO> fileLogCollectPathPOPOList2FileLogCollectPathDOList(List<FileLogCollectPathPO> fileLogCollectPathPOPOList) {
        return ConvertUtil.list2List(fileLogCollectPathPOPOList, FileLogCollectPathDO.class);
    }

}
