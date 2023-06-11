package com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension.impl;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask.DirectoryLogCollectPathPO;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;
import com.didichuxing.datachannel.agentmanager.common.bean.common.CheckResult;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.thirdpart.logcollecttask.logcollectpath.extension.DirectoryLogCollectPathManageServiceExtension;

import java.util.List;

@org.springframework.stereotype.Service
public class DefaultDirectoryLogCollectPathManageServiceExtensionImpl implements DirectoryLogCollectPathManageServiceExtension {

    @Override
    public CheckResult checkCreateParameterDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPath) {
        if(null == directoryLogCollectPath) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=DirectoryLogCollectPathManageServiceExtensionImpl||method=checkCreateParameterDirectoryLogCollectPath||msg={%s}",
                            "入参directoryLogCollectPath对象不可为空"
                    )
            );
        }
        if(null == directoryLogCollectPath.getLogCollectTaskId()) {
            return new CheckResult(
                    false,
                    ErrorCodeEnum.ILLEGAL_PARAMS.getCode(),
                    String.format(
                            "class=DirectoryLogCollectPathManageServiceExtensionImpl||method=checkCreateParameterDirectoryLogCollectPath||msg={%s}",
                            String.format("入参directoryLogCollectPath对象={%s}对应logCollectTaskId属性值不可为空", JSON.toJSONString(directoryLogCollectPath))
                    )
            );
        }


        //TODO：

        return new CheckResult(true);
    }

    @Override
    public DirectoryLogCollectPathPO directoryLogCollectPath2DirectoryLogCollectPathPO(DirectoryLogCollectPathDO directoryLogCollectPath) throws ServiceException {
        DirectoryLogCollectPathPO directoryLogCollectPathPO = null;
        try {
            directoryLogCollectPathPO = ConvertUtil.obj2Obj(directoryLogCollectPath, DirectoryLogCollectPathPO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DirectoryLogCollectPathManageServiceExtensionImpl||method=directoryLogCollectPath2DirectoryLogCollectPathPO||msg={%s}",
                            String.format("DirectoryLogCollectPath对象={%s}转化为DirectoryLogCollectPathPO对象失败，原因为：%s", JSON.toJSONString(directoryLogCollectPath), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if(null == directoryLogCollectPathPO) {
            throw new ServiceException(
                    String.format(
                            "class=DirectoryLogCollectPathManageServiceExtensionImpl||method=directoryLogCollectPath2DirectoryLogCollectPathPO||msg={%s}",
                            String.format("DirectoryLogCollectPath对象={%s}转化为DirectoryLogCollectPathPO对象失败", JSON.toJSONString(directoryLogCollectPath))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return directoryLogCollectPathPO;
    }

    @Override
    public DirectoryLogCollectPathDO directoryLogCollectPathPO2DirectoryLogCollectPath(DirectoryLogCollectPathPO directoryLogCollectPathPO) throws ServiceException {
        DirectoryLogCollectPathDO directoryLogCollectPath = null;
        try {
            directoryLogCollectPath = ConvertUtil.obj2Obj(directoryLogCollectPathPO, DirectoryLogCollectPathDO.class);
        } catch (Exception ex) {
            throw new ServiceException(
                    String.format(
                            "class=DirectoryLogCollectPathManageServiceExtensionImpl||method=directoryLogCollectPathPO2DirectoryLogCollectPath||msg={%s}",
                            String.format("DirectoryLogCollectPathPO对象={%s}转化为DirectoryLogCollectPath对象失败，原因为：%s", JSON.toJSONString(directoryLogCollectPathPO), ex.getMessage())
                    ),
                    ex,
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        if(null == directoryLogCollectPath) {
            throw new ServiceException(
                    String.format(
                            "class=DirectoryLogCollectPathManageServiceExtensionImpl||method=directoryLogCollectPathPO2DirectoryLogCollectPath||msg={%s}",
                            String.format("DirectoryLogCollectPathPO对象={%s}转化为DirectoryLogCollectPath对象失败", JSON.toJSONString(directoryLogCollectPathPO))
                    ),
                    ErrorCodeEnum.SYSTEM_INTERNAL_ERROR.getCode()
            );
        }
        return directoryLogCollectPath;
    }

    @Override
    public List<DirectoryLogCollectPathDO> directoryLogCollectPathPOList2DirectoryLogCollectPathDOList(List<DirectoryLogCollectPathPO> directoryLogCollectPathPOList) {
        return ConvertUtil.list2List(directoryLogCollectPathPOList, DirectoryLogCollectPathDO.class);
    }

}
