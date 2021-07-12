package com.didiglobal.logi.auvjob.core.consensual;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 一致协议工厂，通过名称获取一致性协议.
 *
 * @author dengshan
 */
@Component
public class ConsensualFactory implements ApplicationContextAware {
  private ApplicationContext applicationContext;

  private static Map<String, Consensual> consensualMap = new HashMap<>();

  /**
   * 根据名称获取.
   *
   * @param name name
   * @return Consensual
   */
  public Consensual getConsensual(String name) {
    init();

    Consensual consensual = consensualMap.get(name);
    if (consensual == null) {
      throw new IllegalArgumentException("no such consensual " + name);
    }
    return consensual;
  }

  private void init() {
    if (!consensualMap.isEmpty()) {
      return;
    }

    Map<String, Consensual> beans = applicationContext.getBeansOfType(Consensual.class);

    for (Consensual consensual : beans.values()) {
      consensualMap.put(consensual.getName(), consensual);
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
