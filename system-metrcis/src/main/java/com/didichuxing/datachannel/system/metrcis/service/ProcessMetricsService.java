package com.didichuxing.datachannel.system.metrcis.service;

import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;

import java.util.List;

/**
 * 进程级指标
 * @author william.hu
 */
public interface ProcessMetricsService {

    /*********************** 总览 ***********************/

    /**
     * @return 返回当前Jvm进程启动时间
     */
    Long getProcessStartupTime();

    /**
     * @return 返回当前Jvm进程运行时间
     */
    Long getProcUptime();

    /**
     * @return 返回当前Jvm进程对应pid
     */
    Long getProcessPid();

    /*********************** about cpu ***********************/

    /**
     * @return 返回当前进程cpu使用率(单位：%)
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    PeriodStatistics getProcCpuUtil();

    /**
     * @return 返回当前进程cpu使用率(单位：%)
     * 注意：使用率为总使用比率，如jvm进程使用一颗核，系统共10核，则返回0.1 = 10%
     */
    PeriodStatistics getProcCpuUtilTotalPercent();

    /**
     * @return 返回当前进程cpu使用率(单位：%)
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    Double getCurrentProcCpuUtil();

    /**
     * @return 返回当前进程系统态cpu使用率(单位：%)
     */
    PeriodStatistics getProcCpuSys();

    /**
     * @return 返回当前进程用户态cpu使用率(单位：%)
     */
    PeriodStatistics getProcCpuUser();

    /**
     * @return 返回当前进程cpu每秒上下文交换次数
     */
    PeriodStatistics getProcCpuSwitchesPS();

    /**
     * @return 返回当前进程cpu每秒自愿上下文交换次数（自愿上下文切换，是指进程无法获取所需资源，导致的上下文切换。比如说， I/O、内存等系统资源不足时，就会发生自愿上下文切换 pidstat）
     */
    PeriodStatistics getProcCpuVoluntarySwitchesPS();

    /**
     * @return 返回当前进程cpu每秒非自愿上下文交换次数（非自愿上下文切换，则是指进程由于时间片已到等原因，被系统强制调度，进而发生的上下文切换。比如说，大量进程都在争抢 CPU 时，就容易发生非自愿上下文切换 pidstat）
     */
    PeriodStatistics getProcCpuNonVoluntarySwitchesPS();

    /*********************** about memory ***********************/

    /**
     * @return 返回当前进程内存使用量（单位：byte）
     */
    Long getProcMemUsed();

    /**
     * @return 返回当前进程内存使用率(单位：%)
     */
    Double getProcMemUtil();

    /**
     * @return 返回当前进程data内存大小
     */
    Long getProcMemData();

    /**
     * @return 返回当前进程dirty内存大小
     */
    Long getProcMemDirty();

    /**
     * @return 返回当前进程lib内存大小
     */
    Long getProcMemLib();

    /**
     * @return 返回当前进程常驻内存大小
     */
    Long getProcMemRss();

    /**
     * @return 返回当前进程共享内存大小
     */
    Long getProcMemShared();

    /**
     * @return 返回当前进程交换空间大小
     */
    Long getProcMemSwap();

    /**
     * @return 返回当前进程Text内存大小
     */
    Long getProcMemText();

    /**
     * @return 返回当前进程虚拟内存大小
     */
    Long getProcMemVms();

    /**
     * @return 返回当前jvm进程堆内存使用量（单位：byte）
     */
    Long getJvmProcHeapMemoryUsed();

    /**
     * @return 返回当前jvm进程堆外内存使用量（单位：byte）
     */
    Long getJvmProcNonHeapMemoryUsed();

    /**
     * @return 返回当前jvm进程最大可用堆内存，对应 jvm Xmx（单位：byte）
     */
    Long getJvmProcHeapSizeXmx();

    /**
     * @return 返回当前jvm进程启动以来内存使用量峰值（单位：byte）
     */
    Long getJvmProcMemUsedPeak();

    /**
     * @return 返回jvm堆内存使用率（单位：%）
     */
    Double getJvmProcHeapMemUsedPercent();

    /*********************** about disk io ***********************/

    /**
     * @return 返回当前进程io读取频率(单位：hz)
     */
    PeriodStatistics getProcIOReadRate();

    /**
     * @return 返回当前进程io读取速率(单位：b/s)
     */
    PeriodStatistics getProcIOReadBytesRate();

    /**
     * @return 返回当前进程io写入频率(单位：hz)
     */
    PeriodStatistics getProcIOWriteRate();

    /**
     * @return 返回当前进程io写入速率(单位：b/s)
     */
    PeriodStatistics getProcIOWriteBytesRate();

    /**
     * @return 返回当前进程io读、写频率（单位：hz）
     */
    PeriodStatistics getProcIOReadWriteRate();

    /**
     * @return 返回当前进程io读、写速率（单位：bytes/s）
     */
    PeriodStatistics getProcIOReadWriteBytesRate();

    /**
     * @return 返回当前进程io读写等待时间占总时间百分比（单位：%） 对应 iotop IO
     */
    PeriodStatistics getProcIOAwaitTimePercent();

    /*********************** about gc ***********************/

    /**
     * @return 返回当前jvm进程启动以来 young gc 次数
     */
    Long getJvmProcYoungGcCount();

    /**
     * @return 返回当前jvm进程启动以来 full gc 次数
     */
    Long getJvmProcFullGcCount();

    /**
     * @return 返回当前jvm进程启动以来 young gc 耗时 单位：ms
     */
    Long getJvmProcYoungGcTime();

    /**
     * @return 返回当前jvm进程启动以来 full gc 耗时 单位：ms
     */
    Long getJvmProcFullGcTime();

    /**
     * @return 返回 jvm 第一个幸存区大小
     */
    Double getJvmProcS0C();

    /**
     * @return 返回 jvm 第二个幸存区大小
     */
    Double getJvmProcS1C();

    /**
     * @return 返回 jvm 第一个幸存区使用大小
     */
    Double getJvmProcS0U();

    /**
     * @return 返回 jvm 第二个幸存区使用大小
     */
    Double getJvmProcS1U();

    /**
     * @return 返回 jvm Eden 区大小
     */
    Double getJvmProcEC();

    /**
     * @return 返回 jvm Eden 区使用大小
     */
    Double getJvmProcEU();

    /**
     * @return 返回 jvm 老年代大小
     */
    Double getJvmProcOC();

    /**
     * @return 返回 jvm 老年代使用大小
     */
    Double getJvmProcOU();

    /**
     * @return 返回 jvm 方法区大小
     */
    Double getJvmProcMC();

    /**
     * @return 返回 jvm 方法区使用大小
     */
    Double getJvmProcMU();

    /**
     * @return 返回 jvm 压缩类空间大小
     */
    Double getJvmProcCCSC();

    /**
     * @return 返回 jvm 压缩类空间使用大小
     */
    Double getJvmProcCCSU();


    /*********************** about thread ***********************/

    /**
     * @return 返回当前jvm进程中线程数
     */
    Integer getJvmProcThreadNum();

    /**
     * @return 返回当前jvm进程启动以来线程数峰值
     */
    Integer getJvmProcThreadNumPeak();

    /*********************** about fd ***********************/

    /**
     * @return 返回当前进程打开fd数量
     */
    Integer getProcOpenFdCount();

    /*********************** about network ***********************/

    /**
     * @return 返回当前进程监听端口列表
     */
    List<Integer> getProcPortListen();

    /**
     * @return 返回当前进程网络每秒下行流量
     */
    PeriodStatistics getProcNetworkReceiveBytesPs();

    /**
     * @return 返回当前进程网络每秒上行流量
     */
    PeriodStatistics getProcNetworkSendBytesPs();

    /**
     * @return 返回当前进程网络连接频率(单位：hz)
     */
    PeriodStatistics getProcNetworkConnRate();

    /**
     * @return 返回当前进程当前tcp连接数
     */
    Integer getProcNetworkTcpConnectionNum();

    /**
     * @return 返回当前进程处于 Listening 状态的 tcp 链接数
     */
    Integer getProcNetworkTcpListeningNum();

    /**
     * @return 返回当前进程当前处于 time wait 状态 tcp 连接数
     */
    Integer getProcNetworkTcpTimeWaitNum();

    /**
     * @return 返回当前进程当前处于 close wait 状态 tcp 连接数
     */
    Integer getProcNetworkTcpCloseWaitNum();

    /**
     * @return 返回当前进程处于 ESTABLISHED 状态的 tcp 链接数
     */
    Integer getProcNetworkTcpEstablishedNum();

    /**
     * @return 返回当前进程处于 SYN_SENT 状态的 tcp 链接数
     */
    Integer getProcNetworkTcpSynSentNum();

    /**
     * @return 返回当前进程处于 SYN_RECV 状态的 tcp 链接数
     */
    Integer getProcNetworkTcpSynRecvNum();

    /**
     * @return 返回当前进程处于 FIN_WAIT1 状态的 tcp 链接数
     */
    Integer getProcNetworkTcpFinWait1Num();

    /**
     * @return 返回当前进程处于 FIN_WAIT2 状态的 tcp 链接数
     */
    Integer getProcNetworkTcpFinWait2Num();

    /**
     * @return 返回当前进程处于 closed 状态 tcp 连接数
     */
    Integer getProcNetworkTcpClosedNum();

    /**
     * @return 返回当前进程处于 closing 状态 tcp 连接数
     */
    Integer getProcNetworkTcpClosingNum();

    /**
     * @return 返回当前进程处于 last ack 状态 tcp 连接数
     */
    Integer getProcNetworkTcpLastAckNum();

    /**
     * @return 返回当前进程处于 none 状态 tcp 连接数
     */
    Integer getProcNetworkTcpNoneNum();

}
