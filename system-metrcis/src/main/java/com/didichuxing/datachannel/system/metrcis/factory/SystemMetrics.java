package com.didichuxing.datachannel.system.metrcis.factory;

import java.util.Map;

/**
 * 系统级指标
 * @author william.hu
 */
public interface SystemMetrics {

    /**
     * @return 返回系统时间偏移量
     */
    long getSystemNtpOffset();

    /**
     * @return 返回系统进程个数
     */
    int getSystemProcCount();

    /**
     * @return 返回系统启动时间
     */
    long getSystemStartupTime();

    /**
     * @return 返回系统运行时间
     */
    long getSystemUptime();

    /*********************** about cpu ***********************/

    /**
     * @return 返回系统总体CPU使用率(单位：%)
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    double getSystemCpuUtil();

    /**
     * @return 返回系统总体CPU使用率(单位：%)
     * 注意：使用率为总使用比率，如jvm进程使用一颗核，系统共10核，则返回0.1 = 10%
     */
    double getSystemCpuUtilTotalPercent();

    /**
     * @return 返回cpu上下文交换次数
     */
    long getSystemCpuSwitches();

    /**
     * @return 返回虚拟处理器CPU时间占比(单位：%)
     */
    double getSystemCpuGuest();

    /**
     * @return 返回总总体cpu空闲率（单位：%）
     */
    double getSystemCpuIdle();

    /**
     * @return 返回等待I/O的CPU时间占比(单位：%)
     */
    double getSystemCpuIOWait();

    /**
     * @return 返回系统对应CPU核心数
     */
    int getSystemCpuNumCores();

    /**
     * @return 返回等待处理其他虚拟核的时间占比(单位：%)
     */
    double getSystemCpuSteal();

    /**
     * @return 返回内核态CPU时间占比(单位：%)
     */
    double getSystemCpuSystem();

    /**
     * @return 返回用户态CPU时间占比(单位：%)
     */
    double getSystemCpuUser();

    /*********************** about disk ***********************/
    /**
     * @return 返回磁盘各分区余量大小（单位：byte）
     */
    Map<String, Long> getSystemDiskBytesFree();

    /**
     * @return 返回磁盘各分区用量占比（单位：%）
     */
    Map<String, Double> getSystemDiskUsedPercent();

    /**
     * @return 返回各设备读操作耗时(单位：ms)
     */
    Map<String, Long> getSystemDiskReadTime();

    /**
     * @return 返回读取磁盘时间百分比（单位：%）
     */
    double getSystemDiskReadTimePercent();

    /**
     * @return 返回磁盘各分区总量（单位：byte）
     */
    Map<String, Long> getSystemDiskBytesTotal();

    /**
     * @return 返回磁盘各分区用量大小（单位：byte）
     */
    Map<String, Long> getSystemDiskBytesUsed();

    /**
     * @return 返回各设备写操作耗时(单位：ms)
     */
    Map<String, Long> getSystemDiskWriteTime();

    /**
     * @return 返回写入磁盘时间百分比（单位：%）
     */
    double getSystemDiskWriteTimePercent();

    /**
     * @return 返回系统已分配文件句柄数
     */
    int getSystemFilesAllocated();

    /**
     * @return 返回系统未分配文件句柄数
     */
    int getSystemFilesLeft();

    /**
     * @return 返回系统使用文件句柄占已分配百分比（单位：%）
     */
    double getSystemFilesUsedPercent();

    /**
     * @return 返回系统可以打开的最大文件句柄数
     */
    int getSystemFilesMax();

    /**
     * @return 返回系统使用的已分配文件句柄数
     */
    int getSystemFilesUsed();

    /**
     * @return 返回系统未使用的已分配文件句柄数
     */
    int getSystemFilesNotUsed();

    /**
     * @return 返回系统各分区空闲inode数量
     */
    Map<String, Long> getSystemDiskInodesFree();

    /**
     * @return 返回系统各分区已用inode占比（单位：%）
     */
    Map<String, Double> getSystemDiskInodesUsedPercent();

    /**
     * @return 返回系统各分区inode总数量
     */
    Map<String, Long> getSystemDiskInodesTotal();

    /**
     * @return 返回系统各分区已用inode数量
     */
    Map<String, Long> getSystemDiskInodesUsed();

    /*********************** about io ***********************/

    /**
     * @return 返回各设备平均队列长度
     */
    Map<String, Long> getSystemIOAvgQuSz();

    /**
     * @return 返回各设备平均请求大小
     */
    Map<String, Long> getSystemIOAvgRqSz();

    /**
     * @return 返回各设备每次IO平均处理时间（单位：ms）
     */
    Map<String, Long> getSystemIOAwait();

    /**
     * @return 返回各设备读请求平均耗时(单位：ms)
     */
    Map<String, Long> getSystemIORAwait();

    /**
     * @return 返回各设备每秒读请求数量
     */
    Map<String, Long> getSystemIOReadRequest();

    /**
     * @return 返回各设备每秒读取字节数
     */
    Map<String, Long> getSystemIOReadBytes();

    /**
     * @return 返回各设备每秒合并到设备队列的读请求数
     */
    Map<String, Long> getSystemIORRQMS();

    /**
     * @return 每次各设备IO平均服务时间（单位：ms）
     * 注：仅 做参考
     */
    Map<String, Long> getSystemIOSVCTM();

    /**
     * @return 返回各设备I/O请求的CPU时间百分比
     */
    Map<String, Double> getSystemIOUtil();

    /**
     * @return 返回各设备写请求平均耗时(单位：ms)
     */
    Map<String, Long> getSystemIOWAwait();

    /**
     * @return 返回各设备每秒写请求数量
     */
    Map<String, Long> getSystemIOWriteRequest();

    /**
     * @return 返回各设备每秒写字节数
     */
    Map<String, Long> getSystemIOWriteBytes();

    /**
     * @return 返回各设备每秒合并到设备队列的写请求数
     */
    Map<String, Long> getSystemIOWRQMS();

    /**
     * @return 返回系统近1分钟平均负载
     */
    double getSystemLoad1();

    /**
     * @return 返回系统近5分钟平均负载
     */
    double getSystemLoad5();

    /**
     * @return 返回系统近15分钟平均负载
     */
    double getSystemLoad15();

    /*********************** about memory ***********************/

    /**
     * @return 返回系统文件缓冲区的物理RAM量（单位：byte）
     */
    long getSystemMemBuffered();

    /**
     * @return 返回缓存内存的物理RAM量（单位：byte）
     */
    long getSystemMemCached();

    /**
     * @return 返回系统当前可分配的内存总量（单位：byte）
     */
    long getSystemMemCommitLimit();

    /**
     * @return 返回在磁盘分页文件上保留的物理内存量（单位：byte）
     */
    long getSystemMemCommitted();

    /**
     * @return 返回系统已分配的包括进程未使用的内存量（单位：byte）
     */
    long getSystemMemCommittedAs();

    /**
     * @return 返回不能写入磁盘的物理内存量（单位：byte）
     */
    long getSystemMemNonPaged();

    /**
     * @return 返回没被使用是可以写入磁盘的物理内存量（单位：byte）
     */
    long getSystemMemPaged();

    /**
     * @return 返回系统内存空闲率
     */
    double getSystemMemFreePercent();

    /**
     * @return 返回系统内存使用率
     */
    double getSystemMemUsedPercent();

    /**
     * @return 返回用作共享内存的物理RAM量（单位：byte）
     */
    long getSystemMemShared();

    /**
     * @return 返回内核用来缓存数据结构供自己使用的内存量（单位：byte）
     */
    long getSystemMemSlab();

    /**
     * @return 返回系统物理内存总量（单位：byte）
     */
    long getSystemMemTotal();

    /**
     * @return 返回系统空闲内存大小（单位：byte）
     */
    long getSystemMemFree();

    /**
     * @return 返回系统已用内存大小（单位：byte）
     */
    long getSystemMemUsed();

    /**
     * @return 返回系统用作缓存的交换空间
     */
    long getSystemSwapCached();

    /**
     * @return 返回系统空闲swap大小（单位：byte）
     */
    long getSystemSwapFree();

    /**
     * @return 返回系统空闲swap占比
     */
    double getSystemSwapFreePercent();

    /**
     * @return 返回系统swap总大小（单位：byte）
     */
    long getSystemSwapTotal();

    /**
     * @return 返回系统已用swap大小（单位：byte）
     */
    long getSystemSwapUsed();

    /**
     * @return 返回系统已用swap占比（单位：%）
     */
    double getSystemSwapUsedPercent();

    /*********************** about network ***********************/

    /**
     * @return 返回系统网络每秒下行流量
     */
    long getSystemNetworkReceiveBytesPs();

    /**
     * @return 返回系统网络每秒上行流量
     */
    long getSystemNetworkSendBytesPs();

    /**
     * @return 返回系统tcp连接数
     */
    int getSystemNetworkTcpConnectionNum();

    /**
     * @return 返回系统处于 time wait 状态 tcp 连接数
     */
    int getSystemNetworkTcpTimeWaitNum();

    /**
     * @return 返回系统处于 close wait 状态 tcp 连接数
     */
    int getSystemNetworkTcpCloseWaitNum();

    /**
     * @return 返回系统启动以来 Tcp 主动连接次数
     */
    long getSystemNetworkTcpActiveOpens();

    /**
     * @return 返回系统启动以来 Tcp 被动连接次数
     */
    long getSystemNetworkTcpPassiveOpens();

    /**
     * @return 返回系统启动以来 Tcp 连接失败次数
     */
    long getSystemNetworkTcpAttemptFails();

    /**
     * @return 返回系统启动以来 Tcp 连接异常断开次数
     */
    long getSystemNetworkTcpEstabResets();

    /**
     * @return 返回系统启动以来 Tcp 重传的报文段总个数
     */
    long getSystemNetworkTcpRetransSegs();

    /**
     * @return 返回系统启动以来 Tcp 监听队列溢出次数
     */
    long getSystemNetworkTcpExtListenOverflows();

    /**
     * @return 返回系统启动以来 UDP 入包量
     */
    long getSystemNetworkUdpInDatagrams();

    /**
     * @return 返回系统启动以来 UDP 出包量
     */
    long getSystemNetworkUdpOutDatagrams();

    /**
     * @return 返回系统启动以来 UDP 入包错误数
     */
    long getSystemNetworkUdpInErrors();

    /**
     * @return 返回系统启动以来 UDP 端口不可达个数
     */
    long getSystemNetworkUdpNoPorts();

    /**
     * @return 返回系统启动以来 UDP 发送缓冲区满次数
     */
    long getSystemNetworkUdpSendBufferErrors();

}
