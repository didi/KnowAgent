package com.didichuxing.datachannel.system.metrcis.exception;

public class MetricsException extends RuntimeException {

    /**
     * 服务层异常对象对应的状态码枚举对象
     */
    private Integer code;

    public MetricsException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    public MetricsException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
