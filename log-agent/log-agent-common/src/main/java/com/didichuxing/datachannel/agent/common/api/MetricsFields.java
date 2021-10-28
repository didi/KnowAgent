package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: metrics指标名
 * @author: huangjw
 * @Date: 18/9/4 20:50
 */
public interface MetricsFields {

    String KEY_BASIC                        = "basic";
    String KEY_METRIC                       = "metrics";

    // log指标
    String HEARTBEAT_TIME                   = "heartbeatTime";
    String HOST_NAME                        = "hostname";
    String HOST_NAME_TO_DEL                 = "hostName";
    String HOST_IP                          = "hostIp";

    String LOG_MODEL_ID_STR                 = "logModeId";                         // 采集配置Id
    String LOG_MODEL_VERSION_STR            = "logModelVersion";                   // 采集配置版本号
    String PATH_ID_STR                      = "pathId";                            // 采集路径Id
    String PATH_STR                         = "path";                              // 最新的文件路径
    String CLUSTER_ID_STR                   = "clusterId";                         // 采集发送的集群信息
    String KAFKA_TOPIC_STR                  = "topic";                             // 采集发送的topic信息
    String ODIN_NODE_NAME_STR               = "nsName";                            // cluster级别的节点名
    String COLLECT_FILE_NAMES_STR           = "collectFiles";                      // 当前正在采集的文件列表,
    // 文件滚动时将出现采集多个文件的case
    String LATEST_FILE_NAME_STR             = "latestFile";                        // 最新的文件
    String LATEST_MODIFY_TIME               = "lastModifyTime";                    // 最新更新时间
    String MAX_TIME_GAP_STR                 = "maxTimeGap";                        // 最大延迟时间
    String LATEST_LOG_TIME_STR              = "logTimeStr";                        // 最新采集时间
    String LATEST_LOG_TIME                  = "logTime";                           // 最新采集时间
    String IS_FILE_EXIST                    = "isFileExist";                       // 是否存在有效采集文件
    String RELATED_FILES                    = "relatedFiles";                      // 相关文件数据
    String LOG_PATH_KEY                     = "logPathKey";                        // logPath唯一key
    String MASTER_FILE                      = "masterFile";                        // 主文件名

    // agent指标
    String MESSAGE_VERSION                  = "messageVersion";
    String START_TIME                       = "startTime";
    String CPU_LIMIT                        = "cpuLimit";
    String CPU_USAGE                        = "cpuUsage";
    String TOTAL_CPU_USAGE                  = "totalCpuUsage";
    String LIMIT_TPS                        = "limitTps";
    String FULL_GC_COUNT                    = "gcCount";
    String FULL_GC_TIME                     = "fullGcTime";
    String YOUNG_GC_COUNT                   = "youngGcCount";
    String YOUNG_GC_TIME                    = "youngGcTime";
    String FD_COUNT                         = "fdCount";
    String MEMORY_USAGE                     = "memoryUsage";
    String HEAP_MEMORY_USAGE                = "heapMemoryUsage";
    String NON_HEAP_MEMORY_USAGE            = "nonHeapMemoryUsage";
    String MAX_HEAP_SIZE                    = "maxHeapSize";
    String MEMORY_USED_PEAK                 = "memoryUsedPeak";
    String THREAD_NUM                       = "threadNum";
    String THREAD_NUM_PEAK                  = "threadNumPeak";
    String DISK_IO_READ_BYTES_PS            = "diskIOReadBytesPS";
    String DISK_IO_WRITE_BYTES_PS           = "diskIOWriteBytesPS";
    String DISK_IO_AWAIT_TIME               = "diskIOAwaitTimePercent";
    String NETWORK_RX_BYTES_PS              = "networkRXBytesPS";
    String NETWORK_TX_BYTES_PS              = "networkTXBytesPS";
    String TCP_CONNECTION_NUM               = "tcpConnectionNum";
    String TCP_TIME_WAIT_NUM                = "tcpTimeWaitNum";
    String TCP_CLOSE_WAIT_NUM               = "tcpCloseWaitNum";

    // 系统指标
    String SYSTEM_STARTUP_TIME              = "systemStartupTime";
    String SYSTEM_CURRENT_TIME              = "systemCurrentTime";
    String SYSTEM_CPU_USAGE                 = "systemCpuUsage";
    String TOTAL_SYSTEM_CPU_USAGE           = "totalSystemCpuUsage";
    String SYSTEM_CPU_LOAD                  = "systemCpuLoad";
    String SYSTEM_MEMORY_FREE               = "systemMemoryFree";
    String SYSTEM_MEMORY_TOTAL              = "systemMemoryTotal";
    String SYSTEM_MEMORY_USED               = "systemMemoryUsed";
    String SYSTEM_MEMORY_SWAP_SIZE          = "systemMemorySwapSize";
    String SYSTEM_MEMORY_SWAP_FREE          = "systemMemorySwapFree";
    String SYSTEM_MEMORY_SWAP_USED          = "systemMemorySwapUsed";
    String SYSTEM_DISK_TOTAL                = "systemDiskTotal";
    String SYSTEM_DISK_USED                 = "systemDiskUsed";
    String SYSTEM_DISK_FREE                 = "systemDiskFree";
    String SYSTEM_DISK_FREE_MIN             = "systemDiskFreeMin";
    String SYSTEM_DISK_NUM                  = "systemDiskNum";
    String SYSTEM_FD_USED                   = "systemFdUsed";
    String SYSTEM_MAX_FD_SIZE               = "systemMaxFdSize";
    String SYSTEM_DISK_IO_READ_BYTES_PS     = "systemDiskIOReadBytesPS";
    String SYSTEM_DISK_IO_WRITE_BYTES_PS    = "systemDiskIOWRITEBytesPS";
    String SYSTEM_DISK_IO_USAGE_PERCENT     = "systemDiskIOUsagePercent";
    String SYSTEM_IO_PS                     = "systemIOPS";
    String SYSTEM_DISK_IO_RESPONSE_TIME_AVG = "systemDiskIOResponseTimeAvg";
    String SYSTEM_DISK_IO_PROCESS_TIME_AVG  = "systemDiskIOProcessTimeAvg";
    String SYSTEM_NETWORK_RX_BYTES_PS       = "systemNetworkRXBytesPS";
    String SYSTEM_NETWORK_TX_BYTES_PS       = "systemNetworkTXBytesPS";
    String SYSTEM_TCP_CONNECTION_NUM        = "systemNetworkTcpConnectionNum";
    String SYSTEM_TCP_TIME_WAIT_NUM         = "systemNetworkTcpTimeWaitNum";
    String SYSTEM_TCP_CLOSE_WAIT_NUM        = "systemNetworkTcpCloseWaitNum";
    String SYSTEM_TCP_ACTIVE_OPENS          = "systemNetworkTcpActiveOpens";
    String SYSTEM_TCP_PASSIVE_OPENS         = "systemNetworkTcpPassiveOpens";
    String SYSTEM_TCP_ATTEMPT_FAILS         = "systemNetworkTcpAttemptFails";
    String SYSTEM_TCP_ESTAB_RESETS          = "systemNetworkTcpEstabResets";
    String SYSTEM_TCP_RETRANS_SEGS          = "systemNetworkTcpRetransSegs";
    String SYSTEM_TCP_EXT_LISTEN_OVERFLOWS  = "systemNetworkTcpExtListenOverflows";
    String SYSTEM_UDP_IN_DATAGRAMS          = "systemNetworkUdpInDatagrams";
    String SYSTEM_UDP_OUT_DATAGRAMS         = "systemNetworkUdpOutDatagrams";
    String SYSTEM_UDP_IN_ERRORS             = "systemNetworkUdpInErrors";
    String SYSTEM_UDP_NO_PORTS              = "systemNetworkUdpNoPorts";
    String SYSTEM_UDP_SEND_BUFFER_ERRORS    = "systemNetworkUdpSendBufferErrors";

    // metrics指标
    String COLLECT_COUNT                    = "sendCount";
    String DISCARD_COUNT                    = "filterTooLargeCount";
    String LOSS_COUNT                       = "filterOut";

    String COLLECT_STATUS                   = "collectStatus";

}
