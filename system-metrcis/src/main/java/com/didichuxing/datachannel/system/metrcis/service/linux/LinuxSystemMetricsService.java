package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.SystemMetrics;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemResourceService;
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
public class LinuxSystemMetricsService implements SystemMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxSystemMetricsService.class);

    private LinuxNetFlow lastLinuxNetFlow;

    public LinuxSystemMetricsService() {
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

    @Override
    public SystemMetrics getSystemMetrics() {
        SystemMetrics systemMetrics = new SystemMetrics();
        SystemResourceService systemResourceService = new LinuxSystemResourceService();

        systemResourceService.clearCache();
        systemMetrics.setSystemNtpOffset(systemResourceService.getSystemNtpOffset());
        systemMetrics.setSystemProcCount(systemResourceService.getSystemProcCount());
        systemMetrics.setSystemStartupTime(systemResourceService.getSystemStartupTime());
        systemMetrics.setSystemUptime(systemResourceService.getSystemUptime());
        systemMetrics.setSystemCpuUtil(systemResourceService.getSystemCpuUtil());
        systemMetrics.setSystemCpuUtilTotalPercent(systemResourceService.getSystemCpuUtilTotalPercent());
        systemMetrics.setSystemCpuSwitches(systemResourceService.getSystemCpuSwitches());
        systemMetrics.setSystemCpuGuest(systemResourceService.getSystemCpuGuest());
        systemMetrics.setSystemCpuIdle(systemResourceService.getSystemCpuIdle());
        systemMetrics.setSystemCpuIOWait(systemResourceService.getSystemCpuIOWait());
        systemMetrics.setSystemCpuNumCores(systemResourceService.getSystemCpuNumCores());
        systemMetrics.setSystemCpuSteal(systemResourceService.getSystemCpuSteal());
        systemMetrics.setSystemCpuSystem(systemResourceService.getSystemCpuSystem());
        systemMetrics.setSystemCpuUser(systemResourceService.getSystemCpuUser());
        systemMetrics.setSystemDiskBytesFree(systemResourceService.getSystemDiskBytesFree());
        systemMetrics.setSystemDiskUsedPercent(systemResourceService.getSystemDiskUsedPercent());
        systemMetrics.setSystemDiskReadTime(systemResourceService.getSystemDiskReadTime());
        systemMetrics.setSystemDiskReadTimePercent(systemResourceService.getSystemDiskReadTimePercent());
        systemMetrics.setSystemDiskBytesTotal(systemResourceService.getSystemDiskBytesTotal());
        systemMetrics.setSystemDiskBytesUsed(systemResourceService.getSystemDiskBytesUsed());
        systemMetrics.setSystemDiskWriteTime(systemResourceService.getSystemDiskWriteTime());
        systemMetrics.setSystemDiskWriteTimePercent(systemResourceService.getSystemDiskWriteTimePercent());
        systemMetrics.setSystemFilesAllocated(systemResourceService.getSystemFilesAllocated());
        systemMetrics.setSystemFilesLeft(systemResourceService.getSystemFilesLeft());
        systemMetrics.setSystemFilesUsedPercent(systemResourceService.getSystemFilesUsedPercent());
        systemMetrics.setSystemFilesMax(systemResourceService.getSystemFilesMax());
        systemMetrics.setSystemFilesUsed(systemResourceService.getSystemFilesUsed());
        systemMetrics.setSystemFilesNotUsed(systemResourceService.getSystemFilesNotUsed());
        systemMetrics.setSystemDiskInodesFree(systemResourceService.getSystemDiskInodesFree());
        systemMetrics.setSystemDiskInodesUsedPercent(systemResourceService.getSystemDiskInodesUsedPercent());
        systemMetrics.setSystemDiskInodesTotal(systemResourceService.getSystemDiskInodesTotal());
        systemMetrics.setSystemDiskInodesUsed(systemResourceService.getSystemDiskInodesUsed());
        systemMetrics.setSystemIOAvgQuSz(systemResourceService.getSystemIOAvgQuSz());
        systemMetrics.setSystemIOAvgRqSz(systemResourceService.getSystemIOAvgRqSz());
        systemMetrics.setSystemIOAwait(systemResourceService.getSystemIOAwait());
        systemMetrics.setSystemIORAwait(systemResourceService.getSystemIORAwait());
        systemMetrics.setSystemIOReadRequest(systemResourceService.getSystemIOReadRequest());
        systemMetrics.setSystemIOReadBytes(systemResourceService.getSystemIOReadBytes());
        systemMetrics.setSystemIORRQMS(systemResourceService.getSystemIORRQMS());
        systemMetrics.setSystemIOSVCTM(systemResourceService.getSystemIOSVCTM());
        systemMetrics.setSystemIOUtil(systemResourceService.getSystemIOUtil());
        systemMetrics.setSystemIOWAwait(systemResourceService.getSystemIOWAwait());
        systemMetrics.setSystemIOWriteRequest(systemResourceService.getSystemIOWriteRequest());
        systemMetrics.setSystemIOWriteBytes(systemResourceService.getSystemIOWriteBytes());
        systemMetrics.setSystemIOWRQMS(systemResourceService.getSystemIOWRQMS());
        systemMetrics.setSystemLoad1(systemResourceService.getSystemLoad1());
        systemMetrics.setSystemLoad5(systemResourceService.getSystemLoad5());
        systemMetrics.setSystemLoad15(systemResourceService.getSystemLoad15());
        systemMetrics.setSystemMemCached(systemResourceService.getSystemMemCached());
        systemMetrics.setSystemMemBuffered(systemResourceService.getSystemMemBuffered());
        systemMetrics.setSystemMemFree(systemResourceService.getSystemMemFree());
        systemMetrics.setSystemMemCommitLimit(systemResourceService.getSystemMemCommitLimit());
        systemMetrics.setSystemMemSlab(systemResourceService.getSystemMemSlab());
        systemMetrics.setSystemMemCommitted(systemResourceService.getSystemMemCommitted());
        systemMetrics.setSystemMemCommittedAs(systemResourceService.getSystemMemCommittedAs());
        systemMetrics.setSystemMemFreePercent(systemResourceService.getSystemMemFreePercent());
        systemMetrics.setSystemMemNonPaged(systemResourceService.getSystemMemNonPaged());
        systemMetrics.setSystemMemPaged(systemResourceService.getSystemMemPaged());
        systemMetrics.setSystemMemShared(systemResourceService.getSystemMemShared());
        systemMetrics.setSystemMemTotal(systemResourceService.getSystemMemTotal());
        systemMetrics.setSystemMemUsed(systemResourceService.getSystemMemUsed());
        systemMetrics.setSystemMemUsedPercent(systemResourceService.getSystemMemUsedPercent());
        systemMetrics.setSystemSwapCached(systemResourceService.getSystemSwapCached());
        systemMetrics.setSystemSwapFree(systemResourceService.getSystemSwapFree());
        systemMetrics.setSystemSwapTotal(systemResourceService.getSystemSwapTotal());
        systemMetrics.setSystemSwapUsed(systemResourceService.getSystemSwapUsed());
        systemMetrics.setSystemSwapFreePercent(systemResourceService.getSystemSwapFreePercent());
        systemMetrics.setSystemSwapUsedPercent(systemResourceService.getSystemSwapUsedPercent());
        systemMetrics.setSystemNetworkSendBytesPs(systemResourceService.getSystemNetworkSendBytesPs());
        systemMetrics.setSystemNetworkReceiveBytesPs(systemResourceService.getSystemNetworkReceiveBytesPs());
        systemMetrics.setSystemNetworkTcpActiveOpens(systemResourceService.getSystemNetworkTcpActiveOpens());
        systemMetrics.setSystemNetworkTcpAttemptFails(systemResourceService.getSystemNetworkTcpAttemptFails());
        systemMetrics.setSystemNetworkTcpCloseWaitNum(systemResourceService.getSystemNetworkTcpCloseWaitNum());
        systemMetrics.setSystemNetworkTcpConnectionNum(systemResourceService.getSystemNetworkTcpConnectionNum());
        systemMetrics.setSystemNetworkTcpEstabResets(systemResourceService.getSystemNetworkTcpEstabResets());
        systemMetrics.setSystemNetworkTcpExtListenOverflows(systemResourceService.getSystemNetworkTcpExtListenOverflows());
        systemMetrics.setSystemNetworkTcpPassiveOpens(systemResourceService.getSystemNetworkTcpPassiveOpens());
        systemMetrics.setSystemNetworkTcpRetransSegs(systemResourceService.getSystemNetworkTcpRetransSegs());
        systemMetrics.setSystemNetworkTcpTimeWaitNum(systemResourceService.getSystemNetworkTcpTimeWaitNum());
        systemMetrics.setSystemNetworkUdpInDatagrams(systemResourceService.getSystemNetworkUdpInDatagrams());
        systemMetrics.setSystemNetworkUdpInErrors(systemResourceService.getSystemNetworkUdpInErrors());
        systemMetrics.setSystemNetworkUdpNoPorts(systemResourceService.getSystemNetworkUdpNoPorts());
        systemMetrics.setSystemNetworkUdpOutDatagrams(systemResourceService.getSystemNetworkUdpOutDatagrams());
        systemMetrics.setSystemNetworkUdpSendBufferErrors(systemResourceService.getSystemNetworkUdpSendBufferErrors());

        return systemMetrics;
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

}
