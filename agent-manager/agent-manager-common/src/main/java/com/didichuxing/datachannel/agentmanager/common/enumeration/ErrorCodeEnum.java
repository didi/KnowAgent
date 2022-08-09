package com.didichuxing.datachannel.agentmanager.common.enumeration;

/**
 * @author huqidong
 * @date 2020-09-21
 * 错误码枚举类
 *
 * 错误码由5位整数构成：
 *  第1位表示错误来源：如 1/2/3 分别代表由 用户端/当前系统/第三方系统 引发
 *  第2位表示领域：如 1/2/3/4/5/6 分别代表由 系统相关/Agent相关/host相关/logcollecttask相关/service相关/kafkacluster相关 引发
 *  第3~5位表示具体错误代码
 */
public enum ErrorCodeEnum {

    /**************************************** 用户端错误状态码 ****************************************/
    ILLEGAL_PARAMS(10000, "参数错误"),

    /**************************************** 当前系统错误状态码 ****************************************/
    /*
     * 系统 相关 21000 ~ 21999
     */
    SYSTEM_INTERNAL_ERROR(21000, "系统内部错误"),
    SYSTEM_NOT_SUPPORT(21001, "系统不支持"),
    SOCKET_CLOSE_FAILED(21002, "socket链接关闭失败"),
    METHOD_NOT_SUPPORTED(21003, "方法不支持"),
    HTTP_CONNECT_FAILED(21004, "http请求失败"),
    UNSUPPORTED_CLASS_CAST_EXCEPTION(21005, "未知类型转换异常"),
    REFLECTION_NEW_INSTANCE_EXCEPTION(21006, "反射创建对象异常"),
    /*
     * Agent 相关 22000 ~ 22999
     */
    AGENT_NOT_EXISTS(22000, "Agent不存在"),
    AGENT_COLLECT_NOT_COMPLETE(22001, "Agent存在未采集完的日志"),
    AGENT_EXISTS_IN_HOST_WHEN_AGENT_INSTALL(22002, "执行Agent安装任务时，发现待安装Agent主机上已存在Agent"),
    AGENT_EXISTS_IN_HOST_WHEN_AGENT_CREATE(22003, "创建Agent对象时，发现待创建Agent主机上已存在Agent"),
    AGENT_UNKNOWN_COLLECT_TYPE(22004, "Agent对应collectType数值型未知"),
    AGENT_HEALTH_NOT_EXISTS(22005, "AgentHealth不存在"),
    AGENT_HEALTH_ERROR_LOGS_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS(22006, "AgentHealthy对应ErrorLogsExistsCheckHealthyTime在系统中不存在"),

    /*
     * Host 相关 23000 ~ 23999
     */
    HOST_NOT_EXISTS(23000, "Host不存在"),
    HOST_CONNECT_FAILED(23001, "Host连接异常"),
    HOST_NOT_CONNECT(23002, "Host连接不通"),
    HOST_NAME_DUPLICATE(23003, "主机名在系统中已存在"),
    RELATION_CONTAINER_EXISTS_WHEN_DELETE_HOST(23004, "主机存在关联的容器导致主机删除失败"),
    HOST_IP_DUPLICATE(23005, "ip 在系统中已存在"),
    RELATION_AGENT_EXISTS_WHEN_DELETE_HOST(23006, "主机存在关联Agent导致主机删除失败"),
    UNKNOWN_HOST_TYPE(23007, "未知主机类型"),
    RELATION_SERVICES_EXISTS_WHEN_DELETE_HOST(23008, "主机存在关联的应用导致主机删除失败"),
    /*
     * KafkaCluster 相关 26000 ~ 26999
     */
    KAFKA_CLUSTER_NOT_EXISTS(26000, "KafkaCluster不存在"),
    KAFKA_CLUSTER_DELETE_FAILED_CAUSE_BY_RELA_LOGCOLLECTTASK_EXISTS(26001, "KafkaCluster删除失败，原因为：系统存在KafkaCluster关联的日志采集任务"),
    KAFKA_CLUSTER_DELETE_FAILED_CAUSE_BY_RELA_AGENT_EXISTS(26002, "KafkaCluster删除失败，原因为：系统存在KafkaCluster关联的Agent"),
    KAFKA_CLUSTER_NAME_DUPLICATE(26003, "系统中已存在相同KafkaClusterName的KafkaCluster对象"),
    KAFKA_CLUSTER_BROKER_CONFIGURATION_DUPLICATE(26004, "系统中已存在相同KafkaClusterBrokerConfiguration的KafkaCluster对象"),
    KAFKA_CLUSTER_NOT_ORIGINATED_FROM_KAFKA_MANAGER(26005, "KafkaCluster非源于kafka-manager系统"),
    KAFKA_CLUSTER_PRODUCER_INIT_CONFIGURATION_IS_NULL(26006, "KafkaCluster客户端配置参数kafkaClusterProducerInitConfiguration为空"),
    KAFKA_CLUSTER_CREATE_OR_UPDATE_FAILED_CAUSE_BY_AGENT_ERROR_LOGS_TOPIC_EXISTS(26007, "KafkaCluster创建或更新失败，原因为：系统已存在配置agent errorlogs 流对应topic的kafkacluster"),
    KAFKA_CLUSTER_CREATE_OR_UPDATE_FAILED_CAUSE_BY_AGENT_METRICS_TOPIC_EXISTS(26008, "KafkaCluster创建或更新失败，原因为：系统已存在配置agent metrics 流对应topic的kafkacluster"),
    /*
     * Service 相关 27000 ~ 27999
     */
    SERVICE_NOT_EXISTS(27000, "Service不存在"),
    SERVICE_NAME_DUPLICATE(27001, "服务名在系统中已存在"),
    SERVICE_DELETE_FAILED_CAUSE_BY_RELA_HOST_EXISTS(27002, "Service删除失败，原因为：系统存在Service关联的主机"),
    SERVICE_DELETE_FAILED_CAUSE_BY_RELA_LOGCOLLECTTASK_EXISTS(27003, "Service删除失败，原因为：系统存在Service关联的日志采集任务"),
    /*
     * LogCollectTask 相关 28000 ~ 28999
     */
    LOGCOLLECTTASK_NOT_EXISTS(28000, "LogCollectTask不存在"),
    LOGCOLLECTTASK_HEALTH_RED(28001, "LogCollectTask健康度为红"),
    LOGCOLLECTTASK_HEALTH_NOT_CALCULATED(28002, "LogCollectTask健康度未计算完成"),
    LOGCOLLECTTASK_NEED_NOT_TO_BE_CHECK(28003, "LogCollectTask处于暂停、或已完成、或处于健康度检查黑名单中，无需检查"),
    UNKNOWN_COLLECT_TYPE(28004, "未知采集类型"),
    LOGCOLLECTTASK_HEALTH_DATA_DISCARD_CHECK_HEALTHY_TIME_NOT_EXISTS(28005, "LogCollectTaskHealthy对应DataDiscardCheckHealthyTime在系统中不存在"),
    LOGCOLLECTTASK_HEALTH_FILE_PATH_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS(28006, "LogCollectTaskHealthy对应FilePathExistsCheckHealthyTime在系统中不存在"),
    LOGCOLLECTTASK_HEALTH_FILE_DISORDER_CHECK_HEALTHY_TIME_NOT_EXISTS(28007, "LogCollectTaskHealthy对应FileDisorderCheckHealthyTime在系统中不存在"),
    LOGCOLLECTTASK_HEALTH_LOG_SLICE_CHECK_HEALTHY_TIME_NOT_EXISTS(28008, "LogCollectTaskHealthy对应LogSliceCheckHealthyTime在系统中不存在"),
    LOGCOLLECTTASK_HEALTH_ABNORMAL_TRUNCATION_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS(28009, "LogCollectTaskHealthy对应AbnormalTruncationExistsCheckHealthyTime在系统中不存在"),
    LOGCOLLECTTASK_HEALTH_CONCURRENT_COLLECT_EXISTS_CHECK_HEALTHY_TIME_NOT_EXISTS(28010, "LogCollectTaskHealthy对应ConcurrentCollectExistsCheckHealthyTime在系统中不存在"),
    /*
     * AgentVersion 相关 29000 ~ 29999
     */
    AGENT_VERSION_NOT_EXISTS(29000, "AgentVersion在系统中不存在"),
    AGENT_VERSION_DUPLICATE(29001, "AgentVersion在系统中已存在"),
    AGENT_VERSION_RELATION_EXISTS(29002, "AgentVersion在系统中存在关联的Agent，无法删除"),
    AGENT_NEED_NOT_TO_BE_CHECK(29003, "Agent处于健康度检查黑名单中，无需检查"),
    AGENT_PACKAGE_FILE_EXISTS(29004, "Agent安装文件在系统中已存在"),
    /*
     * LogCollectTaskHealth 相关 30000 ~ 30999
     */
    LOGCOLLECTTASK_HEALTH_NOT_EXISTS(30000, "LogCollectTaskHealth不存在"),
    SCOPE_COLLECT_LOGCOLLECTTASK_HEALTH_CHECK_NOT_SUPPORT(30001, "不支持时间范围采集类型的采集任务健康度检查"),
    /*
     * 文件操作 相关 31000 ~ 31999
     */
    FILE_UPLOAD_FAILED(31000, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(31001, "文件下载失败"),
    FILE_NOT_EXISTS(31002, "文件不存在"),
    FILE_IS_DIRECTORY(31003, "文件为目录"),
    FILE_CREATE_FAILED(31004, "文件创建失败"),
    FILE_DELETE_FAILED(31005, "文件删除失败"),
    EXCEL_FILE_READ_FAILED(31006, "Excel 文件读取失败"),
    /*
     * AgentOperationTask操作 相关 32000 ~ 32999
     */
    AGENT_OPERATION_TASK_NOT_EXISTS(32000, "AgentOperationTask不存在"),
    /*
     * Elasticsearch 查询相关 33000 ~ 33999
     */
    ELASTICSEARCH_QUERY_FAILED(33000, "Elasticsearch 查询失败"),
    /*
     * Metrics 查询相关 34000 ~ 34999
     */
    METRIC_NOT_EXISTS(34000, "指标不存在"),
    METRICS_TYPE_NOT_EXISTS(34001,"待查询指标系统不存在"),
    METRICS_QUERY_ERROR(34002, "指标查询错误"),
    METRICS_RECORD_NOT_EXISTS(34003, "指标记录不存在"),
    /*
     * MetadataFile 相关 35000 ~ 35999
     */
    META_DATA_FILE_NOT_EXISTS(35000, "元数据文件上传记录在系统中不存在"),
    META_DATA_IN_EXCEL_IS_NULL(35001, "元数据Excel文件内容为空"),
    META_DATA_HOST_IN_EXCEL_IS_NULL(35002, "元数据Excel文件内容中host sheet为空"),
    META_DATA_APPLICATION_IN_EXCEL_IS_NULL(35003, "元数据Excel文件内容中application sheet为空"),
    META_DATA_IN_EXCEL_FIELD_IS_NULL_EXISTS(35004, "元数据Excel文件内容中sheet存在空值字段"),
    META_DATA_IN_EXCEL_FIELD_INVALID_EXISTS(35005, "元数据Excel文件内容中sheet存在非法值字段"),
    META_DATA_IN_EXCEL_HOST_HOST_NAME_DUPLICATE(35006, "元数据Excel文件内容中host sheet存在主机名重复主机记录"),
    META_DATA_IN_EXCEL_APPLICATION_HOST_NAME_NOT_EXISTS(35007, "元数据Excel文件内容中application sheet中关联主机对应主机名在host sheet与系统中不存在"),
    META_DATA_IN_EXCEL_APPLICATION_APPLICATION_NAME_DUPLICATE(35008, "元数据Excel文件内容中application sheet存在应用名重复应用记录"),

    UNKNOWN(90001, "未知错误"),

    /**************************************** 第三方系统错误状态码 100000 ~ 199999 ****************************************/

    SERVICE_NODE_REMOTE_REQUEST_FAILED(100000, "远程请求服务节点信息失败"),
    SERVICE_NODE_REMOTE_REQUEST_URL_NOT_CONFIG(100001, "远程请求服务节点url未配置"),
    HOST_REMOTE_REQUEST_URL_NOT_CONFIG(100002, "远程请求主机url未配置"),
    HOST_REMOTE_RESPONSE_PARSE_FAILED(100003, "远程请求的主机信息解析错误"),
    KAFKA_CLUSTER_REMOTE_REQUEST_URL_NOT_CONFIG(100004, "远程请求 kafkacluster url未配置"),
    KAFKA_CLUSTER_REMOTE_REQUEST_FAILED(100005, "远程请求Kafka Cluster信息失败"),
    CHECK_PERMISSION_REMOTE_FAILED(100006,"没有操作权限"),
    K8S_POD_CONFIG_PULL_FAILED(100007, "k8s pod 配置获取失败"),
    K8S_POD_NO_SUCH_ANNOTATION(100008, "k8s找不到对应的annotation"),
    K8S_POD_PATH_NOT_FOUND(100009, "k8s找不到指定路径"),
    K8S_META_DATA_SYNC_ERROR(100010, "k8s 元信息同步失败")
    ;

    private Integer    code;
    private String message;

    ErrorCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }


}
