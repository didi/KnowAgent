package com.didiglobal.logi.auvjob.core.task;

import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import com.didiglobal.logi.auvjob.common.dto.TaskLockDto;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.mapper.AuvTaskLockMapper;
import com.didiglobal.logi.auvjob.utils.BeanUtil;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * task lock service.
 *
 * @author dengshan
 */
@Service
public class TaskLockServiceImpl implements TaskLockService {
  private static final Logger logger = LoggerFactory.getLogger(TaskLockServiceImpl.class);
  // 每次申请锁的默认过期时间
  private static final Long EXPIRE_TIME_SECONDS = 300L;

  private AuvTaskLockMapper auvTaskLockMapper;

  /**
   * constructor.
   *
   */
  @Autowired
  public TaskLockServiceImpl(AuvTaskLockMapper auvTaskLockMapper) {
    this.auvTaskLockMapper = auvTaskLockMapper;
  }

  @Override
  public Boolean tryAcquire(String taskCode) {
    return tryAcquire(taskCode, WorkerSingleton.getInstance().getWorkerInfo().getCode(),
            EXPIRE_TIME_SECONDS);
  }

  @Override
  public Boolean tryAcquire(String taskCode, String workerCode, Long expireTime) {
    List<AuvTaskLock> auvTaskLockList = auvTaskLockMapper.selectByTaskCode(taskCode);

    boolean hasLock;
    if (CollectionUtils.isEmpty(auvTaskLockList)) {
      hasLock = false;
    } else {
      long current = System.currentTimeMillis() / 1000;
      Long inLockSize = auvTaskLockList.stream().filter(auvTaskLock -> auvTaskLock.getCreateTime()
              .getTime() / 1000 + expireTime < current).collect(Collectors.counting());
      hasLock = inLockSize > 0 ? true : false;
    }

    if (!hasLock) {
      AuvTaskLock taskLock = new AuvTaskLock();
      taskLock.setTaskCode(taskCode);
      taskLock.setWorkerCode(workerCode);
      taskLock.setExpireTime(expireTime);
      taskLock.setCreateTime(new Timestamp(System.currentTimeMillis()));
      taskLock.setUpdateTime(new Timestamp(System.currentTimeMillis()));
      try {
        return auvTaskLockMapper.insert(taskLock) > 0 ? true : false;
      } catch (Exception e) {
        logger.error("class=TaskLockServiceImpl||method=tryAcquire||url=||msg=", e);
      }
    }
    return false;
  }

  @Override
  public Boolean tryRelease(String taskCode) {
    return tryRelease(taskCode, WorkerSingleton.getInstance().getWorkerInfo().getCode());
  }

  @Override
  public Boolean tryRelease(String taskCode, String workerCode) {
    List<AuvTaskLock> auvTaskLockList = auvTaskLockMapper.selectByTaskCodeAndWorkerCode(taskCode,
            workerCode);
    if (CollectionUtils.isEmpty(auvTaskLockList)) {
      logger.error("class=TaskLockServiceImpl||method=tryRelease||url=||msg=taskCode={}, "
              + "workerCode={}", taskCode, workerCode);
      return false;
    }
    long current = System.currentTimeMillis() / 1000;
    List<Long> taskLockIdList = auvTaskLockList.stream().filter(auvTaskLock ->
            auvTaskLock.getCreateTime().getTime() / 1000 + auvTaskLock.getExpireTime() < current)
            .map(AuvTaskLock::getId)
            .collect(Collectors.toList());
    int result = auvTaskLockMapper.deleteByIds(taskLockIdList);
    return result > 0 ? true : false;
  }

  @Override
  public List<TaskLockDto> getAll() {
    List<AuvTaskLock> auvTaskLocks = auvTaskLockMapper.selectAll();
    if (CollectionUtils.isEmpty(auvTaskLocks)) {
      return null;
    }
    return auvTaskLocks.stream().map(auvTaskLock -> BeanUtil.convertTo(auvTaskLock,
            TaskLockDto.class)).collect(Collectors.toList());
  }

  @Override
  public void renewAll() {

  }
}
