package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.Result;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.dto.TaskDto;
import java.util.List;

/**
 * 任务的CRUD及执行管控.
 *
 * @author dengshan
 */
public interface TaskManager {

  /**
   * 新增任务.
   *
   */
  boolean add(TaskDto taskDto);

  /**
   * 更新任务.
   *
   * @param taskCode task code
   * @return deleted auv task
   */
  Result delete(String taskCode);

  /**
   * 更新任务.
   *
   */
  boolean update(TaskDto taskDto);

  /**
   * 接下来需要执行的任务,按时间先后顺序排序.
   *
   * @param interval 从现在开始下次执行时间间隔 毫秒
   * @return task info list
   */
  List<TaskInfo> nextTriggers(Long interval);

  /**
   * 接下来需要执行的任务,按时间先后顺序排序.
   *
   * @param fromTime fromTime
   * @param interval interval
   * @return task info list
   */
  List<TaskInfo> nextTriggers(Long fromTime, Long interval);

  /**
   * 提交任务，执行器会根据一致性协同算法判断是否执行.
   *
   * @param taskInfoList task info list
   */
  void submit(List<TaskInfo> taskInfoList);

  /**
   * 根据 task code 执行任务.
   *
   * @param taskCode task code
   * @param executeSubs 是否执行子任务
   */
  Result execute(String taskCode, Boolean executeSubs);

  /**
   * 执行任务, 默认会执行子任务如果有配置.
   *
   * @param taskInfo 任务信息
   * @param executeSubs 是否执行子任务
   */
  void execute(TaskInfo taskInfo, Boolean executeSubs);

  /**
   * 停止所有正在运行的job.
   *
   * @return stop job size
   */
  int stopAll();

  /**
   * 获取所有任务.
   *
   * @return all tasks
   */
  List<TaskInfo> getAll();

  /**
   * 恢复任务 并释放锁.
   *
   * @param taskCode taskCode
   * @return true/false
   */
  Result<Boolean> release(String taskCode, String workerCode);

}
