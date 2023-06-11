package com.didichuxing.datachannel.agentmanager.common.bean.common;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ErrorCodeEnum;
import com.didichuxing.datachannel.agentmanager.common.enumeration.ResultTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author huqidong
 * @date 2020-09-21
 * 返回结果
 */
@ApiModel(description = "返回结构")
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 3472961240718956029L;

    @ApiModelProperty("内容")
    private T                 data;

    @ApiModelProperty("异常信息")
    private String            message;

    @ApiModelProperty("提示")
    private String            tips;

    @ApiModelProperty("返回码，0表示成功；非0表示失败")
    private Integer           code;

    public Result() {
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public String getTips() {
        return tips;
    }

    public Integer getCode() {
        return code;
    }

    public boolean success() {
        return getCode() != null && ResultTypeEnum.SUCCESS.getCode() == getCode();
    }

    public boolean duplicate() {
        return getCode() != null && ResultTypeEnum.DUPLICATION.getCode() == getCode();
    }

    public boolean failed() {
        return !success();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static Result build(ResultTypeEnum resultType) {
        Result result = new Result();
        result.setCode(resultType.getCode());
        result.setMessage(resultType.getMessage());
        return result;
    }

    public static Result build(int code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }
    public static <T> Result<T> build(int code, String msg,T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public static Result buildSuccWithMsg(String msg) {
        Result result = new Result();
        result.setCode(ResultTypeEnum.SUCCESS.getCode());
        result.setMessage(msg);
        return result;
    }

    public static Result buildSucc() {
        Result result = new Result();
        result.setCode(ResultTypeEnum.SUCCESS.getCode());
        result.setMessage(ResultTypeEnum.SUCCESS.getMessage());
        return result;
    }

    public static Result buildSucWithTips(String tips) {
        Result result = new Result();
        result.setCode(ResultTypeEnum.SUCCESS.getCode());
        result.setMessage(ResultTypeEnum.SUCCESS.getMessage());
        result.setTips(tips);
        return result;
    }

    public static Result buildFail(String failMsg) {
        Result result = new Result();
        result.setCode(ResultTypeEnum.FAIL.getCode());
        result.setMessage(failMsg);
        return result;
    }

    public static Result buildFail() {
        Result result = new Result();
        result.setCode(ResultTypeEnum.FAIL.getCode());
        result.setMessage(ResultTypeEnum.FAIL.getMessage());
        return result;
    }

    public static Result build(boolean succ) {
        if (succ) {
            return buildSucc();
        }
        return buildFail();
    }

    public static Result buildWithTips(boolean succ, String tips) {
        if (succ) {
            return buildSucWithTips(tips);
        }
        return buildFail();
    }

    public static Result buildParamIllegal(String msg) {
        Result result = new Result();
        result.setCode(ErrorCodeEnum.ILLEGAL_PARAMS.getCode());
        result.setMessage(msg);
        return result;
    }

    public static Result buildDuplicate(String msg) {
        Result result = new Result();
        result.setCode(ResultTypeEnum.DUPLICATION.getCode());
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> build(boolean succ, T data) {
        Result<T> result = new Result<>();
        if (succ) {
            result.setCode(ResultTypeEnum.SUCCESS.getCode());
            result.setMessage(ResultTypeEnum.SUCCESS.getMessage());
            result.setData(data);
        } else {
            result.setCode(ResultTypeEnum.FAIL.getCode());
            result.setMessage(ResultTypeEnum.FAIL.getMessage());
        }
        return result;
    }

    public static <T> Result<T> buildSucc(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultTypeEnum.SUCCESS.getCode());
        result.setMessage(ResultTypeEnum.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> buildSucc(T data, String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultTypeEnum.SUCCESS.getCode());
        result.setMessage(msg);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> buildSuccWithTips(T data, String tips) {
        Result<T> result = new Result<>();
        result.setCode(ResultTypeEnum.SUCCESS.getCode());
        result.setMessage(ResultTypeEnum.SUCCESS.getMessage());
        result.setData(data);
        result.setTips(tips);
        return result;
    }

    public static <T> Result<T> buildFrom(Result result) {
        Result<T> resultT = new Result<>();
        resultT.setCode(result.getCode());
        resultT.setMessage(result.getMessage());
        return resultT;
    }

}
