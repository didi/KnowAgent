package com.didichuxing.datachannel.swan.agent.common.api;

/**
 * @description: metrics指标名
 * @author: huangjw
 * @Date: 18/9/4 20:50
 */
public interface MetricsFields {

    String KEY_BASIC              = "basic";
    String KEY_METRIC             = "metrics";

    // log指标
    String HEARTBEAT_TIME         = "heartbeatTime";
    String HOST_NAME              = "hostname";
    String HOST_NAME_TO_DEL       = "hostName";
    String HOST_IP                = "hostIp";

    String LOG_MODEL_ID_STR       = "logModeId";          // 采集配置Id
    String LOG_MODEL_VERSION_STR  = "logModelVersion";    // 采集配置版本号
    String PATH_ID_STR            = "pathId";             // 采集路径Id
    String PATH_STR               = "path";               // 最新的文件路径
    String CLUSTER_ID_STR         = "clusterId";          // 采集发送的集群信息
    String KAFKA_TOPIC_STR        = "topic";              // 采集发送的topic信息
    String ODIN_NODE_NAME_STR     = "nsName";             // cluster级别的节点名
    String COLLECT_FILE_NAMES_STR = "collectFiles";       // 当前正在采集的文件列表,
    // 文件滚动时将出现采集多个文件的case
    String LATEST_FILE_NAME_STR   = "latestFile";         // 最新的文件
    String LATEST_MODIFY_TIME     = "lastModifyTime";     // 最新更新时间
    String MAX_TIME_GAP_STR       = "maxTimeGap";         // 最大延迟时间
    String LATEST_LOG_TIME_STR    = "logTimeStr";         // 最新采集时间
    String LATEST_LOG_TIME        = "logTime";            // 最新采集时间
    String IS_FILE_EXIST          = "isFileExist";        // 是否存在有效采集文件
    String RELATED_FILES          = "relatedFiles";       // 相关文件数据
    String LOG_PATH_KEY           = "logPathKey";         // logPath唯一key
    String MASTER_FILE            = "masterFile";         // 主文件名

    // agent指标
    String MESSAGE_VERSION        = "messageVersion";
    String START_TIME             = "startTime";
    String CPU_LIMIT              = "cpuLimit";
    String CPU_USAGE              = "cpuUsage";
    String LIMIT_TPS              = "limitTps";
    String GC_COUNT               = "gcCount";
    String FD_COUNT               = "fdCount";

    // metrics指标
    String COLLECT_COUNT          = "sendCount";
    String DISCARD_COUNT          = "filterTooLargeCount";
    String LOSS_COUNT             = "filterOut";

    String COLLECT_STATUS         = "collectStatus";
    String MEMORY_USAGE           = "memoryUsage";

}
