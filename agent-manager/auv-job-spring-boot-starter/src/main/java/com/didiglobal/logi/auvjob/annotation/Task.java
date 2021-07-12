package com.didiglobal.logi.auvjob.annotation;

import com.didiglobal.logi.auvjob.core.consensual.ConsensualConstant;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Task {
  /**
   * task name.
   */
  String name() default "";

  /**
   * task desc.
   */
  String description() default "";

  /**
   * task cron.
   */
  String cron() default "";

  /**
   * retry times.
   */
  int retryTimes() default 0;

  /**
   * timeout seconds.
   */
  long timeout() default 0;

  /**
   * if not exists in db will auto register.
   */
  boolean autoRegister() default false;

  /**
   * consensual.
   */
  ConsensualConstant consensual() default ConsensualConstant.RANDOM;
}
