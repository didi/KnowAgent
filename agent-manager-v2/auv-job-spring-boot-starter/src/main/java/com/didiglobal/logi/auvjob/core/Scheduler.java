package com.didiglobal.logi.auvjob.core;

/**
 * 一个worker代表调度执行，负责调度管理.
 *
 * @author dengshan
 */
public interface Scheduler {

  /*
   * worker初始化
   */
  void initialize();

  /*
   * 启动工作执行器
   */
  void startup();

  /*
   * 关闭工作执行器
   * @return
   */
  void shutdown();
}
