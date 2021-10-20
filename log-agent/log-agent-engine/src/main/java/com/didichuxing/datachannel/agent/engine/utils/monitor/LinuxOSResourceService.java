package com.didichuxing.datachannel.agent.engine.utils.monitor;

import com.didichuxing.datachannel.agent.engine.limit.cpu.LinuxCpuTime;
import com.didichuxing.datachannel.agent.engine.limit.net.LinuxNetFlow;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.*;

import com.sun.management.OperatingSystemMXBean;

import java.lang.reflect.Method;

/**
 * 默认系统资源服务
 */
public class LinuxOSResourceService implements IOSResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxOSResourceService.class);

    /**
     * 当前agent进程id
     */
    private final long          PID;
    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int           CPU_NUM;

    private LinuxCpuTime        lastLinuxCpuTime;

    private LinuxNetFlow        lastLinuxNetFlow;

    public LinuxOSResourceService() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();
        try {
            lastLinuxCpuTime = new LinuxCpuTime(getPid(), getCpuNum());// 记录上次的cpu耗时
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=CpuTime init failed",
                    e);
        }

        try {
            lastLinuxNetFlow = new LinuxNetFlow(getPid());// 记录上次的收发字节数
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=NetFlow init failed",
                    e);
        }
    }

    /**
     * @return 返回当前 agent 进程 id
     */
    private long initializePid() {
        final String name = ManagementFactory.getRuntimeMXBean().getName();
        try {
            return Long.parseLong(name.split("@")[0]);
        } catch (final NumberFormatException e) {
            LOGGER.warn(String.format("failed parsing PID from [{}]", name), e);
            return -1;
        }
    }

    /**
     * Returns a given method of the UnixOperatingSystemMXBean,
     * or null if the method is not found or unavailable.
     */
    private Method getUnixMethod(String methodName) {
        try {
            return Class.forName("com.sun.management.UnixOperatingSystemMXBean").getMethod(
                methodName);
        } catch (Exception t) {
            // not available
            return null;
        }
    }

    /**
     * Returns a given method of the OperatingSystemMXBean,
     * or null if the method is not found or unavailable.
     */
    private Method getMethod(String methodName) {
        try {
            return Class.forName("com.sun.management.OperatingSystemMXBean").getMethod(methodName);
        } catch (Exception t) {
            // not available
            return null;
        }
    }

    /**
     * invoke the method given use OperatingSystemMXBean
     */
    private <T> T invoke(Method method, OperatingSystemMXBean osMxBean) {
        if (method != null) {
            try {
                T t = (T) method.invoke(osMxBean);
                return t;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public long getPid() {
        return PID;
    }

    @Override
    public long getProcessStartupTime() {
        try {
            return ManagementFactory.getRuntimeMXBean().getStartTime();
        } catch (Exception ex) {
            LOGGER.error("获取jvm启动时间失败", ex);
            return 0;
        }
    }

    @Override
    public long getSystemStartupTime() {
        String output = getOutputByCmd(
            "date -d \"$(awk '{print $1}' /proc/uptime) second ago\" +%s", false, "系统启动时间");
        if (StringUtils.isNotBlank(output)) {
            return 1000 * Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public float getCurrentProcessCpuUsage() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(getPid(), getCpuNum());
            float cpuUsage = curLinuxCpuTime.getUsage(lastLinuxCpuTime);
            lastLinuxCpuTime = curLinuxCpuTime;
            return cpuUsage;
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current process's cpu usage get failed",
                    e);
            return 0;
        }
    }

    @Override
    public float getCurrentProcessCpuUsageTotalPercent() {
        return getCurrentProcessCpuUsage() / getCpuNum();
    }

    @Override
    public float getCurrentSystemCpuUsage() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(getPid(), getCpuNum());
            float cpuUsage = curLinuxCpuTime.getSystemUsage(lastLinuxCpuTime);
            lastLinuxCpuTime = curLinuxCpuTime;
            return cpuUsage;
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=LinuxOSResourceService||method=getCurrentSystemCpuUsage||msg=current process's cpu usage get failed",
                    e);
            return 0;
        }
    }

    @Override
    public float getCurrentSystemCpuUsageTotalPercent() {
        return getCurrentSystemCpuUsage() * getCpuNum();
    }

    @Override
    public double getCurrentSystemCpuLoad() {
        try {
            return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前jvm进程宿主机cpu负载]失败", ex);
            return 0;
        }
    }

    @Override
    public int getCpuNum() {
        return CPU_NUM;
    }

    @Override
    public long getCurrentProcessMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
            return heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程内存使用量]失败", ex);
            return 0;
        }
    }

    @Override
    public long getCurrentProcessHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆内内存使用量]失败", ex);
            return 0;
        }
    }

    @Override
    public long getCurrentProcessNonHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getNonHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆外内存使用量]失败", ex);
            return 0;
        }
    }

    @Override
    public long getCurrentProcessMaxHeapSize() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getMax();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程最大可用堆内存]失败", ex);
            return 0;
        }
    }

    @Override
    public long getCurrentSystemMemoryFree() {
        try {
            OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
            return systemMXBean.getFreePhysicalMemorySize();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[系统当前可用内存]失败", ex);
            return 0;
        }
    }

    @Override
    public long getSystemMemoryTotal() {
        try {
            OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
            return systemMXBean.getTotalPhysicalMemorySize();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[系统内存总大小]失败", ex);
            return 0;
        }
    }

    @Override
    public long getSystemMemoryUsed() {
        return getSystemMemoryTotal() - getCurrentSystemMemoryFree();
    }

    @Override
    public long getSystemMemorySwapSize() {
        try {
            OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
            return systemMXBean.getTotalSwapSpaceSize();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[系统swap内存总大小]失败", ex);
            return 0;
        }
    }

    @Override
    public long getSystemMemorySwapFree() {
        try {
            OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
            return systemMXBean.getFreeSwapSpaceSize();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[系统swap可用内存]失败", ex);
            return 0;
        }
    }

    @Override
    public long getSystemMemorySwapUsed() {
        return getSystemMemorySwapSize() - getSystemMemorySwapFree();
    }

    @Override
    public long getProcessMemoryUsedPeak() {
        String output = getOutputByCmd("grep VmPeak /proc/%d/status | awk '{print $2}'", true,
            "jvm进程使用峰值");
        if (StringUtils.isNotBlank(output)) {
            return 1024 * Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemDiskTotal() {
        String output = getOutputByCmd("df -k | awk 'NR>1{a+=$2} END{print a}'", false, "系统磁盘总容量");
        if (StringUtils.isNotBlank(output)) {
            return 1024 * Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemDiskUsed() {
        String output = getOutputByCmd("df -k | awk 'NR>1{a+=$3} END{print a}'", false, "系统磁盘已使用量");
        if (StringUtils.isNotBlank(output)) {
            return 1024 * Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemDiskFree() {
        String output = getOutputByCmd("df -k | awk 'NR>1{a+=$4} END{print a}'", false,
            "系统磁盘剩余可使用量");
        if (StringUtils.isNotBlank(output)) {
            return 1024 * Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemDiskFreeMin() {
        // 跳过df第一行
        String output = getOutputByCmd(
            "df -k | tail -n +2 | awk 'NR==1{min=$4;next}{min=min<$4?min:$4}END{print min}'",
            false, "系统各磁盘中剩余可使用量最小值");
        if (StringUtils.isNotBlank(output)) {
            return 1024 * Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public int getSystemDiskNum() {
        String output = getOutputByCmd("df -k | awk 'NR>1{a+=1} END{print a}'", false, "系统挂载磁盘数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public long getYoungGcCount() {
        long gcCounts = 0L;
        for (GarbageCollectorMXBean garbageCollector : ManagementFactory
            .getGarbageCollectorMXBeans()) {
            String name = garbageCollector.getName();
            if (StringUtils.isNotBlank(name) && !name.contains("MarkSweep")) {
                gcCounts += garbageCollector.getCollectionCount();
            }
        }
        return gcCounts;
    }

    @Override
    public long getFullGcCount() {
        long gcCounts = 0L;
        for (GarbageCollectorMXBean garbageCollector : ManagementFactory
            .getGarbageCollectorMXBeans()) {
            String name = garbageCollector.getName();
            if (StringUtils.isNotBlank(name) && name.contains("MarkSweep")) {
                gcCounts += garbageCollector.getCollectionCount();
            }
        }
        return gcCounts;
    }

    @Override
    public long getYoungGcTime() {
        long gcTime = 0L;
        for (GarbageCollectorMXBean garbageCollector : ManagementFactory
            .getGarbageCollectorMXBeans()) {
            String name = garbageCollector.getName();
            if (StringUtils.isNotBlank(name) && !name.contains("MarkSweep")) {
                gcTime += garbageCollector.getCollectionTime();
            }
        }
        return gcTime;
    }

    @Override
    public long getFullGcTime() {
        long gcTime = 0L;
        for (GarbageCollectorMXBean garbageCollector : ManagementFactory
            .getGarbageCollectorMXBeans()) {
            String name = garbageCollector.getName();
            if (StringUtils.isNotBlank(name) && name.contains("MarkSweep")) {
                gcTime += garbageCollector.getCollectionTime();
            }
        }
        return gcTime;
    }

    @Override
    public int getCurrentProcessFdUsed() {
        String output = getOutputByCmd("ls /proc/%d/fd | wc -l", true, "jvm进程当前fd使用数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getSystemMaxFdSize() {
        String output = getOutputByCmd("cat /proc/sys/fs/file-max", false, "系统最大fd可用数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getCurrentSystemFdUsed() {
        String output = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $1}'", false,
            "系统fd使用量");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getCurrentProcessThreadNum() {
        try {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            return mxBean.getThreadCount();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前jvm进程线程使用数]失败", ex);
            return 0;
        }
    }

    @Override
    public int getCurrentProcessThreadNumPeak() {
        try {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            return mxBean.getPeakThreadCount();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[jvm进程启动以来线程数峰值]失败", ex);
            return 0;
        }
    }

    @Override
    public float getCurrentSystemDiskIOUsagePercent() {
        String output = getOutputByCmd("iostat -dkx | awk 'NR>2{a+=$14} END{print a}'", false,
            "系统当前磁盘 io 使用率");
        if (StringUtils.isNotBlank(output)) {
            return Float.parseFloat(output);
        }
        return 0;
    }

    @Override
    public float getCurrentProcessDiskIOAwaitTimePercent() {
        String output = getOutputByCmd("iotop -P -b -n 1 | awk '{if($1~/%d/) print $10}'", true,
            "jvm进程磁盘 io 读写等待时间占总时间百分比");
        if (StringUtils.isNotBlank(output)) {
            return Float.parseFloat(output);
        }
        return 0;
    }

    @Override
    public int getCurrentSystemIOPS() {
        String output = getOutputByCmd("iostat -dk | awk 'NR>2{a+=$2} END{print a}'", false,
            "系统当前每秒 io 请求数量");
        if (StringUtils.isNotBlank(output)) {
            double v = Double.parseDouble(output);
            return (int) v;
        }
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOReadBytesPS() {
        String output = getOutputByCmd("iostat -dk | awk 'NR>2{a+=$3} END{print a}'", false,
            "系统当前磁盘 io 每秒读取字节数");
        if (StringUtils.isNotBlank(output)) {
            double v = 1024 * Double.parseDouble(output);
            return (long) v;
        }
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOWriteBytesPS() {
        String output = getOutputByCmd("iostat -dk | awk 'NR>2{a+=$4} END{print a}'", false,
            "系统当前磁盘 io 每秒写入字节数");
        if (StringUtils.isNotBlank(output)) {
            double v = 1024 * Double.parseDouble(output);
            return (long) v;
        }
        return 0;
    }

    @Override
    public long getCurrentProcessDiskIOReadBytesPS() {
        String output = getOutputByCmd("pidstat -p %d -d | awk 'NR==4{print $4}'", true,
            "jvm进程当前磁盘 io 每秒读取字节数");
        if (StringUtils.isNotBlank(output)) {
            double v = 1024 * Double.parseDouble(output);
            return (long) v;
        }
        return 0;
    }

    @Override
    public long getCurrentProcessDiskIOWriteBytesPS() {
        String output = getOutputByCmd("pidstat -p %d -d | awk 'NR==4{print $5}'", true,
            "jvm进程当前磁盘 io 每秒写入字节数");
        if (StringUtils.isNotBlank(output)) {
            double v = 1024 * Double.parseDouble(output);
            return (long) v;
        }
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOResponseTimeAvg() {
        String output = getOutputByCmd("iostat -dkx | awk 'NR>2{a+=$10} END{print a}'", false,
            "系统每一个磁盘 io 请求的平均处理时间");
        if (StringUtils.isNotBlank(output)) {
            double v = Double.parseDouble(output);
            return (long) v;
        }
        return 0;
    }

    @Override
    public long getCurrentSystemDiskIOProcessTimeAvg() {
        String output = getOutputByCmd("iostat -dkx | awk 'NR>2{a+=$13} END{print a}'", false,
            "系统每次设备 io 处理的平均处理时间");
        if (StringUtils.isNotBlank(output)) {
            double v = Double.parseDouble(output);
            return (long) v;
        }
        return 0;
    }

    @Override
    public long getSystemNetworkReceiveBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getPid());
            long receiveBytesPS = curLinuxNetFlow.getBytesPS(lastLinuxNetFlow, true, true);
            lastLinuxNetFlow = curLinuxNetFlow;
            return receiveBytesPS;
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current system receives bytes per second get failed",
                    e);
            return 0;
        }
    }

    @Override
    public long getSystemNetworkSendBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getPid());
            long receiveBytesPS = curLinuxNetFlow.getBytesPS(lastLinuxNetFlow, true, false);
            lastLinuxNetFlow = curLinuxNetFlow;
            return receiveBytesPS;
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current system sends bytes per second get failed",
                    e);
            return 0;
        }
    }

    @Override
    public long getProcNetworkReceiveBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getPid());
            long receiveBytesPS = curLinuxNetFlow.getBytesPS(lastLinuxNetFlow, false, true);
            lastLinuxNetFlow = curLinuxNetFlow;
            return receiveBytesPS;
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current process receives bytes per second get failed",
                    e);
            return 0;
        }
    }

    @Override
    public long getProcNetworkSendBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getPid());
            long receiveBytesPS = curLinuxNetFlow.getBytesPS(lastLinuxNetFlow, false, false);
            lastLinuxNetFlow = curLinuxNetFlow;
            return receiveBytesPS;
        } catch (Exception e) {
            LOGGER
                .error(
                    "class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current process sends bytes per second get failed",
                    e);
            return 0;
        }
    }

    @Override
    public int getSystemNetworkTcpConnectionNum() {
        String output = getOutputByCmd("netstat -ant | wc -l", false, "系统当前tcp连接数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getProcNetworkTcpConnectionNum() {
        String output = getOutputByCmd("netstat -antp | grep %d | wc -l", true, "jvm进程当前tcp连接数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getSystemNetworkTcpTimeWaitNum() {
        String output = getOutputByCmd(
            "netstat -ant | awk '{if($NF~/^TIME_WAIT/) a+=1} END{print a}'", false,
            "系统当前处于 time wait 状态 tcp 连接数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getProcNetworkTcpTimeWaitNum() {
        String output = getOutputByCmd(
            "netstat -antp | grep %d | awk '{if($6~/^TIME_WAIT/) a+=1} END{print a}'", true,
            "jvm进程当前处于 time wait 状态 tcp 连接数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getSystemNetworkTcpCloseWaitNum() {
        String output = getOutputByCmd(
            "netstat -ant | awk '{if($NF~/^CLOSE_WAIT/) a+=1} END{print a}'", false,
            "系统当前处于 close wait 状态 tcp 连接数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public int getProcNetworkTcpCloseWaitNum() {
        String output = getOutputByCmd(
            "netstat -antp | grep %d | awk '{if($6~/^CLOSE_WAIT/) a+=1} END{print a}'", true,
            "jvm进程当前处于 close wait 状态 tcp 连接数");
        if (StringUtils.isNotBlank(output)) {
            return Integer.parseInt(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkTcpActiveOpens() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $6}'",
            false, "系统启动以来 Tcp 主动连接次数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkTcpPassiveOpens() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $7}'",
            false, "系统启动以来 Tcp 被动连接次数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkTcpAttemptFails() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $8}'",
            false, "系统启动以来 Tcp 连接失败次数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkTcpEstabResets() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $9}'",
            false, "系统启动以来 Tcp 连接异常断开次数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkTcpRetransSegs() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $13}'",
            false, "系统启动以来 Tcp 重传的报文段总个数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkTcpExtListenOverflows() {
        String output = getOutputByCmd(
            "netstat -s | egrep \"listen|LISTEN\" | awk '{a+=$1}{print a}'", false,
            "系统启动以来 Tcp 监听队列溢出次数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpInDatagrams() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $2}'",
            false, "系统启动以来 UDP 入包量");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpOutDatagrams() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $5}'",
            false, "系统启动以来 UDP 出包量");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpInErrors() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $4}'",
            false, "系统启动以来 UDP 入包错误数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpNoPorts() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $3}'",
            false, "系统启动以来 UDP 端口不可达个数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpSendBufferErrors() {
        String output = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $7}'",
            false, "系统启动以来 UDP 发送缓冲区满次数");
        if (StringUtils.isNotBlank(output)) {
            return Long.parseLong(output);
        }
        return 0;
    }

    private String getOutputByCmd(String procFDShell, boolean isProcess, String errorMsg) {
        //linux 根据shell命令获取资源
        Process process = null;
        BufferedReader br = null;
        try {
            if (isProcess) {
                procFDShell = String.format(procFDShell, PID);
            }
            String[] cmd = new String[] { "sh", "-c", procFDShell };
            process = Runtime.getRuntime().exec(cmd);
            int resultCode = process.waitFor();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                return line.trim();
            }
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[{}]失败", errorMsg, ex);
            return "";
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程对应输入流失败", errorMsg, errorMsg, ex);
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程失败", errorMsg, errorMsg, ex);
            }
        }
        LOGGER.error("获取系统资源项[{}]失败", errorMsg);
        return "";
    }
}
