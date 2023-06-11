package com.didichuxing.datachannel.agent.engine.utils.monitor;


import com.didichuxing.datachannel.agent.engine.limit.cpu.LinuxCpuTime;
import com.didichuxing.datachannel.agent.engine.limit.io.LinuxIORate;
import com.didichuxing.datachannel.agent.engine.limit.net.LinuxNetFlow;
import com.didichuxing.datachannel.agent.engine.service.TaskRunningPool;

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 默认系统资源服务
 */
public class LinuxOSResourceService implements IOSResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxOSResourceService.class);

    /**
     * 当前agent进程id
     */
    private final long PID;
    /**
     * agent宿主机cpu核（逻辑核）
     */
    private final int CPU_NUM;

    /**
     * 获取磁盘分区情况
     * 返回 磁盘分区名称(名称+挂载点,以:分割) 磁盘容量大小 磁盘已用容量 磁盘可用容量
     */
    private List<String> systemDiskStat = new ArrayList<>();

    /**
     * 获取系统cpu使用情况
     * 返回 %usr %sys %iowait %steal %guest %idle
     */
    private List<String> systemCpuStat = new ArrayList<>();

    /**
     * 获取系统句柄数
     * 返回 已分配且使用中句柄数，已分配未使用，最大数目
     */
    private List<String> systemFileHandleNum = new ArrayList<>();

    /**
     * 系统磁盘读写操作耗时
     * 返回 磁盘名称 读操作耗时 写操作耗时
     */
    private List<String> systemDiskReadWriteTime = new ArrayList<>();

    /**
     * 获取系统磁盘分区inode情况
     * 返回 磁盘分区名称(名称+挂载点,以:分割) inode总量 inode已用 inode可用
     */
    private List<String> systemDiskInodeStat = new ArrayList<>();

    /**
     * 获取系统磁盘IO情况
     */
    private List<String> systemIOStat = new ArrayList<>();

    /**
     * 获取系统平均负载情况
     * 返回 近1分钟平均负载 近5分钟平均负载 近15分钟平均负载
     */
    private List<String> systemLoad = new ArrayList<>();

    /**
     * 系统内存情况
     * 键为具体某种内存使用名称，值为该内存对应的值
     */
    private Map<String, Long> systemMemoryInfo = new HashMap<>();

    /**
     * 当前系统网络Tcp各状态统计
     * Tcp状态：个数
     */
    private Map<String, Long> currentSystemNetworkTcpStat = new HashMap<>();

    /**
     * 系统启动以来 Tcp 统计
     * Tcp:次数
     */
    private Map<String, Long> systemNetworkTcpStat = new HashMap<>();

    /**
     * 系统启动以来 Udp 统计
     * Udp:次数
     */
    private Map<String, Long> systemNetworkUdpStat = new HashMap<>();

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

    private  ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Lock readLock = lock.readLock();

    private Lock writeLock = lock.writeLock();

    public LinuxOSResourceService() {
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
                    systemDiskStat = getOutputByCmd(
                            "df -k | awk 'NR>1{print $1,$2,$3,$4}'", false, "系统磁盘情况");

                    systemCpuStat = getOutputByCmd(
                            "mpstat | awk 'NR==4{print $3,$5,$6,$9,$10,$NF}'", false, "系统cpu使用情况");

                    systemFileHandleNum = getOutputByCmd(
                            "cat /proc/sys/fs/file-nr", false, "系统句柄数");

                    systemDiskReadWriteTime = getOutputByCmd(
                            "vmstat -d | awk 'NR>2{print $1,$5,$9}'", false, "磁盘读写操作耗时");

                    systemDiskInodeStat = getOutputByCmd(
                            "df -i | awk 'NR>1{print $1\":\"$6,$2,$3,$4}'", false, "系统各分区inode情况");

                    systemIOStat = getOutputByCmd(
                            "iostat -dkx | head -n -1 | awk 'NR>3'", false, "系统各磁盘设备IO情况");

                    systemLoad = getOutputByCmd(
                            "sar -q 1 1 | grep ':' | awk '{print $4,$5,$6}'", false, "系统负载情况");

                    systemMemoryInfo = getKeyValueResource(
                            "cat /proc/meminfo", false, 1024, "系统内存情况");

                    currentSystemNetworkTcpStat = getKeyValueResource(
                            "netstat -an | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'", false, 1, "当前系统网络Tcp情况");

                    systemNetworkTcpStat = getSystemNetworkStat(
                            "cat /proc/net/snmp | grep 'Tcp:' | awk '{print $6,$7,$8,$9,$13}'", false, "系统启动以来网络Tcp情况");

                    systemNetworkUdpStat = getSystemNetworkStat(
                            "cat /proc/net/snmp | grep 'Udp:' | awk '{print $2,$3,$4,$5,$7}'", false, "系统启动以来网络Udp情况");

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
            systemDiskStat.clear();
            systemCpuStat.clear();
            systemFileHandleNum.clear();
            systemDiskReadWriteTime.clear();
            systemDiskInodeStat.clear();
            systemIOStat.clear();
            systemLoad.clear();
            systemMemoryInfo.clear();
            currentSystemNetworkTcpStat.clear();
            systemNetworkTcpStat.clear();
            systemNetworkUdpStat.clear();
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
    public long getSystemNtpOffset() {
        List<String> output = getOutputByCmd("clock | awk '{print $4}'", false,
                "系统时间偏移量");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            double v =  1000 * Double.parseDouble(output.get(0));
            return (long) v;
        }
        return 0L;
    }

    @Override
    public int getSystemProcCount() {
        List<String> output = getOutputByCmd(" ps -ef  | wc -l", false,
                "系统进程个数");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            return Integer.parseInt(output.get(0)) - 1;
        }
        return 0;
    }

    @Override
    public long getSystemStartupTime() {
        return System.currentTimeMillis() - getSystemUptime();
    }

    @Override
    public long getSystemUptime() {
        List<String> systemUpTime = getOutputByCmd(
                "awk '{print $1}' /proc/uptime", false, "系统运行时间");
        if (!systemUpTime.isEmpty() && StringUtils.isNotBlank(systemUpTime.get(0))) {
            double v =  1000 * Double.parseDouble(systemUpTime.get(0));
            return (long) v;
        }
        return 0L;
    }

    @Override
    public double getSystemCpuUtil() {
        return getSystemCpuUtilTotalPercent() * getSystemCpuNumCores();
    }

    @Override
    public double getSystemCpuUtilTotalPercent() {
        return 100.0d - getSystemCpuIdle();
    }

    @Override
    public long getSystemCpuSwitches() {
        List<String> output = getOutputByCmd("cat /proc/stat | grep 'ctxt' | awk '{print $2}'", false,
                "cpu上下文交换次数");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            return Long.parseLong(output.get(0));
        }
        return 0;
    }

    @Override
    public double getSystemCpuGuest() {
        return getOneSystemResource(systemCpuStat, 4, 6, "虚拟处理器CPU时间占比");
    }

    @Override
    public double getSystemCpuIdle() {
        return getOneSystemResource(systemCpuStat, 5, 6, "总体cpu空闲率");
    }

    @Override
    public double getSystemCpuIOWait() {
        return getOneSystemResource(systemCpuStat,2, 6, "等待I/O的CPU时间占比");
    }

    @Override
    public int getSystemCpuNumCores() {
        return CPU_NUM;
    }

    @Override
    public double getSystemCpuSteal() {
        return getOneSystemResource(systemCpuStat, 3, 6, "等待处理其他虚拟核的时间占比");
    }

    @Override
    public double getSystemCpuSystem() {
        return getOneSystemResource(systemCpuStat, 1, 6, "内核态CPU时间占比");
    }

    @Override
    public double getSystemCpuUser() {
        return getOneSystemResource(systemCpuStat, 0, 6, "用户态CPU时间占比");
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

    @Override
    public Map<String, Long> getSystemDiskBytesFree() {
        return getMoreSystemResource(systemDiskStat, 3, 4, 1024, "磁盘各分区总量");
    }

    @Override
    public Map<String, Double> getSystemDiskUsedPercent() {
        loadResource();

        Map<String, Double> result = new HashMap<>();
        try {
            if (!systemDiskStat.isEmpty()) {
                for (String s : systemDiskStat) {
                    String[] array = s.split("\\s+");
                    if (array.length < 4) {
                        LOGGER.error("获取系统资源项[磁盘各分区总量]失败");
                        return result;
                    }
                    long used = Long.parseLong(array[2]);
                    long total = Long.parseLong(array[1]);
                    double percent = 1.0 * used / total;
                    result.put(array[0], percent);
                }
            }
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemDiskUsedPercent()||msg=failed to get SystemDiskUsedPercent",
                    e);
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskReadTime() {
        return getMoreSystemResource(systemDiskReadWriteTime, 1, 3, 1, "各设备读操作耗时");
    }

    @Override
    public double getSystemDiskReadTimePercent() {
        return getSystemDiskReadWriteTimePercent(true);
    }

    @Override
    public Map<String, Long> getSystemDiskBytesTotal() {
        return getMoreSystemResource(systemDiskStat, 1, 4, 1024, "磁盘各分区总量");
    }

    @Override
    public Map<String, Long> getSystemDiskBytesUsed() {
        return getMoreSystemResource(systemDiskStat, 2, 4, 1024, "磁盘各分区用量大小");
    }

    @Override
    public Map<String, Long> getSystemDiskWriteTime() {
        return getMoreSystemResource(systemDiskReadWriteTime, 2, 3, 1, "各设备写操作耗时");
    }

    @Override
    public double getSystemDiskWriteTimePercent() {
        return getSystemDiskReadWriteTimePercent(false);
    }

    /**
     * 根据读写操作获取读写操作时间占比
     * @param isRead 是否是读操作
     * @return 读写操作时间占比
     */
    private double getSystemDiskReadWriteTimePercent(boolean isRead) {
        loadResource();

        try {
            if (!systemDiskReadWriteTime.isEmpty()) {
                long readValue = 0;
                long writeValue = 0;
                for (String s : systemDiskReadWriteTime) {
                    String[] array = s.split("\\s+");
                    if (array.length < 3) {
                        LOGGER.error("获取系统资源项[{}}]失败", "读取磁盘时间百分比");
                        return 0.0d;
                    }
                    readValue += Long.parseLong(array[1]);
                    writeValue += Long.parseLong(array[2]);
                }
                long sum = readValue + readValue;
                if (sum == 0) {
                    return 0.0d;
                }
                if (isRead) {
                    return 1.0 * readValue / sum;
                }
                return 1.0 * writeValue / sum;
            }
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemDiskReadWriteTimePercent()||msg=failed to get SystemDiskReadWriteTimePercent",
                    e);
        } finally {
            readLock.unlock();
        }
        return 0.0d;
    }

    @Override
    public int getSystemFilesAllocated() {
        return getSystemFilesUsed() + getSystemFilesNotUsed();
    }

    @Override
    public int getSystemFilesLeft() {
        return getSystemFilesMax() - getSystemFilesAllocated();
    }

    @Override
    public double getSystemFilesUsedPercent() {
        return 1.0 * getSystemFilesUsed() / getSystemFilesAllocated();
    }

    @Override
    public int getSystemFilesMax() {
        return getSystemFileHandle(2, "系统可以打开的最大文件句柄数");
    }

    @Override
    public int getSystemFilesUsed() {
        return getSystemFileHandle(0, "系统使用的已分配文件句柄数");
    }

    @Override
    public int getSystemFilesNotUsed() {
        return getSystemFileHandle(1, "系统未使用的已分配文件句柄数");
    }

    /**
     * 根据下标获取相应的文件句柄数
     * @param index 下标
     * @param message 资源描述
     * @return 相对应文件句柄数
     */
    private int getSystemFileHandle(int index, String message) {
        loadResource();

        try {
            if (!systemFileHandleNum.isEmpty() && StringUtils.isNotBlank(systemFileHandleNum.get(0))) {
                String cpuStat = systemFileHandleNum.get(0);
                String[] array = cpuStat.split("\\s+");
                if (array.length < 3) {
                    LOGGER.error("获取系统资源项[{}}]失败", message);
                }
                return Integer.parseInt(array[index]);
            }
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemFileHandle()||msg=failed to get system file handle",
                    e);
        } finally {
            readLock.unlock();
        }

        return 0;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesFree() {
        return getMoreSystemResource(systemDiskInodeStat, 3, 4, 1, "系统各分区空闲inode数量");
    }

    @Override
    public Map<String, Double> getSystemDiskInodesUsedPercent() {
        loadResource();

        Map<String, Double> result = new HashMap<>();
        try {
            if (!systemDiskInodeStat.isEmpty()) {
                for (String s : systemDiskInodeStat) {
                    String[] array = s.split("\\s+");
                    if (array.length < 4) {
                        LOGGER.error("获取系统资源项[{}}]失败", "系统各分区已用inode占比");
                        return result;
                    }
                    long inodeUsed = Long.parseLong(array[2]);
                    long inodeTotal = Long.parseLong(array[1]);
                    double inodeUsedPercent = 1.0 * inodeUsed / inodeTotal;
                    result.put(array[0], inodeUsedPercent);
                }
            }
            return result;
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemDiskInodesUsedPercent()||msg=failed to get systemDiskInodesUsedPercent",
                    e);
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesTotal() {
        return getMoreSystemResource(systemDiskInodeStat, 1, 4, 1, "系统各分区inode总数量");
    }

    @Override
    public Map<String, Long> getSystemDiskInodesUsed() {
        return getMoreSystemResource(systemDiskInodeStat, 2, 4, 1,"系统各分区已用inode数量");
    }

    /**
     * 获取多行系统资源
     * @param systemResource 资源源文件
     * @param index 下标
     * @param length 每行数据长度限制
     * @param size 单位换算，若结果值单位为KB,需乘以1024；单位为个数，结果乘以1
     * @param message 资源描述
     * @return
     */
    private Map<String, Long> getMoreSystemResource(List<String> systemResource, int index, int length, int size, String message) {
        loadResource();

        Map<String, Long> result = new HashMap<>();
        try {
            if (!systemResource.isEmpty()) {
                for (String s : systemResource) {
                    String[] array = s.split("\\s+");
                    if (array.length < length) {
                        LOGGER.error("获取系统资源项[{}}]失败", message);
                        return result;
                    }
                    double value = size * Double.parseDouble(array[index]);
                    result.put(array[0], (long) value);
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getMoreSystemResource()||msg=failed to get system resource",
                    e);
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemIOAvgQuSz() {
        return getMoreSystemResource(systemIOStat, 8, 14, 1, "各设备平均队列长度");
    }

    @Override
    public Map<String, Long> getSystemIOAvgRqSz() {
        return getMoreSystemResource(systemIOStat, 7, 14, 1, "各设备平均请求大小");
    }

    @Override
    public Map<String, Long> getSystemIOAwait() {
        return getMoreSystemResource(systemIOStat, 9, 14, 1, "各设备每次IO平均处理时间");
    }

    @Override
    public Map<String, Long> getSystemIORAwait() {
        return getMoreSystemResource(systemIOStat, 10, 14, 1, "各设备读请求平均耗时");
    }

    @Override
    public Map<String, Long> getSystemIOReadRequest() {
        return getMoreSystemResource(systemIOStat, 3, 14, 1, "各设备每秒读请求数量");
    }

    @Override
    public Map<String, Long> getSystemIOReadBytes() {
        return getMoreSystemResource(systemIOStat, 5, 14, 1024, "各设备每秒读取字节数");
    }

    @Override
    public Map<String, Long> getSystemIORRQMS() {
        return getMoreSystemResource(systemIOStat, 1, 14, 1, "各设备每秒合并到设备队列的读请求数");
    }

    @Override
    public Map<String, Long> getSystemIOSVCTM() {
        return getMoreSystemResource(systemIOStat, 12, 14, 1, "每次各设备IO平均服务时间");
    }

    @Override
    public Map<String, Double> getSystemIOUtil() {
        loadResource();

        Map<String, Double> result = new HashMap<>();
        try {
            if (!systemIOStat.isEmpty()) {
                for (String s : systemIOStat) {
                    String[] array = s.split("\\s+");
                    if (array.length < 14) {
                        LOGGER.error("获取系统资源项[{}}]失败", "各设备I/O请求的CPU时间百分比");
                        return result;
                    }
                    double value = Double.parseDouble(array[13]);
                    result.put(array[0], value);
                }
            }
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemIOUtil()||msg=failed to get system IOUtil",
                    e);
        } finally {
            readLock.unlock();
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemIOWAwait() {
        return getMoreSystemResource(systemIOStat, 11, 14, 1, "各设备写请求平均耗时");
    }

    @Override
    public Map<String, Long> getSystemIOWriteRequest() {
        return getMoreSystemResource(systemIOStat, 4, 14, 1, "各设备每秒写请求数量");
    }

    @Override
    public Map<String, Long> getSystemIOWriteBytes() {
        return getMoreSystemResource(systemIOStat, 6, 14, 1024, "各设备每秒写字节数");
    }

    @Override
    public Map<String, Long> getSystemIOWRQMS() {
        return getMoreSystemResource(systemIOStat, 2, 14, 1, "各设备每秒合并到设备队列的写请求数");
    }

    @Override
    public double getSystemLoad1() {
        return getOneSystemResource(systemLoad, 0, 3, "系统近1分钟平均负载");
    }

    @Override
    public double getSystemLoad5() {
        return getOneSystemResource(systemLoad, 1, 3, "系统近5分钟平均负载");
    }

    @Override
    public double getSystemLoad15() {
        return getOneSystemResource(systemLoad, 2, 3, "系统近15分钟平均负载");
    }

    @Override
    public long getSystemMemBuffered() {
        return getResourceValueByKey(systemMemoryInfo, "Buffers:", "getSystemMemBuffered");
    }

    @Override
    public long getSystemMemCached() {
        return getResourceValueByKey(systemMemoryInfo, "Cached:", "getSystemMemCached");
    }

    @Override
    public long getSystemMemCommitLimit() {
        return getResourceValueByKey(systemMemoryInfo, "CommitLimit:", "getSystemMemCommitLimit");
    }

    @Override
    public long getSystemMemCommitted() {
        return getSystemMemCommitLimit() - getSystemMemCommittedAs();
    }

    @Override
    public long getSystemMemCommittedAs() {
        return getResourceValueByKey(systemMemoryInfo, "Committed_AS:", "getSystemMemCommittedAs");
    }

    @Override
    public long getSystemMemNonPaged() {
        return getResourceValueByKey(systemMemoryInfo, "KernelStack:", "getSystemMemNonPaged");
    }

    @Override
    public long getSystemMemPaged() {
        return getResourceValueByKey(systemMemoryInfo, "Writeback:", "getSystemMemPaged");
    }

    @Override
    public double getSystemMemFreePercent() {
        long memTotal = getSystemMemTotal();
        if (memTotal == 0) {
            LOGGER.warn("SystemMemoryTotal is zero");
            return 0.0d;
        }
        return 1.0 * getSystemMemFree() / getSystemMemTotal();
    }

    @Override
    public double getSystemMemUsedPercent() {
        long memTotal = getSystemMemTotal();
        if (memTotal == 0) {
            LOGGER.warn("SystemMemoryTotal is zero");
            return 0.0d;
        }
        return 1.0 * getSystemMemUsed() / getSystemMemTotal();
    }

    @Override
    public long getSystemMemShared() {
        return getResourceValueByKey(systemMemoryInfo, "Shmem:", "getSystemMemShared");
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
    @Override
    public long getSystemMemSlab() {
        return getResourceValueByKey(systemMemoryInfo, "Slab:", "getSystemMemSlab");
    }

    @Override
    public long getSystemMemTotal() {
        return getResourceValueByKey(systemMemoryInfo, "MemTotal:", "getSystemMemTotal");
    }

    @Override
    public long getSystemMemFree() {
        return getResourceValueByKey(systemMemoryInfo, "MemFree:", "getSystemMemFree");
    }

    @Override
    public long getSystemMemUsed() {
        return getSystemMemTotal() - getSystemMemFree();
    }

    @Override
    public long getSystemSwapCached() {
        return getResourceValueByKey(systemMemoryInfo, "SwapCached:", "getSystemSwapCached");
    }

    @Override
    public long getSystemSwapFree() {
        return getResourceValueByKey(systemMemoryInfo, "SwapFree:", "getSystemSwapFree");
    }

    @Override
    public double getSystemSwapFreePercent() {
        long swapTotal = getSystemSwapTotal();
        if (swapTotal == 0) {
            LOGGER.warn("SystemSwapMemoryTotal is zero");
            return 0.0d;
        }
        return 1.0 * getSystemSwapFree() / swapTotal;
    }

    @Override
    public long getSystemSwapTotal() {
        return getResourceValueByKey(systemMemoryInfo, "SwapTotal:", "getSystemSwapTotal");
    }

    @Override
    public long getSystemSwapUsed() {
        return getSystemSwapTotal() - getSystemSwapFree();
    }

    @Override
    public double getSystemSwapUsedPercent() {
        long swapTotal = getSystemSwapTotal();
        if (swapTotal == 0) {
            LOGGER.warn("SystemSwapMemoryTotal is zero");
            return 0.0d;
        }
        return 1.0 * getSystemSwapUsed() / swapTotal;
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

    @Override
    public long getSystemNetworkReceiveBytesPs() {
        return (long) getResourceFromList(netFlow, 0, "getSystemNetworkReceiveBytesPs");
    }

    @Override
    public long getSystemNetworkSendBytesPs() {
        return (long) getResourceFromList(netFlow, 1, "getSystemNetworkSendBytesPs");
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

    @Override
    public int getSystemNetworkTcpConnectionNum() {
        loadResource();
        long tcpConnectionNum = 0;
        Set<Map.Entry<String, Long>> entries = currentSystemNetworkTcpStat.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            tcpConnectionNum += entry.getValue();
        }
        return (int) tcpConnectionNum;
    }

    @Override
    public int getSystemNetworkTcpTimeWaitNum() {
        return (int) getResourceValueByKey(currentSystemNetworkTcpStat, "TIME_WAIT", "getSystemNetworkTcpTimeWaitNum");
    }

    @Override
    public int getSystemNetworkTcpCloseWaitNum() {
        return (int) getResourceValueByKey(currentSystemNetworkTcpStat, "CLOSE_WAIT", "getSystemNetworkTcpTimeWaitNum");
    }

    @Override
    public long getSystemNetworkTcpActiveOpens() {
        return getResourceValueByKey(systemNetworkTcpStat, "ActiveOpens", "getSystemNetworkTcpActiveOpens");
    }

    @Override
    public long getSystemNetworkTcpPassiveOpens() {
        return getResourceValueByKey(systemNetworkTcpStat, "PassiveOpens", "getSystemNetworkTcpPassiveOpens");
    }

    @Override
    public long getSystemNetworkTcpAttemptFails() {
        return getResourceValueByKey(systemNetworkTcpStat, "AttemptFails", "getSystemNetworkTcpAttemptFails");
    }

    @Override
    public long getSystemNetworkTcpEstabResets() {
        return getResourceValueByKey(systemNetworkTcpStat, "EstabResets", "getSystemNetworkTcpEstabResets");
    }

    @Override
    public long getSystemNetworkTcpRetransSegs() {
        return getResourceValueByKey(systemNetworkTcpStat, "RetransSegs", "getSystemNetworkTcpRetransSegs");
    }

    /**
     * 根据命令行获取Tcp信息
     * @param procFDShell 命令行
     * @param isProcess 是进程还是系统
     * @param message 资源描述
     * @return
     */
    private Map<String, Long> getSystemNetworkStat(String procFDShell, boolean isProcess, String message) {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd(procFDShell, isProcess, message);
        if(lines.size() == 2) {
            String[] keys = lines.get(0).split("\\s+");
            String[] values = lines.get(1).split("\\s+");
            for (int i = 0; i < keys.length; i++) {
                result.put(keys[i], Long.parseLong(values[i]));
            }
        }
        return result;
    }

    @Override
    public long getSystemNetworkTcpExtListenOverflows() {
        List<String> output = getOutputByCmd(
                "netstat -s | egrep \"listen|LISTEN\" | awk '{a+=$1}{print a}'", false,
                "系统启动以来 Tcp 监听队列溢出次数");
        if (!output.isEmpty()) {
            return Long.parseLong(output.get(0));
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpInDatagrams() {
        return getResourceValueByKey(systemNetworkUdpStat, "InDatagrams", "getSystemNetworkUdpInDatagrams");
    }

    @Override
    public long getSystemNetworkUdpOutDatagrams() {
        return getResourceValueByKey(systemNetworkUdpStat, "OutDatagrams", "getSystemNetworkUdpOutDatagrams");
    }

    @Override
    public long getSystemNetworkUdpInErrors() {
        return getResourceValueByKey(systemNetworkUdpStat, "InErrors", "getSystemNetworkUdpInErrors");
    }

    @Override
    public long getSystemNetworkUdpNoPorts() {
        return getResourceValueByKey(systemNetworkUdpStat, "NoPorts", "getSystemNetworkUdpNoPorts");
    }

    @Override
    public long getSystemNetworkUdpSendBufferErrors() {
        return getResourceValueByKey(systemNetworkUdpStat, "SndbufErrors", "getSystemNetworkUdpSendBufferErrors");
    }

    @Override
    public long getProcStartupTime() {
        try {
            return ManagementFactory.getRuntimeMXBean().getStartTime();
        } catch (Exception ex) {
            LOGGER.error("获取jvm启动时间失败", ex);
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
        return 1.0 * getProcMemUsed() / getSystemMemTotal();
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
    public int getJvmProcNormalSourceThreadPoolMaxThreadNum() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        return ((ThreadPoolExecutor) executorService).getMaximumPoolSize();
    }

    @Override
    public int getJvmProcNormalSourceThreadPoolThreadNum() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        return ((ThreadPoolExecutor) executorService).getActiveCount();
    }

    @Override
    public int getJvmProcNormalThreadPoolMaxQueueSize() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        BlockingQueue<Runnable> queue = ((ThreadPoolExecutor) executorService).getQueue();
        return queue.size() + queue.remainingCapacity();
    }

    @Override
    public int getJvmProcNormalThreadPoolQueueSize() {
        ExecutorService executorService = TaskRunningPool.getExecutorService();
        return ((ThreadPoolExecutor) executorService).getQueue().size();
    }

    @Override
    public int getJvmProcTempSourceThreadPoolMaxThreadNum() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        return ((ThreadPoolExecutor) executorService).getMaximumPoolSize();
    }

    @Override
    public int getJvmProcTempSourceThreadPoolThreadNum() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        return ((ThreadPoolExecutor) executorService).getActiveCount();
    }

    @Override
    public int getJvmProcTempThreadPoolMaxQueueSize() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        BlockingQueue<Runnable> queue = ((ThreadPoolExecutor) executorService).getQueue();
        return queue.size() + queue.remainingCapacity();
    }

    @Override
    public int getJvmProcTempThreadPoolQueueSize() {
        ExecutorService executorService = TaskRunningPool.getTempExecutorService();
        return ((ThreadPoolExecutor) executorService).getQueue().size();
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