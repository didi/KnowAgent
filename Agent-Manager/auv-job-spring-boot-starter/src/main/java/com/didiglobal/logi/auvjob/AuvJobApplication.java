package com.didiglobal.logi.auvjob;

import com.didiglobal.logi.auvjob.core.SimpleScheduler;
import com.didiglobal.logi.auvjob.core.monitor.BeatMonitor;
import com.didiglobal.logi.auvjob.core.monitor.MisfireMonitor;
import com.didiglobal.logi.auvjob.core.monitor.TaskMonitor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.didiglobal.logi.auvjob")
public class AuvJobApplication {

  /**
   * 入口函数.
   *
   * @param args 参数
   */
  public static void main(String[] args) {
    AnnotationConfigApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(AuvJobApplication.class);
    BeatMonitor beatMonitor = applicationContext.getBean(BeatMonitor.class);
    TaskMonitor taskMonitor = applicationContext.getBean(TaskMonitor.class);
    MisfireMonitor misfireMonitor = applicationContext.getBean(MisfireMonitor.class);
    SimpleScheduler simpleScheduler = new SimpleScheduler(beatMonitor, taskMonitor, misfireMonitor);
    simpleScheduler.startup();
  }
}
