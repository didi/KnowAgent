package com.didichuxing.datachannel.system.metrcis.bean;

import java.util.Map;

/**
 * 磁盘io信息
 * @author william.
 */
public class DiskIOInfo {

    /**
     * 返回当前磁盘对应平均队列长度
     */
    private PeriodStatistics<Double> iOAvgQuSz;

    /**
     * 返回当前磁盘平均请求大小（单位：扇区）
     */
    private PeriodStatistics<Double> iOAvgRqSz;

    /**
     * 返回当前磁盘每次IO请求平均处理时间（单位：ms）
     */
    private PeriodStatistics<Long> iOAwait;

    /**
     * 返回当前磁盘读请求平均耗时(单位：ms)
     */
    private PeriodStatistics<Long> iORAwait;

    /**
     * 返回当前磁盘每秒读请求数量
     */
    private PeriodStatistics<Long> iOReadRequest;

    /**
     * 返回当前磁盘每秒读取字节数
     */
    private PeriodStatistics<Long> iOReadBytes;

    /**
     * 返回当前磁盘每秒合并到设备队列的读请求数
     */
    private PeriodStatistics<Long> iORRQMS;

    /**
     * 返回当前磁盘每次IO平均服务时间（单位：ms）
     * 注：仅 做参考
     */
    private PeriodStatistics<Long> iOSVCTM;

    /**
     * 返回当前磁盘I/O请求的时间百分比
     */
    private PeriodStatistics<Double> iOUtil;

    /**
     * 返回当前磁盘写请求平均耗时(单位：ms)
     */
    private PeriodStatistics<Long> iOWAwait;

    /**
     * 返回当前磁盘每秒写请求数量
     */
    private PeriodStatistics<Long> iOWriteRequest;

    /**
     * 返回当前磁盘每秒写字节数
     */
    private PeriodStatistics<Long> iOWriteBytes;

    /**
     * 返回当前磁盘每秒读、写字节数
     */
    private PeriodStatistics<Long> iOReadWriteBytes;

    /**
     * 返回当前磁盘每秒合并到设备队列的写请求数
     */
    private PeriodStatistics<Long> iOWRQMS;

    /**
     * 磁盘读操作耗时(单位：ms)
     */
    private PeriodStatistics<Long> readTime;

    /**
     * 磁盘读取磁盘时间百分比（单位：%）
     */
    private PeriodStatistics<Double> readTimePercent;

    /**
     * 磁盘写操作耗时（单位：ms）
     */
    private PeriodStatistics<Long> writeTime;

    /**
     * 磁盘写入磁盘时间百分比（单位：%）
     */
    private PeriodStatistics<Double> writeTimePercent;

}
