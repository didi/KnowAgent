package com.didichuxing.datachannel.system.metrcis.bean;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;

/**
 * 网卡信息
 * @author william.
 */
public class NetCardInfo {

    /**
     * 最大带宽（单位：byte）
     */
    private Long bandwidthByte;

    /**
     * 每秒下行流量（单位：字节）
     */
    private PeriodStatistics<Long> receiveBytesPs;

    /**
     * 每秒上行流量（单位：字节）
     */
    private PeriodStatistics<Long> sendBytesPs;

    public Long getBandwidthByte() {
        return bandwidthByte;
    }

    public void setBandwidthByte(Long bandwidthByte) {
        this.bandwidthByte = bandwidthByte;
    }

    public PeriodStatistics<Long> getReceiveBytesPs() {
        return receiveBytesPs;
    }

    public void setReceiveBytesPs(PeriodStatistics<Long> receiveBytesPs) {
        this.receiveBytesPs = receiveBytesPs;
    }

    public PeriodStatistics<Long> getSendBytesPs() {
        return sendBytesPs;
    }

    public void setSendBytesPs(PeriodStatistics<Long> sendBytesPs) {
        this.sendBytesPs = sendBytesPs;
    }

}
