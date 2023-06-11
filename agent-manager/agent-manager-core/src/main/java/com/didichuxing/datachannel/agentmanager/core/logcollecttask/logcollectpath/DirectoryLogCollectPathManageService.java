package com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.DirectoryLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;

import java.util.List;

public interface DirectoryLogCollectPathManageService {

    /**
     * 创建目录型日志采集路径对象
     * @param directoryLogCollectPath 待创建目录型日志采集路径对象
     * @param operator 操作人
     * @return 持久化的目录型日志采集路径对象 id 值
     */
    Long createDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPath, String operator);

    /**
     * 根据给定日志采集任务id获取该日志采集任务对应所有目录型采集任务路径集
     * @param logCollectTaskId 日志采集任务id
     * @return 返回根据给定日志采集任务id获取到的该日志采集任务对应所有目录型采集任务路径集
     */
    List<DirectoryLogCollectPathDO> getAllDirectoryLogCollectPathByLogCollectTaskId(Long logCollectTaskId);

    /**
     * 根据id删除对应目录型日志采集路径对象
     * @param id 目录型日志采集路径对象 id
     * @param operator 操作人
     */
    void deleteDirectoryLogCollectPath(Long id, String operator);

    /**
     * 更新DirectoryLogCollectPath对象
     * 注：该更新操作直接按给定directoryLogCollectPathDO对象中的值更新，不进行任何对比
     * @param directoryLogCollectPathDO 待更新DirectoryLogCollectPathDO对象
     * @param operator 操作人
     */
    void updateDirectoryLogCollectPath(DirectoryLogCollectPathDO directoryLogCollectPathDO, String operator);

    /**
     * 根据日志采集任务 id 删除其关联的所有DirectoryLogCollectPath对象
     * @param logCollectTaskId 日志采集任务 id
     */
    void deleteByLogCollectTaskId(Long logCollectTaskId);

}
