package com.didichuxing.datachannel.agentmanager.core.logcollecttask.logcollectpath;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask.FileLogCollectPathDO;
import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;

import java.util.List;

public interface FileLogCollectPathManageService {

    /**
     * 创建文件型日志采集路径对象流程
     * @param fileLogCollectPath 待创建文件型日志采集路径对象
     * @param operator 操作人
     * @return 返回持久化的文件型日志采集路径对象 id 值
     */
    Long createFileLogCollectPath(FileLogCollectPathDO fileLogCollectPath, String operator);

    /**
     * 根据给定日志采集任务id获取该日志采集任务对应所有文件型采集任务路径集
     * @param logCollectTaskId 日志采集任务id
     * @return 返回根据给定日志采集任务id获取到的该日志采集任务对应所有文件型采集任务路径集
     */
    List<FileLogCollectPathDO> getAllFileLogCollectPathByLogCollectTaskId(Long logCollectTaskId);

    /**
     * 根据 id 删除对应 FileLogCollectPath 对象
     * @param id 待删除 FileLogCollectPathDO 对象 id
     * @param operator 操作人
     */
    void deleteFileLogCollectPath(Long id, String operator);

    /**
     * 更新FileLogCollectPath对象
     * 注：该更新操作直接按给定fileLogCollectPathDO对象中的值更新，不进行任何对比
     * @param fileLogCollectPathDO 待更新FileLogCollectPath对象
     * @param operator 操作人
     */
    void updateFileLogCollectPath(FileLogCollectPathDO fileLogCollectPathDO, String operator);

    /**
     * 根据日志采集任务 id 删除其关联的所有FileLogCollectPath对象
     * @param logCollectTaskId 日志采集任务 id
     */
    void deleteByLogCollectTaskId(Long logCollectTaskId);

    /**
     * @return 返回系统全量文件型路径数
     */
    Long countAll();

    FileLogCollectPathDO getById(Long id);

}
