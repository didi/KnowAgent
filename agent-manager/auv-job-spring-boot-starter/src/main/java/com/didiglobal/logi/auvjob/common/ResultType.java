package com.didiglobal.logi.auvjob.common;

/**
 * Created by limeng on 2020-04-27.
 */
public enum ResultType {

  SUCCESS(0, "操作成功"),

  FAIL(19999, "操作失败"),

  ILLEGAL_PARAMS(10000, "参数错误"),

  RESOURCE_NOT_READY(10001, "资源未就绪"),

  RESOURCE_PROCESSING(10002, "资源审批中"),

  ES_OPERATE_ERROR(10003, "es操作失败"),

  DUPLICATION(10004, "数据已存在"),

  OPERATE_FORBIDDEN_ERROR(10005, "无权限"),

  HDFS_ACCESS_ERROR(10006, "访问hdfs失败"),

  LOGX_SQL_ERROR(10007, "logXSql操作失败"),

  LOGX_MODULE_ERROR(10008, "logXModule操作失败"),

  HTTP_REQ_ERROR(10009, "第三方http请求异常"),

  SAVE_MIDDLE_RESULT(20000, "保存中间结果"),


  MONITOR_NOT_EXIST(30000, "monitor not exist");

  private int code;
  private String message;

  ResultType(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

}