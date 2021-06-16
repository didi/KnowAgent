package com.didichuxing.datachannel.agentmanager.common.bean.common;

import com.didichuxing.datachannel.agentmanager.common.enumeration.ResultTypeEnum;

/**
 * @author huqidong
 * @date 2020-09-21
 * 表示针对某（组）参数或某个对象的检查结果
 */
public class CheckResult {

    /**
     * 表示检查结果
     * true：检查通过 false：检查不通过
     */
    private boolean checkResult;
    /**
     * 检查不通过时，对应错误状态码
     */
    private int code;
    /**
     * 检查结果信息
     */
    private String message;

    public CheckResult(boolean checkResult, int code, String message) {
        this.checkResult = checkResult;
        this.code = code;
        this.message = message;
    }

    public CheckResult(boolean checkResult) {
        this.checkResult = checkResult;
        this.code = ResultTypeEnum.SUCCESS.getCode();
        this.message = "";
    }

    public boolean getCheckResult() {
        return checkResult;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "checkResult=" + checkResult +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}
