package com.didiglobal.logi.auvjob.core.beat;

import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.core.job.JobManager;
import com.didiglobal.logi.auvjob.mapper.AuvWorkerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeatManagerImpl implements BeatManager {
  private static final Logger logger = LoggerFactory.getLogger(BeatManagerImpl.class);

  private JobManager jobManager;
  private AuvWorkerMapper auvWorkerMapper;

  /**
   * constructor.
   *
   * @param jobManager job manager
   * @param auvWorkerMapper worker mapper
   */
  @Autowired
  public BeatManagerImpl(JobManager jobManager, AuvWorkerMapper auvWorkerMapper) {
    this.jobManager = jobManager;
    this.auvWorkerMapper = auvWorkerMapper;
  }

  @Override
  public boolean beat() {
    logger.info("class=BeatManagerImpl||method=||url=||msg=beat beat!!!");
    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    workerSingleton.updateInstanceMetrics();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    workerInfo.setJobNum(jobManager.runningJobSize());
    return auvWorkerMapper.saveOrUpdateById(workerInfo.getWorker()) > 0 ? true : false;
  }

  @Override
  public boolean stop() {
    // clean worker
    WorkerSingleton workerSingleton = WorkerSingleton.getInstance();
    WorkerInfo workerInfo = workerSingleton.getWorkerInfo();
    auvWorkerMapper.deleteByCode(workerInfo.getCode());
    return true;
  }

}