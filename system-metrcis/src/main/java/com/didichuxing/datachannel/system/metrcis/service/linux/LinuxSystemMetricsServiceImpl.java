package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.SystemMetrics;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;


/**
 * 获取系统级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author Ronaldo
 * @Date 2021/11/3
 */
public class LinuxSystemMetricsServiceImpl implements SystemMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxSystemMetricsServiceImpl.class);

    private LinuxNetFlow lastLinuxNetFlow;

    public LinuxSystemMetricsServiceImpl() {
        try {
            lastLinuxNetFlow = new LinuxNetFlow();// 记录上次的收发字节数
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsService||method=DefaultLinuxSystemMetricsService()||msg=NetFlow init failed",
                    e);
        }
    }

    @Override
    public long getSystemNtpOffset() {
        List<String> output = getOutputByCmd("clock | awk '{print $4}'",
                "系统时间偏移量");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            double v =  1000 * Double.parseDouble(output.get(0));
            return (long) v;
        }
        return 0L;
    }

    @Override
    public int getSystemProcCount() {
        List<String> output = getOutputByCmd(" ps -ef  | wc -l",
                "系统进程个数");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            return Integer.parseInt(output.get(0)) - 1;
        }else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemProcCount()||msg=data is null");
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
                "awk '{print $1}' /proc/uptime", "系统运行时间");
        if (!systemUpTime.isEmpty() && StringUtils.isNotBlank(systemUpTime.get(0))) {
            double v =  1000 * Double.parseDouble(systemUpTime.get(0));
            return (long) v;
        }else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemUptime()||msg=data is null");
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
        List<String> output = getOutputByCmd("cat /proc/stat | grep 'ctxt' | awk '{print $2}'",
                "cpu上下文交换次数");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            return Long.parseLong(output.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuSwitches||msg=data is null");
        }
        return 0;
    }

    @Override
    public double getSystemCpuGuest() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $10}'", "虚拟处理器CPU时间占比");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuGuest||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public double getSystemCpuIdle() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $12}'", "总体cpu空闲率");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuIdle||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public double getSystemCpuIOWait() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $6}'", "等待I/O的CPU时间占比");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuIOWait||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public int getSystemCpuNumCores() {
        return CPU_NUM;
    }

    @Override
    public double getSystemCpuSteal() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $9}'", "等待处理其他虚拟核的时间占比");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuSteal||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public double getSystemCpuSystem() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $5}'", "内核态CPU时间占比");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuSystem||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public double getSystemCpuUser() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $3}'", "用户态CPU时间占比");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuUser||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public Map<String, Long> getSystemDiskBytesFree() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$4,$6}'", "磁盘各分区余量大小");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskBytesFree()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[2];
            long value = 1024 * Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Double> getSystemDiskUsedPercent() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$2,$3,$6}'", "磁盘各分区用量占比");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 4) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskUsedPercent()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[3];
            double value = 1.0 * Long.parseLong(array[2]) / Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskReadTime() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$5}'", "各设备读操作耗时");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskReadTime()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            long value = Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }
    @Override
    public double getSystemDiskReadTimePercent() {
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $5,$9}'", "读取磁盘时间百分比");
        long readWriteSum = 0L;
        long readSum = 0L;
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskReadTimePercent()||msg=data is not enough");
                return 0.0d;
            }
            long readTime = Long.parseLong(array[0]);
            long writeTime = Long.parseLong(array[1]);
            readWriteSum += readTime + writeTime;
            readSum += readTime;
        }
        if (readWriteSum == 0) {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskReadTimePercent()||msg=readWriteTimeSum is zero");
            return 0.0d;
        }
        return 1.0 * readSum / readWriteSum;
    }

    @Override
    public Map<String, Long> getSystemDiskBytesTotal() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$2,$6}'", "磁盘各分区总量");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskBytesTotal()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[2];
            long value = 1024 * Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskBytesUsed() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -k | awk 'NR>1{print $1,$3,$6}'", "磁盘各分区用量大小");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskBytesUsed()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[2];
            long value = 1024 * Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskWriteTime() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$9}'", "各设备写操作耗时");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskWriteTime()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            long value = Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public double getSystemDiskWriteTimePercent() {
        List<String> lines = getOutputByCmd("vmstat -d | awk 'NR>2{print $5,$9}'", "写入磁盘时间百分比");
        long readWriteSum = 0L;
        long writeSum = 0L;
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskWriteTimePercent()||msg=data is not enough");
                return 0.0d;
            }
            long readTime = Long.parseLong(array[0]);
            long writeTime = Long.parseLong(array[1]);
            readWriteSum += readTime + writeTime;
            writeSum += writeTime;
        }
        if (readWriteSum == 0) {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskWriteTimePercent()||msg=readWriteTimeSum is zero");
            return 0.0d;
        }
        return 1.0 * writeSum / readWriteSum;
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
        List<String> lines = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $3}'", "系统可以打开的最大文件句柄数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemFilesMax()||msg=data is null");
        }
        return 0;
    }

    @Override
    public int getSystemFilesUsed() {
        List<String> lines = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $1}'", "系统使用的已分配文件句柄数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemFilesUsed()||msg=data is null");
        }
        return 0;
    }

    @Override
    public int getSystemFilesNotUsed() {
        List<String> lines = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $2}'", "系统未使用的已分配文件句柄数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemFilesNotUsed()||msg=获取系统未使用的已分配文件句柄数失败");
        }
        return 0;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesFree() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$4,$6}'", "系统各分区空闲inode数量");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskInodesFree()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[2];
            long value = Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Double> getSystemDiskInodesUsedPercent() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$2,$3,$6}'", "系统各分区已用inode占比");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 4) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskInodesUsedPercent()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[3];
            long inodeTotal = Long.parseLong(array[1]);
            long inodeUsed = Long.parseLong(array[2]);
            result.put(key, 1.0 * inodeUsed / inodeTotal);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesTotal() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$2,$6}'", "系统各分区inode总数量");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskInodesTotal()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[2];
            long value = Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemDiskInodesUsed() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("df -i | awk 'NR>1{print $1,$3,$6}'", "系统各分区已用inode数量");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 3) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemDiskInodesUsed()||msg=data is not enough");
                return result;
            }
            String key = array[0] + ":" + array[2];
            long value = Long.parseLong(array[1]);
            result.put(key, value);
        }
        return result;
    }


    @Override
    public Map<String, Long> getSystemIOAvgQuSz() {
        return getSystemIOResource(9, "getSystemIOAvgQuSz()", "各设备平均队列长度");
    }

    @Override
    public Map<String, Long> getSystemIOAvgRqSz() {
        return getSystemIOResource(8, "getSystemIOAvgRqSz()", "各设备平均请求大小");
    }

    @Override
    public Map<String, Long> getSystemIOAwait() {
        return getSystemIOResource(10, "getSystemIORAwait()", "各设备每次IO平均处理时间");
    }

    @Override
    public Map<String, Long> getSystemIORAwait() {
        return getSystemIOResource(11, "getSystemIORAwait()", "各设备读请求平均耗时");
    }

    @Override
    public Map<String, Long> getSystemIOReadRequest() {
        return getSystemIOResource(4, "getSystemIOReadRequest()", "各设备每秒读请求数量");
    }

    @Override
    public Map<String, Long> getSystemIOReadBytes() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$6}'", "各设备每秒读取字节数");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemIOReadBytes()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            double value = 1024 * Double.parseDouble(array[1]);
            result.put(key, (long) value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemIORRQMS() {
        return getSystemIOResource(2, "getSystemIORRQMS()", "各设备每秒合并到设备队列的读请求数");
    }

    @Override
    public Map<String, Long> getSystemIOSVCTM() {
        return getSystemIOResource(13, "getSystemIOSVCTM()", "每次各设备IO平均服务时间");
    }

    @Override
    public Map<String, Double> getSystemIOUtil() {
        Map<String, Double> result = new HashMap<>();
        List<String> lines = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$14}'", "各设备I/O请求的CPU时间百分比");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemIOUtil()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemIOWAwait() {
        return getSystemIOResource(12, "getSystemIOWAwait()", "各设备写请求平均耗时");
    }

    @Override
    public Map<String, Long> getSystemIOWriteRequest() {
        return getSystemIOResource(5, "getSystemIOWriteRequest()", "各设备每秒写请求数量");
    }

    @Override
    public Map<String, Long> getSystemIOWriteBytes() {
        Map<String, Long> result = new HashMap<>();
        List<String> lines = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$7}'", "各设备每秒写字节数");
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemIOWriteBytes()||msg=data is not enough");
                return result;
            }
            String key = array[0];
            double value = 1024 * Double.parseDouble(array[1]);
            result.put(key, (long) value);
        }
        return result;
    }

    @Override
    public Map<String, Long> getSystemIOWRQMS() {
        return getSystemIOResource(3, "getSystemIOWRQMS()", "各设备每秒合并到设备队列的写请求数");
    }

    /**
     * 获取磁盘IO的相关资源
     * @param index 资源下标
     * @param methodName 获取该资源的方法名称
     * @param resourceMessage 资源描述
     * @return
     */
    private Map<String, Long> getSystemIOResource(int index, String methodName, String resourceMessage) {
        Map<String, Long> result = new HashMap<>();
        String procFDShell = String.format("iostat -dkx | head -n -1 | awk 'NR>3{print $1,$%d}'", index);
        List<String> lines = getOutputByCmd(procFDShell, resourceMessage);
        for (String line : lines) {
            String[] array = line.split("\\s+");
            if (array.length < 2) {
                LOGGER.error("class=LinuxSystemMetricsService()||method={}||msg=data is not enough", methodName);
                return result;
            }
            String key = array[0];
            double value = Double.parseDouble(array[1]);
            result.put(key, (long) value);
        }
        return result;
    }

    @Override
    public double getSystemLoad1() {
        List<String> lines = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $4}'", "系统近1分钟平均负载");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemLoad1||msg=获取系统近1分钟平均负载失败");
            return 0.0d;
        }
    }

    @Override
    public double getSystemLoad5() {
        List<String> lines = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $5}'", "系统近5分钟平均负载");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemLoad5||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public double getSystemLoad15() {
        List<String> lines = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $6}'", "系统近15分钟平均负载");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemLoad15||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public long getSystemMemBuffered() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Buffers:' | awk '{print $2}'", "系统文件缓冲区的物理RAM量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemBuffered()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemCached() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Cached:' | awk '{print $2}'", "缓存内存的物理RAM量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemCached()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemCommitLimit() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'CommitLimit:' | awk '{print $2}'", "系统当前可分配的内存总量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemCommitLimit()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemCommitted() {
        return getSystemMemCommitLimit() - getSystemMemCommittedAs();
    }

    @Override
    public long getSystemMemCommittedAs() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Committed_AS:' | awk '{print $2}'", "系统已分配的包括进程未使用的内存量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemCommittedAs()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemNonPaged() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'KernelStack:' | awk '{print $2}'", "写入磁盘的物理内存量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemNonPaged()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemPaged() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Writeback:' | awk '{print $2}'", "没被使用是可以写入磁盘的物理内存量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemPaged()||msg=data is null");
            return 0L;
        }
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
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Shmem:' | awk '{print $2}'", "用作共享内存的物理RAM量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemShared()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemSlab() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Slab:' | awk '{print $2}'", "内核用来缓存数据结构供自己使用的内存量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemSlab()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemTotal() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemTotal:' | awk '{print $2}'", "系统物理内存总量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemTotal()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemFree() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemFree:' | awk '{print $2}'", "系系统空闲内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemFree()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemMemUsed() {
        return getSystemMemTotal() - getSystemMemFree();
    }

    @Override
    public long getSystemSwapCached() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'SwapCached:' | awk '{print $2}'", "系统用作缓存的交换空间");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemSwapCached()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public long getSystemSwapFree() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'SwapFree:' | awk '{print $2}'", "系统空闲swap大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemSwapFree()||msg=data is null");
            return 0L;
        }
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
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'SwapTotal:' | awk '{print $2}'", "系统swap总大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemSwapTotal()||msg=data is null");
            return 0L;
        }
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

    @Override
    public long getSystemNetworkReceiveBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double processReceiveBytesPs = curLinuxNetFlow.getSystemReceiveBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            return (long) processReceiveBytesPs;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getSystemNetworkReceiveBytesPs()||msg=获取系统网络每秒下行流量失败",
                    e);
            return 0L;
        }
    }

    @Override
    public long getSystemNetworkSendBytesPs() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double processTransmitBytesPs = curLinuxNetFlow.getSystemTransmitBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            return (long) processTransmitBytesPs;
        } catch (Exception e) {
            LOGGER.error("class=LinuxOSResourceService||method=getSystemNetworkSendBytesPs()||msg=获取系统网络每秒上行流量失败",
                    e);
            return 0L;
        }
    }

    @Override
    public int getSystemNetworkTcpConnectionNum() {
        List<String> lines = getOutputByCmd("netstat -an | grep -c 'tcp'", "系统tcp连接数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpConnectionNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public int getSystemNetworkTcpTimeWaitNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'TIME_WAIT'", "系统处于 time wait 状态 tcp 连接数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpTimeWaitNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public int getSystemNetworkTcpCloseWaitNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'CLOSE_WAIT'", "系统处于 close wait 状态 tcp 连接数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpCloseWaitNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkTcpActiveOpens() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $6}'", "系统启动以来 Tcp 主动连接次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpActiveOpens()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkTcpPassiveOpens() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $7}'", "系统启动以来 Tcp 被动连接次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpPassiveOpens()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkTcpAttemptFails() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $8}'", "系统启动以来 Tcp 连接失败次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpAttemptFails()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkTcpEstabResets() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $9}'", "系统启动以来 Tcp 连接异常断开次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpEstabResets()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkTcpRetransSegs() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $13}'", "系统启动以来 Tcp 重传的报文段总个数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpRetransSegs()||msg=data is null");
            return 0;
        }
    }


    @Override
    public long getSystemNetworkTcpExtListenOverflows() {
        List<String> lines = getOutputByCmd(
                "netstat -s | egrep \"listen|LISTEN\" | awk '{a+=$1}{print a}'",
                "系统启动以来 Tcp 监听队列溢出次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        }
        return 0;
    }

    @Override
    public long getSystemNetworkUdpInDatagrams() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $2}'", "系统启动以来 UDP 入包量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpInDatagrams()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkUdpOutDatagrams() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $5}'", "系统启动以来 UDP 出包量");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpOutDatagrams()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkUdpInErrors() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $4}'", "系统启动以来 UDP 入包错误数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpInErrors()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkUdpNoPorts() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $3}'", "系统启动以来 UDP 端口不可达个数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpNoPorts()||msg=data is null");
            return 0;
        }
    }

    @Override
    public long getSystemNetworkUdpSendBufferErrors() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $7}'", "系统启动以来 UDP 发送缓冲区满次数");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpSendBufferErrors()||msg=data is null");
            return 0;
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

    @Override
    public SystemMetrics getSystemMetrics() {
        return buildSystemMetrics();
    }

    /**
     * 获取系统所有的指标
     */
    private SystemMetrics buildSystemMetrics() {

        SystemMetrics systemMetrics = new SystemMetrics();

        LinuxSystemResource systemResource = new LinuxSystemResource();

        // 系统时间和进程个数
        buildSystemProcessAndTimeMetrics(systemMetrics);

        // 系统cpu情况
        buildCpuMetrics(systemMetrics, systemResource);

        // 系统磁盘分区情况
        buildDiskMetrics(systemMetrics, systemResource);

        // 系统磁盘读写情况
        buildDiskReadWriteMetrics(systemMetrics, systemResource);

        // 系统文件句柄数
        buildFileHandleMetrics(systemMetrics, systemResource);

        // 系统各分区inode数量
        buildDiskInodeMetrics(systemMetrics, systemResource);

        // 系统IO
        buildIOMetrics(systemMetrics, systemResource);

        // 系统平均负载
        buildSystemLoadMetrics(systemMetrics, systemResource);

        // 系统内存
        buildSystemMemoryMetrics(systemMetrics, systemResource);

        // 系统网络流量
        buildSystemReceiveSendBytesMetrics(systemMetrics);

        // tcp
        buildSystemTcpMetrics(systemMetrics, systemResource);

        // udp
        buildSystemUdpMetrics(systemMetrics, systemResource);



        return systemMetrics;
    }

    private void buildSystemProcessAndTimeMetrics(SystemMetrics systemMetrics) {
        systemMetrics.setSystemStartupTime(getSystemStartupTime());
        systemMetrics.setSystemNtpOffset(getSystemNtpOffset());
        systemMetrics.setSystemProcCount(getSystemProcCount());
        systemMetrics.setSystemUptime(getSystemUptime());
    }

    private void buildSystemUdpMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        Map<String, Long> systemNetworkUdpStat = systemResource.getSystemNetworkUdpStat();
        systemMetrics.setSystemNetworkUdpInDatagrams(getResourceValueByKey(systemNetworkUdpStat, "InDatagrams", "getSystemNetworkUdpInDatagrams"));
        systemMetrics.setSystemNetworkUdpOutDatagrams(getResourceValueByKey(systemNetworkUdpStat, "OutDatagrams", "getSystemNetworkUdpOutDatagrams"));
        systemMetrics.setSystemNetworkUdpSendBufferErrors(getResourceValueByKey(systemNetworkUdpStat, "SndbufErrors", "getSystemNetworkUdpSendBufferErrors"));
        systemMetrics.setSystemNetworkUdpNoPorts(getResourceValueByKey(systemNetworkUdpStat, "NoPorts", "getSystemNetworkUdpNoPorts"));
        systemMetrics.setSystemNetworkUdpInErrors(getResourceValueByKey(systemNetworkUdpStat, "InErrors", "getSystemNetworkUdpInErrors"));
    }

    private void buildSystemTcpMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        Map<String, Long> currentSystemNetworkTcpStat = systemResource.getCurrentSystemNetworkTcpStat();
        Map<String, Long> systemNetworkTcpStat = systemResource.getSystemNetworkTcpStat();
        systemMetrics.setSystemNetworkTcpTimeWaitNum((int) getResourceValueByKey(currentSystemNetworkTcpStat, "TIME_WAIT", "getSystemNetworkTcpTimeWaitNum"));
        systemMetrics.setSystemNetworkTcpCloseWaitNum((int) getResourceValueByKey(currentSystemNetworkTcpStat, "CLOSE_WAIT", "getSystemNetworkTcpTimeWaitNum"));
        systemMetrics.setSystemNetworkTcpConnectionNum(getSystemNetworkTcpConnectionNumByTcpStat(currentSystemNetworkTcpStat));
        systemMetrics.setSystemNetworkTcpActiveOpens(getResourceValueByKey(systemNetworkTcpStat, "ActiveOpens", "getSystemNetworkTcpActiveOpens"));
        systemMetrics.setSystemNetworkTcpPassiveOpens(getResourceValueByKey(systemNetworkTcpStat, "PassiveOpens", "getSystemNetworkTcpPassiveOpens"));
        systemMetrics.setSystemNetworkTcpAttemptFails(getResourceValueByKey(systemNetworkTcpStat, "AttemptFails", "getSystemNetworkTcpAttemptFails"));
        systemMetrics.setSystemNetworkTcpEstabResets(getResourceValueByKey(systemNetworkTcpStat, "EstabResets", "getSystemNetworkTcpEstabResets"));
        systemMetrics.setSystemNetworkTcpRetransSegs(getResourceValueByKey(systemNetworkTcpStat, "RetransSegs", "getSystemNetworkTcpRetransSegs"));
        systemMetrics.setSystemNetworkTcpExtListenOverflows(getSystemNetworkTcpExtListenOverflows());
    }

    private void buildSystemReceiveSendBytesMetrics(SystemMetrics systemMetrics) {
        systemMetrics.setSystemNetworkReceiveBytesPs(getSystemNetworkReceiveBytesPs());
        systemMetrics.setSystemNetworkSendBytesPs(getSystemNetworkSendBytesPs());
    }

    private void buildSystemMemoryMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        Map<String, Long> systemMemoryInfo = systemResource.getSystemMemoryInfo();
        long systemMemTotal = getResourceValueByKey(systemMemoryInfo, "MemTotal:", "getSystemMemTotal");
        long systemMemFree = getResourceValueByKey(systemMemoryInfo, "MemFree:", "getSystemMemFree");
        long systemMemUsed = systemMemTotal - systemMemFree;
        systemMetrics.setSystemMemTotal(systemMemTotal);
        systemMetrics.setSystemMemFree(systemMemFree);
        systemMetrics.setSystemMemUsed(systemMemUsed);
        systemMetrics.setSystemMemUsedPercent(systemMemTotal == 0 ? 0.0d : 1.0 * systemMemUsed / systemMemTotal);
        systemMetrics.setSystemMemFreePercent(systemMemTotal == 0 ? 0.0d : 1.0 * systemMemFree / systemMemTotal);
        systemMetrics.setSystemMemShared(getResourceValueByKey(systemMemoryInfo, "Shmem:", "getSystemMemShared"));
        systemMetrics.setSystemMemPaged(getResourceValueByKey(systemMemoryInfo, "Writeback:", "getSystemMemPaged"));
        systemMetrics.setSystemMemNonPaged(getResourceValueByKey(systemMemoryInfo, "KernelStack:", "getSystemMemNonPaged"));
        systemMetrics.setSystemMemBuffered(getResourceValueByKey(systemMemoryInfo, "Buffers:", "getSystemMemBuffered"));
        systemMetrics.setSystemMemCached(getResourceValueByKey(systemMemoryInfo, "Cached:", "getSystemMemCached"));
        long systemMemCommitLimit = getResourceValueByKey(systemMemoryInfo, "CommitLimit:", "getSystemMemCommitLimit");
        long systemMemCommittedAs = getResourceValueByKey(systemMemoryInfo, "Committed_AS:", "getSystemMemCommittedAs");
        systemMetrics.setSystemMemCommitLimit(systemMemCommitLimit);
        systemMetrics.setSystemMemCommittedAs(systemMemCommittedAs);
        systemMetrics.setSystemMemCommitted(systemMemCommitLimit - systemMemCommittedAs);
        systemMetrics.setSystemMemSlab(getResourceValueByKey(systemMemoryInfo, "Slab:", "getSystemMemSlab"));
        systemMetrics.setSystemSwapCached(getResourceValueByKey(systemMemoryInfo, "SwapCached:", "getSystemSwapCached"));
        long systemSwapTotal = getResourceValueByKey(systemMemoryInfo, "SwapTotal:", "getSystemSwapTotal");
        long systemSwapFree = getResourceValueByKey(systemMemoryInfo, "SwapFree:", "getSystemSwapFree");
        long systemSwapUsed = systemSwapTotal - systemSwapFree;
        systemMetrics.setSystemSwapTotal(systemSwapTotal);
        systemMetrics.setSystemSwapFree(systemSwapFree);
        systemMetrics.setSystemSwapUsed(systemSwapUsed);
        systemMetrics.setSystemSwapUsedPercent(systemSwapTotal == 0 ? 0.0d : 1.0 * systemSwapUsed / systemSwapTotal);
        systemMetrics.setSystemSwapFreePercent(systemSwapTotal == 0 ? 0.0d : 1.0 * systemSwapFree / systemSwapTotal);
    }

    private void buildSystemLoadMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemLoad = systemResource.getSystemLoad();
        systemMetrics.setSystemLoad1(getOneSystemResource(systemLoad, 0, 3, "系统近1分钟平均负载"));
        systemMetrics.setSystemLoad5(getOneSystemResource(systemLoad, 1, 3, "系统近5分钟平均负载"));
        systemMetrics.setSystemLoad15(getOneSystemResource(systemLoad, 2, 3, "系统近15分钟平均负载"));
    }

    private void buildIOMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemIOStat = systemResource.getSystemIOStat();
        systemMetrics.setSystemIORRQMS(getMoreSystemResource(systemIOStat, 1, 14, 1, "各设备每秒合并到设备队列的读请求数"));
        systemMetrics.setSystemIOWRQMS(getMoreSystemResource(systemIOStat, 2, 14, 1, "各设备每秒合并到设备队列的写请求数"));
        systemMetrics.setSystemIOReadRequest(getMoreSystemResource(systemIOStat, 3, 14, 1, "各设备每秒读请求数量"));
        systemMetrics.setSystemIOWriteRequest(getMoreSystemResource(systemIOStat, 4, 14, 1, "各设备每秒写请求数量"));
        systemMetrics.setSystemIOReadBytes(getMoreSystemResource(systemIOStat, 5, 14, 1024, "各设备每秒读取字节数"));
        systemMetrics.setSystemIOWriteBytes(getMoreSystemResource(systemIOStat, 6, 14, 1024, "各设备每秒写字节数"));
        systemMetrics.setSystemIOAvgQuSz(getMoreSystemResource(systemIOStat, 8, 14, 1, "各设备平均队列长度"));
        systemMetrics.setSystemIOAwait(getMoreSystemResource(systemIOStat, 9, 14, 1, "各设备每次IO平均处理时间"));
        systemMetrics.setSystemIORAwait(getMoreSystemResource(systemIOStat, 10, 14, 1, "各设备读请求平均耗时"));
        systemMetrics.setSystemIOWAwait(getMoreSystemResource(systemIOStat, 11, 14, 1, "各设备写请求平均耗时"));
        systemMetrics.setSystemIOSVCTM(getMoreSystemResource(systemIOStat, 12, 14, 1, "每次各设备IO平均服务时间"));
        systemMetrics.setSystemIOUtil(getSystemIOUtilByIOStat(systemIOStat));
    }

    private void buildDiskInodeMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemDiskInodeStat = systemResource.getSystemDiskInodeStat();
        systemMetrics.setSystemDiskInodesTotal(getMoreSystemResource(systemDiskInodeStat, 1, 4, 1, "系统各分区inode总数量"));
        systemMetrics.setSystemDiskInodesUsed(getMoreSystemResource(systemDiskInodeStat, 2, 4, 1,"系统各分区已用inode数量"));
        systemMetrics.setSystemDiskInodesFree(getMoreSystemResource(systemDiskInodeStat, 3, 4, 1, "系统各分区空闲inode数量"));
        systemMetrics.setSystemDiskInodesUsedPercent(getSystemDiskInodesUsedPercentByInodeStat(systemDiskInodeStat));
    }

    private void buildFileHandleMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemFileHandleNum = systemResource.getSystemFileHandleNum();
        int systemFilesUsed = getSystemFileHandle(systemFileHandleNum, 0, "系统使用的已分配文件句柄数");
        int systemFilesNotUsed = getSystemFileHandle(systemFileHandleNum, 1, "系统未使用的已分配文件句柄数");
        int systemFilesMax = getSystemFileHandle(systemFileHandleNum, 2, "系统可以打开的最大文件句柄数");
        int systemFilesAllocated = systemFilesUsed + systemFilesNotUsed;
        int systemFilesLeft = systemFilesMax - systemFilesAllocated;
        double systemFilesUsedPercent = 1.0 * systemFilesUsed / systemFilesAllocated;
        systemMetrics.setSystemFilesUsed(systemFilesUsed);
        systemMetrics.setSystemFilesNotUsed(systemFilesNotUsed);
        systemMetrics.setSystemFilesMax(systemFilesMax);
        systemMetrics.setSystemFilesAllocated(systemFilesAllocated);
        systemMetrics.setSystemFilesLeft(systemFilesLeft);
        systemMetrics.setSystemFilesUsedPercent(systemFilesUsedPercent);
    }

    private void buildDiskReadWriteMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemDiskReadWriteTime = systemResource.getSystemDiskReadWriteTime();
        systemMetrics.setSystemDiskReadTime(getMoreSystemResource(systemDiskReadWriteTime, 1, 3, 1, "各设备读操作耗时"));
        systemMetrics.setSystemDiskWriteTime(getMoreSystemResource(systemDiskReadWriteTime, 2, 3, 1, "各设备写操作耗时"));
        systemMetrics.setSystemDiskReadTimePercent(getSystemDiskReadWriteTimePercent(systemDiskReadWriteTime, true));
        systemMetrics.setSystemDiskWriteTimePercent(getSystemDiskReadWriteTimePercent(systemDiskReadWriteTime, false));
    }

    private void buildDiskMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemDiskStat = systemResource.getSystemDiskStat();
        systemMetrics.setSystemDiskBytesTotal(getMoreSystemResource(systemDiskStat, 1, 4, 1024, "磁盘各分区总量"));
        systemMetrics.setSystemDiskBytesUsed(getMoreSystemResource(systemDiskStat, 2, 4, 1024, "磁盘各分区用量大小"));
        systemMetrics.setSystemDiskBytesFree(getMoreSystemResource(systemDiskStat, 3, 4, 1024, "磁盘各分区总量"));
        systemMetrics.setSystemDiskUsedPercent(getSystemDiskUsedPercentBySystemDiskStat(systemDiskStat));
    }

    private void buildCpuMetrics(SystemMetrics systemMetrics, LinuxSystemResource systemResource) {
        List<String> systemCpuStat = systemResource.getSystemCpuStat();
        systemMetrics.setSystemCpuUser(getOneSystemResource(systemCpuStat, 0, 6, "用户态CPU时间占比"));
        systemMetrics.setSystemCpuSystem(getOneSystemResource(systemCpuStat, 1, 6, "内核态CPU时间占比"));
        systemMetrics.setSystemCpuIOWait(getOneSystemResource(systemCpuStat,2, 6, "等待I/O的CPU时间占比"));
        systemMetrics.setSystemCpuSteal(getOneSystemResource(systemCpuStat, 3, 6, "等待处理其他虚拟核的时间占比"));
        systemMetrics.setSystemCpuGuest(getOneSystemResource(systemCpuStat, 4, 6, "虚拟处理器CPU时间占比"));
        double systemCpuIdle = getOneSystemResource(systemCpuStat, 5, 6, "总体cpu空闲率");
        double systemCpuUtilTotalPercent = 100.0d - systemCpuIdle;
        systemMetrics.setSystemCpuIdle(systemCpuIdle);
        systemMetrics.setSystemCpuUtilTotalPercent(systemCpuUtilTotalPercent);
        systemMetrics.setSystemCpuUtil(systemCpuUtilTotalPercent * getSystemCpuNumCores());
        systemMetrics.setSystemCpuSwitches(getSystemCpuSwitches());
        systemMetrics.setSystemCpuNumCores(getSystemCpuNumCores());
    }

    final class LinuxSystemResource {

        /**
         * 获取系统cpu使用情况
         * 返回 %usr %sys %iowait %steal %guest %idle
         */
        private List<String> systemCpuStat;

        /**
         * 获取磁盘分区情况
         * 返回 磁盘分区名称(名称+挂载点,以:分割) 磁盘容量大小 磁盘已用容量 磁盘可用容量
         */
        private List<String> systemDiskStat;

        /**
         * 获取系统句柄数
         * 返回 已分配且使用中句柄数，已分配未使用，最大数目
         */
        private List<String> systemFileHandleNum;

        /**
         * 系统磁盘读写操作耗时
         * 返回 磁盘名称 读操作耗时 写操作耗时
         */
        private List<String> systemDiskReadWriteTime;

        /**
         * 获取系统磁盘分区inode情况
         * 返回 磁盘分区名称(名称+挂载点,以:分割) inode总量 inode已用 inode可用
         */
        private List<String> systemDiskInodeStat;

        /**
         * 获取系统磁盘IO情况
         */
        private List<String> systemIOStat;

        /**
         * 获取系统平均负载情况
         * 返回 近1分钟平均负载 近5分钟平均负载 近15分钟平均负载
         */
        private List<String> systemLoad;

        /**
         * 系统内存情况
         * 键为具体某种内存使用名称，值为该内存对应的值
         */
        private Map<String, Long> systemMemoryInfo;

        /**
         * 当前系统网络Tcp各状态统计
         * Tcp状态：个数
         */
        private Map<String, Long> currentSystemNetworkTcpStat;

        /**
         * 系统启动以来 Tcp 统计
         * Tcp:次数
         */
        private Map<String, Long> systemNetworkTcpStat;

        /**
         * 系统启动以来 Udp 统计
         * Udp:次数
         */
        private Map<String, Long> systemNetworkUdpStat;

        public LinuxSystemResource() {
            systemCpuStat = getOutputByCmd("mpstat | awk 'NR==4{print $3,$5,$6,$9,$10,$NF}'", "系统cpu使用情况");
            systemDiskStat = getOutputByCmd("df -k | awk 'NR>1{print $1,$2,$3,$4}'", "系统磁盘情况");
            systemFileHandleNum = getOutputByCmd("cat /proc/sys/fs/file-nr", "系统句柄数");
            systemDiskReadWriteTime = getOutputByCmd("vmstat -d | awk 'NR>2{print $1,$5,$9}'", "磁盘读写操作耗时");
            systemDiskInodeStat = getOutputByCmd("df -i | awk 'NR>1{print $1\":\"$6,$2,$3,$4}'", "系统各分区inode情况");
            systemIOStat = getOutputByCmd("iostat -dkx | head -n -1 | awk 'NR>3'", "系统各磁盘设备IO情况");
            systemLoad = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $4,$5,$6}'", "系统负载情况");
            systemMemoryInfo = getKeyValueResource("cat /proc/meminfo", 1024, "系统内存情况");
            currentSystemNetworkTcpStat = getKeyValueResource("netstat -an | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}'", 1, "当前系统网络Tcp情况");
            systemNetworkTcpStat = getSystemNetworkStat("cat /proc/net/snmp | grep 'Tcp:' | awk '{print $6,$7,$8,$9,$13}'", "系统启动以来网络Tcp情况");
            systemNetworkUdpStat = getSystemNetworkStat("cat /proc/net/snmp | grep 'Udp:' | awk '{print $2,$3,$4,$5,$7}'", "系统启动以来网络Udp情况");
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

        /**
         * 根据命令行获取Tcp信息
         * @param procFDShell 命令行
         * @param message 资源描述
         * @return
         */
        private Map<String, Long> getSystemNetworkStat(String procFDShell, String message) {
            Map<String, Long> result = new HashMap<>();
            List<String> lines = getOutputByCmd(procFDShell, message);
            if(lines.size() == 2) {
                String[] keys = lines.get(0).split("\\s+");
                String[] values = lines.get(1).split("\\s+");
                for (int i = 0; i < keys.length; i++) {
                    result.put(keys[i], Long.parseLong(values[i]));
                }
            }
            return result;
        }


        public List<String> getSystemCpuStat() {
            return systemCpuStat;
        }

        public List<String> getSystemDiskStat() {
            return systemDiskStat;
        }

        public List<String> getSystemFileHandleNum() {
            return systemFileHandleNum;
        }

        public List<String> getSystemDiskReadWriteTime() {
            return systemDiskReadWriteTime;
        }

        public List<String> getSystemDiskInodeStat() {
            return systemDiskInodeStat;
        }

        public List<String> getSystemIOStat() {
            return systemIOStat;
        }

        public List<String> getSystemLoad() {
            return systemLoad;
        }

        public Map<String, Long> getSystemMemoryInfo() {
            return systemMemoryInfo;
        }

        public Map<String, Long> getCurrentSystemNetworkTcpStat() {
            return currentSystemNetworkTcpStat;
        }

        public Map<String, Long> getSystemNetworkTcpStat() {
            return systemNetworkTcpStat;
        }

        public Map<String, Long> getSystemNetworkUdpStat() {
            return systemNetworkUdpStat;
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
     * 获取多行系统资源
     * @param systemResource 资源源文件
     * @param index 下标
     * @param length 每行数据长度限制
     * @param size 单位换算，若结果值单位为KB,需乘以1024；单位为个数，结果乘以1
     * @param message 资源描述
     * @return
     */
    private Map<String, Long> getMoreSystemResource(List<String> systemResource, int index, int length, int size, String message) {
        Map<String, Long> result = new HashMap<>();
        try {
            for (String s : systemResource) {
                String[] array = s.split("\\s+");
                if (array.length < length) {
                    LOGGER.error("获取系统资源项[{}}]失败", message);
                    return result;
                }
                double value = size * Double.parseDouble(array[index]);
                result.put(array[0], (long) value);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getMoreSystemResource()||msg=failed to get system resource",
                    e);
        }
        return result;
    }

    /**
     * 根据读写操作获取读写操作时间占比
     * @param isRead 是否是读操作
     * @return 读写操作时间占比
     */
    private double getSystemDiskReadWriteTimePercent(List<String> systemDiskReadWriteTime, boolean isRead) {
        try {
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
        } catch (Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemDiskReadWriteTimePercent()||msg=failed to get SystemDiskReadWriteTimePercent",
                    e);
        }
        return 0.0d;
    }

    private Map<String, Double> getSystemDiskUsedPercentBySystemDiskStat(List<String> systemDiskStat) {
        Map<String, Double> result = new HashMap<>();
        try {
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
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemDiskUsedPercent()||msg=failed to get SystemDiskUsedPercent",
                    e);
        }
        return result;
    }

    /**
     * 根据下标获取相应的文件句柄数
     * @param systemFileHandleNum 资源
     * @param index 下标
     * @param message 资源描述
     * @return 相对应文件句柄数
     */
    private int getSystemFileHandle(List<String> systemFileHandleNum, int index, String message) {

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
        }
        return 0;
    }

    private Map<String, Double> getSystemDiskInodesUsedPercentByInodeStat(List<String> systemDiskInodeStat) {
        Map<String, Double> result = new HashMap<>();
        try {
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
            return result;
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemDiskInodesUsedPercent()||msg=failed to get systemDiskInodesUsedPercent",
                    e);
        }
        return result;
    }

    private Map<String, Double> getSystemIOUtilByIOStat(List<String> systemIOStat) {
        Map<String, Double> result = new HashMap<>();
        try {
            for (String s : systemIOStat) {
                String[] array = s.split("\\s+");
                if (array.length < 14) {
                    LOGGER.error("获取系统资源项[{}}]失败", "各设备I/O请求的CPU时间百分比");
                    return result;
                }
                double value = Double.parseDouble(array[13]);
                result.put(array[0], value);
            }
        } catch(Exception e) {
            LOGGER.error("class=DefaultOSResourceService||method=getSystemIOUtil()||msg=failed to get system IOUtil",
                    e);
        }
        return result;
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

    private int getSystemNetworkTcpConnectionNumByTcpStat(Map<String, Long> currentSystemNetworkTcpStat) {
        long tcpConnectionNum = 0;
        Set<Map.Entry<String, Long>> entries = currentSystemNetworkTcpStat.entrySet();
        for (Map.Entry<String, Long> entry : entries) {
            tcpConnectionNum += entry.getValue();
        }
        return (int) tcpConnectionNum;
    }
}
