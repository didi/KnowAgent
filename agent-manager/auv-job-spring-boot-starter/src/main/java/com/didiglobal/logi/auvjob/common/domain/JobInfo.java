package com.didiglobal.logi.auvjob.common.domain;

import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.core.job.Job;
import com.didiglobal.logi.auvjob.core.task.TaskCallback;
import java.sql.Timestamp;
import java.util.Objects;
import lombok.Data;

@Data
public class JobInfo {
  private String code;
  private String taskCode;
  private String className;
  private Integer retryTimes;
  private Integer tryTimes;
  private String workerCode;
  private Timestamp startTime;
  private Timestamp endTime;
  private Integer status;
  private String error;
  private Long timeout;
  private Object result;
  private Job job;
  private TaskCallback taskCallback;

  /**
   * auv job.
   *
   * @return auv job
   */
  public AuvJob getAuvJob() {
    AuvJob job = new AuvJob();
    job.setCode(getCode());
    job.setTaskCode(getTaskCode());
    job.setClassName(getClassName());
    job.setTryTimes(getTryTimes());
    job.setWorkerCode(getWorkerCode());
    job.setStartTime(new Timestamp(System.currentTimeMillis()));
    job.setCreateTime(new Timestamp(System.currentTimeMillis()));
    job.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    return job;
  }

  /**
   * auv job log.
   *
   * @return job log
   */
  public AuvJobLog getAuvJobLog() {
    AuvJobLog auvJobLog = new AuvJobLog();
    auvJobLog.setJobCode(getCode());
    auvJobLog.setTaskCode(getTaskCode());
    auvJobLog.setClassName(getClassName());
    auvJobLog.setWorkerCode(getWorkerCode());
    auvJobLog.setTryTimes(getTryTimes());
    auvJobLog.setStartTime(getStartTime());
    auvJobLog.setEndTime(new Timestamp(System.currentTimeMillis()));
    auvJobLog.setStatus(getStatus());
    auvJobLog.setError(getError() == null ? "" : getError());
    auvJobLog.setResult(getResult() == null ? "" : getResult().toString());
    auvJobLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
    auvJobLog.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    return auvJobLog;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JobInfo jobInfo = (JobInfo) o;
    return code.equals(jobInfo.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}