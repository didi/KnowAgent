package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.bean.*;
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

    /**
     * agent宿主机cpu核（逻辑核）
     */
    Integer CPU_NUM = Runtime.getRuntime().availableProcessors();

    @Override
    public String getHostName() {
        return null;
    }

    @Override
    public Long getSystemNtpOffset() {

        //TODO：

        List<String> output = getOutputByCmd("clock | awk '{print $4}'",
                "系统时间偏移量");
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            double v =  1000 * Double.parseDouble(output.get(0));
            return (long) v;
        }
        return 0L;
    }

    @Override
    public Integer getSystemProcCount() {
        return null;
    }

    @Override
    public Long getSystemStartupTime() {
        return System.currentTimeMillis() - getSystemUptime();
    }

    @Override
    public Long getSystemUptime() {
        List<String> systemUpTime = getOutputByCmd(
                "awk '{print $1}' /proc/uptime", "系统运行时间");
        if (!systemUpTime.isEmpty() && StringUtils.isNotBlank(systemUpTime.get(0))) {
            double v =  1000 * Double.parseDouble(systemUpTime.get(0));
            return (long) v;
        }else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl()||method=getSystemUptime()||msg=data is null");
        }
        return 0L;
    }

    @Override
    public Integer getProcessesBlocked() {
        return null;
    }

    @Override
    public Integer getProcessesSleeping() {
        return null;
    }

    @Override
    public Integer getProcessesZombies() {
        return null;
    }

    @Override
    public Integer getProcessesStopped() {
        return null;
    }

    @Override
    public Integer getProcessesRunning() {
        return null;
    }

    @Override
    public Integer getProcessesIdle() {
        return null;
    }

    @Override
    public Integer getProcessesWait() {
        return null;
    }

    @Override
    public Integer getProcessesDead() {
        return null;
    }

    @Override
    public Integer getProcessesPaging() {
        return null;
    }

    @Override
    public Integer getProcessesUnknown() {
        return null;
    }

    @Override
    public Integer getProcessesTotal() {
        return null;
    }

    @Override
    public Integer getProcessesTotalThreads() {
        return null;
    }

    @Override
    public Integer getSystemCpuCores() {
        return CPU_NUM;
    }

    @Override
    public PeriodStatistics getSystemCpuUtil() {
        PeriodStatistics periodStatistics = getSystemCpuUtilTotalPercent();
        periodStatistics.setLast(periodStatistics.getLast() * getSystemCpuCores());
        periodStatistics.setMin(periodStatistics.getMin() * getSystemCpuCores());
        periodStatistics.setMax(periodStatistics.getMax() * getSystemCpuCores());
        periodStatistics.setAvg(periodStatistics.getAvg() * getSystemCpuCores());
        periodStatistics.setStdDev(periodStatistics.getStdDev() * getSystemCpuCores());
        periodStatistics.setQuantile55(periodStatistics.getQuantile55() * getSystemCpuCores());
        periodStatistics.setQuantile75(periodStatistics.getQuantile75() * getSystemCpuCores());
        periodStatistics.setQuantile95(periodStatistics.getQuantile95() * getSystemCpuCores());
        periodStatistics.setQuantile99(periodStatistics.getQuantile99() * getSystemCpuCores());
        return periodStatistics;
    }

    @Override
    public PeriodStatistics getSystemCpuUtilTotalPercent() {
        PeriodStatistics periodStatistics = getSystemCpuIdle();
        periodStatistics.setLast(100.0d - periodStatistics.getLast());
        periodStatistics.setMin(100.0d - periodStatistics.getMin());
        periodStatistics.setMax(100.0d - periodStatistics.getMax());
        periodStatistics.setAvg(100.0d - periodStatistics.getAvg());
        periodStatistics.setStdDev(100.0d - periodStatistics.getStdDev());
        periodStatistics.setQuantile55(100.0d - periodStatistics.getQuantile55());
        periodStatistics.setQuantile75(100.0d - periodStatistics.getQuantile75());
        periodStatistics.setQuantile95(100.0d - periodStatistics.getQuantile95());
        periodStatistics.setQuantile99(100.0d - periodStatistics.getQuantile99());
        return periodStatistics;
    }

    @Override
    public PeriodStatistics getSystemCpuSystem() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUser() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuIdle() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $12}'", "总体cpu空闲率");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            Double systemCpuIdle = Double.parseDouble(lines.get(0));
            PeriodStatistics periodStatistics = new PeriodStatistics();
            periodStatistics.setLast(systemCpuIdle);

            //TODO：
            periodStatistics.setMin(systemCpuIdle);
            periodStatistics.setMax(systemCpuIdle);
            periodStatistics.setAvg(systemCpuIdle);
            periodStatistics.setStdDev(systemCpuIdle);
            periodStatistics.setQuantile55(systemCpuIdle);
            periodStatistics.setQuantile75(systemCpuIdle);
            periodStatistics.setQuantile95(systemCpuIdle);
            periodStatistics.setQuantile99(systemCpuIdle);

            return periodStatistics;

        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuIdle||msg=data is null");
            return PeriodStatistics.defaultValue();
        }
    }

    @Override
    public PeriodStatistics getSystemCpuSwitches() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUsageIrq() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUsageSoftIrq() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemLoad1() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemLoad5() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemLoad15() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuIOWait() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuGuest() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuSteal() {
        return null;
    }

    @Override
    public Long getSystemMemCommitLimit() {
        return null;
    }

    @Override
    public Long getSystemMemCommittedAs() {
        return null;
    }

    @Override
    public Long getSystemMemCommitted() {
        return null;
    }

    @Override
    public Long getSystemMemNonPaged() {
        return null;
    }

    @Override
    public Long getSystemMemPaged() {
        return null;
    }

    @Override
    public Long getSystemMemShared() {
        return null;
    }

    @Override
    public Long getSystemMemSlab() {
        return null;
    }

    @Override
    public Long getSystemMemTotal() {
        return null;
    }

    @Override
    public Long getSystemMemFree() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemFree:' | awk '{print $2}'", "系系统空闲内存大小");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemMemFree()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemUsed() {
        return null;
    }

    @Override
    public Long getSystemMemBuffered() {
        return null;
    }

    @Override
    public Long getSystemMemCached() {
        return null;
    }

    @Override
    public Double getSystemMemFreePercent() {
        return null;
    }

    @Override
    public Double getSystemMemUsedPercent() {
        return null;
    }

    @Override
    public Long getSystemSwapCached() {
        return null;
    }

    @Override
    public Long getSystemSwapFree() {
        return null;
    }

    @Override
    public Double getSystemSwapFreePercent() {
        return null;
    }

    @Override
    public Long getSystemSwapTotal() {
        return null;
    }

    @Override
    public Long getSystemSwapUsed() {
        return null;
    }

    @Override
    public Double getSystemSwapUsedPercent() {
        return null;
    }

    @Override
    public Integer getSystemDisks() {
        return null;
    }

    @Override
    public List<DiskInfo> getSystemDiskInfoList() {

        //TODO：

        List<DiskInfo> diskInfoList = new ArrayList<>();
        DiskInfo diskInfo = new DiskInfo();
        diskInfo.setFsType("ext3");
        diskInfo.setPath("/");
        diskInfo.setBytesFree(999 * 1024 * 1024 * 1024L);
        diskInfoList.add(diskInfo);

        return diskInfoList;

    }

    @Override
    public List<DiskIOInfo> getSystemDiskIOInfoList() {

        //TODO：

        List<DiskIOInfo> diskIOInfoList = new ArrayList<>();
        DiskIOInfo diskIOInfo = new DiskIOInfo();
        diskIOInfo.setDevice("vda");
        PeriodStatistics iOUtil = new PeriodStatistics();
        iOUtil.setLast(0.55d);
        iOUtil.setMin(0.55d);
        iOUtil.setMax(0.55d);
        iOUtil.setAvg(0.55d);
        iOUtil.setStdDev(0.55d);
        iOUtil.setQuantile55(0.55d);
        iOUtil.setQuantile75(0.55d);
        iOUtil.setQuantile95(0.55d);
        iOUtil.setQuantile99(0.55d);
        diskIOInfo.setiOUtil(iOUtil);
        diskIOInfoList.add(diskIOInfo);

        return diskIOInfoList;

    }

    @Override
    public Integer getSystemFilesMax() {
        return null;
    }

    @Override
    public Integer getSystemFilesAllocated() {
        return null;
    }

    @Override
    public Integer getSystemFilesLeft() {
        return null;
    }

    @Override
    public Double getSystemFilesUsedPercent() {
        return null;
    }

    @Override
    public Integer getSystemFilesUsed() {
        return null;
    }

    @Override
    public Integer getSystemFilesNotUsed() {
        return null;
    }

    @Override
    public Integer getSystemNetCards() {
        return null;
    }

    @Override
    public List<NetCardInfo> getSystemNetCardInfoList() {

        //TODO：

        List<NetCardInfo> netCardInfoList = new ArrayList<>();

        NetCardInfo netCardInfo = new NetCardInfo();
        netCardInfo.setSystemNetCardsBandDevice("en0");
        netCardInfo.setSystemNetCardsBandMacAddress("88:66:5a:3d:4d:42");
        netCardInfo.setSystemNetCardsBandWidth(87 * 1024 * 1024L);
        PeriodStatistics systemNetCardsSendBytesPs = new PeriodStatistics();
        Double value = 1 * 1024 * 1024d;
        systemNetCardsSendBytesPs.setLast(value);
        systemNetCardsSendBytesPs.setMin(value);
        systemNetCardsSendBytesPs.setMax(value);
        systemNetCardsSendBytesPs.setAvg(value);
        systemNetCardsSendBytesPs.setStdDev(value);
        systemNetCardsSendBytesPs.setQuantile55(value);
        systemNetCardsSendBytesPs.setQuantile75(value);
        systemNetCardsSendBytesPs.setQuantile95(value);
        systemNetCardsSendBytesPs.setQuantile99(value);
        netCardInfo.setSystemNetCardsSendBytesPs(systemNetCardsSendBytesPs);
        netCardInfoList.add(netCardInfo);

        return netCardInfoList;

    }

    @Override
    public PeriodStatistics getSystemNetworkReceiveBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemNetworkSendBytesPs() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemNetworkSendAndReceiveBytesPs() {

        //TODO：

        PeriodStatistics periodStatistics = new PeriodStatistics();
        periodStatistics.setLast(1 * 1024 * 1024d);
        periodStatistics.setMin(1 * 1024 * 1024d);
        periodStatistics.setMax(1 * 1024 * 1024d);
        periodStatistics.setAvg(1 * 1024 * 1024d);
        periodStatistics.setStdDev(1 * 1024 * 1024d);
        periodStatistics.setQuantile55(1 * 1024 * 1024d);
        periodStatistics.setQuantile75(1 * 1024 * 1024d);
        periodStatistics.setQuantile95(1 * 1024 * 1024d);
        periodStatistics.setQuantile99(1 * 1024 * 1024d);

        return periodStatistics;

    }

    @Override
    public PeriodStatistics getSystemNetWorkBandWidthUsedPercent() {

        //TODO：

        PeriodStatistics periodStatistics = new PeriodStatistics();
        periodStatistics.setLast(0.1);
        periodStatistics.setMin(0.1);
        periodStatistics.setMax(0.1);
        periodStatistics.setAvg(0.1);
        periodStatistics.setStdDev(0.1);
        periodStatistics.setQuantile55(0.1);
        periodStatistics.setQuantile75(0.1);
        periodStatistics.setQuantile95(0.1);
        periodStatistics.setQuantile99(0.1);
        return periodStatistics;
    }

    @Override
    public Integer getSystemNetworkTcpConnectionNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpListeningNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpEstablishedNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpSynSentNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpSynRecvNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpFinWait1Num() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpFinWait2Num() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpTimeWaitNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpClosedNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpCloseWaitNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpClosingNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpLastAckNum() {
        return null;
    }

    @Override
    public Integer getSystemNetworkTcpNoneNum() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpActiveOpens() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpPassiveOpens() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpAttemptFails() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpEstabResets() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpRetransSegs() {
        return null;
    }

    @Override
    public Long getSystemNetworkTcpExtListenOverflows() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpInDatagrams() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpOutDatagrams() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpInErrors() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpNoPorts() {
        return null;
    }

    @Override
    public Long getSystemNetworkUdpSendBufferErrors() {
        return null;
    }

    @Override
    public SystemMetrics getSystemMetrics() {
        return null;
    }

    @Override
    public String getOSType() {
        return null;
    }

    @Override
    public String getOSVersion() {
        return null;
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
