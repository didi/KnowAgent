package com.didiglobal.logi.auvjob.common.domain;

import com.didiglobal.logi.auvjob.common.bean.AuvWorker;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class WorkerInfo {

  private String code;
  private String name;

  private Integer cpu;

  private Double cpuUsed;

  /*
   * 以M为单位
   */
  private Double memory;

  private Double memoryUsed;

  /*
   * 以M为单位
   */
  private Double jvmMemory;

  private Double jvmMemoryUsed;

  private Integer jobNum;

  private Timestamp heartbeat;

  /**
   * get auv worker.
   *
   * @return auv worker
   */
  public AuvWorker getWorker() {
    AuvWorker auvWorker = new AuvWorker();
    auvWorker.setCode(this.code);
    auvWorker.setName(this.name);
    auvWorker.setCpu(this.cpu);
    auvWorker.setCpuUsed(this.cpuUsed);
    auvWorker.setMemory(this.memory);
    auvWorker.setMemoryUsed(this.memoryUsed);
    auvWorker.setJvmMemory(this.jvmMemory);
    auvWorker.setJvmMemoryUsed(this.jvmMemoryUsed);
    auvWorker.setJobNum(this.jobNum);
    auvWorker.setHeartbeat(new Timestamp(System.currentTimeMillis()));
    return auvWorker;
  }
}