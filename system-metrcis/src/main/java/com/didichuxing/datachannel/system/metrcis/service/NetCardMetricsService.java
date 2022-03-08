package com.didichuxing.datachannel.system.metrcis.service;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;

import java.util.Map;

/**
 * net card 相关指标服务
 * @author william.
 */
public interface NetCardMetricsService {

    /**
     * @return 返回系统各网卡对应mac地址集
     *  key：net card device value：mac address
     */
    Map<String, String> getMacAddress();

    /**
     * @return 返回系统各网卡对应最大带宽（单位：byte）
     *  key：net card device value：网卡对应最大带宽（单位：byte）
     */
    Map<String, Long> getBandWidth();

    /**
     * @return 返回系统各网卡每秒下行流量（单位：字节）
     *  key：net card device value：网卡对应每秒下行流量（单位：字节）
     */
    Map<String, PeriodStatistics> getReceiveBytesPs();

    /**
     * @return 返回系统各网卡对应每秒上行流量（单位：字节）
     *  key：net card device value：网卡对应每秒上行流量（单位：字节）
     */
    Map<String, PeriodStatistics> getSendBytesPs();

}
