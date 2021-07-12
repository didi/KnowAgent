package com.didichuxing.datachannel.agent.common.api;

/**
 * @description: 静态配置变量
 * @author: huangjw
 * @Date: 18/6/19 17:46
 */
public interface LogConfigConstants {

    String  LONG_TIMESTAMP                     = "LongType-Time";
    int     COLLECT_TYPE_PERIODICITY           = 1;
    int     COLLECT_TYPE_TEMPORALITY           = 2;
    String  NO_LOG_TIME                        = "NoLogTime";

    /**
     * 默认的文件inode
     */
    Long    DEFAULT_INODE                      = -1L;

    /**
     * 默认的文件创建时间
     */
    Long    DEFAULT_CREATE_TIME                = -1L;

    /**
     * 默认的文件修改时间
     */
    Long    DEFAULT_MODIFY_TIME                = -1L;

    /**
     * 过滤类型
     */
    int     FILTER_TYPE_CONTAINS               = 0;
    int     FILTER_TYPE_UNCONTAINS             = 1;

    /**
     * 字符串替换
     */
    String  HOSTNAME_FLAG                      = "${hostname}";
    String  FILE_FLAG                          = "${file}";
    // 类似${time}-1000的形式
    String  TIME_FLAG                          = "${time}";
    String  FILENAME_FLAG                      = "${filename}";
    String  CREATETIME_FLAG                    = "${createTime}";
    String  SOURCEID_FLAG                      = "${sourceId}";
    String  COMPRESSION_FLAG                   = "${compression}";
    String  SINKID_FLAG                        = "${sinkId}";
    String  PATH_FLAG                          = "${path}";
    String  SERVICE_FLAG                       = "${service}";
    String  LEAF_FLAG                          = "${leaf}";

    String  UNDERLINE_SEPARATOR                = "_";
    String  COMMA                              = ",";
    String  SEMICOLON                          = ";";

    /**
     * 本地配置属性
     */
    String  CONFIG_IP                          = "config.ip";
    String  CONFIG_PORT                        = "config.port";
    String  CONFIG_URL                         = "config.url";
    String  MESSSAGE_VERSION                   = "message.version";
    String  HTTP_PORT                          = "http.port";
    String  AGENT_REGISTER_URL                 = "register.url";
    String  AGENT_VERSION                      = "agent.verison";
    String  AGENT_COLLECT_TYPE                 = "collect.type";
    String  CONTAINER_COLLECT_PATH_REQUEST_URL = "container.collect.path.request.url";

    /**
     * 文件过滤黑白名单
     */
    Integer FILE_FILTER_TYPE_BLACK             = 0;
    Integer FILE_FILTER_TYPE_WHIAT             = 1;

    /**
     * post请求 command标记
     */
    String  COMMAND_TAG                        = "command";

    /**
     * post请求 path标记
     */
    String  PATH_TAG                           = "path";

    /**
     * post请求 status标记
     */
    String  STATUS_TAG                         = "status";
    /**
     * 启停
     */
    String  START_TAG                          = "start";
    String  STOP_TAG                           = "stop";

    /**
     * 启停标志
     */
    Integer AGENT_STATUS_RUNNING               = 0;
    Integer AGENT_STATUS_STOP                  = 1;

    /**
     * 全局日志模型标志
     */
    String  GLOBAL_FLAG                        = "Global";

    /**
     * kafka gateway配置
     */
    // 多个gateway ip之间使用分号分割
    String  GATEWAY                            = "gateway";
    String  SEC_PROTOCOL                       = "security_protocol";
    String  SASL_MECHANISM                     = "sasl_mechanism";
    String  SASL_JAAS_CONFIG                   = "sasl_jaas_config";

    byte[]  CTRL_BYTES                         = "\n".getBytes();

    /**
     * 文件头部日志采集长度,用于MD5,用于解决Inode重复的问题
     */
    Integer FILE_HEAD_LENGTH                   = 1024;

    /**
     * 获取头部1K文件Mの5失败是标志
     */
    String  MD5_FAILED_TAG                     = "Failed";

    /**
     * 预估过滤标志
     */
    String  PUBLIC_LOG_TAG                     = "||";

    /**
     * 预估日志过滤字段
     */
    String  FILTER_KEY                         = "_shadow";

}
