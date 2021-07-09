package com.didiglobal.logi.auvjob.common.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务锁.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AuvTaskLock implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  /*
   * task code
   */
  private String taskCode;

  /*
   * worker code
   */
  private String workerCode;

  /*
   * expire time
   */
  private Long expireTime;

  /*
   * 开始时间
   */
  private Timestamp createTime;

  /*
   * 开始时间
   */
  private Timestamp updateTime;

}
