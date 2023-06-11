package com.didiglobal.logi.auvjob.core.consensual;

import com.didiglobal.logi.auvjob.common.bean.AuvWorkerBlacklist;
import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.common.domain.WorkerInfo;
import com.didiglobal.logi.auvjob.core.WorkerSingleton;
import com.didiglobal.logi.auvjob.mapper.AuvWorkerBlacklistMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
public abstract class AbstractConsensual implements Consensual {

  private static final Logger logger = LoggerFactory.getLogger(AbstractConsensual.class);

  @Autowired
  private AuvWorkerBlacklistMapper auvWorkerBlacklistMapper;

  private static final String BLACKLIST_KEY = "BlacklistKey";

  private Cache<Object, Set<String>> blacklistCache = CacheBuilder.newBuilder()
          .expireAfterWrite(2, TimeUnit.MINUTES).build();

  @Override
  public boolean canClaim(TaskInfo taskInfo) {
    if (inBlacklist()) {
      return false;
    }
    return tryClaim(taskInfo);
  }

  public abstract boolean tryClaim(TaskInfo taskInfo);

  //###################################### private ################################################

  private boolean inBlacklist() {
    Set<String> blacklist = blacklist();
    WorkerInfo workerInfo = WorkerSingleton.getInstance().getWorkerInfo();
    return blacklist.contains(workerInfo.getCode());
  }

  private Set<String> blacklist() {
    Set<String> blacklist = new HashSet<>();
    try {
      blacklist = blacklistCache.get(BLACKLIST_KEY, () -> {
        List<AuvWorkerBlacklist> auvWorkerBlacklists = auvWorkerBlacklistMapper.selectAll();
        return auvWorkerBlacklists.stream().map(AuvWorkerBlacklist::getWorkerCode)
                .collect(Collectors.toSet());
      });
    } catch (ExecutionException e) {
      logger.error("class=AbstractConsensual||method=blacklist||url=||msg=", e);
    }
    return blacklist;
  }
}
