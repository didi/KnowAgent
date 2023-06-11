package com.didiglobal.logi.auvjob.common.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * 正在执行的job信息.
 *
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AuvJob implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  /*
   * task code
   */
  private String code;

  /*
   * 任务code
   */
  private String taskCode;

  /*
   * 类的全限定名
   */
  private String className;

  /*
   * 第几次重试
   */
  private Integer tryTimes;

  /*
   * 执行机器
   */
  private String workerCode;

  /*
   * 开始时间
   */
  private Timestamp startTime;

  /*
   * 开始时间
   */
  private Timestamp createTime;

  /*
   * 更新时间
   */
  private Timestamp updateTime;

}
