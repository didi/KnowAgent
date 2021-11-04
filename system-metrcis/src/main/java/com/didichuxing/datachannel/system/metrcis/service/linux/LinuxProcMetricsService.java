package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.ProcMetrics;
import com.didichuxing.datachannel.system.metrcis.service.ProcMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.ProcResourceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public LinuxProcMetricsService() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();

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
        List<String> lines = getOutputByCmd("pidstat -p %d | awk 'NR==4{print $7}'", "当前进程cpu使用率");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Float.parseFloat(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuUtil||msg=data is null");
            return 0L;
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
        ProcMetrics procMetrics = new ProcMetrics();
        ProcResourceService procResourceService = new LinuxProcResourceService();

        procResourceService.clearCache();
        procMetrics.setProcStartupTime(procResourceService.getProcStartupTime());
        procMetrics.setProcUptime(procResourceService.getProcUptime());
        procMetrics.setProcPid(procResourceService.getProcPid());
        procMetrics.setProcCpuSys(procResourceService.getProcCpuSys());
        procMetrics.setProcCpuSwitchesPS(procResourceService.getProcCpuSwitchesPS());
        procMetrics.setProcCpuUser(procResourceService.getProcCpuUser());
        procMetrics.setProcCpuUtil(procResourceService.getProcCpuUtil());
        procMetrics.setProcCpuUtilTotalPercent(procResourceService.getProcCpuUtilTotalPercent());
        procMetrics.setProcCpuNonVoluntarySwitchesPS(procResourceService.getProcCpuNonVoluntarySwitchesPS());
        procMetrics.setProcCpuVoluntarySwitchesPS(procResourceService.getProcCpuVoluntarySwitchesPS());
        procMetrics.setProcIOAwaitTimePercent(procResourceService.getProcIOAwaitTimePercent());
        procMetrics.setProcIOReadBytesRate(procResourceService.getProcIOReadBytesRate());
        procMetrics.setProcIOReadRate(procResourceService.getProcIOReadRate());
        procMetrics.setProcIOWriteRate(procResourceService.getProcIOWriteRate());
        procMetrics.setProcIOWriteBytesRate(procResourceService.getProcIOWriteBytesRate());
        procMetrics.setProcMemData(procResourceService.getProcMemData());

        procMetrics.setProcMemLib(procResourceService.getProcMemLib());
        procMetrics.setProcMemRss(procResourceService.getProcMemRss());
        procMetrics.setProcMemSwap(procResourceService.getProcMemSwap());
        procMetrics.setProcMemDirty(procResourceService.getProcMemDirty());
        procMetrics.setProcMemText(procResourceService.getProcMemText());
        procMetrics.setProcMemShared(procResourceService.getProcMemShared());
        procMetrics.setProcMemUsed(procResourceService.getProcMemUsed());
        procMetrics.setProcMemUtil(procResourceService.getProcMemUtil());
        procMetrics.setProcMemVms(procResourceService.getProcMemVms());
        procMetrics.setJvmProcMemUsedPeak(procResourceService.getJvmProcMemUsedPeak());
        procMetrics.setJvmProcHeapMemoryUsed(procResourceService.getJvmProcHeapMemoryUsed());
        procMetrics.setJvmProcNonHeapMemoryUsed(procResourceService.getJvmProcNonHeapMemoryUsed());
        procMetrics.setJvmProcHeapSizeXmx(procResourceService.getJvmProcHeapSizeXmx());

        procMetrics.setJvmProcFullGcCount(procResourceService.getJvmProcFullGcCount());
        procMetrics.setJvmProcFullGcTime(procResourceService.getJvmProcFullGcTime());
        procMetrics.setJvmProcYoungGcCount(procResourceService.getJvmProcYoungGcCount());
        procMetrics.setJvmProcYoungGcTime(procResourceService.getJvmProcYoungGcTime());

        procMetrics.setProcOpenFdCount(procResourceService.getProcOpenFdCount());
        procMetrics.setJvmProcThreadNum(procResourceService.getJvmProcThreadNum());
        procMetrics.setJvmProcThreadNumPeak(procResourceService.getJvmProcThreadNumPeak());

        procMetrics.setProcPortListen(procResourceService.getProcPortListen());
        procMetrics.setProcNetworkSendBytesPs(procResourceService.getProcNetworkSendBytesPs());
        procMetrics.setProcNetworkReceiveBytesPs(procResourceService.getProcNetworkReceiveBytesPs());
        procMetrics.setProcNetworkTcpCloseWaitNum(procResourceService.getProcNetworkTcpCloseWaitNum());
        procMetrics.setProcNetworkTcpConnectionNum(procResourceService.getProcNetworkTcpConnectionNum());
        procMetrics.setProcNetworkTcpTimeWaitNum(procResourceService.getProcNetworkTcpTimeWaitNum());

        return procMetrics;
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
}
