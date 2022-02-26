package com.didichuxing.datachannel.system.metrcis.service;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;

import java.util.Map;

/**
 * disk/io 相关指标服务
 * @author william.
 */
public interface DiskIOMetricsService {

    /**
     * @return 返回当前磁盘I/O请求的时间百分比
     *  key：device value：io util
     */
    Map<String, PeriodStatistics> getIOUtil();

}
