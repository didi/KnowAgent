package com.didichuxing.datachannel.agent.source.log.metrics;

import com.didichuxing.datachannel.agent.engine.metrics.metric.SourceMetricsFields;

/**
 * @description: log source 指标名
 * @author: huangjw
 * @Date: 2019-07-18 15:16
 */
public interface FileMetricsFields extends SourceMetricsFields {

    String LATEST_FILE_NAME_STR   = PREFIX_METRICS_ + "latestFile";    // 最新的文件
    String LATEST_MODIFY_TIME     = PREFIX_METRICS_ + "lastModifyTime"; // 最新更新时间
    String MAX_TIME_GAP_STR       = PREFIX_METRICS_ + "maxTimeGap";    // 最大延迟时间
    String LATEST_LOG_TIME_STR    = PREFIX_METRICS_ + "logTimeStr";    // 最新采集时间
    String LATEST_LOG_TIME        = PREFIX_METRICS_ + "logTime";       // 最新采集时间
    String IS_FILE_EXIST          = PREFIX_METRICS_ + "isFileExist";   // 是否存在有效采集文件
    String RELATED_FILES          = PREFIX_METRICS_ + "relatedFiles";  // 相关文件数据
    String MASTER_FILE            = PREFIX_METRICS_ + "masterFile";    // 主文件名
    String PATH_STR               = PREFIX_METRICS_ + "path";          // 主文件名
    String COLLECT_FILE_NAMES_STR = PREFIX_METRICS_ + "collectFiles";  // 当前正在采集的文件列表,
    String LOG_PATH_KEY           = PREFIX_METRICS_ + "logPathKey";    // 日志路径唯一key,
    String PATH_ID_STR            = PREFIX_METRICS_ + "pathId";        // pathId,
    String LOG_ID_STR             = PREFIX_METRICS_ + "logModeId";     // logModeId,

    String DOCKER_PATH_STR        = PREFIX_METRICS_ + "dockerPath";    // 容器到宿主机的映射路径
}
