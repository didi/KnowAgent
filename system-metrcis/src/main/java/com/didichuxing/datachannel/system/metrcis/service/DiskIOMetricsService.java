package com.didichuxing.datachannel.system.metrcis.service;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;

import java.util.Map;

/**
 * disk/io 相关指标服务
 * @author william.
 */
public interface DiskIOMetricsService {

    /**
     * @return 返回各磁盘平均队列长度
     *  key：device value：AvgQuSz
     */
    Map<String, PeriodStatistics> getAvgQuSz();

    /**
     * @return 返回各磁盘平均请求大小（单位：扇区）
     *  key：device value：AvgRqSz
     */
    Map<String, PeriodStatistics> getAvgRqSz();

    /**
     * @return 返回各磁盘每次IO请求平均处理时间（单位：ms）
     *  key：device value：IOAwait
     */
    Map<String, PeriodStatistics> getIOAwait();

    /**
     * @return 返回各磁盘读请求平均耗时(单位：ms)
     *  key：device value：IORAwait
     */
    Map<String, PeriodStatistics> getIORAwait();

    /**
     * @return 返回各磁盘每秒读请求数量
     *  key：device value：IOReadRequest
     */
    Map<String, PeriodStatistics> getIOReadRequest();

    /**
     * @return 返回各磁盘每秒读取字节数
     *  key：device value：IOReadBytes
     */
    Map<String, PeriodStatistics> getIOReadBytes();

    /**
     * @return 返回各磁盘每秒合并到设备队列的读请求数
     *  key：device value：IORRQMS
     */
    Map<String, PeriodStatistics> getIORRQMS();

    /**
     * @return 返回各磁盘每次IO平均服务时间（单位：ms）
     *  key：device value：IOSVCTM
     */
    Map<String, PeriodStatistics> getIOSVCTM();

    /**
     * @return 返回各磁盘I/O请求的时间百分比
     *  key：device value：io util
     */
    Map<String, PeriodStatistics> getIOUtil();

    /**
     * @return 返回各磁盘写请求平均耗时（单位：ms）
     *  key：device value：IOWAwait
     */
    Map<String, PeriodStatistics> getIOWAwait();

    /**
     * @return 返回各磁盘每秒写请求数量
     *  key：device value：IOWriteRequest
     */
    Map<String, PeriodStatistics> getIOWriteRequest();

    /**
     * @return 返回各磁盘每秒写字节数
     *  key：device value：IOWriteBytes
     */
    Map<String, PeriodStatistics> getIOWriteBytes();

    /**
     * @return 返回各磁盘每秒读、写字节数
     *  key：device value：IOReadWriteBytes
     */
    Map<String, PeriodStatistics> getIOReadWriteBytes();

    /**
     * @return 返回各磁盘每秒合并到设备队列的写请求数
     *  key：device value：IOWRQMS
     */
    Map<String, PeriodStatistics> getIOWRQMS();

    /**
     * @return 返回各磁盘读操作耗时(单位：ms)
     *  key：device value：DiskReadTime
     */
    Map<String, PeriodStatistics> getDiskReadTime();

    /**
     * @return 返回各磁盘读取磁盘时间百分比（单位：%）
     *  key：device value：DiskReadTimePercent
     */
    Map<String, PeriodStatistics> getDiskReadTimePercent();

    /**
     * @return 返回各磁盘写操作耗时（单位：ms）
     *  key：device value：DiskWriteTime
     */
    Map<String, PeriodStatistics> getDiskWriteTime();

    /**
     * @return 返回各磁盘写入磁盘时间百分比（单位：%）
     *  key：device value：DiskWriteTimePercent
     */
    Map<String, PeriodStatistics> getDiskWriteTimePercent();

}
