package com.didiglobal.logi.auvjob.common.dto;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class TaskDto {

  private String code;
  private String name;
  private String description;
  private String cron;
  private String className;
  private String params;
  private Integer retryTimes;
  private String workerCode;
  private Timestamp lastFireTime;
  private Long timeout;
  private Integer status;
  private String subTaskCodes;
  private String consensual;
  private Timestamp createTime;
  private Timestamp updateTime;

}