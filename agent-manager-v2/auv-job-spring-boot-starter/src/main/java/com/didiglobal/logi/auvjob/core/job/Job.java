package com.didiglobal.logi.auvjob.core.job;

/**
 * job.
 */
public interface Job {

  Object execute(JobContext jobContext) throws Exception;
}
