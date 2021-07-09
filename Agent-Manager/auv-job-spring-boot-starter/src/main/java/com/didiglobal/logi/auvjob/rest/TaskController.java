package com.didiglobal.logi.auvjob.rest;

import com.didiglobal.logi.auvjob.common.Result;
import com.didiglobal.logi.auvjob.common.dto.TaskDto;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * task controller.
 *
 * @author dengshan
 */
@RestController
@RequestMapping(Constants.V1 + "/auv-job/task")
public class TaskController {

  @Autowired
  private TaskManager taskManager;

  @DeleteMapping("/delete")
  public Result delete(@RequestParam String taskCode) {
    return taskManager.delete(taskCode);
  }

  @PutMapping("/update")
  public Result<Boolean> update(@RequestBody TaskDto taskDto) {
    return Result.buildSucc(taskManager.update(taskDto));
  }

  @PostMapping("execute")
  public Result execute(@RequestParam String taskCode,
                        @RequestParam(defaultValue = "false", required = false)
                                Boolean executeSubs) {
    return taskManager.execute(taskCode, executeSubs);
  }

  @GetMapping("/getAll")
  public Object getAll() {
    return taskManager.getAll();
  }

  @PostMapping("/release")
  public Result<Boolean> release(@RequestParam String taskCode, @RequestParam String workerCode) {
    return taskManager.release(taskCode, workerCode);
  }

}
