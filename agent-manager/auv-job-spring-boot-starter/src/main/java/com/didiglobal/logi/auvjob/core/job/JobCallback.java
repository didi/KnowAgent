package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.domain.JobInfo;

public interface JobCallback {
  void callback(JobInfo jobInfo);
}
