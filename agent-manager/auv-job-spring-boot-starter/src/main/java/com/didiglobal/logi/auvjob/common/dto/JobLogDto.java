package com.didiglobal.logi.auvjob.common.dto;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class JobLogDto {

  private String jobCode;
  private String taskCode;
  private String className;
  private Integer tryTimes;
  private String workerCode;
  private Timestamp startTime;
  private Timestamp endTime;
  private Integer status;
  private String error;
  private Timestamp createTime;
  private Timestamp updateTime;
  private String result;
}