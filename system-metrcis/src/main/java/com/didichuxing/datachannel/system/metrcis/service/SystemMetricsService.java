package com.didichuxing.datachannel.system.metrcis.service;

import com.didichuxing.datachannel.system.metrcis.bean.*;

import java.util.List;
import java.util.Map;

/**
 * 系统级指标
 * @author william.hu
 */
public interface SystemMetricsService {

    /*********************** 总览 ***********************/

    /**
     * @return 返回操作系统类型
     */
    String getOsType();

    /**
     * @return 返回操作系统版本
     */
    String getOsVersion();

    /**
     * @return 操作系统内核版本
     */
    String getOsKernelVersion();

    /**
     * @return 返回主机名
     */
    String getHostName();

    /**
     * @return 返回 ip 地址列表（json 格式）
     */
    String getIps();

    /**
     * @return 返回源时钟与本地时钟的时间差（毫秒）
     */
    Long getSystemNtpOffset();

    /**
     * @return 返回系统启动时间
     */
    Long getSystemStartupTime();

    /**
     * @return 返回系统运行时间
     */
    Long getSystemUptime();

    /*********************** about process ***********************/

    /**
     * @return 返回当前不可中断的睡眠状态下的进程数
     */
    Integer getProcessesBlocked();

    /**
     * @return 返回当前可中断的睡眠状态下的进程数
     */
    Integer getProcessesSleeping();

    /**
     * @return 返回当前僵尸态进程数
     */
    Integer getProcessesZombies();

    /**
     * @return 返回当前暂停状态进程数
     */
    Integer getProcessesStopped();

    /**
     * @return 返回当前运行中的进程数
     */
    Integer getProcessesRunning();

    /**
     * @return 返回当前挂起的空闲进程数
     */
    Integer getProcessesIdle();

    /**
     * @return 返回当前等待中的进程数
     */
    Integer getProcessesWait();

    /**
     * @return 返回当前回收中的进程数
     */
    Integer getProcessesDead();

    /**
     * @return 返回当前分页进程数
     */
    Integer getProcessesPaging();

    /**
     * @return 返回当前未知状态进程数
     */
    Integer getProcessesUnknown();

    /**
     * @return 返回当前总进程数
     */
    Integer getProcessesTotal();

    /**
     * @return 返回当前总线程数
     */
    Integer getProcessesTotalThreads();

    /*********************** about cpu ***********************/

    /**
     * @return 返回系统CPU核心数
     */
    Integer getSystemCpuCores();

    /**
     * @return 返回系统总体CPU使用率(单位：%)
     * 注：使用率采用全核方式计数，如系统使用一颗核，则返回100，如使用两颗核，则返回200
     */
    PeriodStatistics getSystemCpuUtil();

    /**
     * @return 返回系统总体CPU使用率(单位：%)
     * 注意：使用率为总使用比率，系统使用一颗核，系统共10核，则返回0.1 = 10%
     */
    PeriodStatistics getSystemCpuUtilTotalPercent();

    /**
     * @return 返回内核态CPU时间占比(单位：%)
     */
    PeriodStatistics getSystemCpuSystem();

    /**
     * @return 返回用户态CPU时间占比(单位：%)
     */
    PeriodStatistics getSystemCpuUser();

    /**
     * @return 返回总体cpu空闲率（单位：%）
     */
    PeriodStatistics getSystemCpuIdle();

    /**
     * @return 返回cpu上下文交换次数
     */
    PeriodStatistics getSystemCpuSwitches();

    /**
     * @return 返回cpu处理硬中断的时间占比（单位：%）
     */
    PeriodStatistics getSystemCpuUsageIrq();

    /**
     * @return 返回cpu处理软中断的时间占比（单位：%），当前值、采样周期统计值
     */
    PeriodStatistics getSystemCpuUsageSoftIrq();

    /**
     * @return 返回系统近1分钟平均负载
     */
    PeriodStatistics getSystemLoad1();

    /**
     * @return 返回系统近5分钟平均负载
     */
    PeriodStatistics getSystemLoad5();

    /**
     * @return 返回系统近15分钟平均负载
     */
    PeriodStatistics getSystemLoad15();

    /**
     * @return 返回等待I/O的CPU时间占比(单位：%)
     */
    PeriodStatistics getSystemCpuIOWait();

    /**
     * @return 返回虚拟处理器CPU时间占比(单位：%)
     */
    PeriodStatistics getSystemCpuGuest();

    /**
     * @return 返回等待处理其他虚拟核的时间占比(单位：%)
     */
    PeriodStatistics getSystemCpuSteal();

    /*********************** about memory ***********************/

    /**
     * @return 返回系统当前可分配的内存总量（单位：byte）
     */
    Long getSystemMemCommitLimit();

    /**
     * @return 返回系统已分配的包括进程未使用的内存量（单位：byte）
     */
    Long getSystemMemCommittedAs();

    /**
     * @return 返回在磁盘分页文件上保留的物理内存量（单位：byte）
     */
    Long getSystemMemCommitted();

    /**
     * @return 返回不能写入磁盘的物理内存量（单位：byte）
     */
    Long getSystemMemNonPaged();

    /**
     * @return 返回没被使用是可以写入磁盘的物理内存量（单位：byte）
     */
    Long getSystemMemPaged();

    /**
     * @return 返回用作共享内存的物理RAM量（单位：byte）
     */
    Long getSystemMemShared();

    /**
     * @return 返回内核用来缓存数据结构供自己使用的内存量（单位：byte）
     */
    Long getSystemMemSlab();

    /**
     * @return 返回系统物理内存总量（单位：byte）
     */
    Long getSystemMemTotal();

    /**
     * @return 返回系统空闲内存大小（单位：byte）
     */
    Long getSystemMemFree();

    /**
     * @return 返回系统已用内存大小（单位：byte）
     */
    Long getSystemMemUsed();

    /**
     * @return 返回系统文件缓冲区的物理RAM量（单位：byte）
     */
    Long getSystemMemBuffered();

    /**
     * @return 返回缓存内存的物理RAM量（单位：byte）
     */
    Long getSystemMemCached();

    /**
     * @return 返回系统内存空闲率
     */
    Double getSystemMemFreePercent();

    /**
     * @return 返回系统内存使用率
     */
    Double getSystemMemUsedPercent();

    /**
     * @return 返回系统用作缓存的交换空间
     */
    Long getSystemSwapCached();

    /**
     * @return 返回系统空闲swap大小（单位：byte）
     */
    Long getSystemSwapFree();

    /**
     * @return 返回系统空闲swap占比
     */
    Double getSystemSwapFreePercent();

    /**
     * @return 返回系统swap总大小（单位：byte）
     */
    Long getSystemSwapTotal();

    /**
     * @return 返回系统已用swap大小（单位：byte）
     */
    Long getSystemSwapUsed();

    /**
     * @return 返回系统已用swap占比（单位：%）
     */
    Double getSystemSwapUsedPercent();

    /*********************** about disk、disk io ***********************/

    /**
     * @return 返回系统磁盘分区数
     */
    Integer getSystemDisks();

    /**
     * @return 返回系统各磁盘分区信息
     */
    List<DiskInfo> getSystemDiskInfoList();

    /**
     * @return 返回系统各磁盘 io 信息
     */
    List<DiskIOInfo> getSystemDiskIOInfoList();

    /*********************** about file handle ***********************/

    /**
     * @return 返回系统可以打开的最大文件句柄数
     */
    Integer getSystemFilesMax();

    /**
     * @return 返回系统已分配文件句柄数
     */
    Integer getSystemFilesAllocated();

    /**
     * @return 返回系统未分配文件句柄数
     */
    Integer getSystemFilesLeft();

    /**
     * @return 返回系统使用文件句柄占已分配百分比（单位：%）
     */
    Double getSystemFilesUsedPercent();

    /**
     * @return 返回系统使用的已分配文件句柄数
     */
    Integer getSystemFilesUsed();

    /**
     * @return 返回系统未使用的已分配文件句柄数
     */
    Integer getSystemFilesNotUsed();

    /*********************** about network ***********************/

    /**
     * @return 返回系统网卡数
     */
    Integer getSystemNetCards();

    /**
     * @return 返回系统网卡总带宽（单位：byte）
     */
    Double getSystemNetCardsBandWidth();

    /**
     * @return 返回系统各网卡信息
     */
    List<NetCardInfo> getSystemNetCardInfoList();

    /**
     * @return 返回系统网络每秒下行流量
     */
    PeriodStatistics getSystemNetworkReceiveBytesPs();

    /**
     * @return 返回系统网络每秒上行流量
     */
    PeriodStatistics getSystemNetworkSendBytesPs();

    /**
     * @return 返回系统网络每秒上、下行流量总流量
     */
    PeriodStatistics getSystemNetworkSendAndReceiveBytesPs();

    /**
     * @return 返回系统网络带宽使用率
     */
    PeriodStatistics getSystemNetWorkBandWidthUsedPercent();

    /**
     * @return 返回系统tcp连接数
     */
    Integer getSystemNetworkTcpConnectionNum();

    /**
     * @return 返回系统 Listening 状态的 tcp 链接数
     */
    Integer getSystemNetworkTcpListeningNum();

    /**
     * @return 返回系统处于 established 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpEstablishedNum();

    /**
     * @return 返回系统处于 SYN_SENT 状态的 tcp 链接数
     */
    Integer getSystemNetworkTcpSynSentNum();

    /**
     * @return 返回系统处于 SYN_RECV 状态的 tcp 链接数
     */
    Integer getSystemNetworkTcpSynRecvNum();

    /**
     * @return 返回系统处于 FIN_WAIT1 状态的 tcp 链接数
     */
    Integer getSystemNetworkTcpFinWait1Num();

    /**
     * @return 返回系统处于 FIN_WAIT2 状态的 tcp 链接数
     */
    Integer getSystemNetworkTcpFinWait2Num();

    /**
     * @return 返回系统处于 time wait 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpTimeWaitNum();

    /**
     * @return 返回系统处于 closed 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpClosedNum();

    /**
     * @return 返回系统处于 close wait 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpCloseWaitNum();

    /**
     * @return 返回系统处于 closing 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpClosingNum();

    /**
     * @return 返回系统处于 last ack 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpLastAckNum();

    /**
     * @return 返回系统处于 none 状态 tcp 连接数
     */
    Integer getSystemNetworkTcpNoneNum();

    /**
     * @return 返回系统启动以来 Tcp 主动连接次数
     */
    Long getSystemNetworkTcpActiveOpens();

    /**
     * @return 返回系统启动以来 Tcp 被动连接次数
     */
    Long getSystemNetworkTcpPassiveOpens();

    /**
     * @return 返回系统启动以来 Tcp 连接失败次数
     */
    Long getSystemNetworkTcpAttemptFails();

    /**
     * @return 返回系统启动以来 Tcp 连接异常断开次数
     */
    Long getSystemNetworkTcpEstabResets();

    /**
     * @return 返回系统启动以来 Tcp 重传的报文段总个数
     */
    Long getSystemNetworkTcpRetransSegs();

    /**
     * @return 返回系统启动以来 Tcp 监听队列溢出次数
     */
    Long getSystemNetworkTcpExtListenOverflows();

    /**
     * @return 返回系统启动以来 UDP 入包量
     */
    Long getSystemNetworkUdpInDatagrams();

    /**
     * @return 返回系统启动以来 UDP 出包量
     */
    Long getSystemNetworkUdpOutDatagrams();

    /**
     * @return 返回系统启动以来 UDP 入包错误数
     */
    Long getSystemNetworkUdpInErrors();

    /**
     * @return 返回系统启动以来 UDP 端口不可达个数
     */
    Long getSystemNetworkUdpNoPorts();

    /**
     * @return 返回系统启动以来 UDP 发送缓冲区满次数
     */
    Long getSystemNetworkUdpSendBufferErrors();

}
