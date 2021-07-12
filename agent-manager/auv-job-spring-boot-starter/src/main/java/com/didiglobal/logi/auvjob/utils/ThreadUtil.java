package com.didiglobal.logi.auvjob.utils;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * thread util.
 */
public class ThreadUtil {
  private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

  /**
   * sleep.
   *
   * @param time time
   * @param timeUnit time unit
   */
  public static void sleep(long time, TimeUnit timeUnit) {
    try {
      switch (timeUnit) {
        case DAYS: {
          TimeUnit.DAYS.sleep(time);
          break;
        }
        case HOURS: {
          TimeUnit.HOURS.sleep(time);
          break;
        }
        case MINUTES: {
          TimeUnit.MINUTES.sleep(time);
          break;
        }
        case SECONDS: {
          TimeUnit.SECONDS.sleep(time);
          break;
        }
        case NANOSECONDS: {
          TimeUnit.NANOSECONDS.sleep(time);
          break;
        }
        case MICROSECONDS: {
          TimeUnit.MICROSECONDS.sleep(time);
          break;
        }
        case MILLISECONDS: {
          TimeUnit.MILLISECONDS.sleep(time);
          break;
        }
        default: break;
      }
    } catch (InterruptedException e) {
      logger.error("class=ThreadUtil||method=sleep||url=||msg={}", e);
    }
  }
}