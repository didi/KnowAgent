package com.didichuxing.datachannel.system.metrcis.service;

import java.util.Map;

/**
 * disk 相关指标服务
 * @author william.
 */
public interface DiskMetricsService {

    /**
     * @return 返回系统各磁盘分区对应文件系统信息
     *  key：path value：fsType
     */
    Map<String, String> getFsType();

    /**
     * @return 返回系统各磁盘分区总量（单位：byte）
     *  key：path value：bytesTotal
     */
    Map<String, Long> getBytesTotal();

    /**
     * @return 返回系统各磁盘分区余量大小（单位：byte）
     *  key：path value：bytesFree
     */
    Map<String, Long> getBytesFree();

    /**
     * @return 返回系统各磁盘分区用量大小（单位：byte）
     *  key：path value：bytesUsed
     */
    Map<String, Long> getBytesUsed();

    /**
     * @return 返回系统各磁盘分区用量占比（单位：%）
     *  key：path value：bytesUsedPercent
     */
    Map<String, Double> getBytesUsedPercent();

    /**
     * @return 返回系统各磁盘分区inode总数量
     *  key：path value：inodesTotal
     */
    Map<String, Integer> getInodesTotal();

    /**
     * @return 返回系统各磁盘分区空闲inode数量
     *  key：path value：inodesFree
     */
    Map<String, Integer> getInodesFree();

    /**
     * @return 返回系统各磁盘分区已用inode数量
     *  key：path value：inodesUsed
     */
    Map<String, Integer> getInodesUsed();

    /**
     * @return 返回系统各磁盘分区已用inode占比
     *  key：path value：inodesUsedPercent
     */
    Map<String, Double> getInodesUsedPercent();

}
