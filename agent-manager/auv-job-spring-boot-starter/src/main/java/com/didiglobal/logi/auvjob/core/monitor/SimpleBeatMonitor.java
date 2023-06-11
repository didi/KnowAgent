package com.didiglobal.logi.auvjob.core.monitor;

import com.didiglobal.logi.auvjob.core.beat.BeatManager;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple beat monitor.
 *
 * @author dengshan
 */
@Service
public class SimpleBeatMonitor implements BeatMonitor {
  private static final Logger logger = LoggerFactory.getLogger(SimpleTaskMonitor.class);

  private BeatManager beatManager;

  private Thread monitorThread;

  @Autowired
  public SimpleBeatMonitor(BeatManager beatManager) {
    this.beatManager = beatManager;
  }

  @Override
  public void maintain() {
    monitorThread = new Thread(new BeatMonitorThread(), "BeatMonitorThread");
    monitorThread.start();
  }

  @Override
  public void stop() {
    logger.info("class=SimpleBeatMonitor||method=stop||url=||msg=beat monitor stop!!!");
    try {
      beatManager.stop();
      if (monitorThread != null && monitorThread.isAlive()) {
        monitorThread.interrupt();
      }
    } catch (Exception e) {
      logger.error("class=SimpleBeatMonitor||method=stop||url=||msg=", e);
    }
  }

  class BeatMonitorThread implements Runnable {
    private static final long INTERVAL = 5L;

    @Override
    public void run() {
      while (true) {
        try {
          beatManager.beat();

          // beat every INTERVAL seconds
          ThreadUtil.sleep(INTERVAL, TimeUnit.SECONDS);
        } catch (Exception e) {
          logger.info("class=SimpleBeatMonitor||method=run||url=||msg=", e);
        }
      }
    }
  }
}
