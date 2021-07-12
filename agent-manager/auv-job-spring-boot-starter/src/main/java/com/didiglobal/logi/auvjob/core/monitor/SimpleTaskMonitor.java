package com.didiglobal.logi.auvjob.core.monitor;

import com.didiglobal.logi.auvjob.common.domain.TaskInfo;
import com.didiglobal.logi.auvjob.core.task.TaskManager;
import com.didiglobal.logi.auvjob.utils.ThreadUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * simple task monitor.
 *
 * @author dengshan
 */
@Service
public class SimpleTaskMonitor implements TaskMonitor {

  private static final Logger logger = LoggerFactory.getLogger(SimpleTaskMonitor.class);

  /*
   * 任务管理器
   */
  private TaskManager taskManager;

  /*
   * 任务监听器执行线程
   */
  private Thread monitorThread;

  @Autowired
  public SimpleTaskMonitor(TaskManager taskManager) {
    this.taskManager = taskManager;
  }

  @Override
  public void maintain() {
    monitorThread = new Thread(new TaskMonitorExecutor(), "TaskMonitorExecutor_Thread");
    // 设置为守护线程
    monitorThread.setDaemon(true);
    monitorThread.start();
  }

  @Override
  public void stop() {
    logger.info("class=SimpleTaskMonitor||method=stop||url=||msg=task monitor stop!!!");
    try {
      taskManager.stopAll();
      if (monitorThread != null && monitorThread.isAlive()) {
        monitorThread.interrupt();
      }
    } catch (Exception e) {
      logger.error("class=SimpleTaskMonitor||method=stop||url=||msg=", e);
    }
  }

  class TaskMonitorExecutor implements Runnable {
    private static final long SCAN_INTERVAL_SLEEP_SECONDS = 1;
    private static final long INTERVAL_SECONDS = 10;

    @Override
    public void run() {
      while (true) {
        try {
          logger.info("class=TaskMonitorExecutor||method=run||url=||msg=fetch tasks at regular {}",
                  SCAN_INTERVAL_SLEEP_SECONDS);
          // 每次扫描，间隔1s。为了线程终端创造条件
          ThreadUtil.sleep(SCAN_INTERVAL_SLEEP_SECONDS, TimeUnit.SECONDS);

          List<TaskInfo> taskInfoList = taskManager.nextTriggers(INTERVAL_SECONDS);

          if (taskInfoList == null || taskInfoList.size() == 0) {
            logger.info("class=TaskMonitorExecutor||method=run||url=||msg=no tasks need run!");
            ThreadUtil.sleep(INTERVAL_SECONDS, TimeUnit.SECONDS);
            continue;
          }

          // 未到执行时间，等待
          logger.info("class=TaskMonitorExecutor||method=run||url=||msg=fetch tasks {}",
                  taskInfoList.stream().map(TaskInfo::getName).collect(Collectors.toList()));
          Long firstFireTime = taskInfoList.stream().findFirst().get().getNextFireTime().getTime();
          Long nowTime = System.currentTimeMillis();
          if (nowTime < firstFireTime) {
            Long between = firstFireTime - nowTime;
            ThreadUtil.sleep(between + 1, TimeUnit.MILLISECONDS);
          }
          logger.info("class=TaskMonitorExecutor||method=run||url=||msg=start tasks={}, "
                          + "firstFireTime={}, nowTime={}",
                  taskInfoList.stream().map(TaskInfo::getName).collect(Collectors.toList()),
                  firstFireTime, nowTime);

          // 提交任务
          taskManager.submit(taskInfoList);
        } catch (Exception e) {
          logger.error("class=TaskMonitorExecutor||method=run||url=||msg=", e);
        }
      }
    }
  }
}
