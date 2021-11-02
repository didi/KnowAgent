package com.didichuxing.datachannel.system.metrcis.bean;

import java.util.Map;

public class SystemMetrics {

    /**
     * 系统时间偏移量
     */
    private long systemNtpOffset;

    /**
     * 返回系统进程个数
     */
    private int systemProcCount;

    /**
     * 系统启动时间
     */
    private long systemStartupTime;

    /**
     * 系统运行时间
     */
    private long systemUptime;

    /*********************** about cpu ***********************/

    /**
     * 系统总体CPU使用率(单位：%)
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    private double systemCpuUtil;

    /**
     * 系统总体CPU使用率(单位：%)
     * 注意：使用率为总使用比率，如jvm进程使用一颗核，系统共10核，则返回0.1 = 10%
     */
    private double systemCpuUtilTotalPercent;

    /**
     * cpu上下文交换次数
     */
    private long systemCpuSwitches;

    /**
     * 虚拟处理器CPU时间占比(单位：%)
     */
    private double systemCpuGuest;

    /**
     * 总体cpu空闲率（单位：%）
     */
    private double systemCpuIdle;

    /**
     * 等待I/O的CPU时间占比(单位：%)
     */
    private double systemCpuIOWait;

    /**
     * 系统对应CPU核心数
     */
    private int systemCpuNumCores;

    /**
     * 等待处理其他虚拟核的时间占比(单位：%)
     */
    private double systemCpuSteal;

    /**
     * 内核态CPU时间占比(单位：%)
     */
    private double systemCpuSystem;

    /**
     * 用户态CPU时间占比(单位：%)
     */
    private double systemCpuUser;

    /*********************** about disk ***********************/
    /**
     * 磁盘各分区余量大小（单位：byte）
     */
    private Map<String, Long> systemDiskBytesFree;

    /**
     * 磁盘各分区用量占比（单位：%）
     */
    private Map<String, Double> systemDiskUsedPercent;

    /**
     * 各设备读操作耗时(单位：ms)
     */
    private Map<String, Long> systemDiskReadTime;

    /**
     * 读取磁盘时间百分比（单位：%）
     */
    private double systemDiskReadTimePercent;

    /**
     * 磁盘各分区总量（单位：byte）
     */
    private Map<String, Long> systemDiskBytesTotal;

    /**
     * 磁盘各分区用量大小（单位：byte）
     */
    private Map<String, Long> systemDiskBytesUsed;

    /**
     * 各设备写操作耗时(单位：ms)
     */
    private Map<String, Long> systemDiskWriteTime;

    /**
     * 写入磁盘时间百分比（单位：%）
     */
    private double systemDiskWriteTimePercent;

    /**
     * 系统已分配文件句柄数
     */
    private int systemFilesAllocated;

    /**
     * 系统未分配文件句柄数
     */
    private int systemFilesLeft;

    /**
     * 系统使用文件句柄占已分配百分比（单位：%）
     */
    private double systemFilesUsedPercent;

    /**
     * 系统可以打开的最大文件句柄数
     */
    private int systemFilesMax;

    /**
     * 系统使用的已分配文件句柄数
     */
    private int systemFilesUsed;

    /**
     * 系统未使用的已分配文件句柄数
     */
    private int systemFilesNotUsed;

    /**
     * 系统各分区空闲inode数量
     */
    private Map<String, Long> systemDiskInodesFree;

    /**
     * 系统各分区已用inode占比（单位：%）
     */
    private Map<String, Double> systemDiskInodesUsedPercent;

    /**
     * 系统各分区inode总数量
     */
    private Map<String, Long> systemDiskInodesTotal;

    /**
     * 系统各分区已用inode数量
     */
    private Map<String, Long> systemDiskInodesUsed;

    /*********************** about io ***********************/

    /**
     * 各设备平均队列长度
     */
    private Map<String, Long> systemIOAvgQuSz;

    /**
     * 各设备平均请求大小
     */
    private Map<String, Long> systemIOAvgRqSz;

    /**
     * 各设备每次IO平均处理时间（单位：ms）
     */
    private Map<String, Long> systemIOAwait;

    /**
     * 各设备读请求平均耗时(单位：ms)
     */
    private Map<String, Long> systemIORAwait;

    /**
     * 各设备每秒读请求数量
     */
    private Map<String, Long> systemIOReadRequest;

    /**
     * 各设备每秒读取字节数
     */
    private Map<String, Long> systemIOReadBytes;

    /**
     * 各设备每秒合并到设备队列的读请求数
     */
    private Map<String, Long> systemIORRQMS;

    /**
     * 每次各设备IO平均服务时间（单位：ms）
     * 注：仅 做参考
     */
    private Map<String, Long> systemIOSVCTM;

    /**
     * 各设备I/O请求的CPU时间百分比
     */
    private Map<String, Double> systemIOUtil;

    /**
     * 各设备写请求平均耗时(单位：ms)
     */
    private Map<String, Long> systemIOWAwait;

    /**
     * 各设备每秒写请求数量
     */
    private Map<String, Long> systemIOWriteRequest;

    /**
     * 各设备每秒写字节数
     */
    private Map<String, Long> systemIOWriteBytes;

    /**
     * 各设备每秒合并到设备队列的写请求数
     */
    private Map<String, Long> systemIOWRQMS;

    /**
     * 系统近1分钟平均负载
     */
    private double systemLoad1;

    /**
     * 系统近5分钟平均负载
     */
    private double systemLoad5;

    /**
     * 系统近15分钟平均负载
     */
    private double systemLoad15;

    /*********************** about memory ***********************/

    /**
     * 系统文件缓冲区的物理RAM量（单位：byte）
     */
    private long systemMemBuffered;

    /**
     * 缓存内存的物理RAM量（单位：byte）
     */
    private long systemMemCached;

    /**
     * 系统当前可分配的内存总量（单位：byte）
     */
    private long systemMemCommitLimit;

    /**
     * 在磁盘分页文件上保留的物理内存量（单位：byte）
     */
    private long systemMemCommitted;

    /**
     * 系统已分配的包括进程未使用的内存量（单位：byte）
     */
    private long systemMemCommittedAs;

    /**
     * 不能写入磁盘的物理内存量（单位：byte）
     */
    private long systemMemNonPaged;

    /**
     * 没被使用是可以写入磁盘的物理内存量（单位：byte）
     */
    private long systemMemPaged;

    /**
     * 系统内存空闲率
     */
    private double systemMemFreePercent;

    /**
     * 系统内存使用率
     */
    private double systemMemUsedPercent;

    /**
     * 用作共享内存的物理RAM量（单位：byte）
     */
    private long systemMemShared;

    /**
     * 内核用来缓存数据结构供自己使用的内存量（单位：byte）
     */
    private long systemMemSlab;

    /**
     * 系统物理内存总量（单位：byte）
     */
    private long systemMemTotal;

    /**
     * 系统空闲内存大小（单位：byte）
     */
    private long systemMemFree;

    /**
     * 系统已用内存大小（单位：byte）
     */
    private long systemMemUsed;

    /**
     * 系统用作缓存的交换空间
     */
    private long systemSwapCached;

    /**
     * 系统空闲swap大小（单位：byte）
     */
    private long systemSwapFree;

    /**
     * 系统空闲swap占比
     */
    private double systemSwapFreePercent;

    /**
     * 系统swap总大小（单位：byte）
     */
    private long systemSwapTotal;

    /**
     * 系统已用swap大小（单位：byte）
     */
    private long systemSwapUsed;

    /**
     * 系统已用swap占比（单位：%）
     */
    private double systemSwapUsedPercent;

    /*********************** about network ***********************/

    /**
     * 系统网络每秒下行流量
     */
    private long systemNetworkReceiveBytesPs;

    /**
     * 系统网络每秒上行流量
     */
    private long systemNetworkSendBytesPs;

    /**
     * 系统tcp连接数
     */
    private int systemNetworkTcpConnectionNum;

    /**
     * 系统处于 time wait 状态 tcp 连接数
     */
    private int systemNetworkTcpTimeWaitNum;

    /**
     * 系统处于 close wait 状态 tcp 连接数
     */
    private int systemNetworkTcpCloseWaitNum;

    /**
     * 系统启动以来 Tcp 主动连接次数
     */
    private long systemNetworkTcpActiveOpens;

    /**
     * 系统启动以来 Tcp 被动连接次数
     */
    private long systemNetworkTcpPassiveOpens;

    /**
     * 系统启动以来 Tcp 连接失败次数
     */
    private long systemNetworkTcpAttemptFails;

    /**
     * 系统启动以来 Tcp 连接异常断开次数
     */
    private long systemNetworkTcpEstabResets;

    /**
     * 系统启动以来 Tcp 重传的报文段总个数
     */
    private long systemNetworkTcpRetransSegs;

    /**
     * 系统启动以来 Tcp 监听队列溢出次数
     */
    private long systemNetworkTcpExtListenOverflows;

    /**
     * 系统启动以来 UDP 入包量
     */
    private long systemNetworkUdpInDatagrams;

    /**
     * 系统启动以来 UDP 出包量
     */
    private long systemNetworkUdpOutDatagrams;

    /**
     * 系统启动以来 UDP 入包错误数
     */
    private long systemNetworkUdpInErrors;

    /**
     * 系统启动以来 UDP 端口不可达个数
     */
    private long systemNetworkUdpNoPorts;

    /**
     * 系统启动以来 UDP 发送缓冲区满次数
     */
    private long systemNetworkUdpSendBufferErrors;

    public long getSystemNtpOffset() {
        return systemNtpOffset;
    }

    public void setSystemNtpOffset(long systemNtpOffset) {
        this.systemNtpOffset = systemNtpOffset;
    }

    public int getSystemProcCount() {
        return systemProcCount;
    }

    public void setSystemProcCount(int systemProcCount) {
        this.systemProcCount = systemProcCount;
    }

    public long getSystemStartupTime() {
        return systemStartupTime;
    }

    public void setSystemStartupTime(long systemStartupTime) {
        this.systemStartupTime = systemStartupTime;
    }

    public long getSystemUptime() {
        return systemUptime;
    }

    public void setSystemUptime(long systemUptime) {
        this.systemUptime = systemUptime;
    }

    public double getSystemCpuUtil() {
        return systemCpuUtil;
    }

    public void setSystemCpuUtil(double systemCpuUtil) {
        this.systemCpuUtil = systemCpuUtil;
    }

    public double getSystemCpuUtilTotalPercent() {
        return systemCpuUtilTotalPercent;
    }

    public void setSystemCpuUtilTotalPercent(double systemCpuUtilTotalPercent) {
        this.systemCpuUtilTotalPercent = systemCpuUtilTotalPercent;
    }

    public long getSystemCpuSwitches() {
        return systemCpuSwitches;
    }

    public void setSystemCpuSwitches(long systemCpuSwitches) {
        this.systemCpuSwitches = systemCpuSwitches;
    }

    public double getSystemCpuGuest() {
        return systemCpuGuest;
    }

    public void setSystemCpuGuest(double systemCpuGuest) {
        this.systemCpuGuest = systemCpuGuest;
    }

    public double getSystemCpuIdle() {
        return systemCpuIdle;
    }

    public void setSystemCpuIdle(double systemCpuIdle) {
        this.systemCpuIdle = systemCpuIdle;
    }

    public double getSystemCpuIOWait() {
        return systemCpuIOWait;
    }

    public void setSystemCpuIOWait(double systemCpuIOWait) {
        this.systemCpuIOWait = systemCpuIOWait;
    }

    public int getSystemCpuNumCores() {
        return systemCpuNumCores;
    }

    public void setSystemCpuNumCores(int systemCpuNumCores) {
        this.systemCpuNumCores = systemCpuNumCores;
    }

    public double getSystemCpuSteal() {
        return systemCpuSteal;
    }

    public void setSystemCpuSteal(double systemCpuSteal) {
        this.systemCpuSteal = systemCpuSteal;
    }

    public double getSystemCpuSystem() {
        return systemCpuSystem;
    }

    public void setSystemCpuSystem(double systemCpuSystem) {
        this.systemCpuSystem = systemCpuSystem;
    }

    public double getSystemCpuUser() {
        return systemCpuUser;
    }

    public void setSystemCpuUser(double systemCpuUser) {
        this.systemCpuUser = systemCpuUser;
    }

    public Map<String, Long> getSystemDiskBytesFree() {
        return systemDiskBytesFree;
    }

    public void setSystemDiskBytesFree(Map<String, Long> systemDiskBytesFree) {
        this.systemDiskBytesFree = systemDiskBytesFree;
    }

    public Map<String, Double> getSystemDiskUsedPercent() {
        return systemDiskUsedPercent;
    }

    public void setSystemDiskUsedPercent(Map<String, Double> systemDiskUsedPercent) {
        this.systemDiskUsedPercent = systemDiskUsedPercent;
    }

    public Map<String, Long> getSystemDiskReadTime() {
        return systemDiskReadTime;
    }

    public void setSystemDiskReadTime(Map<String, Long> systemDiskReadTime) {
        this.systemDiskReadTime = systemDiskReadTime;
    }

    public double getSystemDiskReadTimePercent() {
        return systemDiskReadTimePercent;
    }

    public void setSystemDiskReadTimePercent(double systemDiskReadTimePercent) {
        this.systemDiskReadTimePercent = systemDiskReadTimePercent;
    }

    public Map<String, Long> getSystemDiskBytesTotal() {
        return systemDiskBytesTotal;
    }

    public void setSystemDiskBytesTotal(Map<String, Long> systemDiskBytesTotal) {
        this.systemDiskBytesTotal = systemDiskBytesTotal;
    }

    public Map<String, Long> getSystemDiskBytesUsed() {
        return systemDiskBytesUsed;
    }

    public void setSystemDiskBytesUsed(Map<String, Long> systemDiskBytesUsed) {
        this.systemDiskBytesUsed = systemDiskBytesUsed;
    }

    public Map<String, Long> getSystemDiskWriteTime() {
        return systemDiskWriteTime;
    }

    public void setSystemDiskWriteTime(Map<String, Long> systemDiskWriteTime) {
        this.systemDiskWriteTime = systemDiskWriteTime;
    }

    public double getSystemDiskWriteTimePercent() {
        return systemDiskWriteTimePercent;
    }

    public void setSystemDiskWriteTimePercent(double systemDiskWriteTimePercent) {
        this.systemDiskWriteTimePercent = systemDiskWriteTimePercent;
    }

    public int getSystemFilesAllocated() {
        return systemFilesAllocated;
    }

    public void setSystemFilesAllocated(int systemFilesAllocated) {
        this.systemFilesAllocated = systemFilesAllocated;
    }

    public int getSystemFilesLeft() {
        return systemFilesLeft;
    }

    public void setSystemFilesLeft(int systemFilesLeft) {
        this.systemFilesLeft = systemFilesLeft;
    }

    public double getSystemFilesUsedPercent() {
        return systemFilesUsedPercent;
    }

    public void setSystemFilesUsedPercent(double systemFilesUsedPercent) {
        this.systemFilesUsedPercent = systemFilesUsedPercent;
    }

    public int getSystemFilesMax() {
        return systemFilesMax;
    }

    public void setSystemFilesMax(int systemFilesMax) {
        this.systemFilesMax = systemFilesMax;
    }

    public int getSystemFilesUsed() {
        return systemFilesUsed;
    }

    public void setSystemFilesUsed(int systemFilesUsed) {
        this.systemFilesUsed = systemFilesUsed;
    }

    public int getSystemFilesNotUsed() {
        return systemFilesNotUsed;
    }

    public void setSystemFilesNotUsed(int systemFilesNotUsed) {
        this.systemFilesNotUsed = systemFilesNotUsed;
    }

    public Map<String, Long> getSystemDiskInodesFree() {
        return systemDiskInodesFree;
    }

    public void setSystemDiskInodesFree(Map<String, Long> systemDiskInodesFree) {
        this.systemDiskInodesFree = systemDiskInodesFree;
    }

    public Map<String, Double> getSystemDiskInodesUsedPercent() {
        return systemDiskInodesUsedPercent;
    }

    public void setSystemDiskInodesUsedPercent(Map<String, Double> systemDiskInodesUsedPercent) {
        this.systemDiskInodesUsedPercent = systemDiskInodesUsedPercent;
    }

    public Map<String, Long> getSystemDiskInodesTotal() {
        return systemDiskInodesTotal;
    }

    public void setSystemDiskInodesTotal(Map<String, Long> systemDiskInodesTotal) {
        this.systemDiskInodesTotal = systemDiskInodesTotal;
    }

    public Map<String, Long> getSystemDiskInodesUsed() {
        return systemDiskInodesUsed;
    }

    public void setSystemDiskInodesUsed(Map<String, Long> systemDiskInodesUsed) {
        this.systemDiskInodesUsed = systemDiskInodesUsed;
    }

    public Map<String, Long> getSystemIOAvgQuSz() {
        return systemIOAvgQuSz;
    }

    public void setSystemIOAvgQuSz(Map<String, Long> systemIOAvgQuSz) {
        this.systemIOAvgQuSz = systemIOAvgQuSz;
    }

    public Map<String, Long> getSystemIOAvgRqSz() {
        return systemIOAvgRqSz;
    }

    public void setSystemIOAvgRqSz(Map<String, Long> systemIOAvgRqSz) {
        this.systemIOAvgRqSz = systemIOAvgRqSz;
    }

    public Map<String, Long> getSystemIOAwait() {
        return systemIOAwait;
    }

    public void setSystemIOAwait(Map<String, Long> systemIOAwait) {
        this.systemIOAwait = systemIOAwait;
    }

    public Map<String, Long> getSystemIORAwait() {
        return systemIORAwait;
    }

    public void setSystemIORAwait(Map<String, Long> systemIORAwait) {
        this.systemIORAwait = systemIORAwait;
    }

    public Map<String, Long> getSystemIOReadRequest() {
        return systemIOReadRequest;
    }

    public void setSystemIOReadRequest(Map<String, Long> systemIOReadRequest) {
        this.systemIOReadRequest = systemIOReadRequest;
    }

    public Map<String, Long> getSystemIOReadBytes() {
        return systemIOReadBytes;
    }

    public void setSystemIOReadBytes(Map<String, Long> systemIOReadBytes) {
        this.systemIOReadBytes = systemIOReadBytes;
    }

    public Map<String, Long> getSystemIORRQMS() {
        return systemIORRQMS;
    }

    public void setSystemIORRQMS(Map<String, Long> systemIORRQMS) {
        this.systemIORRQMS = systemIORRQMS;
    }

    public Map<String, Long> getSystemIOSVCTM() {
        return systemIOSVCTM;
    }

    public void setSystemIOSVCTM(Map<String, Long> systemIOSVCTM) {
        this.systemIOSVCTM = systemIOSVCTM;
    }

    public Map<String, Double> getSystemIOUtil() {
        return systemIOUtil;
    }

    public void setSystemIOUtil(Map<String, Double> systemIOUtil) {
        this.systemIOUtil = systemIOUtil;
    }

    public Map<String, Long> getSystemIOWAwait() {
        return systemIOWAwait;
    }

    public void setSystemIOWAwait(Map<String, Long> systemIOWAwait) {
        this.systemIOWAwait = systemIOWAwait;
    }

    public Map<String, Long> getSystemIOWriteRequest() {
        return systemIOWriteRequest;
    }

    public void setSystemIOWriteRequest(Map<String, Long> systemIOWriteRequest) {
        this.systemIOWriteRequest = systemIOWriteRequest;
    }

    public Map<String, Long> getSystemIOWriteBytes() {
        return systemIOWriteBytes;
    }

    public void setSystemIOWriteBytes(Map<String, Long> systemIOWriteBytes) {
        this.systemIOWriteBytes = systemIOWriteBytes;
    }

    public Map<String, Long> getSystemIOWRQMS() {
        return systemIOWRQMS;
    }

    public void setSystemIOWRQMS(Map<String, Long> systemIOWRQMS) {
        this.systemIOWRQMS = systemIOWRQMS;
    }

    public double getSystemLoad1() {
        return systemLoad1;
    }

    public void setSystemLoad1(double systemLoad1) {
        this.systemLoad1 = systemLoad1;
    }

    public double getSystemLoad5() {
        return systemLoad5;
    }

    public void setSystemLoad5(double systemLoad5) {
        this.systemLoad5 = systemLoad5;
    }

    public double getSystemLoad15() {
        return systemLoad15;
    }

    public void setSystemLoad15(double systemLoad15) {
        this.systemLoad15 = systemLoad15;
    }

    public long getSystemMemBuffered() {
        return systemMemBuffered;
    }

    public void setSystemMemBuffered(long systemMemBuffered) {
        this.systemMemBuffered = systemMemBuffered;
    }

    public long getSystemMemCached() {
        return systemMemCached;
    }

    public void setSystemMemCached(long systemMemCached) {
        this.systemMemCached = systemMemCached;
    }

    public long getSystemMemCommitLimit() {
        return systemMemCommitLimit;
    }

    public void setSystemMemCommitLimit(long systemMemCommitLimit) {
        this.systemMemCommitLimit = systemMemCommitLimit;
    }

    public long getSystemMemCommitted() {
        return systemMemCommitted;
    }

    public void setSystemMemCommitted(long systemMemCommitted) {
        this.systemMemCommitted = systemMemCommitted;
    }

    public long getSystemMemCommittedAs() {
        return systemMemCommittedAs;
    }

    public void setSystemMemCommittedAs(long systemMemCommittedAs) {
        this.systemMemCommittedAs = systemMemCommittedAs;
    }

    public long getSystemMemNonPaged() {
        return systemMemNonPaged;
    }

    public void setSystemMemNonPaged(long systemMemNonPaged) {
        this.systemMemNonPaged = systemMemNonPaged;
    }

    public long getSystemMemPaged() {
        return systemMemPaged;
    }

    public void setSystemMemPaged(long systemMemPaged) {
        this.systemMemPaged = systemMemPaged;
    }

    public double getSystemMemFreePercent() {
        return systemMemFreePercent;
    }

    public void setSystemMemFreePercent(double systemMemFreePercent) {
        this.systemMemFreePercent = systemMemFreePercent;
    }

    public double getSystemMemUsedPercent() {
        return systemMemUsedPercent;
    }

    public void setSystemMemUsedPercent(double systemMemUsedPercent) {
        this.systemMemUsedPercent = systemMemUsedPercent;
    }

    public long getSystemMemShared() {
        return systemMemShared;
    }

    public void setSystemMemShared(long systemMemShared) {
        this.systemMemShared = systemMemShared;
    }

    public long getSystemMemSlab() {
        return systemMemSlab;
    }

    public void setSystemMemSlab(long systemMemSlab) {
        this.systemMemSlab = systemMemSlab;
    }

    public long getSystemMemTotal() {
        return systemMemTotal;
    }

    public void setSystemMemTotal(long systemMemTotal) {
        this.systemMemTotal = systemMemTotal;
    }

    public long getSystemMemFree() {
        return systemMemFree;
    }

    public void setSystemMemFree(long systemMemFree) {
        this.systemMemFree = systemMemFree;
    }

    public long getSystemMemUsed() {
        return systemMemUsed;
    }

    public void setSystemMemUsed(long systemMemUsed) {
        this.systemMemUsed = systemMemUsed;
    }

    public long getSystemSwapCached() {
        return systemSwapCached;
    }

    public void setSystemSwapCached(long systemSwapCached) {
        this.systemSwapCached = systemSwapCached;
    }

    public long getSystemSwapFree() {
        return systemSwapFree;
    }

    public void setSystemSwapFree(long systemSwapFree) {
        this.systemSwapFree = systemSwapFree;
    }

    public double getSystemSwapFreePercent() {
        return systemSwapFreePercent;
    }

    public void setSystemSwapFreePercent(double systemSwapFreePercent) {
        this.systemSwapFreePercent = systemSwapFreePercent;
    }

    public long getSystemSwapTotal() {
        return systemSwapTotal;
    }

    public void setSystemSwapTotal(long systemSwapTotal) {
        this.systemSwapTotal = systemSwapTotal;
    }

    public long getSystemSwapUsed() {
        return systemSwapUsed;
    }

    public void setSystemSwapUsed(long systemSwapUsed) {
        this.systemSwapUsed = systemSwapUsed;
    }

    public double getSystemSwapUsedPercent() {
        return systemSwapUsedPercent;
    }

    public void setSystemSwapUsedPercent(double systemSwapUsedPercent) {
        this.systemSwapUsedPercent = systemSwapUsedPercent;
    }

    public long getSystemNetworkReceiveBytesPs() {
        return systemNetworkReceiveBytesPs;
    }

    public void setSystemNetworkReceiveBytesPs(long systemNetworkReceiveBytesPs) {
        this.systemNetworkReceiveBytesPs = systemNetworkReceiveBytesPs;
    }

    public long getSystemNetworkSendBytesPs() {
        return systemNetworkSendBytesPs;
    }

    public void setSystemNetworkSendBytesPs(long systemNetworkSendBytesPs) {
        this.systemNetworkSendBytesPs = systemNetworkSendBytesPs;
    }

    public int getSystemNetworkTcpConnectionNum() {
        return systemNetworkTcpConnectionNum;
    }

    public void setSystemNetworkTcpConnectionNum(int systemNetworkTcpConnectionNum) {
        this.systemNetworkTcpConnectionNum = systemNetworkTcpConnectionNum;
    }

    public int getSystemNetworkTcpTimeWaitNum() {
        return systemNetworkTcpTimeWaitNum;
    }

    public void setSystemNetworkTcpTimeWaitNum(int systemNetworkTcpTimeWaitNum) {
        this.systemNetworkTcpTimeWaitNum = systemNetworkTcpTimeWaitNum;
    }

    public int getSystemNetworkTcpCloseWaitNum() {
        return systemNetworkTcpCloseWaitNum;
    }

    public void setSystemNetworkTcpCloseWaitNum(int systemNetworkTcpCloseWaitNum) {
        this.systemNetworkTcpCloseWaitNum = systemNetworkTcpCloseWaitNum;
    }

    public long getSystemNetworkTcpActiveOpens() {
        return systemNetworkTcpActiveOpens;
    }

    public void setSystemNetworkTcpActiveOpens(long systemNetworkTcpActiveOpens) {
        this.systemNetworkTcpActiveOpens = systemNetworkTcpActiveOpens;
    }

    public long getSystemNetworkTcpPassiveOpens() {
        return systemNetworkTcpPassiveOpens;
    }

    public void setSystemNetworkTcpPassiveOpens(long systemNetworkTcpPassiveOpens) {
        this.systemNetworkTcpPassiveOpens = systemNetworkTcpPassiveOpens;
    }

    public long getSystemNetworkTcpAttemptFails() {
        return systemNetworkTcpAttemptFails;
    }

    public void setSystemNetworkTcpAttemptFails(long systemNetworkTcpAttemptFails) {
        this.systemNetworkTcpAttemptFails = systemNetworkTcpAttemptFails;
    }

    public long getSystemNetworkTcpEstabResets() {
        return systemNetworkTcpEstabResets;
    }

    public void setSystemNetworkTcpEstabResets(long systemNetworkTcpEstabResets) {
        this.systemNetworkTcpEstabResets = systemNetworkTcpEstabResets;
    }

    public long getSystemNetworkTcpRetransSegs() {
        return systemNetworkTcpRetransSegs;
    }

    public void setSystemNetworkTcpRetransSegs(long systemNetworkTcpRetransSegs) {
        this.systemNetworkTcpRetransSegs = systemNetworkTcpRetransSegs;
    }

    public long getSystemNetworkTcpExtListenOverflows() {
        return systemNetworkTcpExtListenOverflows;
    }

    public void setSystemNetworkTcpExtListenOverflows(long systemNetworkTcpExtListenOverflows) {
        this.systemNetworkTcpExtListenOverflows = systemNetworkTcpExtListenOverflows;
    }

    public long getSystemNetworkUdpInDatagrams() {
        return systemNetworkUdpInDatagrams;
    }

    public void setSystemNetworkUdpInDatagrams(long systemNetworkUdpInDatagrams) {
        this.systemNetworkUdpInDatagrams = systemNetworkUdpInDatagrams;
    }

    public long getSystemNetworkUdpOutDatagrams() {
        return systemNetworkUdpOutDatagrams;
    }

    public void setSystemNetworkUdpOutDatagrams(long systemNetworkUdpOutDatagrams) {
        this.systemNetworkUdpOutDatagrams = systemNetworkUdpOutDatagrams;
    }

    public long getSystemNetworkUdpInErrors() {
        return systemNetworkUdpInErrors;
    }

    public void setSystemNetworkUdpInErrors(long systemNetworkUdpInErrors) {
        this.systemNetworkUdpInErrors = systemNetworkUdpInErrors;
    }

    public long getSystemNetworkUdpNoPorts() {
        return systemNetworkUdpNoPorts;
    }

    public void setSystemNetworkUdpNoPorts(long systemNetworkUdpNoPorts) {
        this.systemNetworkUdpNoPorts = systemNetworkUdpNoPorts;
    }

    public long getSystemNetworkUdpSendBufferErrors() {
        return systemNetworkUdpSendBufferErrors;
    }

    public void setSystemNetworkUdpSendBufferErrors(long systemNetworkUdpSendBufferErrors) {
        this.systemNetworkUdpSendBufferErrors = systemNetworkUdpSendBufferErrors;
    }
}
