package com.didichuxing.datachannel.agentmanager.core.metadata;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.metadata.MetadataSyncResult;

/**
 * 元数据管理服务
 */
public interface MetadataManageService {

    /*
     *
     * 元数据 同步，具体包括：
     * 1. 同步全量源数据信息：
     *  1.）定时获取全量 pod 信息 compare & set
     *  2.）全量 service 信息 compare & set
     *  3.）全量 host 信息 compare & set
     *  4.）全量 container 信息 compare & set
     *
     * 2. 同步 关联 关系
     *  1.）同步服务 - 容器关联关系
     *  2.）同步容器 - 主机关联关系
     *  3.）同步容器 - pod 关联关系
     *
     */
    MetadataSyncResult sync();

}
