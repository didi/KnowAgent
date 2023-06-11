
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
