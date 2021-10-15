package com.didichuxing.datachannel.agent.engine.utils.monitor;

/**
 * 系统资源服务
 */
public interface IOSResourceService {

    /**
     * @return 返回jvm进程对应pid
     */
    long getPid();

    /**
     * @return 返回jvm进程启动时间
     */
    long getProcessStartupTime();

    /**
     * @return 返回系统启动时间
     */
    long getSystemStartupTime();

    /**
     * @return 返回系统当前时间
     */
    long getSystemCurrentTimeMillis();

    /*********************************** about cpu ***********************************/

    /**
     * @return 返回系统cpu核数
     */
    int getCpuNum();

    /**
     * @return 返回jvm进程当前cpu使用率
     * 注：使用率采用全核方式计数，如jvm进程使用一颗核，则返回100，如jvm进程使用两颗核，则返回200
     */
    float getCurrentProcessCpuUsage();

    /**
     * @return 返回jvm进程当前cpu使用率
     * 注意：使用率为总使用比率，如jvm进程使用一颗核，系统共10核，则返回0.1 = 10%
     */
    float getCurrentProcessCpuUsageTotalPercent();

    /**
     * @return 返回系统当前cpu总体使用率
     * 注：使用率采用全核方式计数，如系统使用一颗核，则返回100，如使用两颗核，则返回200
     */
    float getCurrentSystemCpuUsage();

    /**
     * @return 返回系统当前cpu使用率
     * 注意：使用率为总使用比率，如agent进程宿主机使用一颗核，系统共10核，则返回0.1 = 10%
     */
    float getCurrentSystemCpuUsageTotalPercent();

    /**
     * @return 返回当前jvm进程宿主机cpu负载
     */
    double getCurrentSystemCpuLoad();

    /*********************************** about memory ***********************************/

    /**
     * @return 返回jvm进程当前内存总使用量（堆内内存 + 堆外内存） 单位：byte
     */
    long getCurrentProcessMemoryUsed();

    /**
     * @return 返回jvm进程而且堆内存使用量 单位：byte
     */
    long getCurrentProcessHeapMemoryUsed();

    /**
     * @return 返回jvm进程当前堆外内存使用量 单位：byte
     */
    long getCurrentProcessNonHeapMemoryUsed();

    /**
     * @return 返回jvm进程最大可用堆内存(对应 jvm Xmx) 单位：byte
     */
    long getCurrentProcessMaxHeapSize();

    /**
     * @return 返回系统当前可用内存 单位：byte
     */
    long getCurrentSystemMemoryFree();

    /**
     * @return 返回系统内存总大小 单位：byte
     */
    long getSystemMemoryTotal();

    /**
     * @return 返回系统已使用内存大小 单位：byte
     */
    long getSystemMemoryUsed();

    /**
     * @return 返回系统swap内存总大小 单位：byte
     */
    long getSystemMemorySwapSize();

    /**
     * @return 返回系统可用swap内存 单位：byte
     */
    long getSystemMemorySwapFree();

    /**
     * @return 返回系统swap内存使用量 单位：byte
     */
    long getSystemMemorySwapUsed();

    /**
     * @return 返回jvm进程启动以来内存使用量峰值 单位：byte
     */
    long getProcessMemoryUsedPeak();

    /*********************************** about disk ***********************************/

    /**
     * @return 返回系统磁盘总容量 单位：字节
     * 注：如系统挂载多块磁盘，则返回系统各磁盘容量之和
     */
    long getSystemDiskTotal();

    /**
     * @return 返回系统磁盘已使用量 单位：字节
     * 注：如系统挂载多块磁盘，则返回系统各磁盘使用量之和
     */
    long getSystemDiskUsed();

    /**
     * @return 返回系统磁盘剩余可用使用量 单位：字节
     * 注：如系统挂载多块磁盘，则返回系统各磁盘可使用量之和
     */
    long getSystemDiskFree();

    /**
     * @return 返回系统挂载的各磁盘中，最小磁盘可使用量 单位：字节
     */
    long getSystemDiskFreeMin();

    /**
     * @return 返回系统挂载磁盘数
     */
    int getSystemDiskNum();

    /*********************************** about agent process gc ***********************************/

    /**
     * @return 返回jvm进程启动以来 young gc 次数
     */
    long getYoungGcCount();

    /**
     * @return 返回jvm进程启动以来 full gc 次数
     */
    long getFullGcCount();

    /**
     * @return 返回jvm进程启动以来 young gc 耗时 单位：ms
     */
    long getYoungGcTime();

    /**
     * @return 返回jvm进程启动以来 full gc 耗时 单位：ms
     */
    long getFullGcTime();

    /*********************************** about fd ***********************************/

    /**
     * @return 返回jvm进程当前fd使用数
     */
    int getCurrentProcessFdUsed();

    /**
     * @return 返回系统fd使用量
     */
    int getCurrentSystemFdUsed();

    /**
     * @return 返回系统最大fd可用数
     */
    int getSystemMaxFdSize();

    /*********************************** about thread ***********************************/

    /**
     * @return 返回jvm进程当前线程使用数
     */
    int getCurrentProcessThreadNum();

    /**
     * @return 返回jvm进程启动以来线程数峰值
     */
    int getCurrentProcessThreadNumPeak();

    /*********************************** about io ***********************************/

    /**
     * @return 返回系统当前磁盘 io 每秒读取字节数
     */
    long getCurrentSystemDiskIOReadBytesPS();

    /**
     * @return 返回系统当前磁盘 io 每秒写入字节数
     */
    long getCurrentSystemDiskIOWriteBytesPS();

    /**
     * @return 返回jvm进程当前磁盘 io 每秒读取字节数
     */
    long getCurrentProcessDiskIOReadBytesPS();

    /**
     * @return 返回jvm进程当前磁盘 io 每秒写入字节数
     */
    long getCurrentProcessDiskIOWriteBytesPS();

    /**
     * @return 返回系统当前磁盘 io 使用率，100% 表示磁盘io能力达到饱和，对应 linux %util
     */
    float getCurrentSystemDiskIOUsagePercent();

    /**
     * @return 返回jvm进程磁盘 io 读写等待时间占总时间百分比 对应 iotop IO
     */
    float getCurrentProcessDiskIOAwaitTimePercent();

    /**
     * @return 返回系统当前每秒 io 请求数量（随机读场景须关注）
     */
    int getCurrentSystemIOPS();

    /**
     * @return 返回系统每一个磁盘 io 请求的平均处理时间 单位：ms，对应 linux await
     */
    long getCurrentSystemDiskIOResponseTimeAvg();

    /**
     * @return 返回系统每次设备 io 处理的平均处理时间 单位：ms，对应 linux svctm
     * 如：getCurrentSystemIOProcessTimeAvg 值趋近于 getCurrentSystemIOResponseTimeAvg，表示几乎不存在 io 等待，
     * getCurrentSystemIOResponseTimeAvg 值远高于 getCurrentSystemIOProcessTimeAvg，表示 I/O队列等待较长，可能导致系统上运行的应用程序将变慢
     */
    long getCurrentSystemDiskIOProcessTimeAvg();

    /*********************************** about network ***********************************/

    /**
     * @return 返回系统当前网络每秒下行流量
     */
    long getCurrentSystemNetworkReceiveBytesPS();

    /**
     * @return 返回系统网络当前每秒上行流量
     */
    long getCurrentSystemNetworkSendBytesPS();

    /**
     * @return 返回jvm进程当前网络每秒下行流量
     */
    long getCurrentProcessNetworkReceiveBytesPS();

    /**
     * @return 返回jvm进程当前网络每秒上行流量
     */
    long getCurrentProcessNetworkSendBytesPS();

    /************************** about network tcp **************************/

    /**
     * @return 返回系统当前tcp连接数
     */
    int getCurrentSystemNetworkTcpConnectionNum();

    /**
     * @return 返回jvm进程当前tcp连接数
     */
    int getCurrentProcessNetworkTcpConnectionNum();

    /**
     * @return 返回系统当前处于 time wait 状态 tcp 连接数
     */
    int getCurrentSystemNetworkTcpTimeWaitNum();

    /**
     * @return 返回jvm进程当前处于 time wait 状态 tcp 连接数
     */
    int getCurrentProcessNetworkTcpTimeWaitNum();

    /**
     * @return 返回系统当前处于 close wait 状态 tcp 连接数
     */
    int getCurrentSystemNetworkTcpCloseWaitNum();

    /**
     * @return 返回jvm进程当前处于 close wait 状态 tcp 连接数
     */
    int getCurrentProcessNetworkTcpCloseWaitNum();

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

    /************************** about network udp **************************/

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
