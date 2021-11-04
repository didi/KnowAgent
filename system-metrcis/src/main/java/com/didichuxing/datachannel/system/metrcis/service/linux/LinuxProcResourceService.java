package com.didichuxing.datachannel.system.metrcis.service.linux;

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
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 获取进程的所有指标数据，服务于一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxProcResourceService implements ProcResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxProcResourceService.class);

    /**
     * 当前agent进程id
     */
    private final long PID;
    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int CPU_NUM;
    /**
     * 当前进程网络Tcp各状态统计
     * Tcp状态：个数
     */
    private Map<String, Long> processNetworkTcpStat = new HashMap<>();

    /**
     * 当前进程CPU状态
     * %usr %system %CPU
     */
    private List<String> processCpuStat = new ArrayList<>();

    /**
     * 当前进程CPU上下文切换次数
     * 返回 每秒主动切换次数 每秒被动切换次数
     */
    private List<String> processCpuSwitchesPS = new ArrayList<>();

    private Map<String, Long> processMemoryInfo = new HashMap<>();

    private LinuxCpuTime lastLinuxCpuTime;

    private LinuxNetFlow lastLinuxNetFlow;

    /**
     * 存储网络流量
     */
    private List<Double>   netFlow = new ArrayList<>();

    private LinuxIORate lastLinuxIORate;

    /**
     * 存储进程IO速率
     */
    private List<Double> processIORate = new ArrayList<>();

    /**
     * 是否使用已有资源
     */
    private volatile boolean loadCache = false;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Lock readLock = lock.readLock();

    private Lock writeLock = lock.writeLock();

    public LinuxProcResourceService() {
        PID = initializePid();
        CPU_NUM = Runtime.getRuntime().availableProcessors();

        try {
            lastLinuxCpuTime = new LinuxCpuTime(getProcPid(), getSystemCpuNumCores());// 记录上次的cpu耗时
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=CpuTime init failed",
                    e);
        }

        try {
            lastLinuxNetFlow = new LinuxNetFlow(getProcPid());// 记录上次的收发字节数
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=NetFlow init failed",
                    e);
        }

        try {
            lastLinuxIORate = new LinuxIORate(getProcPid());// 记录上次IO速率
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=DefaultOSResourceService()||msg=processIORate init failed",
                    e);
        }

    }

    @Override
    public int getSystemCpuNumCores() {
        return CPU_NUM;
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
     * 初始化所有资源
     */
    private void loadResource() {
        readLock.lock();
        if (!this.loadCache) {
            readLock.unlock();
            writeLock.lock();
            try {
                if (!this.loadCache) {

                    processNetworkTcpStat = getKeyValueResource(
                            "netstat -antp | grep %d | awk '{++S[$6]} END {for(a in S) print a, S[a]}'", true, 1, "进程网络Tcp情况");

                    processCpuStat = getOutputByCmd(
                            "pidstat -p %d | awk 'NR==4{print $4,$5,$7}'", true, "进程CPU使用情况");

                    processCpuSwitchesPS = getOutputByCmd(
                            "pidstat -w -p %d | awk 'NR==4{print $4,$5}'", true, "进程CPU每秒上下文切换次数");

                    processMemoryInfo = getKeyValueResource("cat /proc/%d/status | awk 'NR>12 && NR<28'", true, 1024, "进程内存使用情况");

                    getLatestNetFlow();

                    getlLatestProcessIORate();

                    this.loadCache = true;
                }
                readLock.lock();
            } catch(Exception e) {
                LOGGER.error("class=DefaultOSResourceService||method=getMoreSystemResource()||msg=failed to initResource",
                        e);
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * 清空当前已加载的资源
     */
    @Override
    public void clearCache() {
        writeLock.lock();
        try {
            processNetworkTcpStat.clear();
            processCpuStat.clear();
            processCpuSwitchesPS.clear();
            processMemoryInfo.clear();
            processIORate.clear();
            netFlow.clear();

            this.loadCache = false;
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=clearCache()||msg=failed to clear cache",
                    e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public long getProcStartupTime() {
        try {
            return ManagementFactory.getRuntimeMXBean().getStartTime();
        } catch (Exception ex) {
            LOGGER.error("class=LinuxProcResourceService||method=getProcStartupTime()||msg=failed to get process startup time", ex);
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
        return getOneSystemResource(processCpuStat, 1, 3, "当前进程系统态cpu使用率");
    }

    @Override
    public long getProcCpuSwitchesPS() {
        return getProcCpuVoluntarySwitchesPS() + getProcCpuNonVoluntarySwitchesPS();
    }

    @Override
    public long getProcCpuVoluntarySwitchesPS() {
        return (long) getOneSystemResource(processCpuSwitchesPS, 0, 2, "当前进程cpu每秒自愿上下文交换次数");
    }

    @Override
    public long getProcCpuNonVoluntarySwitchesPS() {
        return (long) getOneSystemResource(processCpuSwitchesPS, 1, 2, "当前进程cpu每秒非自愿上下文交换次数");
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
    public double getProcCpuUser() {
        return getOneSystemResource(processCpuStat, 0, 3, "当前进程用户态cpu使用率");
    }

    @Override
    public double getProcIOReadRate() {
        return getResourceFromList(processIORate, 0, "getProcIOReadRate");
    }



    @Override
    public long getProcIOReadBytesRate() {
        return (long) getResourceFromList(processIORate, 2, "getProcIOReadBytesRate");
    }

    @Override
    public double getProcIOWriteRate() {
        return getResourceFromList(processIORate, 1, "getProcIOWriteRate");
    }

    @Override
    public long getProcIOWriteBytesRate() {
        return (long) getResourceFromList(processIORate, 3, "getProcIOWriteBytesRate");
    }

    /**
     * 获取当前IO rate
     */
    private void getlLatestProcessIORate() {
        List<Double> curProcessIORate = new ArrayList<>();
        try {
            LinuxIORate curLinuxIORate = new LinuxIORate(getProcPid());
            curProcessIORate.add(curLinuxIORate.getIOReadTimesRate(lastLinuxIORate));
            curProcessIORate.add(curLinuxIORate.getIOWriteTimesRate(lastLinuxIORate));
            curProcessIORate.add(curLinuxIORate.getIOReadBytesRate(lastLinuxIORate));
            curProcessIORate.add(curLinuxIORate.getIOWriteBytesRate(lastLinuxIORate));
            this.processIORate = curProcessIORate;
            this.lastLinuxIORate = curLinuxIORate;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getlLatestProcessIORate||msg=current process gets io rates get failed",
                    e);
        }
    }

    @Override
    public double getProcIOAwaitTimePercent() {
        List<String> output = getOutputByCmd("iotop -p %d -P -b -n 1 | awk 'NR==4{print $10}'", true,
                "当前进程io读写等待时间占总时间百分比");
        if (!output.isEmpty()) {
            return Double.parseDouble(output.get(0));
        }
        return 0.0d;
    }

    @Override
    public long getProcMemData() {
        return getResourceValueByKey(processMemoryInfo, "VmData:", "getProcMemData");
    }

    @Override
    public long getProcMemDirty() {
        return getResourceValueByKey(processMemoryInfo, "RssAnon:", "getProcMemDirty");
    }

    @Override
    public long getProcMemLib() {
        return getResourceValueByKey(processMemoryInfo, "VmLib:", "getProcMemLib");
    }

    @Override
    public long getProcMemRss() {
        return getResourceValueByKey(processMemoryInfo, "VmRSS:", "getProcMemRss");
    }

    @Override
    public long getProcMemShared() {
        return getResourceValueByKey(processMemoryInfo, "RssShmem:", "getProcMemShared");
    }

    @Override
    public long getProcMemSwap() {
        return getResourceValueByKey(processMemoryInfo, "VmSwap:", "getProcMemSwap");
    }

    @Override
    public long getProcMemText() {
        return getResourceValueByKey(processMemoryInfo, "VmExe:", "getProcMemText");
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
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemTotal' | awk '{print $2}'", false, "系统总内存");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        }
        return 0L;
    }

    @Override
    public long getProcMemVms() {
        return getResourceValueByKey(processMemoryInfo, "VmSize:", "getProcMemVms");
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
        return getResourceValueByKey(processMemoryInfo, "VmPeak:", "getJvmProcMemUsedPeak");
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
        List<String> output = getOutputByCmd("ls /proc/%d/fd | wc -l", true, "jvm进程当前fd使用数");
        if (!output.isEmpty()) {
            return Integer.parseInt(output.get(0));
        }
        return 0;
    }

    @Override
    public List<Integer> getProcPortListen() {
        List<Integer> result = new ArrayList<>();
        List<String> output = getOutputByCmd("netstat -nltp | grep %d | awk '{print $4}' | awk -F: '{print $NF}'", true,
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
        return (long) getResourceFromList(netFlow, 2, "getProcNetworkReceiveBytesPs");
    }

    @Override
    public long getProcNetworkSendBytesPs() {
        return (long) getResourceFromList(netFlow, 3, "getProcNetworkReceiveBytesPs");
    }

    @Override
    public int getProcNetworkTcpConnectionNum() {
        loadResource();
        long tcpConnectionNum = 0;
        Set<Map.Entry<String, Long>> entries = processNetworkTcpStat.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            tcpConnectionNum += entry.getValue();
        }
        return (int) tcpConnectionNum;
    }

    @Override
    public int getProcNetworkTcpTimeWaitNum() {
        return (int) getResourceValueByKey(processNetworkTcpStat, "TIME_WAIT", "getProcNetworkTcpTimeWaitNum");
    }

    @Override
    public int getProcNetworkTcpCloseWaitNum() {
        return (int) getResourceValueByKey(processNetworkTcpStat, "CLOSE_WAIT", "getProcNetworkTcpCloseWaitNum");
    }

    /**
     * 获取最新网络流量
     */
    private void getLatestNetFlow() {
        List<Double> curNetFlow = new ArrayList<>();
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow(getProcPid());
            curNetFlow.add(curLinuxNetFlow.getSystemReceiveBytesPs(lastLinuxNetFlow));
            curNetFlow.add(curLinuxNetFlow.getSystemTransmitBytesPs(lastLinuxNetFlow));
            curNetFlow.add(curLinuxNetFlow.getProcessReceiveBytesPs(lastLinuxNetFlow));
            curNetFlow.add(curLinuxNetFlow.getProcessTransmitBytesPs(lastLinuxNetFlow));
            this.netFlow = curNetFlow;
            lastLinuxNetFlow = curLinuxNetFlow;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=setNetFlow||msg=setNetFlow get failed",
                    e);
        }
    }
    /**
     * 由key获取系统资源中的值
     * @param resource key为具体资源名称， value为其值
     * @param key 键
     * @param methodName 调用方法名称
     * @return
     */
    private long getResourceValueByKey(Map<String, Long> resource, String key, String methodName) {
        loadResource();

        try {
            if (resource.containsKey(key)) {
                return resource.get(key);
            }
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method={}()||msg=failed to get resource", methodName,
                    e);
        } finally {
            readLock.unlock();
        }
        return 0L;
    }

    /**
     * 根据下标获取资源列表中某项数据
     * @param resource 资源列表
     * @param index 下标
     * @param methodName 方法名称
     * @return
     */
    private double getResourceFromList(List<Double> resource, int index, String methodName) {
        loadResource();

        try {
            return resource.get(index);
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method={}()||msg=failed to get resource", methodName,
                    e);
        } finally {
            readLock.unlock();
        }
        return 0.0d;
    }

    /**
     * 根据命令行获取key value 资源
     * @param procFDShell 命令行
     * @param isProcess 是否是进程
     * @param size 单位转换
     * @param message 资源信息
     * @return
     */
    private Map<String, Long> getKeyValueResource(String procFDShell, boolean isProcess, int size, String message) {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd(procFDShell, isProcess, message);
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

    /**
     * 获取一项系统资源
     * @param systemResource 系统资源
     * @param index 下标
     * @param length 单行数据长度
     * @param message 某状态描述
     * @return
     */
    private double getOneSystemResource(List<String> systemResource, int index, int length, String message) {
        loadResource();

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
        } finally {
            readLock.unlock();
        }
        return 0.0d;
    }

    /**
     * linux 根据shell命令获取系统或者进程资源
     * @param procFDShell shell命令
     * @param isProcess   是不是进程
     * @param resourceMessage    资源描述信息
     * @return
     */
    private List<String> getOutputByCmd(String procFDShell, boolean isProcess, String resourceMessage) {
        Process process = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
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
