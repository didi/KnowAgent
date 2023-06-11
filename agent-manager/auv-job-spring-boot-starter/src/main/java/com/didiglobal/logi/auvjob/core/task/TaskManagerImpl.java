package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.Result;
import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.dto.TaskDto;
import com.didiglobal.logi.auvjob.common.enums.TaskStatusEnum;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.core.consensual.Consensual;
import com.didiglobal.logi.auvjob.core.consensual.ConsensualFactory;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.mapper.AuvTaskMapper;
import com.didiglobal.logi.auvjob.utils.BeanUtil;
import com.didiglobal.logi.auvjob.utils.CronExpression;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * task manager impl.
 *
 * @author dengshan
 */
@Service
public class TaskManagerImpl implements TaskManager {
  private static final Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

  private static final long WAIT_INTERVAL_SECONDS = 10L;

  private JobManager jobManager;
  private ConsensualFactory consensualFactory;
  private TaskLockService taskLockService;
  private AuvTaskMapper auvTaskMapper;

  /**
   * constructor.
   *
   * @param jobManager      jobManager
   * @param taskLockService taskLockService
   * @param auvTaskMapper   auvTaskMapper
   */
  public TaskManagerImpl(JobManager jobManager, ConsensualFactory consensualFactory,
                         TaskLockService taskLockService, AuvTaskMapper auvTaskMapper) {
    this.jobManager = jobManager;
    this.consensualFactory = consensualFactory;
    this.taskLockService = taskLockService;
    this.auvTaskMapper = auvTaskMapper;
  }

  @Override
  public boolean add(TaskDto taskDto) {
    return false;
  }

  @Override
  public Result delete(String taskCode) {
    AuvTask auvTask = auvTaskMapper.selectByCode(taskCode);
    if (auvTask == null) {
      return Result.buildFail("任务不存在！");
    }
    return Result.buildSucc(auvTaskMapper.deleteByCode(taskCode) > 0);
  }

  @Override
  public boolean update(TaskDto taskDto) {
    AuvTask auvTask = BeanUtil.convertTo(taskDto, AuvTask.class);
    return auvTaskMapper.updateByCode(auvTask) > 0 ? true : false;
  }

  @Override
  public List<TaskInfo> nextTriggers(Long interval) {
    return nextTriggers(System.currentTimeMillis(), interval);
  }

  @Override
  public List<TaskInfo> nextTriggers(Long fromTime, Long interval) {
    List<TaskInfo> taskInfoList = getAll();
    taskInfoList = taskInfoList.stream().filter(taskInfo -> {
      try {
        Timestamp lastFireTime = new Timestamp(0L);
        List<TaskInfo.TaskWorker> taskWorkers = taskInfo.getTaskWorkers();
        for (TaskInfo.TaskWorker taskWorker : taskWorkers) {
          // 取到当前worker做进一步判断，如果没有找到证明没有执行过
          if (Objects.equals(WorkerSingleton.getInstance().getWorkerInfo().getCode(),
                  taskWorker.getWorkerCode())) {
            // 判断是否在当前worker可执行状态
            if (!Objects.equals(taskWorker.getStatus(), TaskStatusEnum.WAITING.getValue())) {
              logger.info("class=TaskManagerImpl||method=nextTriggers||url=||msg=has task running! "
                      + "taskCode={}, workerCode={}", taskInfo.getCode(),
                      taskWorker.getWorkerCode());
              return false;
            }
            lastFireTime = taskWorker.getLastFireTime();
            break;
          }
        }
        CronExpression cronExpression = new CronExpression(taskInfo.getCron());
        long nextTime = cronExpression.getNextValidTimeAfter(lastFireTime).getTime();
        taskInfo.setNextFireTime(new Timestamp(nextTime));
      } catch (Exception e) {
        logger.error("class=TaskManagerImpl||method=nextTriggers||url=||msg=", e);
        return false;
      }
      return (new Timestamp(fromTime + interval * 1000)).after(taskInfo.getNextFireTime());
    }).collect(Collectors.toList());

    // sort
    taskInfoList.sort(Comparator.comparing(TaskInfo::getNextFireTime));
    return taskInfoList;
  }

  @Override
  public void submit(List<TaskInfo> taskInfoList) {
    if (CollectionUtils.isEmpty(taskInfoList)) {
      return;
    }
    for (TaskInfo taskInfo : taskInfoList) {
      // 不能在本工作器执行，跳过
      Consensual consensual = consensualFactory.getConsensual(taskInfo.getConsensual());
      if (!consensual.canClaim(taskInfo)) {
        continue;
      }
      execute(taskInfo, false);
    }
  }

  /**
   * execute.
   */
  public Result execute(String taskCode, Boolean executeSubs) {
    AuvTask auvTask = auvTaskMapper.selectByCode(taskCode);
    if (auvTask == null) {
      return Result.buildFail("任务不存在！");
    }
    if (!Objects.equals(auvTask.getStatus(), TaskStatusEnum.WAITING.getValue())) {
      return Result.buildFail("任务非等待执行状态！");
    }
    if (!taskLockService.tryAcquire(taskCode)) {
      return Result.buildFail("未能获取到执行锁！");
    }

    TaskInfo taskInfo = BeanUtil.convertTo(auvTask, TaskInfo.class);
    taskInfo.setTaskCallback(code -> taskLockService.tryRelease(code));
    execute(taskInfo, false);

    return Result.buildSucc();
  }

  @Override
  public void execute(TaskInfo taskInfo, Boolean executeSubs) {
    AuvTask auvTask = BeanUtil.convertTo(taskInfo, AuvTask.class);
    List<TaskInfo.TaskWorker> taskWorkers = taskInfo.getTaskWorkers();
    boolean worked = false;
    for (TaskInfo.TaskWorker taskWorker : taskWorkers) {
      if (Objects.equals(taskWorker.getWorkerCode(),
              WorkerSingleton.getInstance().getWorkerInfo().getCode())) {
        taskWorker.setLastFireTime(new Timestamp(System.currentTimeMillis()));
        taskWorker.setStatus(TaskStatusEnum.RUNNING.getValue());
        worked = true;
        break;
      }
    }
    if (!worked) {
      taskWorkers.add(new TaskInfo.TaskWorker(TaskStatusEnum.RUNNING.getValue(),
              new Timestamp(System.currentTimeMillis()),
              WorkerSingleton.getInstance().getWorkerInfo().getCode()));
    }

    auvTask.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
    // 更新任务状态，最近更新时间
    auvTaskMapper.updateByCode(auvTask);

    // 执行
    executeInternal(taskInfo, executeSubs);
  }

  @Override
  public int stopAll() {
    return jobManager.stopAll();
  }

  @Override
  public List<TaskInfo> getAll() {
    List<TaskInfo> taskInfoList = new ArrayList<>();
    List<AuvTask> auvTaskList = auvTaskMapper.selectAll();
    if (CollectionUtils.isEmpty(auvTaskList)) {
      return taskInfoList;
    }

    // 转taskInfo
    taskInfoList = auvTaskList.stream().map(auvTask -> {
      TaskInfo info = BeanUtil.convertTo(auvTask, TaskInfo.class);
      List<TaskInfo.TaskWorker> taskWorkers = Lists.newArrayList();
      if (!StringUtils.isEmpty(auvTask.getTaskWorkerStr())) {
        List<TaskInfo.TaskWorker> tmpTaskWorkers = BeanUtil.convertToList(
                auvTask.getTaskWorkerStr(), TaskInfo.TaskWorker.class);
        if (!CollectionUtils.isEmpty(tmpTaskWorkers)) {
          taskWorkers = tmpTaskWorkers;
        }
      }
      info.setTaskWorkers(taskWorkers);
      return info;
    }).collect(Collectors.toList());
    return taskInfoList;
  }

  @Override
  public Result<Boolean> release(String taskCode, String workerCode) {
    // 释放锁
    Boolean lockRet = taskLockService.tryRelease(taskCode, workerCode);
    if (!lockRet) {
      return Result.buildFail("释放锁失败！");
    }

    // 更新任务状态
    boolean updateResult = updateTaskWorker(taskCode, workerCode);
    if (!updateResult) {
      return Result.buildFail("更新锁失败！");
    }
    return Result.buildSucc();
  }

  // ################################## private method #######################################
  private void executeInternal(TaskInfo taskInfo, Boolean executeSubs) {
    // jobManager 将job管理起来，超时退出抛异常
    final Future<Object> jobFuture = jobManager.start(taskInfo);
    if (jobFuture == null || !executeSubs) {
      return;
    }
    // 等待父任务运行完
    while (!jobFuture.isDone()) {
      ThreadUtil.sleep(WAIT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
    // 递归拉起子任务
    if (!StringUtils.isEmpty(taskInfo.getSubTaskCodes())) {
      String[] subTaskCodeArray = taskInfo.getSubTaskCodes().split(",");
      List<AuvTask> subTasks = auvTaskMapper.selectByCodes(Arrays.asList(subTaskCodeArray));
      List<TaskInfo> subTaskInfoList = subTasks.stream().map(auvTask -> BeanUtil.convertTo(auvTask,
              TaskInfo.class)).collect(Collectors.toList());
      for (TaskInfo subTaskInfo : subTaskInfoList) {
        execute(subTaskInfo, executeSubs);
      }
    }
  }

  private boolean updateTaskWorker(String taskCode, String workerCode) {
    AuvTask auvTask = auvTaskMapper.selectByCode(taskCode);
    if (auvTask == null) {
      return false;
    }
    List<TaskInfo.TaskWorker> taskWorkers = BeanUtil.convertToList(auvTask.getTaskWorkerStr(),
            TaskInfo.TaskWorker.class);
    boolean needUpdate = false;
    if (!CollectionUtils.isEmpty(taskWorkers)) {
      for (TaskInfo.TaskWorker taskWorker : taskWorkers) {
        if (Objects.equals(taskWorker.getWorkerCode(), workerCode)
                && Objects.equals(taskWorker.getStatus(), TaskStatusEnum.RUNNING.getValue())) {
          needUpdate = true;
          taskWorker.setStatus(TaskStatusEnum.WAITING.getValue());
        }
      }
    }
    if (needUpdate) {
      auvTask.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
      int updateResult = auvTaskMapper.updateByCode(auvTask);
      if (updateResult <= 0) {
        return false;
      }
    }
    return true;
  }
}
