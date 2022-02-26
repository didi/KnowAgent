package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.didichuxing.datachannel.system.metrcis.Metrics;
import com.didichuxing.datachannel.system.metrcis.bean.*;
import com.didichuxing.datachannel.system.metrcis.exception.MetricsException;
import com.didichuxing.datachannel.system.metrcis.service.DiskIOMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.DiskMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.NetCardMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    private Integer CPU_NUM = Runtime.getRuntime().availableProcessors();

    private String                   HOSTNAME;

    private LinuxNetFlow lastLinuxNetFlow;

    /**************************** 待计算字段 ****************************/

    private PeriodStatistics systemCpuUtil = new PeriodStatistics();

    private PeriodStatistics systemCpuIdle = new PeriodStatistics();

    private PeriodStatistics systemCpuUtilTotalPercent = new PeriodStatistics();

    private PeriodStatistics systemNetworkReceiveBytesPs = new PeriodStatistics();

    private PeriodStatistics systemNetworkSendBytesPs = new PeriodStatistics();

    private PeriodStatistics systemNetworkSendAndReceiveBytesPs = new PeriodStatistics();

    private PeriodStatistics systemNetWorkBandWidthUsedPercent = new PeriodStatistics();

    /**************************** 指标计算服务对象 ****************************/

    private DiskIOMetricsService diskIOMetricsService;

    private DiskMetricsService diskMetricsService;

    private NetCardMetricsService netCardMetricsService;

    public LinuxSystemMetricsServiceImpl() throws MetricsException {
        setHostName();
        diskIOMetricsService = Metrics.getMetricsServiceFactory().createDiskIOMetricsService();
        diskMetricsService = Metrics.getMetricsServiceFactory().createDiskMetricsService();
        netCardMetricsService = Metrics.getMetricsServiceFactory().createNetCardMetricsService();
        try {
            lastLinuxNetFlow = new LinuxNetFlow();// 记录上次的收发字节数
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsService||method=LinuxSystemMetricsServiceImpl()||msg=NetFlow init failed",
                    e);
        }
    }

    @Override
    public String getHostName() {
        return HOSTNAME;
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

    private void calcSystemCpuUtil() {
        systemCpuUtil.add(getSystemCpuUtilTotalPercentOnly() * getSystemCpuCores());
    }

    @Override
    public PeriodStatistics getSystemCpuUtil() {
        return systemCpuUtil.snapshot();
    }

    private Double getSystemCpuUtilTotalPercentOnly() {
        return 100.0d - getSystemCpuIdleOnly();
    }

    private void calcSystemCpuUtilTotalPercent() {
        systemCpuUtilTotalPercent.add(100.0d - getSystemCpuIdleOnly());
    }

    @Override
    public PeriodStatistics getSystemCpuUtilTotalPercent() {
        return systemCpuUtilTotalPercent.snapshot();
    }

    @Override
    public PeriodStatistics getSystemCpuSystem() {
        return null;
    }

    @Override
    public PeriodStatistics getSystemCpuUser() {
        return null;
    }

    private Double getSystemCpuIdleOnly() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $12}'", "总体cpu空闲率");
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuIdle||msg=data is null");
            return 0d;
        }
    }

    private void calcSystemCpuIdle() {
        systemCpuIdle.add(getSystemCpuIdleOnly());
    }

    @Override
    public PeriodStatistics getSystemCpuIdle() {
        return systemCpuIdle.snapshot();
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
        Map<String, String> path2FsTypeMap = diskMetricsService.getFsType();
        Map<String, Long> path2BytesFreeMap = diskMetricsService.getBytesFree();
        List<DiskInfo> diskInfoList = new ArrayList<>(path2FsTypeMap.size());
        for (Map.Entry<String, String> path2FsTypeEntry : path2FsTypeMap.entrySet()) {
            String path = path2FsTypeEntry.getKey();
            String fsType = path2FsTypeEntry.getValue();
            Long bytesFree = path2BytesFreeMap.get(path);
            if(null == bytesFree) {
                //TODO：
                throw new RuntimeException();
            }
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setFsType(fsType);
            diskInfo.setPath(path);
            diskInfo.setBytesFree(bytesFree);
            diskInfoList.add(diskInfo);
        }
        return diskInfoList;
    }

    @Override
    public List<DiskIOInfo> getSystemDiskIOInfoList() {
        Map<String, PeriodStatistics> device2IOUtilMap = diskIOMetricsService.getIOUtil();
        List<DiskIOInfo> diskIOInfoList = new ArrayList<>(device2IOUtilMap.size());
        for (Map.Entry<String, PeriodStatistics> device2IOUtilEntry : device2IOUtilMap.entrySet()) {
            String device = device2IOUtilEntry.getKey();
            PeriodStatistics iOUtil = device2IOUtilEntry.getValue();
            DiskIOInfo diskIOInfo = new DiskIOInfo();
            diskIOInfo.setDevice(device);
            diskIOInfo.setiOUtil(iOUtil);
            diskIOInfoList.add(diskIOInfo);
        }
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
        Map<String, PeriodStatistics> device2SendBytesPsMap = netCardMetricsService.getSendBytesPs();
        Map<String, String> device2MacAddressMap = netCardMetricsService.getMacAddress();
        Map<String, Long> device2BandWidthMap = netCardMetricsService.getBandWidth();
        List<NetCardInfo> netCardInfoList = new ArrayList<>(device2MacAddressMap.size());

        for (Map.Entry<String, String> device2MacAddressEntry : device2MacAddressMap.entrySet()) {
            String device = device2MacAddressEntry.getKey();
            String macAddress = device2MacAddressEntry.getValue();
            Long bandWidth = device2BandWidthMap.get(device);
            PeriodStatistics sendBytesPs = device2SendBytesPsMap.get(device);
            if(null == sendBytesPs || null == bandWidth) {
                //TODO：
                throw new RuntimeException();
            }
            NetCardInfo netCardInfo = new NetCardInfo();
            netCardInfo.setSystemNetCardsBandDevice(device);
            netCardInfo.setSystemNetCardsBandMacAddress(macAddress);
            netCardInfo.setSystemNetCardsBandWidth(bandWidth);
            netCardInfo.setSystemNetCardsSendBytesPs(sendBytesPs);
            netCardInfoList.add(netCardInfo);
        }
        return netCardInfoList;

    }

    private void calcSystemNetworkReceiveBytesPs() {
        systemNetworkReceiveBytesPs.add(getSystemNetworkReceiveBytesPsOnly());
    }

    private Double getSystemNetworkReceiveBytesPsOnly() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double processReceiveBytesPs = curLinuxNetFlow.getSystemReceiveBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            return  processReceiveBytesPs;
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemNetworkReceiveBytesPsOnly()||msg=获取系统网络每秒下行流量失败",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getSystemNetworkReceiveBytesPs() {
        return systemNetworkReceiveBytesPs.snapshot();
    }

    private void calcSystemNetworkSendBytesPs() {
        systemNetworkSendBytesPs.add(getSystemNetworkSendBytesPsOnly());
    }

    private Double getSystemNetworkSendBytesPsOnly() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double processTransmitBytesPs = curLinuxNetFlow.getSystemTransmitBytesPs(lastLinuxNetFlow);
            lastLinuxNetFlow = curLinuxNetFlow;
            return  processTransmitBytesPs;
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemNetworkSendBytesPsOnly()||msg=获取系统网络每秒上行流量失败",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getSystemNetworkSendBytesPs() {
        return systemNetworkSendBytesPs.snapshot();
    }

    private void calcSystemNetworkSendAndReceiveBytesPs() {
        systemNetworkSendAndReceiveBytesPs.add(getSystemNetworkSendAndReceiveBytesPsOnly());
    }

    private Double getSystemNetworkSendAndReceiveBytesPsOnly() {
        return this.getSystemNetworkSendBytesPsOnly() + this.getSystemNetworkReceiveBytesPsOnly();
    }

    @Override
    public PeriodStatistics getSystemNetworkSendAndReceiveBytesPs() {
        return systemNetworkSendAndReceiveBytesPs.snapshot();
    }

    private void calcSystemNetWorkBandWidthUsedPercent() {
        systemNetWorkBandWidthUsedPercent.add(getSystemNetWorkBandWidthUsedPercentOnly());
    }

    private Double getSystemNetWorkBandWidthUsedPercentOnly() {
        Long systemNetWorkBand = 0L;
        /*
         * 1.）获取系统全部网卡对应带宽之和
         */
        for(NetCardInfo netCardInfo : this.getSystemNetCardInfoList()) {
            systemNetWorkBand += netCardInfo.getSystemNetCardsBandWidth();
        }
        /*
         * 2.）获取系统当前上、下行总流量
         */
        Double systemNetworkSendAndReceiveBytesPs = getSystemNetworkSendAndReceiveBytesPsOnly();
        return systemNetworkSendAndReceiveBytesPs / systemNetWorkBand;
    }

    @Override
    public PeriodStatistics getSystemNetWorkBandWidthUsedPercent() {
        return systemNetWorkBandWidthUsedPercent.snapshot();
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

    private void setHostName() {
        try {
            String hostname = getHostnameByexec();
            if (StringUtils.isNotBlank(hostname)) {
                HOSTNAME = hostname.trim();
            } else {
                HOSTNAME = InetAddress.getLocalHost().getHostName();
            }
        } catch (UnknownHostException e) {
            HOSTNAME = "LocalHost";
        }
    }

    private String getHostnameByexec() {
        StringBuffer buf = new StringBuffer();
        try {
            Runtime run = Runtime.getRuntime();
            Process proc = run.exec("hostname");
            BufferedInputStream in = new BufferedInputStream(proc.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = br.readLine()) != null) {
                buf.append(s);
            }
            String hostname = buf.toString();
            if (StringUtils.isBlank(hostname) || hostname.contains("localhost")
                    || hostname.indexOf("请求超时") != -1) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return buf.toString();
    }

}
