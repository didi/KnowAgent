package com.didiglobal.logi.auvjob.core.job;

import com.didiglobal.logi.auvjob.AuvJobProperties;
import com.didiglobal.logi.auvjob.common.bean.AuvJob;
import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import com.didiglobal.logi.auvjob.common.domain.JobInfo;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.dto.JobDto;
import com.didiglobal.logi.auvjob.common.dto.JobLogDto;
import com.didiglobal.logi.auvjob.common.enums.JobStatusEnum;
import com.didiglobal.logi.auvjob.common.enums.TaskStatusEnum;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.core.task.TaskLockService;
import com.didiglobal.logi.auvjob.mapper.AuvJobLogMapper;
import com.didiglobal.logi.auvjob.mapper.AuvJobMapper;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import com.didiglobal.logi.auvjob.mapper.AuvTaskMapper;
import com.didiglobal.logi.auvjob.utils.BeanUtil;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * job manager impl.
 *
 * @author dengshan
 */
@Service
public class JobManagerImpl implements JobManager {
  private static final Logger logger = LoggerFactory.getLogger(JobManagerImpl.class);
  // 停止任务尝试次数
  private static final int TRY_MAX_TIMES = 3;
  // 停止任务每次尝试后sleep 时间 秒
  private static final int STOP_SLEEP_SECONDS = 3;

  // 续约锁提前检查时间 秒
  private static final Long CHECK_BEFORE_INTERVAL = 60L;
  // 每次给锁续约的时间 秒
  private static final Long RENEW_INTERVAL = 60L;

  private JobFactory jobFactory;
  private AuvJobMapper auvJobMapper;
  private AuvJobLogMapper auvJobLogMapper;
  private AuvTaskMapper auvTaskMapper;
  private JobExecutor jobExecutor;
  private TaskLockService taskLockService;
  private AuvTaskLockMapper auvTaskLockMapper;
  private AuvJobProperties auvJobProperties;

  private ConcurrentHashMap<JobInfo, Future> jobFutureMap = new ConcurrentHashMap<>();

  /**
   * constructor.
   *
   */
  @Autowired
  public JobManagerImpl(JobFactory jobFactory, AuvJobMapper auvJobMapper,
                        AuvJobLogMapper auvJobLogMapper, AuvTaskMapper auvTaskMapper,
                        JobExecutor jobExecutor, TaskLockService taskLockService,
                        AuvTaskLockMapper auvTaskLockMapper, AuvJobProperties auvJobProperties) {
    this.jobFactory = jobFactory;
    this.auvJobMapper = auvJobMapper;
    this.auvJobLogMapper = auvJobLogMapper;
    this.auvTaskMapper = auvTaskMapper;
    this.jobExecutor = jobExecutor;
    this.taskLockService = taskLockService;
    this.auvTaskLockMapper = auvTaskLockMapper;
    this.auvJobProperties = auvJobProperties;
    initialize();
  }

  private void initialize() {
    new Thread(new JobFutureHandler(), "JobFutureHandler Thread").start();
    new Thread(new LockRenewHandler(), "LockRenewHandler Thread").start();
    new Thread(new LogCleanHandler(this.auvJobProperties.getLogExpire()),
            "LogCleanHandler Thread").start();
  }

  @Override
  public Future<Object> start(TaskInfo taskInfo) {
    // 添加job信息
    JobInfo jobInfo = jobFactory.newJob(taskInfo);
    AuvJob job = jobInfo.getAuvJob();
    auvJobMapper.insert(job);

    Future jobFuture = jobExecutor.submit(new JobHandler(jobInfo));
    jobFutureMap.put(jobInfo, jobFuture);
    return jobFuture;
  }

  @Override
  public Integer runningJobSize() {
    return jobFutureMap.size();
  }

  @Override
  public boolean stop(String jobCode) {
    for (Map.Entry<JobInfo, Future> jobFuture : jobFutureMap.entrySet()) {
      JobInfo jobInfo = jobFuture.getKey();
      if (Objects.equals(jobCode, jobInfo.getCode())) {
        int tryTime = 0;
        Future future = jobFuture.getValue();
        while (tryTime < TRY_MAX_TIMES) {
          if (future.isDone()) {
            jobInfo.setStatus(JobStatusEnum.CANCELED.getValue());
            if (jobInfo.getTaskCallback() != null) {
              jobInfo.getTaskCallback().callback(jobInfo.getTaskCode());
            }
            reorganizeFinishedJob(jobFuture.getKey());
            return true;
          }
          future.cancel(true);
          tryTime++;
          ThreadUtil.sleep(STOP_SLEEP_SECONDS, TimeUnit.SECONDS);
        }
      }
    }
    return false;
  }

  @Override
  public int stopAll() {
    AtomicInteger succeedNum = new AtomicInteger();
    int tryTime = 0;
    while (!jobFutureMap.isEmpty() && tryTime < TRY_MAX_TIMES) {
      jobFutureMap.forEach(((jobInfo, future) -> {
        if (future.isDone()) {
          jobInfo.setStatus(JobStatusEnum.CANCELED.getValue());
          if (jobInfo.getTaskCallback() != null) {
            jobInfo.getTaskCallback().callback(jobInfo.getTaskCode());
          }
          reorganizeFinishedJob(jobInfo);
          succeedNum.addAndGet(1);
        } else {
          future.cancel(true);
        }
      }));
      tryTime++;
      ThreadUtil.sleep(STOP_SLEEP_SECONDS, TimeUnit.SECONDS);
    }
    return succeedNum.get();
  }

  @Override
  public List<JobDto> getJobs() {
    List<AuvJob> auvJobs = auvJobMapper.selectAll();
    if (CollectionUtils.isEmpty(auvJobs)) {
      return null;
    }
    List<JobDto> jobDtos = auvJobs.stream().map(auvJob -> BeanUtil.convertTo(auvJob, JobDto.class))
            .collect(Collectors.toList());
    return jobDtos;
  }

  @Override
  public List<JobLogDto> getJobLogs(String taskCode, Integer limit) {
    List<AuvJobLog> auvJobLogs = auvJobLogMapper.selectByTaskCode(taskCode, limit);
    if (CollectionUtils.isEmpty(auvJobLogs)) {
      return null;
    }
    return auvJobLogs.stream().map(auvJobLog -> BeanUtil.convertTo(auvJobLog, JobLogDto.class))
            .collect(Collectors.toList());
  }

  /**
   * job 执行线程.
   */
  class JobHandler implements Callable {

    private JobInfo jobInfo;

    public JobHandler(JobInfo jobInfo) {
      this.jobInfo = jobInfo;
    }

    @Override
    public Object call() {
      Object object = null;
      logger.info("class=JobHandler||method=call||url=||msg=start job {} with classname {}",
              jobInfo.getCode(), jobInfo.getClassName());
      try {
        jobInfo.setStartTime(new Timestamp(System.currentTimeMillis()));
        object = jobInfo.getJob().execute(null);
        jobInfo.setStatus(JobStatusEnum.SUCCEED.getValue());
        jobInfo.setError("");
      } catch (InterruptedException e) {
        // 记录任务被打断 进程关闭/线程关闭
        jobInfo.setStatus(JobStatusEnum.CANCELED.getValue());
        String errorMessage = String.format("StackTrace[%s] || Message[%s]", e, e.getMessage());
        jobInfo.setError(errorMessage);
        logger.error("class=JobHandler||method=call||url=||msg={}", e);
      } catch (Exception e) {
        // 记录任务异常信息
        jobInfo.setStatus(JobStatusEnum.FAILED.getValue());
        String errorMessage = String.format("StackTrace[%s] || Message[%s]", e, e.getMessage());
        jobInfo.setError(errorMessage);
        logger.error("class=JobHandler||method=call||url=||msg={}", e);
      } finally {
        // job callback, 释放任务锁
        if (jobInfo.getTaskCallback() != null) {
          jobInfo.getTaskCallback().callback(jobInfo.getTaskCode());
        }
      }
      jobInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
      jobInfo.setError(jobInfo.getError() == null ? "" : jobInfo.getError());
      jobInfo.setResult(object);
      return object;
    }
  }

  /**
   * Job 执行清理线程，对超时的要主动杀死，执行完的收集信息并记录日志.
   */
  class JobFutureHandler implements Runnable {
    private static final long JOB_FUTURE_CLEAN_INTERVAL = 10L;

    public JobFutureHandler() {
    }

    @Override
    public void run() {
      while (true) {
        try {
          logger.info("class=JobFutureHandler||method=run||url=||msg=check running jobs at regular "
                  + "time {}", JOB_FUTURE_CLEAN_INTERVAL);

          // 定时轮询任务，检查状态并处理
          jobFutureMap.forEach(((jobInfo, future) -> {
            // job完成，信息整理
            if (future.isDone()) {
              reorganizeFinishedJob(jobInfo);
            }

            // 超时处理
            Long timeout = jobInfo.getTimeout();
            if (timeout <= 0) {
              return;
            }
            Long startTime = jobInfo.getStartTime().getTime();
            Long now = System.currentTimeMillis();
            Long between = (now - startTime) / 1000;

            if (between > timeout && !future.isDone()) {
              future.cancel(true);
            }
          }));

          // 间隔一段时间执行一次
          ThreadUtil.sleep(JOB_FUTURE_CLEAN_INTERVAL, TimeUnit.SECONDS);
        } catch (Exception e) {
          logger.error("class=JobFutureHandler||method=run||url=||msg=", e);
        }
      }
    }
  }

  /**
   * 整理已完成的任务.
   *
   * @param jobInfo jobInfo
   */
  @Transactional
  public void reorganizeFinishedJob(JobInfo jobInfo) {
    // 移除记录
    jobFutureMap.remove(jobInfo);

    // 增加auvJobLog
    AuvJobLog auvJobLog = jobInfo.getAuvJobLog();
    auvJobLogMapper.insert(auvJobLog);

    // 删除auvJob
    auvJobMapper.deleteByCode(jobInfo.getCode());

    // 更新任务状态
    AuvTask auvTask = auvTaskMapper.selectByCode(jobInfo.getTaskCode());
    List<TaskInfo.TaskWorker> taskWorkers = BeanUtil.convertToList(auvTask.getTaskWorkerStr(),
            TaskInfo.TaskWorker.class);
    if (!CollectionUtils.isEmpty(taskWorkers)) {
      for (TaskInfo.TaskWorker taskWorker : taskWorkers) {
        if (Objects.equals(taskWorker.getWorkerCode(), WorkerSingleton.getInstance()
                .getWorkerInfo().getCode())) {
          taskWorker.setStatus(TaskStatusEnum.WAITING.getValue());
        }
      }
    }
    auvTask.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
    auvTaskMapper.updateByCode(auvTask);
  }

  /**
   * 锁续约线程.
   */
  class LockRenewHandler implements Runnable {
    private static final long JOB_INTERVAL = 10L;

    public LockRenewHandler() {
    }

    @Override
    public void run() {
      while (true) {
        try {
          logger.info("class=LockRenewHandler||method=run||url=||msg=check need renew lock at "
                  + "regular time {}", JOB_INTERVAL);

          // 锁续约
          List<AuvTaskLock> auvTaskLocks = auvTaskLockMapper.selectByWorkerCode(WorkerSingleton
                  .getInstance().getWorkerInfo().getCode());
          if (!CollectionUtils.isEmpty(auvTaskLocks)) {
            for (AuvTaskLock auvTaskLock : auvTaskLocks) {
              boolean matched = jobFutureMap.keySet().stream().anyMatch(jobInfo ->
                      Objects.equals(auvTaskLock.getTaskCode(), jobInfo.getTaskCode()));
              if (matched) {
                long current = System.currentTimeMillis() / 1000;
                long exTime = (auvTaskLock.getCreateTime().getTime() / 1000)
                        + auvTaskLock.getExpireTime();
                if (current < exTime) {
                  // 续约
                  if (current > exTime - CHECK_BEFORE_INTERVAL) {
                    logger.info("class=TaskLockServiceImpl||method=run||url=||msg=update lock "
                            + "expireTime id={}, expireTime={}", auvTaskLock.getId(),
                            auvTaskLock.getExpireTime());
                    auvTaskLockMapper.update(auvTaskLock.getId(), auvTaskLock.getExpireTime()
                            + RENEW_INTERVAL);
                  }
                  continue;
                }
              }

              // 否则，删除无效的锁、过期的锁
              logger.info("class=TaskLockServiceImpl||method=run||url=||msg=lock clean "
                      + "lockInfo={}", BeanUtil.convertToJson(auvTaskLock));
              auvTaskLockMapper.deleteById(auvTaskLock.getId());

              // 更新当前worker任务状态
              AuvTask auvTask = auvTaskMapper.selectByCode(auvTaskLock.getTaskCode());
              if (auvTask != null) {
                List<TaskInfo.TaskWorker> taskWorkers = BeanUtil.convertToList(
                        auvTask.getTaskWorkerStr(), TaskInfo.TaskWorker.class);
                if (!CollectionUtils.isEmpty(taskWorkers)) {
                  for (TaskInfo.TaskWorker taskWorker : taskWorkers) {
                    if (Objects.equals(taskWorker.getWorkerCode(), WorkerSingleton.getInstance()
                            .getWorkerInfo().getCode())) {
                      taskWorker.setStatus(TaskStatusEnum.WAITING.getValue());
                    }
                  }
                }
                auvTask.setTaskWorkerStr(BeanUtil.convertToJson(taskWorkers));
                logger.info("class=TaskLockServiceImpl||method=run||url=||msg=update task workers "
                        + "status taskInfo={}", BeanUtil.convertToJson(auvTask));
                auvTaskMapper.updateByCode(auvTask);
              }
            }
          }

          // 间隔一段时间执行一次
          ThreadUtil.sleep(JOB_INTERVAL, TimeUnit.SECONDS);
        } catch (Exception e) {
          logger.error("class=LockRenewHandler||method=run||url=||msg=", e);
        }
      }
    }
  }

  /**
   * 定时清理日志.
   */
  class LogCleanHandler implements Runnable {
    // 每小时执行一次
    private static final long JOB_INTERVAL = 3600L;
    // 日志保存时间[默认保存7天]
    private Integer logExpire = 7;

    public LogCleanHandler(Integer logExpire) {
      if (logExpire != null) {
        this.logExpire = logExpire;
      }
    }

    @Override
    public void run() {
      while (true) {
        try {
          logger.info("class=LogCleanHandler||method=run||url=||msg=clean auv_job_log regular"
                  + " time {}", JOB_INTERVAL);
          // 删除日志
          Calendar calendar = Calendar.getInstance();
          calendar.add(Calendar.DATE, -1 * logExpire);
          int count = auvJobLogMapper.deleteByCreateTime(new Timestamp(calendar.getTimeInMillis()));
          logger.info("class=LogCleanHandler||method=run||url=||msg=clean log count={}", count);
          // 间隔一段时间执行一次
          ThreadUtil.sleep(JOB_INTERVAL, TimeUnit.SECONDS);
        } catch (Exception e) {
          logger.error("class=LogCleanHandler||method=run||url=||msg=", e);
        }
      }
    }
  }
}
