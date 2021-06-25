
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for agent_metric
-- ----------------------------
DROP TABLE IF EXISTS `agent_metric`;
CREATE TABLE `agent_metric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cpu_usage` double NOT NULL DEFAULT '0',
  `heartbeat_time` bigint(20) NOT NULL DEFAULT '0',
  `host_ip` varchar(32) NOT NULL DEFAULT '',
  `cpu_limit` double NOT NULL DEFAULT '0',
  `gc_count` int(11) NOT NULL DEFAULT '0',
  `path_id` int(11) NOT NULL DEFAULT '-1',
  `log_mode_id` int(11) NOT NULL DEFAULT '-1',
  `hostname` varchar(64) NOT NULL DEFAULT '',
  `fd_count` int(11) NOT NULL DEFAULT '0',
  `limit_tps` bigint NOT NULL DEFAULT '0',
  `start_time` timestamp NOT NULL DEFAULT '1971-01-01 00:00:00',
  `log_path_key` int(11) NOT NULL DEFAULT '-1',
  `message_version` varchar(32) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `operator` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11922 DEFAULT CHARSET=utf8;

drop table if exists `auv_task`;
CREATE TABLE `auv_task` (
                          `id` BIGINT(20) auto_increment,
                          `code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'task code',
                          `name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '名称',
                          `description` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '任务描述',
                          `cron` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'cron 表达式',
                          `class_name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '类的全限定名',
                          `params` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '执行参数 map 形式{key1:value1,key2:value2}',
                          `retry_times` INT(10) DEFAULT 0 NOT NULL COMMENT '允许重试次数',
                          `last_fire_time` DATETIME DEFAULT NOW() COMMENT '上次执行时间 [Deprecated]',
                          `timeout` BIGINT(20) DEFAULT 0 NOT NULL COMMENT '超时 毫秒',
                          `status` TINYINT(4) DEFAULT 0 NOT NULL COMMENT '1等待 2运行中 3暂停 [Deprecated]',
                          `sub_task_codes` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '子任务code列表,逗号分隔',
                          `consensual` VARCHAR(200) DEFAULT '' NOT NULL COMMENT '执行策略',
                          `task_worker_str` VARCHAR(1000) DEFAULT '' NOT NULL COMMENT '机器执行信息',
                          `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
                          `update_time` DATETIME DEFAULT NOW() ON UPDATE NOW() COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务信息';

drop table if exists `auv_task_lock`;
CREATE TABLE `auv_task_lock` (
                               `id` BIGINT(20) auto_increment,
                               `task_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'task code',
                               `worker_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'worker code',
                               `expire_time` bigint(20) DEFAULT 0 NOT NULL COMMENT '过期时间',
                               `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
                               `update_time` DATETIME DEFAULT NOW() ON UPDATE NOW() COMMENT '更新时间',
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='任务锁';

drop table if exists `auv_job`;
CREATE TABLE `auv_job` (
                         `id` BIGINT(20) auto_increment,
                         `code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'task code',
                         `task_code` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '任务code',
                         `class_name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '类的全限定名',
                         `try_times` INT(10) DEFAULT 0 NOT NULL COMMENT '第几次重试',
                         `worker_code` varchar(200) default '' not null comment '执行机器',
                         `start_time` DATETIME DEFAULT '1971-1-1 00:00:00' COMMENT '开始时间',
                         `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
                         `update_time` DATETIME DEFAULT NOW() ON UPDATE NOW() COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='正在执行的job信息';

drop table if exists `auv_job_log`;
CREATE TABLE `auv_job_log` (
                             `id` BIGINT(20) auto_increment,
                             `job_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'job code',
                             `task_code` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '任务code',
                             `class_name` VARCHAR(255) DEFAULT '' NOT NULL COMMENT '类的全限定名',
                             `try_times` INT(10) DEFAULT 0 NOT NULL COMMENT '第几次重试',
                             `worker_code` varchar(200) default '' not null comment '执行机器',
                             `start_time` DATETIME DEFAULT '1971-1-1 00:00:00' COMMENT '开始时间',
                             `end_time` DATETIME DEFAULT '1971-1-1 00:00:00' COMMENT '结束时间',
                             `status` TINYINT(4) DEFAULT 0 NOT NULL COMMENT '执行结果 1成功 2失败 3取消',
                             `error` TEXT NOT NULL COMMENT '错误信息',
                             `result` TEXT NOT NULL COMMENT '执行结果',
                             `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
                             `update_time` DATETIME DEFAULT NOW() ON UPDATE NOW() COMMENT '更新时间',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job执行历史日志';

drop table if exists `auv_worker`;
CREATE TABLE `auv_worker` (
                            `id` BIGINT(20) auto_increment,
                            `code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'worker code',
                            `name` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'worker名',
                            `cpu` int(11) DEFAULT 0 NOT NULL COMMENT 'cpu数量',
                            `cpu_used` DOUBLE DEFAULT 0 NOT NULL COMMENT 'cpu使用率',
                            `memory` DOUBLE DEFAULT 0 NOT NULL COMMENT '内存,以M为单位',
                            `memory_used` DOUBLE DEFAULT 0 NOT NULL COMMENT '内存使用率',
                            `jvm_memory` DOUBLE DEFAULT 0 NOT NULL COMMENT 'jvm堆大小，以M为单位',
                            `jvm_memory_used` DOUBLE DEFAULT 0 NOT NULL COMMENT 'jvm堆使用率',
                            `job_num` INT(10) DEFAULT 0 NOT NULL COMMENT '正在执行job数',
                            `heartbeat` DATETIME DEFAULT '1971-1-1 00:00:00' COMMENT '心跳时间',
                            `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT NOW() ON UPDATE NOW() COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker信息';

drop table if exists `auv_worker_blacklist`;
CREATE TABLE `auv_worker_blacklist` (
                                      `id` BIGINT(20) auto_increment,
                                      `worker_code` VARCHAR(100) DEFAULT '' NOT NULL COMMENT 'worker code',
                                      `create_time` DATETIME DEFAULT NOW() COMMENT '创建时间',
                                      `update_time` DATETIME DEFAULT NOW() ON UPDATE NOW() COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `code` (`worker_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='worker黑名单列表';

-- ----------------------------
-- Table structure for collect_task_metric
-- ----------------------------
DROP TABLE IF EXISTS `collect_task_metric`;
CREATE TABLE `collect_task_metric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `read_time_mean` int(11) NOT NULL DEFAULT '0',
  `filter_remained` int(11) NOT NULL DEFAULT '0',
  `channel_capacity` varchar(32) NOT NULL DEFAULT '',
  `is_file_exist` tinyint(1) NOT NULL DEFAULT '0',
  `path_id` bigint(20) NOT NULL DEFAULT '-1',
  `type` varchar(32) NOT NULL DEFAULT '',
  `read_count` int(11) NOT NULL DEFAULT '0',
  `send_time_mean` int(11) NOT NULL DEFAULT '0',
  `master_file` varchar(64) NOT NULL DEFAULT '',
  `path` varchar(256) NOT NULL DEFAULT '',
  `hostname` varchar(64) NOT NULL DEFAULT '',
  `heartbeat_time` bigint(20) NOT NULL DEFAULT '0',
  `host_ip` varchar(32) NOT NULL DEFAULT '',
  `sink_num` int(11) NOT NULL DEFAULT '0',
  `flush_time_mean` int(11) NOT NULL DEFAULT '0',
  `latest_file` varchar(64) NOT NULL DEFAULT '',
  `filter_too_large_count` int(11) NOT NULL DEFAULT '0',
  `channel_type` varchar(64) NOT NULL DEFAULT '',
  `log_model_version` int(11) NOT NULL DEFAULT '0',
  `topic` varchar(64) NOT NULL DEFAULT '',
  `flush_count` int(11) NOT NULL DEFAULT '0',
  `flush_time_max` int(11) NOT NULL DEFAULT '0',
  `filter_out` int(11) NOT NULL DEFAULT '0',
  `related_files` int(11) NOT NULL DEFAULT '0',
  `log_model_host_name` varchar(64) NOT NULL DEFAULT '',
  `cluster_id` bigint(20) NOT NULL DEFAULT '-1',
  `limit_rate` int(11) NOT NULL DEFAULT '0',
  `control_time_mean` bigint(20) NOT NULL DEFAULT '0',
  `limit_time` int(11) NOT NULL DEFAULT '0',
  `log_mode_id` bigint(20) NOT NULL DEFAULT '-1',
  `flush_time_min` int(11) NOT NULL DEFAULT '0',
  `read_time_min` int(11) NOT NULL DEFAULT '0',
  `send_time_max` int(11) NOT NULL DEFAULT '0',
  `dynamic_limiter` int(11) NOT NULL DEFAULT '0',
  `log_path_key` varchar(256) NOT NULL DEFAULT '',
  `max_time_gap` int(11) NOT NULL DEFAULT '0',
  `send_byte` int(11) NOT NULL DEFAULT '0',
  `send_time_min` int(11) NOT NULL DEFAULT '0',
  `log_time_str` varchar(256) NOT NULL DEFAULT '',
  `control_time_max` bigint(20) NOT NULL DEFAULT '0',
  `send_count` int(11) NOT NULL DEFAULT '0',
  `source_type` varchar(64) NOT NULL DEFAULT '',
  `log_time` bigint(20) NOT NULL DEFAULT '0',
  `flush_failed_count` int(11) NOT NULL DEFAULT '0',
  `channel_size` int(11) NOT NULL DEFAULT '0',
  `filter_total_too_large_count` int(11) NOT NULL DEFAULT '0',
  `collect_files` varchar(1024) NOT NULL DEFAULT '',
  `control_time_min` bigint(20) NOT NULL DEFAULT '0',
  `read_byte` int(11) NOT NULL DEFAULT '0',
  `read_time_max` int(11) NOT NULL DEFAULT '0',
  `is_file_disorder` tinyint(1) NOT NULL DEFAULT '0',
  `valid_time_config` tinyint(1) NOT NULL DEFAULT '0',
  `operator` varchar(64) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3578 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for operate_record
-- ----------------------------
DROP TABLE IF EXISTS `operate_record`;
CREATE TABLE `operate_record` (
  `id` int(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `module_id` int(10) NOT NULL DEFAULT '-1' COMMENT '模块id',
  `operate_id` int(10) NOT NULL DEFAULT '-1' COMMENT '操作id',
  `biz_id` varchar(100) NOT NULL DEFAULT '' COMMENT '业务id string类型',
  `business_id` int(20) NOT NULL DEFAULT '-1' COMMENT '业务id',
  `content` text COMMENT '操作内容',
  `operator` varchar(50) NOT NULL DEFAULT '' COMMENT '操作人',
  `operate_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_module_business` (`module_id`,`business_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41484 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_agent
-- ----------------------------
DROP TABLE IF EXISTS `tb_agent`;
CREATE TABLE `tb_agent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `host_name` varchar(128) NOT NULL DEFAULT '' COMMENT 'Agent宿主机名',
  `ip` varchar(64) NOT NULL DEFAULT '' COMMENT 'Agent宿主机ip',
  `collect_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '采集方式：\n0：采集宿主机日志\n1：采集宿主机所有容器日志\n2：采集宿主机日志 & 宿主机所有容器日志\n',
  `cpu_limit_threshold` int(11) DEFAULT '0' COMMENT '采集端限流 cpu 阈值',
  `byte_limit_threshold` bigint(20) DEFAULT '0' COMMENT '采集端限流流量阈值 单位：字节',
  `agent_version_id` bigint(255) NOT NULL DEFAULT '0' COMMENT 'Agent版本id',
  `advanced_configuration_json_string` varchar(4096) DEFAULT '' COMMENT 'Agent高级配置项集，为json形式字符串',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `configuration_version` int(11) NOT NULL DEFAULT '0' COMMENT 'Agent 配置版本号',
  `metrics_send_topic` varchar(255) DEFAULT NULL COMMENT 'Agent指标信息发往的topic名',
  `metrics_send_receiver_id` bigint(20) DEFAULT NULL COMMENT 'Agent指标信息发往的接收端id',
  `error_logs_send_topic` varchar(255) DEFAULT NULL COMMENT 'Agent错误日志信息发往的topic名',
  `error_logs_send_receiver_id` bigint(20) DEFAULT NULL COMMENT 'Agent错误日志信息发往的接收端id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_host_name` (`host_name`) COMMENT '主机名唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=394 DEFAULT CHARSET=utf8 COMMENT='采集端表：表示一个部署在某host上的采集端';

-- ----------------------------
-- Table structure for tb_agent_health
-- ----------------------------
DROP TABLE IF EXISTS `tb_agent_health`;
CREATE TABLE `tb_agent_health` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_id` bigint(20) NOT NULL COMMENT '表tb_agent主键',
  `agent_health_level` tinyint(4) NOT NULL COMMENT ' agent健康等级',
  `agent_health_description` varchar(1024) NOT NULL DEFAULT '' COMMENT ' agent健康描述信息',
  `lastest_error_logs_exists_check_healthy_time` bigint(20) NOT NULL COMMENT ' 近一次“错误日志存在健康检查”为健康时的时间点',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `agent_startup_time` bigint(20) DEFAULT NULL COMMENT ' agent启动时间',
  `agent_startup_time_last_time` bigint(20) DEFAULT NULL COMMENT ' agent上一次启动时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_agent_id` (`agent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_agent_operation_sub_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_agent_operation_sub_task`;
CREATE TABLE `tb_agent_operation_sub_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_operation_task_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '表tb_agent_operation_task主键 id',
  `host_name` varchar(128) NOT NULL DEFAULT '' COMMENT '主机名',
  `ip` varchar(64) NOT NULL,
  `container` tinyint(4) NOT NULL DEFAULT '0' COMMENT '标识是否为容器节点\n0：否\n1：是\n',
  `source_agent_version_id` bigint(20) DEFAULT NULL COMMENT '原 agent_version id',
  `task_start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '该主机任务开始执行时间',
  `task_end_time` timestamp NULL DEFAULT NULL COMMENT '该主机任务执行结束时间',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `execute_status` tinyint(4) DEFAULT NULL COMMENT '执行状态：见枚举类 AgentOperationTaskSubStateEnum\n\n',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_agent_operation_task_id` (`agent_operation_task_id`) USING BTREE,
  KEY `idx_hostname_start_time` (`host_name`,`task_start_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8 COMMENT='采集端操作任务 & 主机名关联关系表：表示采集端操作任务 &主机名的关联关系，agentOperationTask： hostName 一对多关联关系';

-- ----------------------------
-- Table structure for tb_agent_operation_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_agent_operation_task`;
CREATE TABLE `tb_agent_operation_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `task_name` varchar(128) NOT NULL DEFAULT '' COMMENT '任务名',
  `task_status` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '任务状态 30：执行中 100：已完成',
  `task_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '任务类型：0：安装 1：卸载 2：升级',
  `hosts_number` int(11) NOT NULL COMMENT '任务涉及主机数量',
  `source_agent_version_id` bigint(20) DEFAULT '0' COMMENT '安装任务时：无须传入；卸载任务时：无须传入；升级任务时：表示agent升级前agent对应agent_version id',
  `target_agent_version_id` bigint(20) DEFAULT NULL COMMENT '安装任务时：表示待安装 agent 对应 agent version id；卸载任务时：无须传入；升级任务时：表示agent升级后agent对应agent_version id',
  `external_agent_task_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '外部对应 agent 执行任务 id，如宙斯系统的 agent 任务 id',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `task_start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务开始执行时间',
  `task_end_time` timestamp NULL DEFAULT NULL COMMENT '任务执行结束时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=utf8 COMMENT='采集端操作任务表：表示针对某个host上的agent部署、卸载操作计划任务';

-- ----------------------------
-- Table structure for tb_agent_version
-- ----------------------------
DROP TABLE IF EXISTS `tb_agent_version`;
CREATE TABLE `tb_agent_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT ' pk',
  `file_name` varchar(128) NOT NULL DEFAULT '' COMMENT '文件名',
  `file_md5` varchar(256) NOT NULL DEFAULT '' COMMENT '文件md5',
  `file_type` tinyint(255) NOT NULL DEFAULT '0' COMMENT '0：agent安装压缩包 1：agent配置文件',
  `description` varchar(4096) DEFAULT '' COMMENT '备注信息',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `version` varchar(255) NOT NULL DEFAULT '' COMMENT 'agent 安装包版本号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unq_idx_version` (`version`) USING BTREE,
  UNIQUE KEY `unq_idx_file_md5` (`file_md5`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=506 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_collect_delay_monitor_black_list
-- ----------------------------
DROP TABLE IF EXISTS `tb_collect_delay_monitor_black_list`;
CREATE TABLE `tb_collect_delay_monitor_black_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `collect_delay_monitor_black_list_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '采集延迟检查黑名单类型：\n\n0：表示主机\n\n1：表示采集任务\n\n2：表示主机 + 采集任务',
  `host_name` varchar(128) NOT NULL DEFAULT '' COMMENT '主机名',
  `log_collector_task_id` bigint(20) NOT NULL COMMENT '表tb_log_collector_task 主键 id',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集延迟检查黑名单信息表：表示一个采集延迟检查黑名单信息';

-- ----------------------------
-- Table structure for tb_directory_log_collect_path
-- ----------------------------
DROP TABLE IF EXISTS `tb_directory_log_collect_path`;
CREATE TABLE `tb_directory_log_collect_path` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `log_collect_task_id` bigint(20) NOT NULL COMMENT '表tb_log_collector_task主键',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '待采集路径',
  `collect_files_filter_regular_pipeline_json_string` varchar(4096) NOT NULL DEFAULT '' COMMENT '采集文件筛选正则集 pipeline json 形式字符串，集合中每一项为一个过滤正则项< filterRegular , type >，filterRegular表示过滤正则内容，type表示黑/白名单类型0：白名单 1：黑名单\n\n注：FilterRegular 须有序存储，过滤时按集合顺序进行过滤计算',
  `directory_collect_depth` int(11) NOT NULL DEFAULT '1' COMMENT '目录采集深度',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ' 修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=985 DEFAULT CHARSET=utf8 COMMENT='目录类型日志采集路径表：表示一个目录类型日志采集路径，DirectoryLogCollectPath：LogCollectorTask 多对一关联关系';

-- ----------------------------
-- Table structure for tb_file_log_collect_path
-- ----------------------------
DROP TABLE IF EXISTS `tb_file_log_collect_path`;
CREATE TABLE `tb_file_log_collect_path` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `log_collect_task_id` bigint(20) NOT NULL COMMENT '表tb_log_collector_task主键',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '待采集路径',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1163 DEFAULT CHARSET=utf8 COMMENT='文件类型日志采集路径表：表示一个文件类型日志采集路径，FileLogCollectPath：LogCollectorTask 多对一关联关系';

-- ----------------------------
-- Table structure for tb_global_config
-- ----------------------------
DROP TABLE IF EXISTS `tb_global_config`;
CREATE TABLE `tb_global_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `key` varchar(255) NOT NULL DEFAULT '' COMMENT '键',
  `value` varchar(4096) NOT NULL DEFAULT '' COMMENT '值',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='全局配置信息表：表示一个全局配置信息';

-- ----------------------------
-- Table structure for tb_host
-- ----------------------------
DROP TABLE IF EXISTS `tb_host`;
CREATE TABLE `tb_host` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `host_name` varchar(128) NOT NULL DEFAULT '' COMMENT '主机名',
  `ip` varchar(64) NOT NULL DEFAULT '' COMMENT '主机IP',
  `container` tinyint(4) NOT NULL DEFAULT '0' COMMENT '标识是否为容器节点\n0：否\n1：是\n',
  `parent_host_name` varchar(128) NOT NULL DEFAULT '-1' COMMENT '针对容器场景，表示容器对应宿主机id',
  `machine_zone` varchar(64) DEFAULT '' COMMENT '主机所属机器单元',
  `department` varchar(64) DEFAULT '' COMMENT '机器所属部门',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `extend_field` varchar(4096) DEFAULT '' COMMENT '扩展字段，json格式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_host_name` (`host_name`) COMMENT '主机名唯一索引',
  KEY `idx_ip` (`ip`) USING BTREE COMMENT '主机 ip 索引'
) ENGINE=InnoDB AUTO_INCREMENT=1708 DEFAULT CHARSET=utf8 COMMENT='主机表：表示一台主机，可表示物理机、虚拟机、容器';

-- ----------------------------
-- Table structure for tb_k8s_pod
-- ----------------------------
DROP TABLE IF EXISTS `tb_k8s_pod`;
CREATE TABLE `tb_k8s_pod` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(255) DEFAULT '' COMMENT ' pod 实例唯一键',
  `pod_ip` varchar(255) DEFAULT '' COMMENT ' pod ip 地址',
  `service_name` varchar(255) DEFAULT '' COMMENT ' pod 所属服务对应服务名',
  `log_mount_path` varchar(255) DEFAULT '' COMMENT ' 容器内路径',
  `log_host_path` varchar(255) DEFAULT '' COMMENT ' 主机对应真实路径',
  `node_name` varchar(255) DEFAULT '' COMMENT 'pod 对应宿主机名',
  `node_ip` varchar(255) DEFAULT '' COMMENT 'pod 对应宿主机 ip',
  `container_names` varchar(2048) DEFAULT '' COMMENT ' pod 包含容器名集',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_k8s_pod_host
-- ----------------------------
DROP TABLE IF EXISTS `tb_k8s_pod_host`;
CREATE TABLE `tb_k8s_pod_host` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT ' 主键 id',
  `k8s_pod_id` bigint(20) NOT NULL COMMENT ' 表 tb_k8s_pod 主键 id',
  `host_id` bigint(20) NOT NULL COMMENT ' 表 tb_host 主键 id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_kafka_cluster
-- ----------------------------
DROP TABLE IF EXISTS `tb_kafka_cluster`;
CREATE TABLE `tb_kafka_cluster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `kafka_cluster_name` varchar(128) NOT NULL DEFAULT '' COMMENT 'kafka 集群名',
  `kafka_cluster_broker_configuration` varchar(1024) NOT NULL DEFAULT '' COMMENT 'kafka 集群 broker 配置',
  `kafka_cluster_producer_init_configuration` varchar(4096) DEFAULT '' COMMENT 'kafka 集群对应生产端初始化配置',
  `kafka_cluster_id` bigint(20) DEFAULT NULL COMMENT '外部kafka集群表id字段',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unq_kafka_cluster_name` (`kafka_cluster_name`) USING BTREE,
  KEY `idx_kafka_cluster_id` (`kafka_cluster_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1396 DEFAULT CHARSET=utf8 COMMENT='Kafka集群信息表：表示一个 kafka 集群信息，KafkaCluster：LogCollectorTask 一对多关联关系';

-- ----------------------------
-- Table structure for tb_log_collect_task
-- ----------------------------
DROP TABLE IF EXISTS `tb_log_collect_task`;
CREATE TABLE `tb_log_collect_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `log_collect_task_name` varchar(255) NOT NULL DEFAULT '' COMMENT '采集任务名',
  `log_collect_task_remark` varchar(1024) DEFAULT '' COMMENT '采集任务备注',
  `log_collect_task_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '采集任务类型 0：常规流式采集 1：按指定时间范围采集',
  `collect_start_time_business` bigint(20) DEFAULT '0' COMMENT '采集任务对应采集开始业务时间\n\n注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填',
  `collect_end_time_business` bigint(20) DEFAULT '0' COMMENT '采集任务对应采集结束业务时间\n\n注：针对 logCollectTaskType = 1 情况，该值必填；logCollectTaskType = 0 情况，该值不填',
  `limit_priority` tinyint(255) NOT NULL DEFAULT '0' COMMENT '采集任务限流保障优先级 0：高 1：中 2：低',
  `log_collect_task_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 "按指定时间范围采集" 类型）',
  `send_topic` varchar(256) NOT NULL DEFAULT '' COMMENT '采集任务采集的日志需要发往的topic名',
  `kafka_cluster_id` bigint(20) NOT NULL COMMENT '表tb_kafka_cluster主键',
  `host_filter_rule_logic_json_string` varchar(4096) NOT NULL COMMENT '主机过滤规则信息（存储 BaseHostFilterRuleLogic 某具体实现类的 json 化形式）',
  `advanced_configuration_json_string` varchar(4096) NOT NULL DEFAULT '' COMMENT '采集任务高级配置项集，为json形式字符串',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `configuration_version` int(11) NOT NULL DEFAULT '0' COMMENT '日志采集任务配置版本号',
  `old_data_filter_type` tinyint(255) NOT NULL COMMENT '历史数据过滤 0：不过滤 1：从当前时间开始采集 2：从自定义时间开始采集，自定义时间取collectStartBusinessTime属性值',
  `log_collect_task_execute_timeout_ms` bigint(20) DEFAULT NULL COMMENT '日志采集任务执行超时时间，注意：该字段仅在日志采集任务类型为类型"按指定时间范围采集"时才存在值',
  `log_content_filter_rule_logic_json_string` varchar(255) DEFAULT NULL COMMENT '日志内容过滤规则信息（存储 BaseLogContentFilterRuleLogic 某具体实现类的 json 化形式）',
  `log_collect_task_finish_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志采集任务执行完成时间\n注：仅日志采集任务为时间范围采集类型时',
  `kafka_producer_configuration` varchar(1024) DEFAULT '' COMMENT '日志采集任务对应kafka生产端属性',
  `log_content_slice_rule_logic_json_string` varchar(1024) NOT NULL DEFAULT '' COMMENT '日志内容切片规则信息（存储 BaseLogContentSliceRuleLogic 某具体实现类的 json 化形式）',
  `file_name_suffix_match_rule_logic_json_string` varchar(1024) NOT NULL DEFAULT '' COMMENT '待采集文件后缀匹配规则信息（存储 BaseCollectFileSuffixMatchRuleLogic 某具体实现类的 json 化形式）',
  `collect_delay_threshold_ms` bigint(255) NOT NULL COMMENT '该路径的日志对应采集延迟监控阈值 单位：ms，该阈值表示：该采集路径对应到所有待采集主机上正在采集的业务时间最小值 ~ 当前时间间隔',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1072 DEFAULT CHARSET=utf8 COMMENT='日志采集任务表：表示一个待运行在agent的采集任务';

-- ----------------------------
-- Table structure for tb_log_collect_task_health
-- ----------------------------
DROP TABLE IF EXISTS `tb_log_collect_task_health`;
CREATE TABLE `tb_log_collect_task_health` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `log_collect_task_id` bigint(20) NOT NULL COMMENT '表tb_log_collector_task主键',
  `log_collect_task_health_level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '采集任务健康等级\n\n0：绿色 表示：采集任务很健康，对业务没有任何影响，且运行该采集任务的 Agent 也健康\n\n1：黄色 表示：采集任务存在风险，该采集任务有对应错误日志输出\n\n2：红色 表示：采集任务不健康，对业务有影响，该采集任务需要做采集延迟监控但乱序输出，或该采集任务需要做采集延迟监控但延迟时间超过指定阈值、该采集任务对应 kafka 集群信息不存在 待维护',
  `log_collect_task_health_description` varchar(1024) NOT NULL COMMENT '日志采集任务健康描述信息',
  `lastest_collect_dquality_time_per_log_file_path_json_string` varchar(2048) NOT NULL DEFAULT '' COMMENT '日志采集任务中各日志主文件对应的最近采集完整性时间，json 字符串形式，[{“logfilePath1”: 1602323589023 }, …]',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `lastest_abnormal_truncation_check_healthy_time_per_log_file_path` varchar(2048) NOT NULL DEFAULT '' COMMENT ' 近一次“日志异常截断健康检查”为健康时的时间点',
  `lastest_log_slice_check_healthy_time_per_log_file_path` varchar(2048) NOT NULL DEFAULT '' COMMENT ' 近一次“日志切片健康检查”为健康时的时间点',
  `lastest_file_disorder_check_healthy_time_per_log_file_path` varchar(2048) NOT NULL DEFAULT '' COMMENT ' 近一次“文件乱序健康检查”为健康时的时间点',
  `lastest_file_path_exists_check_healthy_time_per_log_file_path` varchar(2048) NOT NULL DEFAULT '' COMMENT ' 近一次“文件路径是否存在健康检查”为健康时的时间点',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1058 DEFAULT CHARSET=utf8 COMMENT='日志采集任务健康度信息表：表示一个日志采集任务健康度信息，LogCollectorTaskHealth：LogCollectorTask 一对一关联关系';

-- ----------------------------
-- Table structure for tb_log_collect_task_service
-- ----------------------------
DROP TABLE IF EXISTS `tb_log_collect_task_service`;
CREATE TABLE `tb_log_collect_task_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `log_collector_task_id` bigint(20) NOT NULL COMMENT '表tb_log_collector_task 主键 id',
  `service_id` bigint(20) NOT NULL COMMENT '表 tb_service 主键 id',
  PRIMARY KEY (`id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_logcollecttask_id` (`log_collector_task_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2006 DEFAULT CHARSET=utf8 COMMENT='日志采集任务 & 服务关联关系表：表示服务 & 日志采集任务的关联关系，Service：LogCollectorTask 多对多关联关系';

-- ----------------------------
-- Table structure for tb_meta_table_version
-- ----------------------------
DROP TABLE IF EXISTS `tb_meta_table_version`;
CREATE TABLE `tb_meta_table_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `table_name` varchar(255) NOT NULL DEFAULT '' COMMENT '表名',
  `table_version` bigint(20) NOT NULL DEFAULT '1' COMMENT '表对应版本号，该变每一次变更，其版本号+1',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表版本号信息表：表示一个表当前版本号信息（ps：该表发生变动操作，其对应版本号 +1）';

-- ----------------------------
-- Table structure for tb_service
-- ----------------------------
DROP TABLE IF EXISTS `tb_service`;
CREATE TABLE `tb_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `service_name` varchar(255) NOT NULL DEFAULT '' COMMENT '服务名',
  `operator` varchar(64) NOT NULL DEFAULT '' COMMENT '操作人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `extenal_service_id` bigint(20) DEFAULT NULL COMMENT '外部系统服务id，如：夜莺服务节点 id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_service_name` (`service_name`) COMMENT '服务名唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=5228 DEFAULT CHARSET=utf8 COMMENT='服务表：表示一个服务';

-- ----------------------------
-- Table structure for tb_service_host
-- ----------------------------
DROP TABLE IF EXISTS `tb_service_host`;
CREATE TABLE `tb_service_host` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '	\n自增id',
  `service_id` bigint(20) NOT NULL COMMENT '表 tb_service主键 id',
  `host_id` bigint(20) NOT NULL COMMENT '表 tb_host主键 id',
  PRIMARY KEY (`id`),
  KEY `idx_host_id` (`host_id`),
  KEY `idx_service_id` (`service_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3340 DEFAULT CHARSET=utf8 COMMENT='服务 & 主机关联关系表：表示服务 & 主机的关联关系，Service : Host多对多关联关系';

-- ----------------------------
-- Table structure for tb_service_project
-- ----------------------------
DROP TABLE IF EXISTS `tb_service_project`;
CREATE TABLE `tb_service_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `service_id` bigint(20) NOT NULL COMMENT '对应表 tb_service id',
  `project_id` bigint(20) NOT NULL COMMENT '项目id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=821 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
