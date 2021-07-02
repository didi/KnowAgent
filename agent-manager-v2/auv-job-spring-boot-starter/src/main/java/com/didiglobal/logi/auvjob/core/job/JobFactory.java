package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;

/**
 * job factory.
 *
 * @author dengshan
 */
public interface JobFactory {
  void addJob(String className, Job job);

  JobInfo newJob(TaskInfo taskInfo);
}
