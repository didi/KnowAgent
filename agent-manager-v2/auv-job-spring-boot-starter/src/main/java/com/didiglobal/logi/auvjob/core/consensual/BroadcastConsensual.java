package com.didiglobal.logi.auvjob.core.consensual;

import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import org.springframework.stereotype.Service;

/**
 * 随机算法.
 *
 * @author dengshan
 */
@Service
public class BroadcastConsensual extends AbstractConsensual {

  @Override
  public String getName() {
    return ConsensualConstant.BROADCAST.name();
  }

  @Override
  public boolean tryClaim(TaskInfo taskInfo) {
    return true;
  }
}
