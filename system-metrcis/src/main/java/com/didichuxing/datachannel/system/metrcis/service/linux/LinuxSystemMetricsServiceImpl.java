package com.didichuxing.datachannel.system.metrcis.service.linux;

import com.alibaba.fastjson.JSON;
import com.didichuxing.datachannel.system.metrcis.annotation.PeriodMethod;
import com.didichuxing.datachannel.system.metrcis.bean.*;
import com.didichuxing.datachannel.system.metrcis.service.DiskIOMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.DiskMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.NetCardMetricsService;
import com.didichuxing.datachannel.system.metrcis.service.SystemMetricsService;
import com.didichuxing.datachannel.system.metrcis.util.MathUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * 获取系统级指标
 * 包括按需获取指标数据和一次性获取所有指标数据
 * @author william.
 */
public class LinuxSystemMetricsServiceImpl extends LinuxMetricsService implements SystemMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinuxSystemMetricsServiceImpl.class);

    private LinuxNetFlow lastLinuxNetFlowSend;

    private LinuxNetFlow lastLinuxNetFlowReceive;

    private LinuxNetFlow lastLinuxNetFlowSendReceive;

    private LinuxNetFlow lastLinuxNetFlowSendReceiveUsedInNetWorkBandWidthUsedPercent;

    private LinuxCpuTime lastLinuxCpuTimeSystemCpuUtilTotalPercent;

    private LinuxCpuTime lastLinuxCpuTimeSystemCpuUtil;

    /**************************** 待计算字段 ****************************/

    private PeriodStatistics systemCpuUtil = new PeriodStatistics();

    private PeriodStatistics systemCpuIdle = new PeriodStatistics();

    private PeriodStatistics systemCpuUtilTotalPercent = new PeriodStatistics();

    private PeriodStatistics systemNetworkReceiveBytesPs = new PeriodStatistics();

    private PeriodStatistics systemNetworkSendBytesPs = new PeriodStatistics();

    private PeriodStatistics systemNetworkSendAndReceiveBytesPs = new PeriodStatistics();

    private PeriodStatistics systemNetWorkBandWidthUsedPercent = new PeriodStatistics();

    private PeriodStatistics systemCpuSystem = new PeriodStatistics();

    private PeriodStatistics systemCpuUser = new PeriodStatistics();

    private PeriodStatistics systemCpuSwitches = new PeriodStatistics();

    private PeriodStatistics systemCpuUsageIrq = new PeriodStatistics();

    private PeriodStatistics systemCpuUsageSoftIrq = new PeriodStatistics();

    private PeriodStatistics systemLoad1 = new PeriodStatistics();

    private PeriodStatistics systemLoad5 = new PeriodStatistics();

    private PeriodStatistics systemLoad15 = new PeriodStatistics();

    private PeriodStatistics systemCpuIOWait = new PeriodStatistics();

    private PeriodStatistics systemCpuGuest = new PeriodStatistics();

    private PeriodStatistics systemCpuSteal = new PeriodStatistics();

    /**************************** 指标计算服务对象 ****************************/

    private DiskIOMetricsService diskIOMetricsService;

    private DiskMetricsService diskMetricsService;

    private NetCardMetricsService netCardMetricsService;

    private static LinuxSystemMetricsServiceImpl instance;

    private Double preSystemCpuSwitches;

    public static synchronized LinuxSystemMetricsServiceImpl getInstance() {
        if(null == instance) {
            instance = new LinuxSystemMetricsServiceImpl();
        }
        return instance;
    }

    private LinuxSystemMetricsServiceImpl() {
        diskIOMetricsService = LinuxDiskIOMetricsServiceImpl.getInstance();
        diskMetricsService = LinuxDiskMetricsServiceImpl.getInstance();
        netCardMetricsService = LinuxNetCardMetricsServiceImpl.getInstance();
        try {
            lastLinuxNetFlowSend = new LinuxNetFlow();// 记录上次的收发字节数
            lastLinuxNetFlowReceive = new LinuxNetFlow();
            lastLinuxNetFlowSendReceive = new LinuxNetFlow();
            lastLinuxNetFlowSendReceiveUsedInNetWorkBandWidthUsedPercent = new LinuxNetFlow();
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsService||method=LinuxSystemMetricsServiceImpl()||msg=NetFlow init failed",
                    e);
        }
        try {
            lastLinuxCpuTimeSystemCpuUtil = new LinuxCpuTime(CPU_NUM);// 记录上次的cpu耗时
            lastLinuxCpuTimeSystemCpuUtilTotalPercent = new LinuxCpuTime(CPU_NUM);
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=LinuxSystemMetricsServiceImpl()||msg=CpuTime init failed",
                    e);
        }
        preSystemCpuSwitches = getSystemCpuSwitchesOnly();
    }

    @Override
    public String getOsType() {
        return ManagementFactory.getOperatingSystemMXBean().getName();
    }

    @Override
    public String getOsVersion() {
        return ManagementFactory.getOperatingSystemMXBean().getVersion();
    }

    @Override
    public String getOsKernelVersion() {
        List<String> osKernelVersion = getOutputByCmd(
                "uname -r", "linux内核版本", null);
        if (!osKernelVersion.isEmpty() && StringUtils.isNotBlank(osKernelVersion.get(0))) {
            return osKernelVersion.get(0);
        }else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl()||method=getOsKernelVersion()||msg=data is null");
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getHostName() {
        List<String> hostName = getOutputByCmd(
                "hostname", "主机名", null);
        if (!hostName.isEmpty() && StringUtils.isNotBlank(hostName.get(0))) {
            return hostName.get(0);
        }else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl()||method=getHostName()||msg=data is null");
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getIps() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostAddress();
        } catch (UnknownHostException ex) {
            LOGGER.error(
                    String.format("class=LinuxSystemMetricsServiceImpl()||method=getIps()||errMsg=%s", ex.getMessage()),
                    ex
            );
            return StringUtils.EMPTY;
        }
    }

    @Override
    public Long getSystemNtpOffset() {
        List<String> output = getOutputByCmd("ntpq -p",
                "系统时间偏移量", null);
        Double offset = Double.MIN_VALUE;
        if (!output.isEmpty() && output.size() > 2) {
            for(int i = 2; i < output.size(); i++) {
                String[] properties = output.get(i).split("\\s+");
                if(properties.length != 10) {
                    return 0L;
                } else {
                    offset = Math.max(offset, Double.valueOf(properties[8]));
                }
            }
        }
        if(offset.equals(Long.MIN_VALUE)) {
            return 0L;
        }
        return offset.longValue();
    }

    @Override
    public Long getSystemStartupTime() {
        return System.currentTimeMillis() - getSystemUptime();
    }

    @Override
    public Long getSystemUptime() {
        List<String> systemUpTime = getOutputByCmd(
                "awk '{print $1}' /proc/uptime", "系统运行时间", null);
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
        return getProcessesCountByProcessStatus("D");
    }

    private Integer getProcessesCountByProcessStatus(String status) {
        String command = String.format("ps -aux | awk '{print $8}' | grep %s | wc -l", status);
        List<String> lines = getOutputByCmd(
                command, String.format("系统进程状态[%s]", status), null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.valueOf(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getProcessesCountByProcessStatus||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcessesSleeping() {
        return getProcessesCountByProcessStatus("S");
    }

    @Override
    public Integer getProcessesZombies() {
        return getProcessesCountByProcessStatus("Z");
    }

    @Override
    public Integer getProcessesStopped() {
        return getProcessesCountByProcessStatus("T");
    }

    @Override
    public Integer getProcessesRunning() {
        return getProcessesCountByProcessStatus("R");
    }

    @Override
    public Integer getProcessesIdle() {
        return getProcessesCountByProcessStatus("I");
    }

    @Override
    public Integer getProcessesWait() {
        return getProcessesCountByProcessStatus("S");
    }

    @Override
    public Integer getProcessesDead() {
        return getProcessesCountByProcessStatus("X");
    }

    @Override
    public Integer getProcessesPaging() {
        return getProcessesCountByProcessStatus("W");
    }

    @Override
    public Integer getProcessesUnknown() {

        //TODO：

        return 0;

    }

    @Override
    public Integer getProcessesTotal() {
        List<String> processesTotal = getOutputByCmd("ps -ef | wc -l", "系统总进程数", null);
        if (!processesTotal.isEmpty() && StringUtils.isNotBlank(processesTotal.get(0))) {
            return Integer.parseInt(processesTotal.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getProcessesTotal||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getProcessesTotalThreads() {
        List<String> processesTotal = getOutputByCmd("ps -eLf | wc -l", "系统总线程数", null);
        if (!processesTotal.isEmpty() && StringUtils.isNotBlank(processesTotal.get(0))) {
            return Integer.parseInt(processesTotal.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getProcessesTotalThreads||msg=data is null");
            return 0;
        }
    }

    private Integer getThreadNumByPid(Integer pid) {
        List<String> threadNum = getOutputByCmd(
                String.format("ls /proc/%d/task | wc -l", pid), String.format("进程pid=%d线程数", pid), null);
        if (!threadNum.isEmpty() && threadNum.size() == 1) {
            return Integer.valueOf(threadNum.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl()||method=getThreadNumByPid()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemCpuCores() {
        return CPU_NUM;
    }

    private Double getSystemCpuUtilOnly() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(CPU_NUM);
            float cpuUsage = curLinuxCpuTime.getSystemUsage(lastLinuxCpuTimeSystemCpuUtil);
            lastLinuxCpuTimeSystemCpuUtil = curLinuxCpuTime;
            return MathUtil.divideWith2Digit(Float.valueOf(cpuUsage).doubleValue(), 1.0d);
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemCpuUtilOnly||msg=current system's cpu usage get failed",
                    e);
            return 0d;
        }
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuUtil() {
        systemCpuUtil.add(getSystemCpuUtilOnly());
    }

    @Override
    public PeriodStatistics getSystemCpuUtil() {
        if(systemCpuUtil.isEmpty()) {
            calcSystemCpuUtil();
        }
        return systemCpuUtil.snapshot();
    }

    private Double getSystemCpuUtilTotalPercentOnly() {
        try {
            LinuxCpuTime curLinuxCpuTime = new LinuxCpuTime(CPU_NUM);
            float cpuUsage = curLinuxCpuTime.getSystemUsage(lastLinuxCpuTimeSystemCpuUtilTotalPercent);
            lastLinuxCpuTimeSystemCpuUtilTotalPercent = curLinuxCpuTime;
            return MathUtil.divideWith2Digit(Float.valueOf(cpuUsage).doubleValue(), getSystemCpuCores());
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemCpuUtilOnly||msg=current system's cpu usage get failed",
                    e);
            return 0d;
        }
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuUtilTotalPercent() {
        systemCpuUtilTotalPercent.add(getSystemCpuUtilTotalPercentOnly());
    }

    @Override
    public PeriodStatistics getSystemCpuUtilTotalPercent() {
        if(systemCpuUtilTotalPercent.isEmpty()) {
            calcSystemCpuUtilTotalPercent();
        }
        return systemCpuUtilTotalPercent.snapshot();
    }

    @Override
    public PeriodStatistics getSystemCpuSystem() {
        if(systemCpuSystem.isEmpty()) {
            calcSystemCpuSystem();
        }
        return systemCpuSystem.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuSystem() {
        systemCpuSystem.add(getSystemCpuSystemOnly());
    }

    private Double getSystemCpuSystemOnly() {
        List<String> lines = getOutputByCmd("top -b -n 1", "内核态CPU时间占比", null);
        if (!lines.isEmpty() && lines.size() > 3 && StringUtils.isNotBlank(lines.get(2))) {
            String[] properties = lines.get(2).split("\\s+");
            if(properties.length >= 4) {
                return Double.valueOf(properties[3]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxSystemMetricsService()||method=getSystemCpuSystemOnly||msg=data is null, lines is:%s", JSON.toJSONString(lines))
                );
                return 0.0d;
            }
        } else {
            LOGGER.error(
                    String.format("class=LinuxSystemMetricsService()||method=getSystemCpuSystemOnly||msg=data is null, lines is:%s", JSON.toJSONString(lines))
            );
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getSystemCpuUser() {
        if(systemCpuUser.isEmpty()) {
            calcSystemCpuUser();
        }
        return systemCpuUser.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuUser() {
        systemCpuUser.add(getSystemCpuUserOnly());
    }

    private Double getSystemCpuUserOnly() {
        List<String> lines = getOutputByCmd("top -b -n 1", "用户态CPU时间占比", null);
        if (!lines.isEmpty() && lines.size() > 3 && StringUtils.isNotBlank(lines.get(2))) {
            String[] properties = lines.get(2).split("\\s+");
            if(properties.length >= 2) {
                return Double.valueOf(properties[1]);
            } else {
                LOGGER.error(
                        String.format(
                                "class=LinuxSystemMetricsService()||method=getSystemCpuUserOnly||msg=data is null,lines is:%s",
                                JSON.toJSONString(lines)
                        )
                );
                return 0.0d;
            }
        } else {
            LOGGER.error(
                    String.format(
                            "class=LinuxSystemMetricsService()||method=getSystemCpuUserOnly||msg=data is null,lines is:%s",
                            JSON.toJSONString(lines)
                    )
            );
            return 0.0d;
        }
    }

    private Double getSystemCpuIdleOnly() {
        List<String> lines = getOutputByCmd("top -b -n 1", "总体cpu空闲率", null);
        if (!lines.isEmpty() && lines.size() > 3 && StringUtils.isNotBlank(lines.get(2))) {
            String[] properties = lines.get(2).split("\\s+");
            if(properties.length >= 8) {
                return Double.valueOf(properties[7]);
            } else {
                LOGGER.error(
                        String.format("class=LinuxSystemMetricsService()||method=getSystemCpuIdleOnly||msg=data is null, lines is:%s", JSON.toJSONString(lines))
                );
                return 0.0d;
            }
        } else {
            LOGGER.error(
                    String.format("class=LinuxSystemMetricsService()||method=getSystemCpuIdleOnly||msg=data is null, lines is:%s", JSON.toJSONString(lines))
            );
            return 0.0d;
        }
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuIdle() {
        systemCpuIdle.add(getSystemCpuIdleOnly());
    }

    @Override
    public PeriodStatistics getSystemCpuIdle() {
        if(systemCpuIdle.isEmpty()) {
            calcSystemCpuIdle();
        }
        return systemCpuIdle.snapshot();
    }

    @Override
    public PeriodStatistics getSystemCpuSwitches() {
        if(systemCpuSwitches.isEmpty()) {
            calcSystemCpuSwitches();
        }
        return systemCpuSwitches.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuSwitches() {
        Double currentSystemCpuSwitches = getSystemCpuSwitchesOnly();
        systemCpuSwitches.add(currentSystemCpuSwitches - preSystemCpuSwitches);
        preSystemCpuSwitches = currentSystemCpuSwitches;
    }

    private Double getSystemCpuSwitchesOnly() {
        List<String> output = getOutputByCmd("cat /proc/stat | grep 'ctxt' | awk '{print $2}'",
                "cpu上下文交换次数", null);
        if (!output.isEmpty() && StringUtils.isNotBlank(output.get(0))) {
            return Double.parseDouble(output.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuSwitchesOnly||msg=data is null");
        }
        return 0d;
    }

    @Override
    public PeriodStatistics getSystemCpuUsageIrq() {
        if(systemCpuUsageIrq.isEmpty()) {
            calcSystemCpuUsageIrq();
        }
        return systemCpuUsageIrq.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuUsageIrq() {
        systemCpuUsageIrq.add(getSystemCpuUsageIrqOnly());
    }

    private Double getSystemCpuUsageIrqOnly() {
        List<String> output = getOutputByCmd("top -bn 1  -i -c", "cpu状态信息", null);
        if (!output.isEmpty() && output.size() >= 3) {
            String[] properties = output.get(2).split("\\s+");//
            Double irq = Double.valueOf(properties[11]);
            return irq;
        } else {
            LOGGER.error(
                    "class=LinuxSystemMetricsService()||method=getSystemCpuUsageIrqOnly||msg=data is null");
        }
        return 0d;
    }

    @Override
    public PeriodStatistics getSystemCpuUsageSoftIrq() {
        if(systemCpuUsageSoftIrq.isEmpty()) {
            calcSystemCpuUsageSoftIrq();
        }
        return systemCpuUsageSoftIrq.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuUsageSoftIrq() {
        systemCpuUsageSoftIrq.add(getSystemCpuUsageSoftIrqOnly());
    }

    private Double getSystemCpuUsageSoftIrqOnly() {
        List<String> output = getOutputByCmd("top -bn 1  -i -c", "cpu状态信息", null);
        if (!output.isEmpty() && output.size() >= 3) {
            String[] properties = output.get(2).split("\\s+");//
            Double softIrq = Double.valueOf(properties[13]);
            return softIrq;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuUsageSoftIrqOnly||msg=data is null");
        }
        return 0d;
    }


    @Override
    public PeriodStatistics getSystemLoad1() {
        if(systemLoad1.isEmpty()) {
            calcSystemLoad1();
        }
        return systemLoad1.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemLoad1() {
        systemLoad1.add(getSystemLoad1Only());
    }

    private Double getSystemLoad1Only() {
        List<String> lines = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $4}'", "系统近1分钟平均负载", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            Double systemLoad1 = Double.parseDouble(lines.get(0));
            return MathUtil.divideWith2Digit(systemLoad1, getSystemCpuCores());
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemLoad1Only||msg=获取系统近1分钟平均负载失败");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getSystemLoad5() {
        if(systemLoad5.isEmpty()) {
            calcSystemLoad5();
        }
        return systemLoad5.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemLoad5() {
        systemLoad5.add(getSystemLoad5Only());
    }

    private Double getSystemLoad5Only() {
        List<String> lines = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $5}'", "系统近5分钟平均负载", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            Double systemLoad5 = Double.parseDouble(lines.get(0));
            return MathUtil.divideWith2Digit(systemLoad5, getSystemCpuCores());
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemLoad5Only||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getSystemLoad15() {
        if(systemLoad15.isEmpty()) {
            calcSystemLoad15();
        }
        return systemLoad15.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemLoad15() {
        systemLoad15.add(getSystemLoad15Only());
    }

    private Double getSystemLoad15Only() {
        List<String> lines = getOutputByCmd("sar -q 1 1 | grep ':' | awk '{print $6}'", "系统近15分钟平均负载", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            Double systemLoad15 = Double.parseDouble(lines.get(0));
            return MathUtil.divideWith2Digit(systemLoad15, getSystemCpuCores());
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemLoad15Only||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getSystemCpuIOWait() {
        if(systemCpuIOWait.isEmpty()) {
            calcSystemCpuIOWait();
        }
        return systemCpuIOWait.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuIOWait() {
        systemCpuIOWait.add(getSystemCpuIOWaitOnly());
    }
    private Double getSystemCpuIOWaitOnly() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $6}'", "等待I/O的CPU时间占比", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuIOWaitOnly||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getSystemCpuGuest() {
        if(systemCpuGuest.isEmpty()) {
            calcSystemCpuGuest();
        }
        return systemCpuGuest.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuGuest() {
        systemCpuGuest.add(getSystemCpuGuestOnly());
    }

    private Double getSystemCpuGuestOnly() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $10}'", "虚拟处理器CPU时间占比", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuGuestOnly||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public PeriodStatistics getSystemCpuSteal() {
        if(systemCpuSteal.isEmpty()) {
            calcSystemCpuSteal();
        }
        return systemCpuSteal.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemCpuSteal() {
        systemCpuSteal.add(getSystemCpuStealOnly());
    }

    private Double getSystemCpuStealOnly() {
        List<String> lines = getOutputByCmd("mpstat | awk 'NR==4{print $9}'", "等待处理其他虚拟核的时间占比", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Double.parseDouble(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemCpuStealOnly||msg=data is null");
            return 0.0d;
        }
    }

    @Override
    public Long getSystemMemCommitLimit() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'CommitLimit:' | awk '{print $2}'", "系统当前可分配的内存总量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemCommitLimit()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemCommittedAs() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Committed_AS:' | awk '{print $2}'", "系统已分配的包括进程未使用的内存量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemCommittedAs()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemCommitted() {
        return getSystemMemCommittedAs() - getSystemMemCommitLimit();
    }

    @Override
    public Long getSystemMemNonPaged() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'KernelStack:' | awk '{print $2}'", "写入磁盘的物理内存量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemNonPaged()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemPaged() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Writeback:' | awk '{print $2}'", "没被使用是可以写入磁盘的物理内存量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemPaged()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemShared() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Shmem:' | awk '{print $2}'", "用作共享内存的物理RAM量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemShared()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemSlab() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Slab:' | awk '{print $2}'", "内核用来缓存数据结构供自己使用的内存量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemSlab()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemTotal() {
        return super.getSystemMemTotal();
    }

    @Override
    public Long getSystemMemFree() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'MemFree:' | awk '{print $2}'", "系系统空闲内存大小", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemMemFree()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemUsed() {
        return getSystemMemTotal() - getSystemMemFree();
    }

    @Override
    public Long getSystemMemBuffered() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Buffers:' | awk '{print $2}'", "系统文件缓冲区的物理RAM量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemBuffered()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemMemCached() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'Cached:' | awk '{print $2}'", "缓存内存的物理RAM量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemMemCached()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Double getSystemMemFreePercent() {
        long memTotal = getSystemMemTotal();
        if (memTotal == 0) {
            LOGGER.warn("SystemMemoryTotal is zero");
            return 0.0d;
        }
        return MathUtil.divideWith2Digit(getSystemMemFree() * 100, getSystemMemTotal());
    }

    @Override
    public Double getSystemMemUsedPercent() {
        long memTotal = getSystemMemTotal();
        if (memTotal == 0) {
            LOGGER.warn("SystemMemoryTotal is zero");
            return 0.0d;
        }
        return MathUtil.divideWith2Digit(getSystemMemUsed() * 100, getSystemMemTotal());
    }

    @Override
    public Long getSystemSwapCached() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'SwapCached:' | awk '{print $2}'", "系统用作缓存的交换空间", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemSwapCached()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemSwapFree() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'SwapFree:' | awk '{print $2}'", "系统空闲swap大小", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemSwapFree()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Double getSystemSwapFreePercent() {
        long swapTotal = getSystemSwapTotal();
        if (swapTotal == 0) {
            LOGGER.warn("SystemSwapMemoryTotal is zero");
            return 0.0d;
        }
        return MathUtil.divideWith2Digit(getSystemSwapFree() * 100, swapTotal);
    }

    @Override
    public Long getSystemSwapTotal() {
        List<String> lines = getOutputByCmd("cat /proc/meminfo | grep 'SwapTotal:' | awk '{print $2}'", "系统swap总大小", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0)) * 1024l;
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemSwapTotal()||msg=data is null");
            return 0L;
        }
    }

    @Override
    public Long getSystemSwapUsed() {
        return getSystemSwapTotal() - getSystemSwapFree();
    }

    @Override
    public Double getSystemSwapUsedPercent() {
        long swapTotal = getSystemSwapTotal();
        if (swapTotal == 0) {
            LOGGER.warn("SystemSwapMemoryTotal is zero");
            return 0.0d;
        }
        return MathUtil.divideWith2Digit(getSystemSwapUsed() * 100, swapTotal);
    }

    @Override
    public Integer getSystemDisks() {
        return diskMetricsService.getFsType().size();
    }

    @Override
    public List<DiskInfo> getSystemDiskInfoList() {
        Map<String, String> path2FsTypeMap = diskMetricsService.getFsType();
        List<DiskInfo> diskInfoList = new ArrayList<>(path2FsTypeMap.size());
        for (Map.Entry<String, String> path2FsTypeEntry : path2FsTypeMap.entrySet()) {
            String path = path2FsTypeEntry.getKey();
            String fsType = path2FsTypeEntry.getValue();
            DiskInfo diskInfo = new DiskInfo();
            diskInfo.setFsType(fsType);
            diskInfo.setPath(path);
            diskInfo.setBytesFree(
                    getMetricValueByKey(diskMetricsService.getBytesFree(), path, "diskBytesFree", 0L)
            );
            diskInfo.setBytesTotal(
                    getMetricValueByKey(diskMetricsService.getBytesTotal(), path, "diskBytesTotal", 0L)
            );
            diskInfo.setBytesUsed(
                    getMetricValueByKey(diskMetricsService.getBytesUsed(), path, "diskBytesUsed", 0L)
            );
            diskInfo.setBytesUsedPercent(
                    getMetricValueByKey(diskMetricsService.getBytesUsedPercent(), path, "diskBytesUsedPercent", 0d)
            );
            diskInfo.setInodesTotal(
                    getMetricValueByKey(diskMetricsService.getInodesTotal(), path, "diskInodesTotal", 0)
            );
            diskInfo.setInodesFree(
                    getMetricValueByKey(diskMetricsService.getInodesFree(), path, "diskInodesFree", 0)
            );
            diskInfo.setInodesUsed(
                    getMetricValueByKey(diskMetricsService.getInodesUsed(), path, "diskInodesUsed", 0)
            );
            diskInfo.setInodesUsedPercent(
                    getMetricValueByKey(diskMetricsService.getInodesUsedPercent(), path, "diskInodesUsedPercent", 0d)
            );
            diskInfoList.add(diskInfo);
        }
        return diskInfoList;
    }

    @Override
    public List<DiskIOInfo> getSystemDiskIOInfoList() {
        Map<String, PeriodStatistics> device2IOUtilMap = diskIOMetricsService.getIOUtil();
        Map<String, PeriodStatistics> device2AvgQuSzMap = diskIOMetricsService.getAvgQuSz();
        Map<String, PeriodStatistics> device2AvgRqSzMap = diskIOMetricsService.getAvgRqSz();
        Map<String, PeriodStatistics> device2IOAwaitMap = diskIOMetricsService.getIOAwait();
        Map<String, PeriodStatistics> device2IORAwaitMap = diskIOMetricsService.getIORAwait();
        Map<String, PeriodStatistics> device2IOReadRequestMap = diskIOMetricsService.getIOReadRequest();
        Map<String, PeriodStatistics> device2IOReadBytesMap = diskIOMetricsService.getIOReadBytes();
        Map<String, PeriodStatistics> device2IORRQMSMap = diskIOMetricsService.getIORRQMS();
        Map<String, PeriodStatistics> device2IOSVCTMMap = diskIOMetricsService.getIOSVCTM();
        Map<String, PeriodStatistics> device2IOWAwaitMap = diskIOMetricsService.getIOWAwait();
        Map<String, PeriodStatistics> device2IOWriteRequestMap = diskIOMetricsService.getIOWriteRequest();
        Map<String, PeriodStatistics> device2IOWriteBytesMap = diskIOMetricsService.getIOWriteBytes();
        Map<String, PeriodStatistics> device2IOReadWriteBytesMap = diskIOMetricsService.getIOReadWriteBytes();
        Map<String, PeriodStatistics> device2IOWRQMSMap = diskIOMetricsService.getIOWRQMS();
        Map<String, PeriodStatistics> device2DiskReadTimeMap = diskIOMetricsService.getDiskReadTime();
        Map<String, PeriodStatistics> device2DiskReadTimePercentMap = diskIOMetricsService.getDiskReadTimePercent();
        Map<String, PeriodStatistics> device2DiskWriteTimeMap = diskIOMetricsService.getDiskWriteTime();
        Map<String, PeriodStatistics> device2DiskWriteTimePercentMap = diskIOMetricsService.getDiskWriteTimePercent();
        List<DiskIOInfo> diskIOInfoList = new ArrayList<>(device2IOUtilMap.size());
        for (Map.Entry<String, PeriodStatistics> device2IOUtilEntry : device2IOUtilMap.entrySet()) {
            String device = device2IOUtilEntry.getKey();
            PeriodStatistics iOUtil = device2IOUtilEntry.getValue();
            DiskIOInfo diskIOInfo = new DiskIOInfo();
            diskIOInfo.setDevice(device);
            diskIOInfo.setiOAvgQuSz(
                    getMetricValueByKey(device2AvgQuSzMap, device, "iOAvgQuSz", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOAvgRqSz(
                    getMetricValueByKey(device2AvgRqSzMap, device, "iOAvgRqSz", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOAwait(
                    getMetricValueByKey(device2IOAwaitMap, device, "iOAwait", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiORAwait(
                    getMetricValueByKey(device2IORAwaitMap, device, "iORAwait", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOReadRequest(
                    getMetricValueByKey(device2IOReadRequestMap, device, "iOReadRequest", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOReadBytes(
                    getMetricValueByKey(device2IOReadBytesMap, device, "iOReadBytes", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiORRQMS(
                    getMetricValueByKey(device2IORRQMSMap, device, "iORRQMS", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOSVCTM(
                    getMetricValueByKey(device2IOSVCTMMap, device, "iOSVCTM", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOUtil(iOUtil);
            diskIOInfo.setiOWAwait(
                    getMetricValueByKey(device2IOWAwaitMap, device, "iOWAwait", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOWriteRequest(
                    getMetricValueByKey(device2IOWriteRequestMap, device, "iOWriteRequest", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOWriteBytes(
                    getMetricValueByKey(device2IOWriteBytesMap, device, "iOWriteBytes", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOReadWriteBytes(
                    getMetricValueByKey(device2IOReadWriteBytesMap, device, "iOReadWriteBytes", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setiOWRQMS(
                    getMetricValueByKey(device2IOWRQMSMap, device, "iOWRQMS", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setReadTime(
                    getMetricValueByKey(device2DiskReadTimeMap, device, "readTime", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setReadTimePercent(
                    getMetricValueByKey(device2DiskReadTimePercentMap, device, "readTimePercent", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setWriteTime(
                    getMetricValueByKey(device2DiskWriteTimeMap, device, "writeTime", PeriodStatistics.defaultValue())
            );
            diskIOInfo.setWriteTimePercent(
                    getMetricValueByKey(device2DiskWriteTimePercentMap, device, "writeTimePercent", PeriodStatistics.defaultValue())
            );
            diskIOInfoList.add(diskIOInfo);
        }
        return diskIOInfoList;
    }

    @Override
    public Integer getSystemFilesMax() {
        List<String> lines = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $3}'", "系统可以打开的最大文件句柄数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemFilesMax()||msg=data is null");
        }
        return 0;
    }

    @Override
    public Integer getSystemFilesAllocated() {
        return getSystemFilesUsed() + getSystemFilesNotUsed();
    }

    @Override
    public Integer getSystemFilesLeft() {
        return getSystemFilesMax() - getSystemFilesAllocated();
    }

    @Override
    public Double getSystemFilesUsedPercent() {
        return MathUtil.divideWith2Digit(getSystemFilesUsed() * 100, getSystemFilesMax());
    }

    @Override
    public Integer getSystemFilesUsed() {
        List<String> lines = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $1}'", "系统使用的已分配文件句柄数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemFilesUsed()||msg=data is null");
        }
        return 0;
    }

    @Override
    public Integer getSystemFilesNotUsed() {
        List<String> lines = getOutputByCmd("cat /proc/sys/fs/file-nr | awk '{print $2}'", "系统未使用的已分配文件句柄数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemFilesNotUsed()||msg=获取系统未使用的已分配文件句柄数失败");
        }
        return 0;
    }

    @Override
    public Integer getSystemNetCards() {
        return netCardMetricsService.getMacAddress().size();
    }

    @Override
    public Double getSystemNetCardsBandWidth() {
        Map<String, Long> map = netCardMetricsService.getBandWidth();
        if(MapUtils.isNotEmpty(map)) {
            Double bandWidthSum = 0d;
            for (Long bandWidth : map.values()) {
                bandWidthSum += bandWidth;
            }
            return bandWidthSum;
        } else {
            return 0d;
        }
    }

    @Override
    public List<NetCardInfo> getSystemNetCardInfoList() {
        Map<String, PeriodStatistics> device2SendBytesPsMap = netCardMetricsService.getSendBytesPs();
        Map<String, String> device2MacAddressMap = netCardMetricsService.getMacAddress();
        Map<String, Long> device2BandWidthMap = netCardMetricsService.getBandWidth();
        Map<String, PeriodStatistics> device2ReceiveBytesPsMap = netCardMetricsService.getReceiveBytesPs();
        List<NetCardInfo> netCardInfoList = new ArrayList<>(device2MacAddressMap.size());
        for (Map.Entry<String, String> device2MacAddressEntry : device2MacAddressMap.entrySet()) {
            String device = device2MacAddressEntry.getKey();
            String macAddress = device2MacAddressEntry.getValue();
            Long bandWidth = getMetricValueByKey(device2BandWidthMap, device, "bandWidth", 0L);
            PeriodStatistics sendBytesPs = getMetricValueByKey(device2SendBytesPsMap, device, "sendBytesPs", PeriodStatistics.defaultValue());
            PeriodStatistics receiveBytesPs = getMetricValueByKey(device2ReceiveBytesPsMap, device, "receiveBytesPs", PeriodStatistics.defaultValue());
            NetCardInfo netCardInfo = new NetCardInfo();
            netCardInfo.setSystemNetCardsBandMacAddress(macAddress);
            netCardInfo.setSystemNetCardsBandDevice(device);
            netCardInfo.setSystemNetCardsBandWidth(bandWidth);
            netCardInfo.setSystemNetCardsReceiveBytesPs(receiveBytesPs);
            netCardInfo.setSystemNetCardsSendBytesPs(sendBytesPs);
            netCardInfoList.add(netCardInfo);
        }
        return netCardInfoList;
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemNetworkReceiveBytesPs() {
        systemNetworkReceiveBytesPs.add(getSystemNetworkReceiveBytesPsOnly());
    }

    private Double getSystemNetworkReceiveBytesPsOnly() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double processReceiveBytesPs = curLinuxNetFlow.getSystemReceiveBytesPs(lastLinuxNetFlowReceive);
            lastLinuxNetFlowReceive = curLinuxNetFlow;
            return MathUtil.divideWith2Digit(processReceiveBytesPs, 1.0);
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemNetworkReceiveBytesPsOnly()||msg=获取系统网络每秒下行流量失败",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getSystemNetworkReceiveBytesPs() {
        if(systemNetworkReceiveBytesPs.isEmpty()) {
            calcSystemNetworkReceiveBytesPs();
        }
        return systemNetworkReceiveBytesPs.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemNetworkSendBytesPs() {
        systemNetworkSendBytesPs.add(getSystemNetworkSendBytesPsOnly());
    }

    private Double getSystemNetworkSendBytesPsOnly() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double processTransmitBytesPs = curLinuxNetFlow.getSystemTransmitBytesPs(lastLinuxNetFlowSend);
            lastLinuxNetFlowSend = curLinuxNetFlow;
            return MathUtil.divideWith2Digit(processTransmitBytesPs, 1.0);
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemNetworkSendBytesPsOnly()||msg=获取系统网络每秒上行流量失败",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getSystemNetworkSendBytesPs() {
        if(systemNetworkSendBytesPs.isEmpty()) {
            calcSystemNetworkSendBytesPs();
        }
        return systemNetworkSendBytesPs.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemNetworkSendAndReceiveBytesPs() {
        systemNetworkSendAndReceiveBytesPs.add(getSystemNetworkSendAndReceiveBytesPsOnly());
    }

    private Double getSystemNetworkSendAndReceiveBytesPsOnly() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double systemSendReceiveBytesPs = curLinuxNetFlow.getSystemSendReceiveBytesPs(lastLinuxNetFlowSendReceive);
            lastLinuxNetFlowSendReceive = curLinuxNetFlow;
            return MathUtil.divideWith2Digit(systemSendReceiveBytesPs, 1.0);
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemNetworkSendAndReceiveBytesPsOnly()||msg=获取系统网络每秒下行流量失败",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getSystemNetworkSendAndReceiveBytesPs() {
        if(systemNetworkSendAndReceiveBytesPs.isEmpty()) {
            calcSystemNetworkSendAndReceiveBytesPs();
        }
        return systemNetworkSendAndReceiveBytesPs.snapshot();
    }

    @PeriodMethod(periodMs = 5 * 1000)
    private void calcSystemNetWorkBandWidthUsedPercent() {
        Double bandWidthUsedPercent = getSystemNetWorkBandWidthUsedPercentOnly();
        systemNetWorkBandWidthUsedPercent.add(bandWidthUsedPercent);
    }

    private Double getSystemNetWorkBandWidthUsedPercentOnly() {
        /*
         * 1.）获取系统全部网卡对应带宽之和
         */
        Double systemNetWorkBand = getSystemNetCardsBandWidth();
        /*
         * 2.）获取系统当前上、下行总流量
         */
        if(0d < systemNetWorkBand) {
            Double systemNetworkSendAndReceiveBytesPs = getSystemNetworkSendAndReceiveBytesPsOnlyUsedInNetWorkBandWidthUsedPercent();
            return MathUtil.divideWith2Digit(systemNetworkSendAndReceiveBytesPs * 100, systemNetWorkBand);
        } else {
            return 0d;
        }
    }

    private Double getSystemNetworkSendAndReceiveBytesPsOnlyUsedInNetWorkBandWidthUsedPercent() {
        try {
            LinuxNetFlow curLinuxNetFlow = new LinuxNetFlow();
            double systemSendReceiveBytesPs = curLinuxNetFlow.getSystemSendReceiveBytesPs(lastLinuxNetFlowSendReceiveUsedInNetWorkBandWidthUsedPercent);
            lastLinuxNetFlowSendReceiveUsedInNetWorkBandWidthUsedPercent = curLinuxNetFlow;
            return MathUtil.divideWith2Digit(systemSendReceiveBytesPs, 1.0);
        } catch (Exception e) {
            LOGGER.error("class=LinuxSystemMetricsServiceImpl||method=getSystemNetworkSendAndReceiveBytesPsOnlyUsedInNetWorkBandWidthUsedPercent()||msg=获取系统网络每秒下行流量失败",
                    e);
            return 0d;
        }
    }

    @Override
    public PeriodStatistics getSystemNetWorkBandWidthUsedPercent() {
        if(systemNetWorkBandWidthUsedPercent.isEmpty()) {
            calcSystemNetWorkBandWidthUsedPercent();
        }
        return systemNetWorkBandWidthUsedPercent.snapshot();
    }

    @Override
    public Integer getSystemNetworkTcpConnectionNum() {
        List<String> lines = getOutputByCmd("netstat -an | grep -c 'tcp'", "系统tcp连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpConnectionNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpListeningNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'LISTEN'", "系统处于 LISTEN 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpListeningNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpEstablishedNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'ESTABLISHED'", "系统处于 Established 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpEstablishedNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpSynSentNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'SYN_SENT'", "系统处于 SYN_SENT 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpSynSentNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpSynRecvNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'SYN_RCVD'", "系统处于 SYN_RCVD 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpSynRecvNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpFinWait1Num() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'FIN_WAIT1'", "系统处于 FIN_WAIT1 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpFinWait1Num()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpFinWait2Num() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'FIN_WAIT2'", "系统处于 FIN_WAIT2 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpFinWait2Num()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpTimeWaitNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'TIME_WAIT'", "系统处于 time wait 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpTimeWaitNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpClosedNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'CLOSED'", "系统处于 CLOSED 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpClosedNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpCloseWaitNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'CLOSE_WAIT'", "系统处于 close wait 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpCloseWaitNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpClosingNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'CLOSING'", "系统处于 CLOSING 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpClosingNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpLastAckNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'LAST_ACK'", "系统处于 LAST_ACK 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpLastAckNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Integer getSystemNetworkTcpNoneNum() {
        List<String> lines = getOutputByCmd("netstat -an | awk '/^tcp/' | grep -c 'NONE'", "系统处于 NONE 状态 tcp 连接数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Integer.parseInt(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpNoneNum()||msg=data is null");
            return 0;
        }
    }

    @Override
    public Long getSystemNetworkTcpActiveOpens() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $6}'", "系统启动以来 Tcp 主动连接次数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpActiveOpens()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkTcpPassiveOpens() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $7}'", "系统启动以来 Tcp 被动连接次数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpPassiveOpens()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkTcpAttemptFails() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $8}'", "系统启动以来 Tcp 连接失败次数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpAttemptFails()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkTcpEstabResets() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $9}'", "系统启动以来 Tcp 连接异常断开次数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpEstabResets()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkTcpRetransSegs() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Tcp:' | awk 'NR==2{print $13}'", "系统启动以来 Tcp 重传的报文段总个数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkTcpRetransSegs()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkTcpExtListenOverflows() {
        List<String> lines = getOutputByCmd(
                "netstat -s | egrep \"listen|LISTEN\" | awk '{a+=$1}{print a}'",
                "系统启动以来 Tcp 监听队列溢出次数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        }
        return 0l;
    }

    @Override
    public Long getSystemNetworkUdpInDatagrams() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $2}'", "系统启动以来 UDP 入包量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpInDatagrams()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkUdpOutDatagrams() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $5}'", "系统启动以来 UDP 出包量", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpOutDatagrams()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkUdpInErrors() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $4}'", "系统启动以来 UDP 入包错误数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpInErrors()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkUdpNoPorts() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $3}'", "系统启动以来 UDP 端口不可达个数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpNoPorts()||msg=data is null");
            return 0l;
        }
    }

    @Override
    public Long getSystemNetworkUdpSendBufferErrors() {
        List<String> lines = getOutputByCmd("cat /proc/net/snmp | grep 'Udp:' | awk 'NR==2{print $7}'", "系统启动以来 UDP 发送缓冲区满次数", null);
        if (!lines.isEmpty() && StringUtils.isNotBlank(lines.get(0))) {
            return Long.parseLong(lines.get(0));
        } else {
            LOGGER.error("class=LinuxSystemMetricsService()||method=getSystemNetworkUdpSendBufferErrors()||msg=data is null");
            return 0l;
        }
    }

}
