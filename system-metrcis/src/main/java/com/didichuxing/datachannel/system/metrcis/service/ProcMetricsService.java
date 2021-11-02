package com.didichuxing.datachannel.system.metrcis.service;

import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;

import java.util.List;

/**
 * 进程级指标
 * @author william.hu
 */
public interface ProcMetricsService {

    /**
     * @return 返回当前Jvm进程启动时间
     */
    long getProcStartupTime();

    /**
     * @return 返回当前Jvm进程运行时间
     */
    long getProcUptime();

    /**
     * @return 返回当前Jvm进程对应pid
     */
    long getProcPid();

    /*********************** about cpu ***********************/

    /**
     *
     * @return 返回当前进程系统态cpu使用率(单位：%)
     */
    double getProcCpuSys();

    /**
     * @return 返回当前进程cpu每秒上下文交换次数
     */
    long getProcCpuSwitchesPS();

    /**
     * @return 返回当前进程cpu每秒自愿上下文交换次数（自愿上下文切换，是指进程无法获取所需资源，导致的上下文切换。比如说， I/O、内存等系统资源不足时，就会发生自愿上下文切换 pidstat）
     */
    long getProcCpuVoluntarySwitchesPS();

    /**
     * @return 返回当前进程cpu每秒非自愿上下文交换次数（非自愿上下文切换，则是指进程由于时间片已到等原因，被系统强制调度，进而发生的上下文切换。比如说，大量进程都在争抢 CPU 时，就容易发生非自愿上下文切换 pidstat）
     */
    long getProcCpuNonVoluntarySwitchesPS();

    /**
     * @return 返回当前进程cpu使用率(单位：%)
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    float getProcCpuUtil();

    /**
     * @return 返回当前进程cpu使用率(单位：%)
     * 注意：使用率为总使用比率，如jvm进程使用一颗核，系统共10核，则返回0.1 = 10%
     */
    float getProcCpuUtilTotalPercent();

    /**
     * @return 返回当前进程用户态cpu使用率(单位：%)
     */
    double getProcCpuUser();

    /*********************** about io ***********************/

    /**
     * @return 返回当前进程io读取频率(单位：hz)
     */
    double getProcIOReadRate();

    /**
     * @return 返回当前进程io读取速率(单位：b/s)
     */
    long getProcIOReadBytesRate();

    /**
     * @return 返回当前进程io写入频率(单位：hz)
     */
    double getProcIOWriteRate();

    /**
     * @return 返回当前进程io写入速率(单位：b/s)
     */
    long getProcIOWriteBytesRate();

    /**
     * @return 返回当前进程io读写等待时间占总时间百分比（单位：%） 对应 iotop IO
     */
    double getProcIOAwaitTimePercent();

    /*********************** about memory ***********************/

    /**
     * @return 返回当前进程data内存大小
     */
    long getProcMemData();

    /**
     * @return 返回当前进程dirty内存大小
     */
    long getProcMemDirty();

    /**
     * @return 返回当前进程lib内存大小
     */
    long getProcMemLib();

    /**
     * @return 返回当前进程常驻内存大小
     */
    long getProcMemRss();

    /**
     * @return 返回当前进程共享内存大小
     */
    long getProcMemShared();

    /**
     * @return 返回当前进程交换空间大小
     */
    long getProcMemSwap();

    /**
     * @return 返回当前进程Text内存大小
     */
    long getProcMemText();

    /**
     * @return 返回当前进程内存使用量（单位：byte）
     */
    long getProcMemUsed();

    /**
     * @return 返回当前进程内存使用率(单位：%)
     */
    double getProcMemUtil();

    /**
     * @return 返回当前进程虚拟内存大小
     */
    long getProcMemVms();

    /**
     * @return 返回当前jvm进程堆内存使用量（单位：byte）
     */
    long getJvmProcHeapMemoryUsed();

    /**
     * @return 返回当前jvm进程堆外内存使用量（单位：byte）
     */
    long getJvmProcNonHeapMemoryUsed();

    /**
     * @return 返回当前jvm进程最大可用堆内存，对应 jvm Xmx（单位：byte）
     */
    long getJvmProcHeapSizeXmx();

    /**
     * @return 返回当前jvm进程启动以来内存使用量峰值（单位：byte）
     */
    long getJvmProcMemUsedPeak();

    /*********************** about gc ***********************/

    /**
     * @return 返回当前jvm进程启动以来 young gc 次数
     */
    long getJvmProcYoungGcCount();

    /**
     * @return 返回当前jvm进程启动以来 full gc 次数
     */
    long getJvmProcFullGcCount();

    /**
     * @return 返回当前jvm进程启动以来 young gc 耗时 单位：ms
     */
    long getJvmProcYoungGcTime();

    /**
     * @return 返回当前jvm进程启动以来 full gc 耗时 单位：ms
     */
    long getJvmProcFullGcTime();

    /*********************** about thread ***********************/

    /**
     * @return 返回当前jvm进程中线程数
     */
    int getJvmProcThreadNum();

    /**
     * @return 返回当前jvm进程启动以来线程数峰值
     */
    int getJvmProcThreadNumPeak();

    /**
     * @return 返回当前jvm进程正常采集线程池可容纳的最大线程数
     */
    int getJvmProcNormalSourceThreadPoolMaxThreadNum();

    /**
     * @return 返回当前jvm进程正常采集线程池实际线程数
     */
    int getJvmProcNormalSourceThreadPoolThreadNum();

    /**
     * @return 返回当前jvm进程正常采集线程池任务队列最大容量
     */
    int getJvmProcNormalThreadPoolMaxQueueSize();

    /**
     * @return 返回当前jvm进程正常采集线程池任务队列实际数量
     */
    int getJvmProcNormalThreadPoolQueueSize();

    /**
     * @return 返回当前jvm进程临时采集线程池可容纳的最大线程数
     */
    int getJvmProcTempSourceThreadPoolMaxThreadNum();

    /**
     * @return 返回当前jvm进程临时采集线程池实际线程数
     */
    int getJvmProcTempSourceThreadPoolThreadNum();

    /**
     * @return 返回当前jvm进程临时采集线程池任务队列最大容量
     */
    int getJvmProcTempThreadPoolMaxQueueSize();

    /**
     * @return 返回当前jvm进程临时采集线程池任务队列实际数量
     */
    int getJvmProcTempThreadPoolQueueSize();

    /*********************** about fd ***********************/

    /**
     * @return 返回当前Jvm进程打开fd数量
     */
    int getProcOpenFdCount();

    /*********************** about network ***********************/

    /**
     * @return 返回当前Jvm进程监听端口
     */
    List<Integer> getProcPortListen();

    /**
     * @return 返回当前Jvm进程网络每秒下行流量
     */
    long getProcNetworkReceiveBytesPs();

    /**
     * @return 返回当前Jvm进程网络每秒上行流量
     */
    long getProcNetworkSendBytesPs();

    /**
     * @return 返回当前Jvm进程当前tcp连接数
     */
    int getProcNetworkTcpConnectionNum();

    /**
     * @return 返回当前Jvm进程当前处于 time wait 状态 tcp 连接数
     */
    int getProcNetworkTcpTimeWaitNum();

    /**
     * @return 返回当前Jvm进程当前处于 close wait 状态 tcp 连接数
     */
    int getProcNetworkTcpCloseWaitNum();

    /**
     * @return 返回当前进程指标集 如须获取全量当前进程指标，请调用该方法而非挨个调用各指标获取函数以提升其性能、降低消耗
     */
    ProcMetrics getProcMetrics();

}
