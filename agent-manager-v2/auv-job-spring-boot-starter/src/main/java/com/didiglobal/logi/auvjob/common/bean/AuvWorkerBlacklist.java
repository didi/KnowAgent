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
public class AuvWorkerBlacklist implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  /*
   * workerCode
   */
  private String workerCode;

}
