package com.didiglobal.logi.auvjob.common.domain;

import com.didiglobal.logi.auvjob.core.task.TaskCallback;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TaskInfo {
  private String code;
  private String name;
  private String description;
  private String cron;
  private String className;
  /*
   * 执行参数 map 形式{key1:value1,key2:value2}
   */
  private String params;
  private Integer retryTimes;
  private Timestamp lastFireTime;
  private Timestamp nextFireTime;
  private Long timeout;
  private Integer status;
  private String subTaskCodes;
  private String consensual;
  private List<TaskWorker> taskWorkers;
  private Timestamp createTime;
  private Timestamp updateTime;

  private TaskCallback taskCallback;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class TaskWorker {
    private Integer status;
    private Timestamp lastFireTime;
    private String workerCode;
  }
}