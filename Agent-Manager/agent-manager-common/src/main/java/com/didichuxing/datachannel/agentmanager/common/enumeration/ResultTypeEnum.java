package com.didichuxing.datachannel.agentmanager.common.enumeration;

/**
 * @author huqidong
 * @date 2020-09-21
 * 结果类型
 */
public enum ResultTypeEnum {

    SUCCESS(0, "操作成功"),

    FAIL(19999, "操作失败"),

    RESOURCE_NOT_READY(10001, "资源未就绪"),

    RESOURCE_PROCESSING(10002, "资源审批中"),

    RESOURCE_NOT_EXISTS(10010, "资源不存在"),

    ES_OPERATE_ERROR(10003, "es操作失败"),

    DUPLICATION(10004, "数据已存在"),

    OPERATE_FORBIDDEN_ERROR(10005, "无权限"),

    HTTP_REQ_ERROR(10009, "第三方http请求异常");

    private Integer    code;
    private String message;

    ResultTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
