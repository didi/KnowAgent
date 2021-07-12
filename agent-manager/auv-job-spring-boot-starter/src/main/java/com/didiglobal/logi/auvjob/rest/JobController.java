package com.didiglobal.logi.auvjob.rest;

import com.didiglobal.logi.auvjob.common.Result;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * job 的启动、暂停、job信息、job日志相关操作.
 *
 * @author dengshan
 */
@RestController
@RequestMapping(Constants.V1 + "/auv-job/job")
public class JobController {
  private static final Logger logger = LoggerFactory.getLogger(JobController.class);

  @Autowired
  private JobManager jobManager;

  @PutMapping("/stop")
  public Result stop(@RequestParam String jobCode) {
    return Result.buildSucc(jobManager.stop(jobCode));
  }

  @PutMapping("/stopAll")
  public Result stopAll() {
    return Result.buildSucc(jobManager.stopAll());
  }

  @GetMapping("/runningJobs")
  public Result getRunningJobs() {
    return Result.buildSucc(jobManager.getJobs());
  }

  @GetMapping("/jobLogs")
  public Result getJobLogs(@RequestParam String taskCode,
                           @RequestParam(defaultValue = "10", required = false) Integer limit) {
    return Result.buildSucc(jobManager.getJobLogs(taskCode, limit));
  }
}
