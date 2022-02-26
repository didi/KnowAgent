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
     * @return 返回系统各磁盘分区余量大小（单位：byte）
     *  key：path value：bytesFree
     */
    Map<String, Long> getBytesFree();

}
