package com.didichuxing.datachannel.system.metrcis.bean;

import java.util.List;

/**
 * 进程级指标
 */
public class ProcMetrics {

    /**
     * 当前Jvm进程启动时间
     */
    private long procStartupTime;

    /**
     * 当前Jvm进程运行时间
     */
    private long procUptime;

    /**
     * 当前Jvm进程对应pid
     */
    private long procPid;

    /*********************** about cpu ***********************/

    /**
     * 当前进程系统态cpu使用率(单位：%)
     */
    private double procCpuSys;

    /**
     * 当前进程cpu每秒上下文交换次数
     */
    private long procCpuSwitchesPS;

    /**
     * 当前进程cpu每秒自愿上下文交换次数（自愿上下文切换，是指进程无法获取所需资源，导致的上下文切换。比如说， I/O、内存等系统资源不足时，就会发生自愿上下文切换 pidstat）
     */
    private long procCpuVoluntarySwitchesPS;

    /**
     * 当前进程cpu每秒非自愿上下文交换次数（非自愿上下文切换，则是指进程由于时间片已到等原因，被系统强制调度，进而发生的上下文切换。比如说，大量进程都在争抢 CPU 时，就容易发生非自愿上下文切换 pidstat）
     */
    private long procCpuNonVoluntarySwitchesPS;

    /**
     * 当前进程cpu使用率(单位：%)
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    private float procCpuUtil;

    /**
     * 当前进程cpu使用率(单位：%)
     * 注意：使用率为总使用比率，如jvm进程使用一颗核，系统共10核，则返回0.1 = 10%
     */
    private float procCpuUtilTotalPercent;

    /**
     * 当前进程用户态cpu使用率(单位：%)
     */
    private double procCpuUser;

    /*********************** about io ***********************/

    /**
     * 当前进程io读取频率(单位：hz)
     */
    private double procIOReadRate;

    /**
     * 当前进程io读取速率(单位：b/s)
     */
    private long procIOReadBytesRate;

    /**
     * 当前进程io写入频率(单位：hz)
     */
    private double procIOWriteRate;

    /**
     * 当前进程io写入速率(单位：b/s)
     */
    private long procIOWriteBytesRate;

    /**
     * 当前进程io读写等待时间占总时间百分比（单位：%） 对应 iotop IO
     */
    private double procIOAwaitTimePercent;

    /*********************** about memory ***********************/

    /**
     * 当前进程data内存大小
     */
    private long procMemData;

    /**
     * 当前进程dirty内存大小
     */
    private long procMemDirty;

    /**
     * 当前进程lib内存大小
     */
    private long procMemLib;

    /**
     * 当前进程常驻内存大小
     */
    private long procMemRss;

    /**
     * 当前进程共享内存大小
     */
    private long procMemShared;

    /**
     * 当前进程交换空间大小
     */
    private long procMemSwap;

    /**
     * 当前进程Text内存大小
     */
    private long procMemText;

    /**
     * 当前进程内存使用量（单位：byte）
     */
    private long procMemUsed;

    /**
     * 当前进程内存使用率(单位：%)
     */
    private double procMemUtil;

    /**
     * 当前进程虚拟内存大小
     */
    private long procMemVms;

    /**
     * 当前jvm进程堆内存使用量（单位：byte）
     */
    private long jvmProcHeapMemoryUsed;

    /**
     * 当前jvm进程堆外内存使用量（单位：byte）
     */
    private long jvmProcNonHeapMemoryUsed;

    /**
     * 当前jvm进程最大可用堆内存，对应 jvm Xmx（单位：byte）
     */
    private long jvmProcHeapSizeXmx;

    /**
     * 当前jvm进程启动以来内存使用量峰值（单位：byte）
     */
    private long jvmProcMemUsedPeak;

    /*********************** about gc ***********************/

    /**
     * 当前jvm进程启动以来 young gc 次数
     */
    private long jvmProcYoungGcCount;

    /**
     * 当前jvm进程启动以来 full gc 次数
     */
    private long jvmProcFullGcCount;

    /**
     * 当前jvm进程启动以来 young gc 耗时 单位：ms
     */
    private long jvmProcYoungGcTime;

    /**
     * 当前jvm进程启动以来 full gc 耗时 单位：ms
     */
    private long jvmProcFullGcTime;

    /*********************** about thread ***********************/

    /**
     * 当前jvm进程中线程数
     */
    private int jvmProcThreadNum;

    /**
     * 当前jvm进程启动以来线程数峰值
     */
    private int jvmProcThreadNumPeak;

    /*********************** about fd ***********************/

    /**
     * 当前Jvm进程打开fd数量
     */
    private int procOpenFdCount;

    /*********************** about network ***********************/

    /**
     * 当前Jvm进程监听端口
     */
    private List<Integer> procPortListen;

    /**
     * 当前Jvm进程网络每秒下行流量
     */
    private long procNetworkReceiveBytesPs;

    /**
     * 当前Jvm进程网络每秒上行流量
     */
    private long procNetworkSendBytesPs;

    /**
     * 当前Jvm进程当前tcp连接数
     */
    private int procNetworkTcpConnectionNum;

    /**
     * 当前Jvm进程当前处于 time wait 状态 tcp 连接数
     */
    private int procNetworkTcpTimeWaitNum;

    /**
     * 当前Jvm进程当前处于 close wait 状态 tcp 连接数
     */
    private int procNetworkTcpCloseWaitNum;

    public long getProcStartupTime() {
        return procStartupTime;
    }

    public void setProcStartupTime(long procStartupTime) {
        this.procStartupTime = procStartupTime;
    }

    public long getProcUptime() {
        return procUptime;
    }

    public void setProcUptime(long procUptime) {
        this.procUptime = procUptime;
    }

    public long getProcPid() {
        return procPid;
    }

    public void setProcPid(long procPid) {
        this.procPid = procPid;
    }

    public double getProcCpuSys() {
        return procCpuSys;
    }

    public void setProcCpuSys(double procCpuSys) {
        this.procCpuSys = procCpuSys;
    }

    public long getProcCpuSwitchesPS() {
        return procCpuSwitchesPS;
    }

    public void setProcCpuSwitchesPS(long procCpuSwitchesPS) {
        this.procCpuSwitchesPS = procCpuSwitchesPS;
    }

    public long getProcCpuVoluntarySwitchesPS() {
        return procCpuVoluntarySwitchesPS;
    }

    public void setProcCpuVoluntarySwitchesPS(long procCpuVoluntarySwitchesPS) {
        this.procCpuVoluntarySwitchesPS = procCpuVoluntarySwitchesPS;
    }

    public long getProcCpuNonVoluntarySwitchesPS() {
        return procCpuNonVoluntarySwitchesPS;
    }

    public void setProcCpuNonVoluntarySwitchesPS(long procCpuNonVoluntarySwitchesPS) {
        this.procCpuNonVoluntarySwitchesPS = procCpuNonVoluntarySwitchesPS;
    }

    public float getProcCpuUtil() {
        return procCpuUtil;
    }

    public void setProcCpuUtil(float procCpuUtil) {
        this.procCpuUtil = procCpuUtil;
    }

    public float getProcCpuUtilTotalPercent() {
        return procCpuUtilTotalPercent;
    }

    public void setProcCpuUtilTotalPercent(float procCpuUtilTotalPercent) {
        this.procCpuUtilTotalPercent = procCpuUtilTotalPercent;
    }

    public double getProcCpuUser() {
        return procCpuUser;
    }

    public void setProcCpuUser(double procCpuUser) {
        this.procCpuUser = procCpuUser;
    }

    public double getProcIOReadRate() {
        return procIOReadRate;
    }

    public void setProcIOReadRate(double procIOReadRate) {
        this.procIOReadRate = procIOReadRate;
    }

    public long getProcIOReadBytesRate() {
        return procIOReadBytesRate;
    }

    public void setProcIOReadBytesRate(long procIOReadBytesRate) {
        this.procIOReadBytesRate = procIOReadBytesRate;
    }

    public double getProcIOWriteRate() {
        return procIOWriteRate;
    }

    public void setProcIOWriteRate(double procIOWriteRate) {
        this.procIOWriteRate = procIOWriteRate;
    }

    public long getProcIOWriteBytesRate() {
        return procIOWriteBytesRate;
    }

    public void setProcIOWriteBytesRate(long procIOWriteBytesRate) {
        this.procIOWriteBytesRate = procIOWriteBytesRate;
    }

    public double getProcIOAwaitTimePercent() {
        return procIOAwaitTimePercent;
    }

    public void setProcIOAwaitTimePercent(double procIOAwaitTimePercent) {
        this.procIOAwaitTimePercent = procIOAwaitTimePercent;
    }

    public long getProcMemData() {
        return procMemData;
    }

    public void setProcMemData(long procMemData) {
        this.procMemData = procMemData;
    }

    public long getProcMemDirty() {
        return procMemDirty;
    }

    public void setProcMemDirty(long procMemDirty) {
        this.procMemDirty = procMemDirty;
    }

    public long getProcMemLib() {
        return procMemLib;
    }

    public void setProcMemLib(long procMemLib) {
        this.procMemLib = procMemLib;
    }

    public long getProcMemRss() {
        return procMemRss;
    }

    public void setProcMemRss(long procMemRss) {
        this.procMemRss = procMemRss;
    }

    public long getProcMemShared() {
        return procMemShared;
    }

    public void setProcMemShared(long procMemShared) {
        this.procMemShared = procMemShared;
    }

    public long getProcMemSwap() {
        return procMemSwap;
    }

    public void setProcMemSwap(long procMemSwap) {
        this.procMemSwap = procMemSwap;
    }

    public long getProcMemText() {
        return procMemText;
    }

    public void setProcMemText(long procMemText) {
        this.procMemText = procMemText;
    }

    public long getProcMemUsed() {
        return procMemUsed;
    }

    public void setProcMemUsed(long procMemUsed) {
        this.procMemUsed = procMemUsed;
    }

    public double getProcMemUtil() {
        return procMemUtil;
    }

    public void setProcMemUtil(double procMemUtil) {
        this.procMemUtil = procMemUtil;
    }

    public long getProcMemVms() {
        return procMemVms;
    }

    public void setProcMemVms(long procMemVms) {
        this.procMemVms = procMemVms;
    }

    public long getJvmProcHeapMemoryUsed() {
        return jvmProcHeapMemoryUsed;
    }

    public void setJvmProcHeapMemoryUsed(long jvmProcHeapMemoryUsed) {
        this.jvmProcHeapMemoryUsed = jvmProcHeapMemoryUsed;
    }

    public long getJvmProcNonHeapMemoryUsed() {
        return jvmProcNonHeapMemoryUsed;
    }

    public void setJvmProcNonHeapMemoryUsed(long jvmProcNonHeapMemoryUsed) {
        this.jvmProcNonHeapMemoryUsed = jvmProcNonHeapMemoryUsed;
    }

    public long getJvmProcHeapSizeXmx() {
        return jvmProcHeapSizeXmx;
    }

    public void setJvmProcHeapSizeXmx(long jvmProcHeapSizeXmx) {
        this.jvmProcHeapSizeXmx = jvmProcHeapSizeXmx;
    }

    public long getJvmProcMemUsedPeak() {
        return jvmProcMemUsedPeak;
    }

    public void setJvmProcMemUsedPeak(long jvmProcMemUsedPeak) {
        this.jvmProcMemUsedPeak = jvmProcMemUsedPeak;
    }

    public long getJvmProcYoungGcCount() {
        return jvmProcYoungGcCount;
    }

    public void setJvmProcYoungGcCount(long jvmProcYoungGcCount) {
        this.jvmProcYoungGcCount = jvmProcYoungGcCount;
    }

    public long getJvmProcFullGcCount() {
        return jvmProcFullGcCount;
    }

    public void setJvmProcFullGcCount(long jvmProcFullGcCount) {
        this.jvmProcFullGcCount = jvmProcFullGcCount;
    }

    public long getJvmProcYoungGcTime() {
        return jvmProcYoungGcTime;
    }

    public void setJvmProcYoungGcTime(long jvmProcYoungGcTime) {
        this.jvmProcYoungGcTime = jvmProcYoungGcTime;
    }

    public long getJvmProcFullGcTime() {
        return jvmProcFullGcTime;
    }

    public void setJvmProcFullGcTime(long jvmProcFullGcTime) {
        this.jvmProcFullGcTime = jvmProcFullGcTime;
    }

    public int getJvmProcThreadNum() {
        return jvmProcThreadNum;
    }

    public void setJvmProcThreadNum(int jvmProcThreadNum) {
        this.jvmProcThreadNum = jvmProcThreadNum;
    }

    public int getJvmProcThreadNumPeak() {
        return jvmProcThreadNumPeak;
    }

    public void setJvmProcThreadNumPeak(int jvmProcThreadNumPeak) {
        this.jvmProcThreadNumPeak = jvmProcThreadNumPeak;
    }

    public int getProcOpenFdCount() {
        return procOpenFdCount;
    }

    public void setProcOpenFdCount(int procOpenFdCount) {
        this.procOpenFdCount = procOpenFdCount;
    }

    public List<Integer> getProcPortListen() {
        return procPortListen;
    }

    public void setProcPortListen(List<Integer> procPortListen) {
        this.procPortListen = procPortListen;
    }

    public long getProcNetworkReceiveBytesPs() {
        return procNetworkReceiveBytesPs;
    }

    public void setProcNetworkReceiveBytesPs(long procNetworkReceiveBytesPs) {
        this.procNetworkReceiveBytesPs = procNetworkReceiveBytesPs;
    }

    public long getProcNetworkSendBytesPs() {
        return procNetworkSendBytesPs;
    }

    public void setProcNetworkSendBytesPs(long procNetworkSendBytesPs) {
        this.procNetworkSendBytesPs = procNetworkSendBytesPs;
    }

    public int getProcNetworkTcpConnectionNum() {
        return procNetworkTcpConnectionNum;
    }

    public void setProcNetworkTcpConnectionNum(int procNetworkTcpConnectionNum) {
        this.procNetworkTcpConnectionNum = procNetworkTcpConnectionNum;
    }

    public int getProcNetworkTcpTimeWaitNum() {
        return procNetworkTcpTimeWaitNum;
    }

    public void setProcNetworkTcpTimeWaitNum(int procNetworkTcpTimeWaitNum) {
        this.procNetworkTcpTimeWaitNum = procNetworkTcpTimeWaitNum;
    }

    public int getProcNetworkTcpCloseWaitNum() {
        return procNetworkTcpCloseWaitNum;
    }

    public void setProcNetworkTcpCloseWaitNum(int procNetworkTcpCloseWaitNum) {
        this.procNetworkTcpCloseWaitNum = procNetworkTcpCloseWaitNum;
    }
}
