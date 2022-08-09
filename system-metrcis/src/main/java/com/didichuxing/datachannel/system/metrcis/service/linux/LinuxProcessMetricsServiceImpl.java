package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.bean.PeriodStatistics;
import com.didichuxing.datachannel.system.metrcis.service.ProcessMetricsService;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * 获取进程级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author william.
 * @Date 2021/11/3
 */
public class LinuxProcessMetricsServiceImpl extends LinuxMetricsService implements ProcessMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxProcessMetricsServiceImpl.class);

    /**
     * 当前agent进程id
     */
    private final Long PID;

    private LinuxCpuTime lastLinuxCpuTimeProcessCpuUtil;
    private LinuxCpuTime lastLinuxCpuTimeProcessCpuUtilTotalPercent;

    private LinuxNetFlow lastLinuxNetFlowSend;

    private LinuxNetFlow lastLinuxNetFlowReceive;

    private LinuxIORate lastLinuxIORateReadRate;
    private LinuxIORate lastLinuxIORateReadBytesRate;
    private LinuxIORate lastLinuxIORateWriteRate;
    private LinuxIORate lastLinuxIORateWriteBytesRate;

    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int CPU_NUM;

    /**************************** 待计算字段 ****************************/

    private PeriodStatistics procCpuUtil = new PeriodStatistics();

    private PeriodStatistics procNetworkReceiveBytesPs = new PeriodStatistics();

    private PeriodStatistics procNetworkSendBytesPs = new PeriodStatistics();

    private PeriodStatistics procCpuUtilTotalPercent = new PeriodStatistics();

    private PeriodStatistics procCpuSys = new PeriodStatistics();

    private PeriodStatistics procCpuUser = new PeriodStatistics();

    private PeriodStatistics procCpuSwitchesPS = new PeriodStatistics();

    private PeriodStatistics procCpuVoluntarySwitchesPS = new PeriodStatistics();

    private PeriodStatistics procCpuNonVoluntarySwitchesPS = new PeriodStatistics();

    private PeriodStatistics procIOReadRate = new PeriodStatistics();

    private PeriodStatistics procIOReadBytesRate = new PeriodStatistics();

    private PeriodStatistics procIOWriteRate = new PeriodStatistics();

    private PeriodStatistics procIOWriteBytesRate = new PeriodStatistics();

    private PeriodStatistics procIOReadWriteRate = new PeriodStatistics();

    private PeriodStatistics procIOReadWriteBytesRate = new PeriodStatistics();

    private PeriodStatistics procIOAwaitTimePercent = new PeriodStatistics();

    private static LinuxProcessMetricsServiceImpl instance;

    private Long yGcCountCurrent = 0l;
    private Long yGcTimeCurrent = 0l;
    private Long fGcCountCurrent = 0l;
    private Long fGcTimeCurrent = 0l;
    private Long yGcCountPre = 0l;
    private Long yGcTimePre = 0l;
    private Long fGcCountPre = 0l;
    private Long fGcTimePre = 0l;

    public static synchronized LinuxProcessMetricsServiceImpl getInstance() {
        if(null == instance) {
            instance = new LinuxProcessMetricsServiceImpl();
        }
        return instance;
    }

    private LinuxProcessMetricsServiceImpl() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();
        try {
            lastLinuxCpuTimeProcessCpuUtil = new LinuxCpuTime(getProcessPid(), CPU_NUM);// 记录上次的cpu耗时
            lastLinuxCpuTimeProcessCpuUtilTotalPercent = new LinuxCpuTime(getProcessPid(), CPU_NUM);
        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=LinuxProcessMetricsServiceImpl()||msg=CpuTime init failed",
                    e);
        }
        try {
            lastLinuxNetFlowSend = new LinuxNetFlow(getProcessPid());// 记录上次的收发字节数
            lastLinuxNetFlowReceive = new LinuxNetFlow(getProcessPid());
        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=LinuxProcessMetricsServiceImpl()||msg=NetFlow init failed",
                    e);
        }
        try {
            lastLinuxIORateReadBytesRate = new LinuxIORate(getProcessPid());// 记录上次IO速率
            lastLinuxIORateWriteRate = new LinuxIORate(getProcessPid());// 记录上次IO速率
            lastLinuxIORateWriteBytesRate = new LinuxIORate(getProcessPid());// 记录上次IO速率
            lastLinuxIORateReadRate = new LinuxIORate(getProcessPid());// 记录上次IO速率;
        } catch (Exception e) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=LinuxProcessMetricsServiceImpl()||msg=processIORate init failed",
                    e);
        }
    }

    @Override
    public Long getProcessStartupTime() {
        try {
            return ManagementFactory.getRuntimeMXBean().getStartTime();
        } catch (Exception ex) {
            LOGGER.error("class=LinuxProcessMetricsServiceImpl||method=getProcessStartupTime()||msg=failed to get process startup time", ex);
            return 0L;
        }
    }

    @Override
    public Long getProcUptime() {
        return System.currentTimeMillis() - getProcessStartupTime();
    }

    @Override
    public Long getProcessPid() {
        return PID;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuUtil() {
        procCpuUtil.add(getCurrentProcCpuUtil());
    }

    @Override
    public PeriodStatistics getProcCpuUtil() {
        if(procCpuUtil.isEmpty()) {
            calcProcCpuUtil();
        }
        return procCpuUtil.snapshot();
    }

    @Override
    public Double getCurrentProcCpuUtil() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(getProcessPid(), CPU_NUM);
            float cpuUsage = curLinuxCpuTime.getUsage(lastLinuxCpuTimeProcessCpuUtil);
            lastLinuxCpuTimeProcessCpuUtil = curLinuxCpuTime;
            return MathUtil.divideWith2Digit(Float.valueOf(cpuUsage).doubleValue(), 1.0d);
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current process's cpu usage get failed",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getProcCpuUtilTotalPercent() {
        if(procCpuUtilTotalPercent.isEmpty()) {
            calcProcCpuUtilTotalPercent();
        }
        return procCpuUtilTotalPercent.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuUtilTotalPercent() {
        procCpuUtilTotalPercent.add(getProcCpuUtilTotalPercentOnly());
    }

    private Double getProcCpuUtilTotalPercentOnly() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(getProcessPid(), CPU_NUM);
            float cpuUsage = curLinuxCpuTime.getUsage(lastLinuxCpuTimeProcessCpuUtilTotalPercent);
            lastLinuxCpuTimeProcessCpuUtilTotalPercent = curLinuxCpuTime;
            return MathUtil.divideWith2Digit(Float.valueOf(cpuUsage).doubleValue(), CPU_NUM);
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getCurrentProcessCpuUsage||msg=current process's cpu usage get failed",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getProcCpuSys() {
        if(procCpuSys.isEmpty()) {
            calcProcCpuSys();
        }
        return procCpuSys.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuSys() {
        procCpuSys.add(getProcCpuSysOnly());
    }

    private Double getProcCpuSysOnly() {
        List<String> lines = getOutputByCmd("pidstat -p %d 1 1 | awk 'NR==4{print $5}'", "当前进程系统态cpu使用率", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuSysOnly()||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getProcCpuUser() {
        if(procCpuUser.isEmpty()) {
            calcProcCpuUser();
        }
        return procCpuUser.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuUser() {
        procCpuUser.add(getProcCpuUserOnly());
    }

    private Double getProcCpuUserOnly() {
        List<String> lines = getOutputByCmd("pidstat -p %d 1 1 | awk 'NR==4{print $4}'", "当前进程系统态cpu使用率", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuUserOnly||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getProcCpuSwitchesPS() {
        if(procCpuSwitchesPS.isEmpty()) {
            calcProcCpuSwitchesPS();
        }
        return procCpuSwitchesPS.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuSwitchesPS() {
        procCpuSwitchesPS.add(getProcCpuSwitchesPSOnly());
    }

    private Double getProcCpuSwitchesPSOnly() {
        return getProcCpuVoluntarySwitchesPSOnly() + getProcCpuNonVoluntarySwitchesPSOnly();
    }

    @Override
    public PeriodStatistics getProcCpuVoluntarySwitchesPS() {
        if(procCpuVoluntarySwitchesPS.isEmpty()) {
            calcProcCpuVoluntarySwitchesPS();
        }
        return procCpuVoluntarySwitchesPS.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuVoluntarySwitchesPS() {
        procCpuVoluntarySwitchesPS.add(getProcCpuVoluntarySwitchesPSOnly());
    }

    private Double getProcCpuVoluntarySwitchesPSOnly() {
        List<String> lines = getOutputByCmd("pidstat -w -p %d 1 1 | awk 'NR==4{print $4}'", "进程CPU每秒上下文自愿切换次数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuVoluntarySwitchesPSOnly||msg=data is null");
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getProcCpuNonVoluntarySwitchesPS() {
        if(procCpuNonVoluntarySwitchesPS.isEmpty()) {
            calcProcCpuNonVoluntarySwitchesPS();
        }
        return procCpuNonVoluntarySwitchesPS.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcCpuNonVoluntarySwitchesPS() {
        procCpuNonVoluntarySwitchesPS.add(getProcCpuNonVoluntarySwitchesPSOnly());
    }

    private Double getProcCpuNonVoluntarySwitchesPSOnly() {
        List<String> lines = getOutputByCmd("pidstat -w -p %d 1 1 | awk 'NR==4{print $5}'", "进程CPU每秒上下文非自愿切换次数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcCpuNonVoluntarySwitchesPSOnly||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Long getProcMemUsed() {
        return getJvmProcHeapMemoryUsed() + getJvmProcNonHeapMemoryUsed();
    }

    @Override
    public Double getProcMemUtil() {
        long memTotal = getSystemMemTotal();
        if (memTotal == 0) {
            LOGGER.warn("SystemMemoryTotal is zero");
            return 0.0d;
        }
        return MathUtil.divideWith2Digit(getProcMemUsed() * 100, memTotal);
    }

    @Override
    public Long getProcMemData() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmData' | awk '{print $2}'", "当前进程data内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemData()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemDirty() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'RssAnon' | awk '{print $2}'", "当前进程dirty内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemDirty||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemLib() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmLib' | awk '{print $2}'", "当前进程lib内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemLib||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemRss() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmRSS' | awk '{print $2}'", "当前进程常驻内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemRss||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemShared() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'RssShmem' | awk '{print $2}'", "当前进程共享内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemShared||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemSwap() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmSwap' | awk '{print $2}'", "当前进程交换空间大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemSwap||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemText() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmExe' | awk '{print $2}'", "当前进程Text内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemText||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getProcMemVms() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmSize' | awk '{print $2}'", "当前进程虚拟内存大小", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return 1024 * Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcMemVms||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getJvmProcHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆内内存使用量]失败", ex);
            return 0L;
        }
    }

    @Override
    public Long getJvmProcNonHeapMemoryUsed() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getNonHeapMemoryUsage().getUsed();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程堆外内存使用量]失败", ex);
            return 0L;
        }
    }

    @Override
    public Long getJvmProcHeapSizeXmx() {
        try {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            return memoryMXBean.getHeapMemoryUsage().getMax();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前进程最大可用堆内存]失败", ex);
            return 0l;
        }
    }

    @Override
    public Long getJvmProcMemUsedPeak() {
        List<String> lines = getOutputByCmd("cat /proc/%d/status | grep 'VmPeak' | awk '{print $2}'", "当前jvm进程启动以来内存使用量峰值", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcMemUsedPeak||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Double getJvmProcHeapMemUsedPercent() {
        return MathUtil.divideWith2Digit(getJvmProcHeapMemoryUsed() * 100, getJvmProcHeapSizeXmx());
    }

    @Override
    public PeriodStatistics getProcIOReadRate() {
        if(procIOReadRate.isEmpty()) {
            calcProcIOReadRate();
        }
        return procIOReadRate.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOReadRate() {
        procIOReadRate.add(getProcIOReadRateOnly());
    }

    private Double getProcIOReadRateOnly() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(PID);
            double ioReadTimesRate = curLinuxIORate.getIOReadTimesRate(lastLinuxIORateReadRate);
            this.lastLinuxIORateReadRate = curLinuxIORate;
            return ioReadTimesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOReadRateOnly||msg=failed to get process IO read rate",
                    e);
        }
        return 0.0d;
    }

    @Override
    public PeriodStatistics getProcIOReadBytesRate() {
        if(procIOReadBytesRate.isEmpty()) {
            calcProcIOReadBytesRate();
        }
        return procIOReadBytesRate.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOReadBytesRate() {
        procIOReadBytesRate.add(getProcIOReadBytesRateOnly());
    }

    private Double getProcIOReadBytesRateOnly() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(PID);
            double ioReadBytesRate = curLinuxIORate.getIOReadBytesRate(lastLinuxIORateReadBytesRate);
            this.lastLinuxIORateReadBytesRate = curLinuxIORate;
            return ioReadBytesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOReadBytesRateOnly||msg=failed to get process IO read bytes rate",
                    e);
        }
        return 0d;
    }

    @Override
    public PeriodStatistics getProcIOWriteRate() {
        if(procIOWriteRate.isEmpty()) {
            calcProcIOWriteRate();
        }
        return procIOWriteRate.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOWriteRate() {
        procIOWriteRate.add(getProcIOWriteRateOnly());
    }

    private Double getProcIOWriteRateOnly() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(PID);
            double ioWriteTimesRate = curLinuxIORate.getIOWriteTimesRate(lastLinuxIORateWriteRate);
            this.lastLinuxIORateWriteRate = curLinuxIORate;
            return ioWriteTimesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOWriteRateOnly||msg=failed to get process IO write rate",
                    e);
        }
        return 0d;
    }

    @Override
    public PeriodStatistics getProcIOWriteBytesRate() {
        if(procIOWriteBytesRate.isEmpty()) {
            calcProcIOWriteBytesRate();
        }
        return procIOWriteBytesRate.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOWriteBytesRate() {
        procIOWriteBytesRate.add(getProcIOWriteBytesRateOnly());
    }

    private Double getProcIOWriteBytesRateOnly() {
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(PID);
            double ioWriteBytesRate = curLinuxIORate.getIOWriteBytesRate(lastLinuxIORateWriteBytesRate);
            this.lastLinuxIORateWriteBytesRate = curLinuxIORate;
            return ioWriteBytesRate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getProcIOWriteBytesRateOnly||msg=failed to get process IO write bytes rate",
                    e);
        }
        return 0d;
    }

    @Override
    public PeriodStatistics getProcIOReadWriteRate() {
        if(procIOReadWriteRate.isEmpty()) {
            calcProcIOReadWriteRate();
        }
        return procIOReadWriteRate.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOReadWriteRate() {
        procIOReadWriteRate.add(getProcIOReadWriteRateOnly());
    }

    private Double getProcIOReadWriteRateOnly() {
        return getProcIOReadRateOnly() + getProcIOWriteRateOnly();
    }

    @Override
    public PeriodStatistics getProcIOReadWriteBytesRate() {
        if(procIOReadWriteBytesRate.isEmpty()) {
            calcProcIOReadWriteBytesRate();
        }
        return procIOReadWriteBytesRate.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOReadWriteBytesRate() {
        procIOReadWriteBytesRate.add(getProcIOReadWriteBytesRateOnly());
    }

    private Double getProcIOReadWriteBytesRateOnly() {
        return getProcIOReadBytesRateOnly() + getProcIOWriteBytesRateOnly();
    }

    @Override
    public PeriodStatistics getProcIOAwaitTimePercent() {
        if(procIOAwaitTimePercent.isEmpty()) {
            calcProcIOAwaitTimePercent();
        }
        return procIOAwaitTimePercent.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcIOAwaitTimePercent() {
        procIOAwaitTimePercent.add(getProcIOAwaitTimePercentOnly());
    }

    private Double getProcIOAwaitTimePercentOnly() {
        List<String> lines = getOutputByCmd("iotop -P -b -n 1 | grep %d | awk 'NR==1{print $10}'",
                "当前进程io读写等待时间占总时间百分比", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcIOAwaitTimePercent||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Long getJvmProcYoungGcCount() {
        yGcCountCurrent = getJvmProcYoungGcCountOnly();
        Long yGcCount = yGcCountCurrent - yGcCountPre;
        yGcCountPre = yGcCountCurrent;
        return yGcCount;
    }

    private Long getJvmProcYoungGcCountOnly() {
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
    public Long getJvmProcFullGcCount() {
        fGcCountCurrent = getJvmProcFullGcCountOnly();
        Long fGcCount = fGcCountCurrent - fGcCountPre;
        fGcCountPre = fGcCountCurrent;
        return fGcCount;
    }

    private Long getJvmProcFullGcCountOnly() {
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
    public Long getJvmProcYoungGcTime() {
        yGcTimeCurrent = getJvmProcYoungGcTimeOnly();
        Long yGcTime = yGcTimeCurrent - yGcTimePre;
        yGcTimePre = yGcTimeCurrent;
        return yGcTime;
    }

    private Long getJvmProcYoungGcTimeOnly() {
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
    public Long getJvmProcFullGcTime() {
        fGcTimeCurrent = getJvmProcFullGcTimeOnly();
        Long fGcTime = fGcTimeCurrent - fGcTimePre;
        fGcTimePre = fGcTimeCurrent;
        return fGcTime;
    }

    private Long getJvmProcFullGcTimeOnly() {
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
    public Double getJvmProcS0C() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程S0C", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[0]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcS0C||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcS0C||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcS1C() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程S1C", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[1]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcS1C||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcS1C||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcS0U() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程S0U", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[2]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcS0U||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcS0U||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcS1U() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程S1U", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[3]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcS1U||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcS1U||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcEC() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程EC", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[4]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcEC||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcEC||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcEU() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程EU", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[5]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcEU||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcEU||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcOC() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程OC", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[6]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcOC||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcOC||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcOU() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程OU", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[7]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcOU||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcOU||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcMC() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程MC", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[8]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcMC||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcMC||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcMU() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程MU", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[9]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcMU||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcMU||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcCCSC() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程CCSC", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[10]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcCCSC||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcCCSC||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Double getJvmProcCCSU() {
        List<String> lines = getOutputByCmd("jstat -gc %d",
                "当前jvm进程CCSU", PID);
        if (!lines.isEmpty() && 2 == lines.size() && StringUtils.isNotBlank(lines.get(1))) {
            String[] properties = lines.get(1).split("\\s+");
            if(19 == properties.length) {
                return Double.parseDouble(properties[11]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxProcMetricsService||method=getJvmProcCCSU||msg=data is invalid, data is %s", lines.get(1))
                );
                return 0d;
            }
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getJvmProcCCSU||msg=data is null");
            return 0d;
        }
    }

    @Override
    public Integer getJvmProcThreadNum() {
        try {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            return mxBean.getThreadCount();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[当前jvm进程线程使用数]失败", ex);
            return 0;
        }
    }

    @Override
    public Integer getJvmProcThreadNumPeak() {
        try {
            ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
            return mxBean.getPeakThreadCount();
        } catch (Exception ex) {
            LOGGER.error("获取系统资源项[jvm进程启动以来线程数峰值]失败", ex);
            return 0;
        }
    }

    @Override
    public Integer getProcOpenFdCount() {
        List<String> lines = getOutputByCmd("ls /proc/%d/fd | wc -l", "jvm进程当前fd使用数", PID);
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
                "当前Jvm进程监听端口", PID);
        if (!output.isEmpty()) {
            for (String s : output) {
                result.add(Integer.parseInt(s));
            }
        }
        return result;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcNetworkReceiveBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcessPid());
            double processReceiveBytesPs = curLinuxNetFlow.getProcessReceiveBytesPs(lastLinuxNetFlowReceive);
            lastLinuxNetFlowReceive = curLinuxNetFlow;
            procNetworkReceiveBytesPs.add(processReceiveBytesPs);
        } catch (Exception e) {
            LOGGER.error(
                    String.format(
                            "class=LinuxProcMetricsService||method=calcProcNetworkReceiveBytesPs||msg=%s",
                            e.getMessage()
                    ),
                    e
            );
            procNetworkReceiveBytesPs.add(0d);
        }
    }

    @Override
    public PeriodStatistics getProcNetworkReceiveBytesPs() {
        if(procNetworkReceiveBytesPs.isEmpty()) {
            calcProcNetworkReceiveBytesPs();
        }
        return procNetworkReceiveBytesPs.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcProcNetworkSendBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcessPid());
            double processTransmitBytesPs = curLinuxNetFlow.getProcessTransmitBytesPs(lastLinuxNetFlowSend);
            lastLinuxNetFlowSend = curLinuxNetFlow;
            procNetworkSendBytesPs.add(processTransmitBytesPs);
        } catch (Exception e) {
            LOGGER.error(
                    String.format(
                            "class=LinuxProcMetricsService||method=calcProcNetworkSendBytesPs||msg=%s",
                            e.getMessage()
                    ),
                    e
            );
            procNetworkSendBytesPs.add(0d);
        }
    }

    @Override
    public PeriodStatistics getProcNetworkSendBytesPs() {
        if(procNetworkSendBytesPs.isEmpty()) {
            calcProcNetworkSendBytesPs();
        }
        return procNetworkSendBytesPs.snapshot();
    }

    @Override
    public PeriodStatistics getProcNetworkConnRate() {
        return PeriodStatistics.defaultValue();
    }

    @Override
    public Integer getProcNetworkTcpConnectionNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep -c %d", "当前Jvm进程当前tcp连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpConnectionNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpListeningNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'LISTEN'", "当前Jvm进程当前处于 listening 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpListeningNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpEstablishedNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'ESTABLISHED'", "当前Jvm进程当前处于 ESTABLISHED 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpEstablishedNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpSynSentNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'SYN_SENT'", "当前Jvm进程当前处于 SYN_SENT 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpSynSentNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpSynRecvNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'SYN_RCVD'", "当前Jvm进程当前处于 SYN_RCVD 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpSynRecvNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpFinWait1Num() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'FIN_WAIT1'", "当前Jvm进程当前处于 FIN_WAIT1 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpFinWait1Num||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpFinWait2Num() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'FIN_WAIT2'", "当前Jvm进程当前处于 FIN_WAIT2 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpFinWait2Num||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpTimeWaitNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'TIME_WAIT'", "当前Jvm进程当前处于 time wait 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpTimeWaitNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpClosedNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'CLOSED'", "当前Jvm进程当前处于 CLOSED 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpClosedNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpCloseWaitNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'CLOSE_WAIT'", "当前Jvm进程当前处于 close wait 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpCloseWaitNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpClosingNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'CLOSING'", "当前Jvm进程当前处于 CLOSING 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpClosingNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpLastAckNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'LAST_ACK'", "当前Jvm进程当前处于 LAST_ACK 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpLastAckNum||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcNetworkTcpNoneNum() {
        List<String> lines = getOutputByCmd("netstat -antp | grep %d | grep -c 'NONE'", "当前Jvm进程当前处于 NONE 状态 tcp 连接数", PID);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxProcMetricsService||method=getProcNetworkTcpNoneNum||msg=data is null");
            return 0;
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

}
