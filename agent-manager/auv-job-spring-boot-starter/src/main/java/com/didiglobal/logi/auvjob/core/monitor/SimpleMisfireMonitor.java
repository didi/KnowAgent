package com.didiglobal.logi.auvjob.core.monitor;

import org.springframework.stereotype.Service;

/**
 * simple misfire monitor.
 *
 * @author dengshan
 */
@Service
public class SimpleMisfireMonitor implements MisfireMonitor {

  @Override
  public void maintain() {
    // todo
  }

  @Override
  public void stop() {
    // todo
  }

  class MisfireMonitorThread implements Runnable {

    @Override
    public void run() {
      // todo
    }
  }
}
