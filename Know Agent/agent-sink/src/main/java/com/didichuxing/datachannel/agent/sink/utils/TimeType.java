package com.didichuxing.datachannel.agent.sink.utils;

/**
 * @description:
 * @author: huangjw
 * @Date: 2019-09-06 18:51
 */
public enum TimeType {
    YMDH(0, "\\d{4}\\d{2}\\d{2}\\d{2}", "yyyyMMddHH"), // 按小时1
    Y_M_D_H(1, "\\d{4}-\\d{2}-\\d{2}-\\d{2}", "yyyy-MM-dd-HH"), // 按小时2
    YMD(2, "\\d{4}\\d{2}\\d{2}", "yyyyMMdd"), // 按天1
    Y_M_D(3, "\\d{4}-\\d{2}-\\d{2}", "yyyy-MM-dd"); // 按天2

    private int    status;

    private String matchSample;

    private String format;

    private TimeType(int status, String matchSample, String format) {
        this.status = status;
        this.matchSample = matchSample;
        this.format = format;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMatchSample() {
        return matchSample;
    }

    public void setMatchSample(String matchSample) {
        this.matchSample = matchSample;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
