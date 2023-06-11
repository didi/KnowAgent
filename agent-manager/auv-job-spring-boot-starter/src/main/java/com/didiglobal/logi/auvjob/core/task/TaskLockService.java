package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.dto.TaskLockDto;
import java.util.List;

/**
 * task lock service.
 *
 * @author dengshan
 */
public interface TaskLockService {
  /**
   * 尝试获取锁.
   *
   * @return true/false
   */
  Boolean tryAcquire(String taskCode);

  /**
   * 尝试获取锁.
   *
   * @param taskCode taskCode
   * @param workerCode workerCode
   * @param expireTime expireTime
   *
   * @return true/false
   */
  Boolean tryAcquire(String taskCode, String workerCode, Long expireTime);

  /**
   * 尝试释放锁.
   *
   * @return true/false
   */
  Boolean tryRelease(String taskCode);

  /**
   * 尝试释放锁.
   *
   * @param taskCode taskCode
   * @param workerCode workerCode
   * @return true/false
   */
  Boolean tryRelease(String taskCode, String workerCode);

  /**
   * 获取所有任务.
   *
   * @return tasks
   */
  List<TaskLockDto> getAll();

  /**
   * 刷新当前任务的锁.
   */
  void renewAll();
}
