package com.didiglobal.logi.auvjob.common.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * worker信息.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AuvWorker implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  /*
   * auv task code
   */
  private String code;

  /*
   * worker名
   */
  private String name;

  /*
   * cpu
   */
  private Integer cpu;

  /*
   * cpu使用
   */
  private Double cpuUsed;

  /*
   * 内存
   */
  private Double memory;

  /*
   * 内存使用
   */
  private Double memoryUsed;

  /*
   * jvm堆大小
   */
  private Double jvmMemory;

  /*
   * jvm堆使用
   */
  private Double jvmMemoryUsed;

  /*
   * 正在执行job数
   */
  private Integer jobNum;

  /*
   * 心跳时间
   */
  private Timestamp heartbeat;

}
