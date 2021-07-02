package com.didiglobal.logi.auvjob.core.consensual;

import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.core.task.TaskLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author dengshan
 */
@Service
public class RandomConsensual extends AbstractConsensual {
  private static final Logger logger = LoggerFactory.getLogger(RandomConsensual.class);

  @Autowired
  private TaskLockService taskLockService;

  @Override
  public String getName() {
    return ConsensualConstant.RANDOM.name();
  }

  @Override
  public boolean tryClaim(TaskInfo taskInfo) {
    if (taskLockService.tryAcquire(taskInfo.getCode())) {
      taskInfo.setTaskCallback(taskCode -> {
        logger.info("class=RandomConsensual||method=tryClaim||url=||msg=release task lock "
                + "taskCode {}", taskCode);
        taskLockService.tryRelease(taskCode);
      });
      return true;
    }
    return false;
  }
}
