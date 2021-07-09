package com.didichuxing.datachannel.agentmanager.thirdpart.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetadataResult;

/**
 * k8s 元数据管理服务类
 */
public interface MetadataManageServiceExtension {

    MetadataResult pullMetadataResultFromRemote();

}
