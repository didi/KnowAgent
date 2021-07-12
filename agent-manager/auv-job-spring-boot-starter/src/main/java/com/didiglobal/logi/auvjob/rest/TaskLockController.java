package com.didiglobal.logi.auvjob.rest;

import com.didiglobal.logi.auvjob.core.task.TaskLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.V1 + "/auv-job/taskLock")
public class TaskLockController {

  @Autowired
  private TaskLockService taskLockService;

  @PostMapping("/release")
  public Object release(@RequestParam String taskCode, @RequestParam String workerCode) {
    return taskLockService.tryRelease(taskCode, workerCode);
  }

  @GetMapping("/getAll")
  public Object getAll() {
    return taskLockService.getAll();
  }

}