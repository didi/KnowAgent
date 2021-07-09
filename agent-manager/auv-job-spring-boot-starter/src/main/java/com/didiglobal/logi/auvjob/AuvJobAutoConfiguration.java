package com.didiglobal.logi.auvjob;

import com.didiglobal.logi.auvjob.core.Scheduler;
import com.didiglobal.logi.auvjob.core.SimpleScheduler;
import com.didiglobal.logi.auvjob.core.monitor.BeatMonitor;
import com.didiglobal.logi.auvjob.core.monitor.MisfireMonitor;
import com.didiglobal.logi.auvjob.core.monitor.TaskMonitor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 配置类.
 *
 * @author dengshan
 */
@Configuration
@ConditionalOnClass({Scheduler.class, PlatformTransactionManager.class})
@EnableConfigurationProperties({AuvJobProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.didiglobal.logi.auvjob")
public class AuvJobAutoConfiguration {

  /**
   * start scheduler.
   */
  @Bean
  @ConditionalOnMissingBean
  public Scheduler quartzScheduler(ApplicationContext applicationContext) {
    BeatMonitor beatMonitor = applicationContext.getBean(BeatMonitor.class);
    TaskMonitor taskMonitor = applicationContext.getBean(TaskMonitor.class);
    MisfireMonitor misfireMonitor = applicationContext.getBean(MisfireMonitor.class);
    SimpleScheduler simpleScheduler = new SimpleScheduler(beatMonitor, taskMonitor, misfireMonitor);
    simpleScheduler.startup();
    return simpleScheduler;
  }
}