package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.service.ProcMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * 获取进程级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxProcMetricsService implements ProcMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxProcMetricsService.class);

    /**
     * 当前agent进程id
     */
    private final long PID;

    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int CPU_NUM;

    private LinuxIORate lastLinuxIORate;

    private LinuxNetFlow lastLinuxNetFlow;

    private LinuxCpuTime lastLinuxCpuTime;

    public LinuxProcMetricsService() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();

        try {
            lastLinuxCpuTime = new LinuxCpuTime(getProcPid(), getSystemCpuNumCores());// 记录上次的cpu耗时
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=CpuTime init failed",
                    e);
        }

        try {
            lastLinuxIORate = new LinuxIORate(getProcPid());// 记录上次IO速率
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=processIORate init failed",
                    e);
        }

        try {
            lastLinuxNetFlow = new LinuxNetFlow(getProcPid());// 记录上次的收发字节数
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=NetFlow init failed",
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

    @Override
    public long getProcStartupTime() {
        try {
            return ManagementFactory.getRuntimeMXBean().getStartTime();
        } catch (Exception ex) {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcStartupTime()||msg=failed to get process startup time", ex);
            return 0;
        }
    }

    @Override
    public long getProcUptime() {
        return System.currentTimeMillis() - getProcStartupTime();
    }

    @Override
    public long getProcPid() {
        return PID;
    }

    @Override
    public double getProcCpuSys() {
        List<String> lines = getOutputByCmd("pidstat -p %d | awk 'NR==4{print $5}'", "当前进程系统态cpu使用率");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuSys()||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public long getProcCpuSwitchesPS() {
        return getProcCpuVoluntarySwitchesPS() + getProcCpuNonVoluntarySwitchesPS();
    }

    @Override
    public long getProcCpuVoluntarySwitchesPS() {
        List<String> lines = getOutputByCmd("pidstat -w -p %d | awk 'NR==4{print $4}'", "进程CPU每秒上下文自愿切换次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return (long) Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuVoluntarySwitchesPS||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcCpuNonVoluntarySwitchesPS() {
        List<String> lines = getOutputByCmd("pidstat -w -p %d | awk 'NR==4{print $5}'", "进程CPU每秒上下文非自愿切换次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return (long) Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuNonVoluntarySwitchesPS||msg=data is null");
            return 0L;
        }
    }

    @Override
    public float getProcCpuUtil() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(getProcPid(), getSystemCpuNumCores());
            float cpuUsage = curLinuxCpuTime.getUsage(lastLinuxCpuTime);
            lastLinuxCpuTime = curLinuxCpuTime;
            return cpuUsage;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current process's cpu usage get failed",
                    e);
            return 0f;
        }
    }

    @Override
    public float getProcCpuUtilTotalPercent() {
        return getProcCpuUtil() / getSystemCpuNumCores();
    }

    @Override
    public int getSystemCpuNumCores() {
        return CPU_NUM;
    }

    @Override
    public double getProcCpuUser() {
        List<String> lines = getOutputByCmd("pidstat -p %d | awk 'NR==4{print $4}'", "当前进程系统态cpu使用率");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuUser||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public double getProcIOReadRate() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(getProcPid());
            double ioReadTimesRate = curLinuxIORate.getIOReadTimesRate(lastLinuxIORate);
            this.lastLinuxIORate = curLinuxIORate;
            return ioReadTimesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOReadRate||msg=failed to get process IO read rate",
                    e);
        }
        return 0.0d;
    }

    @Override
    public long getProcIOReadBytesRate() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(getProcPid());
            double ioReadBytesRate = curLinuxIORate.getIOReadBytesRate(lastLinuxIORate);
            this.lastLinuxIORate = curLinuxIORate;
            return (long) ioReadBytesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOReadBytesRate||msg=failed to get process IO read bytes rate",
                    e);
        }
        return 0L;
    }

    @Override
    public double getProcIOWriteRate() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(getProcPid());
            double ioWriteTimesRate = curLinuxIORate.getIOWriteTimesRate(lastLinuxIORate);
            this.lastLinuxIORate = curLinuxIORate;
            return ioWriteTimesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOWriteRate||msg=failed to get process IO write rate",
                    e);
        }
        return 0.0d;
    }

    @Override
    public long getProcIOWriteBytesRate() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(getProcPid());
            double ioWriteBytesRate = curLinuxIORate.getIOWriteBytesRate(lastLinuxIORate);
            this.lastLinuxIORate = curLinuxIORate;
            return (long) ioWriteBytesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOReadBytesRate||msg=failed to get process IO write bytes rate",
                    e);
        }
        return 0L;
    }

    @Override
    public double getProcIOAwaitTimePercent() {
        List<String> lines = getOutputByCmd("iotop -p %d -P -b -n 1 | awk 'NR==4{print $10}'",
                "当前进程io读写等待时间占总时间百分比");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcIOAwaitTimePercent||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public long getProcMemData() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmData' | awk '{print $2}'", "当前进程data内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemData()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemDirty() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'RssAnon' | awk '{print $2}'", "当前进程dirty内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemDirty||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemLib() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmLib' | awk '{print $2}'", "当前进程lib内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemLib||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemRss() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmRSS' | awk '{print $2}'", "当前进程常驻内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemRss||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemShared() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'RssShmem' | awk '{print $2}'", "当前进程共享内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemShared||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemSwap() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmSwap' | awk '{print $2}'", "当前进程交换空间大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemSwap||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemText() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmExe' | awk '{print $2}'", "当前进程Text内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemText||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemUsed() {
        return getJvmProcHeapMemoryUsed() + getJvmProcNonHeapMemoryUsed();
    }

    @Override
    public double getProcMemUtil() {
        long memTotal = getSystemMemTotal();
        if (memTotal == 0) {
            LOGGER.warn("SystemMemoryTotal is zero");
            return 0.0d;
        }
        return 1.0 * getProcMemUsed() / memTotal;
    }

    private long getSystemMemTotal() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemTotal' | awk '{print $2}'", "系统总内存");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getSystemMemTotal||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getProcMemVms() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmSize' | awk '{print $2}'", "当前进程虚拟内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemVms||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getJvmProcHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆内内存使用量]失败", ex);
            return 0;
        }
    }

    @Override
    public long getJvmProcNonHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getNonHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆外内存使用量]失败", ex);
            return 0;
        }
    }

    @Override
    public long getJvmProcHeapSizeXmx() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getMax();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程最大可用堆内存]失败", ex);
            return 0;
        }
    }

    @Override
    public long getJvmProcMemUsedPeak() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmPeak' | awk '{print $2}'", "当前jvm进程启动以来内存使用量峰值");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcMemUsedPeak||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getJvmProcYoungGcCount() {
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
    public long getJvmProcFullGcCount() {
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
    public long getJvmProcYoungGcTime() {
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
    public long getJvmProcFullGcTime() {
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
    public int getJvmProcThreadNum() {
        try {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            return mxBean.getThreadCount();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前jvm进程线程使用数]失败", ex);
            return 0;
        }
    }

    @Override
    public int getJvmProcThreadNumPeak() {
        try {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            return mxBean.getPeakThreadCount();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[jvm进程启动以来线程数峰值]失败", ex);
            return 0;
        }
    }

    @Override
    public int getProcOpenFdCount() {
        List<String> lines = getOutputByCmd("ls /proc/%d/fd | wc -l", "jvm进程当前fd使用数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcOpenFdCount()||msg=data is null");
            return 0;
        }
    }

    @Override
    public List<Integer> getProcPortListen() {
        List<Integer> result = new ArrayList<>();
        List<String> output = getOutputByCmd("netstat -nltp | grep %d | awk '{print $4}' | awk -F: '{print $NF}'",
                "当前Jvm进程监听端口");
        if (!output.isEmpty()) {
            for (String s : output) {
                result.add(Integer.parseInt(s));
            }
        }
        return result;
    }

    @Override
    public long getProcNetworkReceiveBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcPid());
            double processReceiveBytesPs = curLinuxNetFlow.getProcessReceiveBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            return (long) processReceiveBytesPs;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcNetworkReceiveBytesPs||msg=data is null",
                    e);
            return 0L;
        }
    }

    @Override
    public long getProcNetworkSendBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcPid());
            double processTransmitBytesPs = curLinuxNetFlow.getProcessTransmitBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            return (long) processTransmitBytesPs;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcNetworkSendBytesPs||msg=data is null",
                    e);
            return 0L;
        }
    }

    @Override
    public int getProcNetworkTcpConnectionNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep -c %d", "当前Jvm进程当前tcp连接数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpConnectionNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public int getProcNetworkTcpTimeWaitNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'TIME_WAIT'", "当前Jvm进程当前处于 time wait 状态 tcp 连接数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpTimeWaitNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public int getProcNetworkTcpCloseWaitNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'CLOSE_WAIT'", "当前Jvm进程当前处于 close wait 状态 tcp 连接数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpCloseWaitNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public ProcMetrics getProcMetrics() {
        return buildProMetrics();
    }

    /**
     * 获取所有进程指标
     * @return
     */
    private ProcMetrics buildProMetrics() {
        ProcMetrics procMetrics = new ProcMetrics();

        LinuxProcessResource processResource = new LinuxProcessResource();

        // pid and time
        buildProcPidAndTimeMetrics(procMetrics);

        // cpu
        buildCpuMetrics(procMetrics, processResource);

        // io
        buildIOMetrics(procMetrics);

        // memory
        buildMemoryMetrics(procMetrics, processResource);

        // gc
        buildGcMetrics(procMetrics);

        // thread
        buildThreadMetrics(procMetrics);

        // fd
        procMetrics.setProcOpenFdCount(getProcOpenFdCount());

        // network
        buildNetworkMetrics(procMetrics, processResource);

        return procMetrics;
    }

    private void buildNetworkMetrics(ProcMetrics procMetrics, LinuxProcessResource processResource) {
        Map<String, Long> processNetworkTcpStat = processResource.getProcessNetworkTcpStat();
        procMetrics.setProcNetworkTcpTimeWaitNum((int) getResourceValueByKey(processNetworkTcpStat, "TIME_WAIT", "getProcNetworkTcpTimeWaitNum"));
        procMetrics.setProcPortListen(getProcPortListen());
        procMetrics.setProcNetworkTcpCloseWaitNum((int) getResourceValueByKey(processNetworkTcpStat, "CLOSE_WAIT", "getProcNetworkTcpCloseWaitNum"));
        procMetrics.setProcNetworkTcpConnectionNum(getProcNetworkTcpConnectionNumByTcpStat(processNetworkTcpStat));
        procMetrics.setProcNetworkReceiveBytesPs(getProcNetworkReceiveBytesPs());
        procMetrics.setProcNetworkSendBytesPs(getProcNetworkSendBytesPs());
    }

    private void buildThreadMetrics(ProcMetrics procMetrics) {
        procMetrics.setJvmProcThreadNumPeak(getJvmProcThreadNumPeak());
        procMetrics.setJvmProcThreadNum(getJvmProcThreadNum());
    }

    private void buildGcMetrics(ProcMetrics procMetrics) {
        procMetrics.setJvmProcYoungGcTime(getJvmProcYoungGcTime());
        procMetrics.setJvmProcYoungGcCount(getJvmProcYoungGcCount());
        procMetrics.setJvmProcFullGcTime(getJvmProcFullGcTime());
        procMetrics.setJvmProcFullGcCount(getJvmProcFullGcCount());
    }

    private void buildMemoryMetrics(ProcMetrics procMetrics, LinuxProcessResource processResource) {
        Map<String, Long> processMemoryInfo = processResource.getProcessMemoryInfo();
        procMetrics.setProcMemVms(getResourceValueByKey(processMemoryInfo, "VmSize:", "getProcMemVms"));
        procMetrics.setProcMemData(getResourceValueByKey(processMemoryInfo, "VmData:", "getProcMemData"));
        procMetrics.setProcMemDirty(getResourceValueByKey(processMemoryInfo, "RssAnon:", "getProcMemDirty"));
        procMetrics.setProcMemShared(getResourceValueByKey(processMemoryInfo, "RssShmem:", "getProcMemShared"));
        procMetrics.setProcMemRss(getResourceValueByKey(processMemoryInfo, "VmRSS:", "getProcMemRss"));
        procMetrics.setProcMemText(getResourceValueByKey(processMemoryInfo, "VmExe:", "getProcMemText"));
        procMetrics.setProcMemLib(getResourceValueByKey(processMemoryInfo, "VmLib:", "getProcMemLib"));
        long jvmProcHeapMemoryUsed = getJvmProcHeapMemoryUsed();
        long jvmProcNonHeapMemoryUsed = getJvmProcNonHeapMemoryUsed();
        procMetrics.setJvmProcHeapMemoryUsed(jvmProcHeapMemoryUsed);
        procMetrics.setJvmProcNonHeapMemoryUsed(jvmProcNonHeapMemoryUsed);
        procMetrics.setProcMemUsed(jvmProcHeapMemoryUsed + jvmProcNonHeapMemoryUsed);
        procMetrics.setProcMemUtil(getProcMemUtil());
        procMetrics.setProcMemSwap(getProcMemSwap());
        procMetrics.setJvmProcMemUsedPeak(getJvmProcMemUsedPeak());
        procMetrics.setJvmProcHeapSizeXmx(getJvmProcHeapSizeXmx());
    }

    private void buildIOMetrics(ProcMetrics procMetrics) {
        procMetrics.setProcIOWriteBytesRate(getProcIOWriteBytesRate());
        procMetrics.setProcIOWriteRate(getProcIOWriteRate());
        procMetrics.setProcIOReadRate(getProcIOReadRate());
        procMetrics.setProcIOReadBytesRate(getProcIOReadBytesRate());
        procMetrics.setProcIOAwaitTimePercent(getProcIOAwaitTimePercent());
    }

    private void buildCpuMetrics(ProcMetrics procMetrics, LinuxProcessResource processResource) {
        List<String> processCpuStat = processResource.getProcessCpuStat();
        List<String> processCpuSwitchesPS = processResource.getProcessCpuSwitchesPS();
        procMetrics.setProcCpuUser(getOneSystemResource(processCpuStat, 0, 3, "当前进程用户态cpu使用率"));
        procMetrics.setProcCpuSys(getOneSystemResource(processCpuStat, 1, 3, "当前进程系统态cpu使用率"));
        float procCpuUtil = (float) getOneSystemResource(processCpuStat, 2, 3, "当前进程系统态cpu使用率");
        procMetrics.setProcCpuUtil(procCpuUtil);
        procMetrics.setProcCpuUtilTotalPercent(procCpuUtil / getSystemCpuNumCores() );
        long procCpuVoluntarySwitchesPS = (long) getOneSystemResource(processCpuSwitchesPS, 0, 2, "当前进程cpu每秒自愿上下文交换次数");
        long procCpuNonVoluntarySwitchesPS = (long) getOneSystemResource(processCpuSwitchesPS, 1, 2, "当前进程cpu每秒非自愿上下文交换次数");
        procMetrics.setProcCpuVoluntarySwitchesPS(procCpuVoluntarySwitchesPS);
        procMetrics.setProcCpuNonVoluntarySwitchesPS(procCpuNonVoluntarySwitchesPS);
        procMetrics.setProcCpuSwitchesPS(procCpuVoluntarySwitchesPS + procCpuNonVoluntarySwitchesPS);
    }

    private void buildProcPidAndTimeMetrics(ProcMetrics procMetrics) {
        procMetrics.setProcPid(getProcPid());
        procMetrics.setProcStartupTime(getProcStartupTime());
        procMetrics.setProcUptime(getProcUptime());
    }

    final class LinuxProcessResource {

        /**
         * 当前进程网络Tcp各状态统计
         * Tcp状态：个数
         */
        private Map<String, Long> processNetworkTcpStat;

        /**
         * 当前进程CPU状态
         * %usr %system %CPU
         */
        private List<String> processCpuStat;

        /**
         * 当前进程CPU上下文切换次数
         * 返回 每秒主动切换次数 每秒被动切换次数
         */
        private List<String> processCpuSwitchesPS;

        /**
         * 进程内存占用情况
         */
        private Map<String, Long> processMemoryInfo;

        public LinuxProcessResource() {
            processCpuStat = getOutputByCmd("pidstat -p %d | awk 'NR==4{print $4,$5,$7}'", "进程CPU使用情况");
            processNetworkTcpStat = getKeyValueResource("netstat -antp | grep %d | awk '{++S[$6]} END {for(a in S) print a, S[a]}'", 1, "进程网络Tcp情况");
            processCpuSwitchesPS = getOutputByCmd("pidstat -w -p %d | awk 'NR==4{print $4,$5}'", "进程CPU每秒上下文切换次数");
            processMemoryInfo = getKeyValueResource("cat /proc/%d/status | awk 'NR>12 && NR<28'", 1024, "进程内存使用情况");
        }

        /**
         * 根据命令行获取key value 资源
         * @param procFDShell 命令行
         * @param size 单位转换
         * @param message 资源信息
         * @return
         */
        private Map<String, Long> getKeyValueResource(String procFDShell, int size, String message) {
            Map<String, Long> result = new HashMap<>();
            List<String> lines = getOutputByCmd(procFDShell, message);
            if(!lines.isEmpty()) {
                for (String line : lines) {
                    String[] array = line.split("\\s+");
                    if(array.length < 2) {
                        LOGGER.error("获取系统资源项[{}]失败,每行数据太短", message);
                        return result;
                    }
                    long value = size * Long.parseLong(array[1]);
                    result.put(array[0], value);
                }
            }
            return result;
        }

        public Map<String, Long> getProcessNetworkTcpStat() {
            return processNetworkTcpStat;
        }

        public List<String> getProcessCpuStat() {
            return processCpuStat;
        }

        public List<String> getProcessCpuSwitchesPS() {
            return processCpuSwitchesPS;
        }

        public Map<String, Long> getProcessMemoryInfo() {
            return processMemoryInfo;
        }
    }

    /**
     * linux 根据shell命令获取系统或者进程资源
     * @param procFDShell shell命令
     * @param resourceMessage    资源描述信息
     * @return
     */
    private List<String> getOutputByCmd(String procFDShell, String resourceMessage) {
        Process process = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            procFDShell = String.format(procFDShell, PID);
            String[] cmd = new String[] { "sh", "-c", procFDShell };
            process = Runtime.getRuntime().exec(cmd);
            int resultCode = process.waitFor();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line.trim());
            }
            return lines;
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[{}]失败", resourceMessage, ex);
            return Collections.emptyList();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程对应输入流失败", resourceMessage, resourceMessage, ex);
            }
            try {
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("获取系统资源项[{}]失败，原因为关闭执行获取{}的脚本进程失败", resourceMessage, resourceMessage, ex);
            }
        }
    }

    /**
     * 获取一项系统资源
     * @param systemResource 系统资源
     * @param index 下标
     * @param length 单行数据长度
     * @param message 某状态描述
     * @return
     */
    private double getOneSystemResource(List<String> systemResource, int index, int length, String message) {
        try {
            if (!systemResource.isEmpty() && StringUtils.isNotBlank(systemResource.get(0))) {
                String cpuStat = systemResource.get(0);
                String[] array = cpuStat.split("\\s+");
                if (array.length < length) {
                    LOGGER.error("获取系统资源项[{}}]失败", message);
                    return 0.0d;
                }
                return Double.parseDouble(array[index]);
            }
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getOneSystemResource()||msg=failed to get system resource",
                    e);
        }
        return 0.0d;
    }

    /**
     * 由key获取系统资源中的值
     * @param resource key为具体资源名称， value为其值
     * @param key 键
     * @param methodName 调用方法名称
     * @return
     */
    private long getResourceValueByKey(Map<String, Long> resource, String key, String methodName) {
        try {
            if (resource.containsKey(key)) {
                return resource.get(key);
            }
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method={}()||msg=failed to get resource", methodName,
                    e);
        }
        return 0L;
    }

    private int getProcNetworkTcpConnectionNumByTcpStat(Map<String, Long> processNetworkTcpStat) {
        long tcpConnectionNum = 0;
        Set<Map.Entry<String, Long>> entries = processNetworkTcpStat.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            tcpConnectionNum += entry.getValue();
        }
        return (int) tcpConnectionNum;
    }
}
