package com.didiglobal.logi.auvjob.utils;


public interface IdentifierGenerator {
  Number nextId(Object entity);

  default String nextUuid(Object entity) {
    return IdWorker.get32Uuid();
  }
}