package com.didiglobal.logi.auvjob.common.dto;

import java.sql.Timestamp;
import lombok.Data;

@Data
public class TaskLockDto {
  private Long id;
  private String taskCode;
  private String workerCode;
  private Timestamp createTime;
  private Timestamp updateTime;
}