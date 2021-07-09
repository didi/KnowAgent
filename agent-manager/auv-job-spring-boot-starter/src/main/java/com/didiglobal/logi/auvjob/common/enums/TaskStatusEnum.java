package com.didiglobal.logi.auvjob.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum TaskStatusEnum {
  WAITING(1),
  RUNNING(2),
  STOPPED(3);

  private static Map<Integer, TaskStatusEnum> map = new HashMap<>(8);

  static {
    map.put(WAITING.getValue(), WAITING);
    map.put(RUNNING.getValue(), RUNNING);
    map.put(STOPPED.getValue(), STOPPED);
  }

  private Integer value;

  public Integer getValue() {
    return value;
  }

  TaskStatusEnum(Integer value) {
    this.value = value;
  }

  public static TaskStatusEnum get(Integer value) {
    return map.get(value);
  }
}