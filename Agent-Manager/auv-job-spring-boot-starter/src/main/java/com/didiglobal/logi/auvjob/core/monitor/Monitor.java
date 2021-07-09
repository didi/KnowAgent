package com.didiglobal.logi.auvjob.core.monitor;

/**
 * 监控器.
 *
 * @author dengshan
 */
public interface Monitor {

  /*
   * 保持执行
   */
  void maintain();

  /*
   * 停止
   */
  void stop();
}
