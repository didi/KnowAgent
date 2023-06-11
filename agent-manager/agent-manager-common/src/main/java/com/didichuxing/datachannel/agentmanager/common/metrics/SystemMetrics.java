package com.didichuxing.datachannel.agentmanager.common.metrics;

public class SystemMetrics {

    private String ostype;

    private String osversion;

    private String oskernelversion;

    private String hostname;

    private String ips;

    private Long systemntpoffset;

    private Long systemstartuptime;

    private Long systemuptime;

    private Integer processesblocked;

    private Integer processessleeping;

    private Integer processeszombies;

    private Integer processesstopped;

    private Integer processesrunning;

    private Integer processesidle;

    private Integer processeswait;

    private Integer processesdead;

    private Integer processespaging;

    private Integer processesunknown;

    private Integer processestotal;

    private Integer processestotalthreads;

    private Integer cpucores;

    private Double systemcpuutil;

    private Double systemcpuutilmin;

    private Double systemcpuutilmax;

    private Double systemcpuutilmean;

    private Double systemcpuutilstd;

    private Double systemcpuutil55quantile;

    private Double systemcpuutil75quantile;

    private Double systemcpuutil95quantile;

    private Double systemcpuutil99quantile;

    private Double systemcpuutiltotalpercent;

    private Double systemcpuutiltotalpercentmin;

    private Double systemcpuutiltotalpercentmax;

    private Double systemcpuutiltotalpercentmean;

    private Double systemcpuutiltotalpercentstd;

    private Double systemcpuutiltotalpercent55quantile;

    private Double systemcpuutiltotalpercent75quantile;

    private Double systemcpuutiltotalpercent95quantile;

    private Double systemcpuutiltotalpercent99quantile;

    private Double systemcpusystem;

    private Double systemcpusystemmin;

    private Double systemcpusystemmax;

    private Double systemcpusystemmean;

    private Double systemcpusystemstd;

    private Double systemcpusystem55quantile;

    private Double systemcpusystem75quantile;

    private Double systemcpusystem95quantile;

    private Double systemcpusystem99quantile;

    private Double systemcpuuser;

    private Double systemcpuusermin;

    private Double systemcpuusermax;

    private Double systemcpuusermean;

    private Double systemcpuuserstd;

    private Double systemcpuuser55quantile;

    private Double systemcpuuser75quantile;

    private Double systemcpuuser95quantile;

    private Double systemcpuuser99quantile;

    private Double systemcpuidle;

    private Double systemcpuidlemin;

    private Double systemcpuidlemax;

    private Double systemcpuidlemean;

    private Double systemcpuidlestd;

    private Double systemcpuidle55quantile;

    private Double systemcpuidle75quantile;

    private Double systemcpuidle95quantile;

    private Double systemcpuidle99quantile;

    private Double systemcpuswitches;

    private Double systemcpuswitchesmin;

    private Double systemcpuswitchesmax;

    private Double systemcpuswitchesmean;

    private Double systemcpuswitchesstd;

    private Double systemcpuswitches55quantile;

    private Double systemcpuswitches75quantile;

    private Double systemcpuswitches95quantile;

    private Double systemcpuswitches99quantile;

    private Double systemcpuusageirq;

    private Double systemcpuusageirqmin;

    private Double systemcpuusageirqmax;

    private Double systemcpuusageirqmean;

    private Double systemcpuusageirqstd;

    private Double systemcpuusageirq55quantile;

    private Double systemcpuusageirq75quantile;

    private Double systemcpuusageirq95quantile;

    private Double systemcpuusageirq99quantile;

    private Double systemcpuusagesoftirq;

    private Double systemcpuusagesoftirqmin;

    private Double systemcpuusagesoftirqmax;

    private Double systemcpuusagesoftirqmean;

    private Double systemcpuusagesoftirqstd;

    private Double systemcpuusagesoftirq55quantile;

    private Double systemcpuusagesoftirq75quantile;

    private Double systemcpuusagesoftirq95quantile;

    private Double systemcpuusagesoftirq99quantile;

    private Double systemload1;

    private Double systemload1min;

    private Double systemload1max;

    private Double systemload1mean;

    private Double systemload1std;

    private Double systemload155quantile;

    private Double systemload175quantile;

    private Double systemload195quantile;

    private Double systemload199quantile;

    private Double systemload5;

    private Double systemload5min;

    private Double systemload5max;

    private Double systemload5mean;

    private Double systemload5std;

    private Double systemload555quantile;

    private Double systemload575quantile;

    private Double systemload595quantile;

    private Double systemload599quantile;

    private Double systemload15;

    private Double systemload15min;

    private Double systemload15max;

    private Double systemload15mean;

    private Double systemload15std;

    private Double systemload1555quantile;

    private Double systemload1575quantile;

    private Double systemload1595quantile;

    private Double systemload1599quantile;

    private Double systemcpuiowait;

    private Double systemcpuiowaitmin;

    private Double systemcpuiowaitmax;

    private Double systemcpuiowaitmean;

    private Double systemcpuiowaitstd;

    private Double systemcpuiowait55quantile;

    private Double systemcpuiowait75quantile;

    private Double systemcpuiowait95quantile;

    private Double systemcpuiowait99quantile;

    private Double systemcpuguest;

    private Double systemcpuguestmin;

    private Double systemcpuguestmax;

    private Double systemcpuguestmean;

    private Double systemcpugueststd;

    private Double systemcpuguest55quantile;

    private Double systemcpuguest75quantile;

    private Double systemcpuguest95quantile;

    private Double systemcpuguest99quantile;

    private Double systemcpusteal;

    private Double systemcpustealmin;

    private Double systemcpustealmax;

    private Double systemcpustealmean;

    private Double systemcpustealstd;

    private Double systemcpusteal55quantile;

    private Double systemcpusteal75quantile;

    private Double systemcpusteal95quantile;

    private Double systemcpusteal99quantile;

    private Long systemmemcommitlimit;

    private Long systemmemcommittedas;

    private Long systemmemcommitted;

    private Long systemmemnonpaged;

    private Long systemmempaged;

    private Long systemmemshared;

    private Long systemmemslab;

    private Long systemmemtotal;

    private Long systemmemfree;

    private Long systemmemused;

    private Long systemmembuffered;

    private Long systemmemcached;

    private Double systemmemfreepercent;

    private Double systemmemusedpercent;

    private Long systemswapcached;

    private Long systemswapfree;

    private Double systemswapfreepercent;

    private Long systemswaptotal;

    private Long systemswapused;

    private Double systemswapusedpercent;

    private Integer systemdisks;

    private Integer systemfilesmax;

    private Integer systemfilesallocated;

    private Integer systemfilesleft;

    private Double systemfilesusedpercent;

    private Integer systemfilesused;

    private Integer systemfilesnotused;

    private Integer systemnetcards;

    private Double systemnetworkreceivebytesps;

    private Double systemnetworkreceivebytespsmin;

    private Double systemnetworkreceivebytespsmax;

    private Double systemnetworkreceivebytespsmean;

    private Double systemnetworkreceivebytespsstd;

    private Double systemnetworkreceivebytesps55quantile;

    private Double systemnetworkreceivebytesps75quantile;

    private Double systemnetworkreceivebytesps95quantile;

    private Double systemnetworkreceivebytesps99quantile;

    private Double systemnetworksendbytesps;

    private Double systemnetworksendbytespsmin;

    private Double systemnetworksendbytespsmax;

    private Double systemnetworksendbytespsmean;

    private Double systemnetworksendbytespsstd;

    private Double systemnetworksendbytesps55quantile;

    private Double systemnetworksendbytesps75quantile;

    private Double systemnetworksendbytesps95quantile;

    private Double systemnetworksendbytesps99quantile;

    private Integer systemnetworktcpconnectionnum;

    private Integer systemnetworktcplisteningnum;

    private Integer systemnetworktcpestablishednum;

    private Integer systemnetworktcpsynsentnum;

    private Integer systemnetworktcpsynrecvnum;

    private Integer systemnetworktcpfinwait1num;

    private Integer systemnetworktcpfinwait2num;

    private Integer systemnetworktcptimewaitnum;

    private Integer systemnetworktcpclosednum;

    private Integer systemnetworktcpclosewaitnum;

    private Integer systemnetworktcpclosingnum;

    private Integer systemnetworktcplastacknum;

    private Integer systemnetworktcpnonenum;

    private Long systemnetworktcpactiveopens;

    private Long systemnetworktcppassiveopens;

    private Long systemnetworktcpattemptfails;

    private Long systemnetworktcpestabresets;

    private Long systemnetworktcpretranssegs;

    private Long systemnetworktcpextlistenoverflows;

    private Long systemnetworkudpindatagrams;

    private Long systemnetworkudpoutdatagrams;

    private Long systemnetworkudpinerrors;

    private Long systemnetworkudpnoports;

    private Long systemnetworkudpsendbuffererrors;

    private Long heartbeattime;

    private Long heartbeattimeminute;

    private Long heartbeattimehour;

    private Long heartbeatTimeDay;

    private Double systemNetCardsBandWidth;

    private Double systemNetworkSendAndReceiveBytesPs;

    private Double systemNetworkSendAndReceiveBytesPsMin;

    private Double systemNetworkSendAndReceiveBytesPsMax;

    private Double systemNetworkSendAndReceiveBytesPsMean;

    private Double systemNetworkSendAndReceiveBytesPsStd;

    private Double systemNetworkSendAndReceiveBytesPs55Quantile;

    private Double systemNetworkSendAndReceiveBytesPs75Quantile;

    private Double systemNetworkSendAndReceiveBytesPs95Quantile;

    private Double systemNetworkSendAndReceiveBytesPs99Quantile;

    private Double systemNetWorkBandWidthUsedPercent;

    private Double systemNetWorkBandWidthUsedPercentMin;

    private Double systemNetWorkBandWidthUsedPercentMax;

    private Double systemNetWorkBandWidthUsedPercentMean;

    private Double systemNetWorkBandWidthUsedPercentStd;

    private Double systemNetWorkBandWidthUsedPercent55Quantile;

    private Double systemNetWorkBandWidthUsedPercent75Quantile;

    private Double systemNetWorkBandWidthUsedPercent95Quantile;

    private Double systemNetWorkBandWidthUsedPercent99Quantile;

    public String getOstype() {
        return ostype;
    }

    public void setOstype(String ostype) {
        this.ostype = ostype;
    }

    public String getOsversion() {
        return osversion;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public String getOskernelversion() {
        return oskernelversion;
    }

    public void setOskernelversion(String oskernelversion) {
        this.oskernelversion = oskernelversion;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public Long getSystemntpoffset() {
        return systemntpoffset;
    }

    public void setSystemntpoffset(Long systemntpoffset) {
        this.systemntpoffset = systemntpoffset;
    }

    public Long getSystemstartuptime() {
        return systemstartuptime;
    }

    public void setSystemstartuptime(Long systemstartuptime) {
        this.systemstartuptime = systemstartuptime;
    }

    public Long getSystemuptime() {
        return systemuptime;
    }

    public void setSystemuptime(Long systemuptime) {
        this.systemuptime = systemuptime;
    }

    public Integer getProcessesblocked() {
        return processesblocked;
    }

    public void setProcessesblocked(Integer processesblocked) {
        this.processesblocked = processesblocked;
    }

    public Integer getProcessessleeping() {
        return processessleeping;
    }

    public void setProcessessleeping(Integer processessleeping) {
        this.processessleeping = processessleeping;
    }

    public Integer getProcesseszombies() {
        return processeszombies;
    }

    public void setProcesseszombies(Integer processeszombies) {
        this.processeszombies = processeszombies;
    }

    public Integer getProcessesstopped() {
        return processesstopped;
    }

    public void setProcessesstopped(Integer processesstopped) {
        this.processesstopped = processesstopped;
    }

    public Integer getProcessesrunning() {
        return processesrunning;
    }

    public void setProcessesrunning(Integer processesrunning) {
        this.processesrunning = processesrunning;
    }

    public Integer getProcessesidle() {
        return processesidle;
    }

    public void setProcessesidle(Integer processesidle) {
        this.processesidle = processesidle;
    }

    public Integer getProcesseswait() {
        return processeswait;
    }

    public void setProcesseswait(Integer processeswait) {
        this.processeswait = processeswait;
    }

    public Integer getProcessesdead() {
        return processesdead;
    }

    public void setProcessesdead(Integer processesdead) {
        this.processesdead = processesdead;
    }

    public Integer getProcessespaging() {
        return processespaging;
    }

    public void setProcessespaging(Integer processespaging) {
        this.processespaging = processespaging;
    }

    public Integer getProcessesunknown() {
        return processesunknown;
    }

    public void setProcessesunknown(Integer processesunknown) {
        this.processesunknown = processesunknown;
    }

    public Integer getProcessestotal() {
        return processestotal;
    }

    public void setProcessestotal(Integer processestotal) {
        this.processestotal = processestotal;
    }

    public Integer getProcessestotalthreads() {
        return processestotalthreads;
    }

    public void setProcessestotalthreads(Integer processestotalthreads) {
        this.processestotalthreads = processestotalthreads;
    }

    public Integer getCpucores() {
        return cpucores;
    }

    public void setCpucores(Integer cpucores) {
        this.cpucores = cpucores;
    }

    public Double getSystemcpuutil() {
        return systemcpuutil;
    }

    public void setSystemcpuutil(Double systemcpuutil) {
        this.systemcpuutil = systemcpuutil;
    }

    public Double getSystemcpuutilmin() {
        return systemcpuutilmin;
    }

    public void setSystemcpuutilmin(Double systemcpuutilmin) {
        this.systemcpuutilmin = systemcpuutilmin;
    }

    public Double getSystemcpuutilmax() {
        return systemcpuutilmax;
    }

    public void setSystemcpuutilmax(Double systemcpuutilmax) {
        this.systemcpuutilmax = systemcpuutilmax;
    }

    public Double getSystemcpuutilmean() {
        return systemcpuutilmean;
    }

    public void setSystemcpuutilmean(Double systemcpuutilmean) {
        this.systemcpuutilmean = systemcpuutilmean;
    }

    public Double getSystemcpuutilstd() {
        return systemcpuutilstd;
    }

    public void setSystemcpuutilstd(Double systemcpuutilstd) {
        this.systemcpuutilstd = systemcpuutilstd;
    }

    public Double getSystemcpuutil55quantile() {
        return systemcpuutil55quantile;
    }

    public void setSystemcpuutil55quantile(Double systemcpuutil55quantile) {
        this.systemcpuutil55quantile = systemcpuutil55quantile;
    }

    public Double getSystemcpuutil75quantile() {
        return systemcpuutil75quantile;
    }

    public void setSystemcpuutil75quantile(Double systemcpuutil75quantile) {
        this.systemcpuutil75quantile = systemcpuutil75quantile;
    }

    public Double getSystemcpuutil95quantile() {
        return systemcpuutil95quantile;
    }

    public void setSystemcpuutil95quantile(Double systemcpuutil95quantile) {
        this.systemcpuutil95quantile = systemcpuutil95quantile;
    }

    public Double getSystemcpuutil99quantile() {
        return systemcpuutil99quantile;
    }

    public void setSystemcpuutil99quantile(Double systemcpuutil99quantile) {
        this.systemcpuutil99quantile = systemcpuutil99quantile;
    }

    public Double getSystemcpuutiltotalpercent() {
        return systemcpuutiltotalpercent;
    }

    public void setSystemcpuutiltotalpercent(Double systemcpuutiltotalpercent) {
        this.systemcpuutiltotalpercent = systemcpuutiltotalpercent;
    }

    public Double getSystemcpuutiltotalpercentmin() {
        return systemcpuutiltotalpercentmin;
    }

    public void setSystemcpuutiltotalpercentmin(Double systemcpuutiltotalpercentmin) {
        this.systemcpuutiltotalpercentmin = systemcpuutiltotalpercentmin;
    }

    public Double getSystemcpuutiltotalpercentmax() {
        return systemcpuutiltotalpercentmax;
    }

    public void setSystemcpuutiltotalpercentmax(Double systemcpuutiltotalpercentmax) {
        this.systemcpuutiltotalpercentmax = systemcpuutiltotalpercentmax;
    }

    public Double getSystemcpuutiltotalpercentmean() {
        return systemcpuutiltotalpercentmean;
    }

    public void setSystemcpuutiltotalpercentmean(Double systemcpuutiltotalpercentmean) {
        this.systemcpuutiltotalpercentmean = systemcpuutiltotalpercentmean;
    }

    public Double getSystemcpuutiltotalpercentstd() {
        return systemcpuutiltotalpercentstd;
    }

    public void setSystemcpuutiltotalpercentstd(Double systemcpuutiltotalpercentstd) {
        this.systemcpuutiltotalpercentstd = systemcpuutiltotalpercentstd;
    }

    public Double getSystemcpuutiltotalpercent55quantile() {
        return systemcpuutiltotalpercent55quantile;
    }

    public void setSystemcpuutiltotalpercent55quantile(Double systemcpuutiltotalpercent55quantile) {
        this.systemcpuutiltotalpercent55quantile = systemcpuutiltotalpercent55quantile;
    }

    public Double getSystemcpuutiltotalpercent75quantile() {
        return systemcpuutiltotalpercent75quantile;
    }

    public void setSystemcpuutiltotalpercent75quantile(Double systemcpuutiltotalpercent75quantile) {
        this.systemcpuutiltotalpercent75quantile = systemcpuutiltotalpercent75quantile;
    }

    public Double getSystemcpuutiltotalpercent95quantile() {
        return systemcpuutiltotalpercent95quantile;
    }

    public void setSystemcpuutiltotalpercent95quantile(Double systemcpuutiltotalpercent95quantile) {
        this.systemcpuutiltotalpercent95quantile = systemcpuutiltotalpercent95quantile;
    }

    public Double getSystemcpuutiltotalpercent99quantile() {
        return systemcpuutiltotalpercent99quantile;
    }

    public void setSystemcpuutiltotalpercent99quantile(Double systemcpuutiltotalpercent99quantile) {
        this.systemcpuutiltotalpercent99quantile = systemcpuutiltotalpercent99quantile;
    }

    public Double getSystemcpusystem() {
        return systemcpusystem;
    }

    public void setSystemcpusystem(Double systemcpusystem) {
        this.systemcpusystem = systemcpusystem;
    }

    public Double getSystemcpusystemmin() {
        return systemcpusystemmin;
    }

    public void setSystemcpusystemmin(Double systemcpusystemmin) {
        this.systemcpusystemmin = systemcpusystemmin;
    }

    public Double getSystemcpusystemmax() {
        return systemcpusystemmax;
    }

    public void setSystemcpusystemmax(Double systemcpusystemmax) {
        this.systemcpusystemmax = systemcpusystemmax;
    }

    public Double getSystemcpusystemmean() {
        return systemcpusystemmean;
    }

    public void setSystemcpusystemmean(Double systemcpusystemmean) {
        this.systemcpusystemmean = systemcpusystemmean;
    }

    public Double getSystemcpusystemstd() {
        return systemcpusystemstd;
    }

    public void setSystemcpusystemstd(Double systemcpusystemstd) {
        this.systemcpusystemstd = systemcpusystemstd;
    }

    public Double getSystemcpusystem55quantile() {
        return systemcpusystem55quantile;
    }

    public void setSystemcpusystem55quantile(Double systemcpusystem55quantile) {
        this.systemcpusystem55quantile = systemcpusystem55quantile;
    }

    public Double getSystemcpusystem75quantile() {
        return systemcpusystem75quantile;
    }

    public void setSystemcpusystem75quantile(Double systemcpusystem75quantile) {
        this.systemcpusystem75quantile = systemcpusystem75quantile;
    }

    public Double getSystemcpusystem95quantile() {
        return systemcpusystem95quantile;
    }

    public void setSystemcpusystem95quantile(Double systemcpusystem95quantile) {
        this.systemcpusystem95quantile = systemcpusystem95quantile;
    }

    public Double getSystemcpusystem99quantile() {
        return systemcpusystem99quantile;
    }

    public void setSystemcpusystem99quantile(Double systemcpusystem99quantile) {
        this.systemcpusystem99quantile = systemcpusystem99quantile;
    }

    public Double getSystemcpuuser() {
        return systemcpuuser;
    }

    public void setSystemcpuuser(Double systemcpuuser) {
        this.systemcpuuser = systemcpuuser;
    }

    public Double getSystemcpuusermin() {
        return systemcpuusermin;
    }

    public void setSystemcpuusermin(Double systemcpuusermin) {
        this.systemcpuusermin = systemcpuusermin;
    }

    public Double getSystemcpuusermax() {
        return systemcpuusermax;
    }

    public void setSystemcpuusermax(Double systemcpuusermax) {
        this.systemcpuusermax = systemcpuusermax;
    }

    public Double getSystemcpuusermean() {
        return systemcpuusermean;
    }

    public void setSystemcpuusermean(Double systemcpuusermean) {
        this.systemcpuusermean = systemcpuusermean;
    }

    public Double getSystemcpuuserstd() {
        return systemcpuuserstd;
    }

    public void setSystemcpuuserstd(Double systemcpuuserstd) {
        this.systemcpuuserstd = systemcpuuserstd;
    }

    public Double getSystemcpuuser55quantile() {
        return systemcpuuser55quantile;
    }

    public void setSystemcpuuser55quantile(Double systemcpuuser55quantile) {
        this.systemcpuuser55quantile = systemcpuuser55quantile;
    }

    public Double getSystemcpuuser75quantile() {
        return systemcpuuser75quantile;
    }

    public void setSystemcpuuser75quantile(Double systemcpuuser75quantile) {
        this.systemcpuuser75quantile = systemcpuuser75quantile;
    }

    public Double getSystemcpuuser95quantile() {
        return systemcpuuser95quantile;
    }

    public void setSystemcpuuser95quantile(Double systemcpuuser95quantile) {
        this.systemcpuuser95quantile = systemcpuuser95quantile;
    }

    public Double getSystemcpuuser99quantile() {
        return systemcpuuser99quantile;
    }

    public void setSystemcpuuser99quantile(Double systemcpuuser99quantile) {
        this.systemcpuuser99quantile = systemcpuuser99quantile;
    }

    public Double getSystemcpuidle() {
        return systemcpuidle;
    }

    public void setSystemcpuidle(Double systemcpuidle) {
        this.systemcpuidle = systemcpuidle;
    }

    public Double getSystemcpuidlemin() {
        return systemcpuidlemin;
    }

    public void setSystemcpuidlemin(Double systemcpuidlemin) {
        this.systemcpuidlemin = systemcpuidlemin;
    }

    public Double getSystemcpuidlemax() {
        return systemcpuidlemax;
    }

    public void setSystemcpuidlemax(Double systemcpuidlemax) {
        this.systemcpuidlemax = systemcpuidlemax;
    }

    public Double getSystemcpuidlemean() {
        return systemcpuidlemean;
    }

    public void setSystemcpuidlemean(Double systemcpuidlemean) {
        this.systemcpuidlemean = systemcpuidlemean;
    }

    public Double getSystemcpuidlestd() {
        return systemcpuidlestd;
    }

    public void setSystemcpuidlestd(Double systemcpuidlestd) {
        this.systemcpuidlestd = systemcpuidlestd;
    }

    public Double getSystemcpuidle55quantile() {
        return systemcpuidle55quantile;
    }

    public void setSystemcpuidle55quantile(Double systemcpuidle55quantile) {
        this.systemcpuidle55quantile = systemcpuidle55quantile;
    }

    public Double getSystemcpuidle75quantile() {
        return systemcpuidle75quantile;
    }

    public void setSystemcpuidle75quantile(Double systemcpuidle75quantile) {
        this.systemcpuidle75quantile = systemcpuidle75quantile;
    }

    public Double getSystemcpuidle95quantile() {
        return systemcpuidle95quantile;
    }

    public void setSystemcpuidle95quantile(Double systemcpuidle95quantile) {
        this.systemcpuidle95quantile = systemcpuidle95quantile;
    }

    public Double getSystemcpuidle99quantile() {
        return systemcpuidle99quantile;
    }

    public void setSystemcpuidle99quantile(Double systemcpuidle99quantile) {
        this.systemcpuidle99quantile = systemcpuidle99quantile;
    }

    public Double getSystemcpuswitches() {
        return systemcpuswitches;
    }

    public void setSystemcpuswitches(Double systemcpuswitches) {
        this.systemcpuswitches = systemcpuswitches;
    }

    public Double getSystemcpuswitchesmin() {
        return systemcpuswitchesmin;
    }

    public void setSystemcpuswitchesmin(Double systemcpuswitchesmin) {
        this.systemcpuswitchesmin = systemcpuswitchesmin;
    }

    public Double getSystemcpuswitchesmax() {
        return systemcpuswitchesmax;
    }

    public void setSystemcpuswitchesmax(Double systemcpuswitchesmax) {
        this.systemcpuswitchesmax = systemcpuswitchesmax;
    }

    public Double getSystemcpuswitchesmean() {
        return systemcpuswitchesmean;
    }

    public void setSystemcpuswitchesmean(Double systemcpuswitchesmean) {
        this.systemcpuswitchesmean = systemcpuswitchesmean;
    }

    public Double getSystemcpuswitchesstd() {
        return systemcpuswitchesstd;
    }

    public void setSystemcpuswitchesstd(Double systemcpuswitchesstd) {
        this.systemcpuswitchesstd = systemcpuswitchesstd;
    }

    public Double getSystemcpuswitches55quantile() {
        return systemcpuswitches55quantile;
    }

    public void setSystemcpuswitches55quantile(Double systemcpuswitches55quantile) {
        this.systemcpuswitches55quantile = systemcpuswitches55quantile;
    }

    public Double getSystemcpuswitches75quantile() {
        return systemcpuswitches75quantile;
    }

    public void setSystemcpuswitches75quantile(Double systemcpuswitches75quantile) {
        this.systemcpuswitches75quantile = systemcpuswitches75quantile;
    }

    public Double getSystemcpuswitches95quantile() {
        return systemcpuswitches95quantile;
    }

    public void setSystemcpuswitches95quantile(Double systemcpuswitches95quantile) {
        this.systemcpuswitches95quantile = systemcpuswitches95quantile;
    }

    public Double getSystemcpuswitches99quantile() {
        return systemcpuswitches99quantile;
    }

    public void setSystemcpuswitches99quantile(Double systemcpuswitches99quantile) {
        this.systemcpuswitches99quantile = systemcpuswitches99quantile;
    }

    public Double getSystemcpuusageirq() {
        return systemcpuusageirq;
    }

    public void setSystemcpuusageirq(Double systemcpuusageirq) {
        this.systemcpuusageirq = systemcpuusageirq;
    }

    public Double getSystemcpuusageirqmin() {
        return systemcpuusageirqmin;
    }

    public void setSystemcpuusageirqmin(Double systemcpuusageirqmin) {
        this.systemcpuusageirqmin = systemcpuusageirqmin;
    }

    public Double getSystemcpuusageirqmax() {
        return systemcpuusageirqmax;
    }

    public void setSystemcpuusageirqmax(Double systemcpuusageirqmax) {
        this.systemcpuusageirqmax = systemcpuusageirqmax;
    }

    public Double getSystemcpuusageirqmean() {
        return systemcpuusageirqmean;
    }

    public void setSystemcpuusageirqmean(Double systemcpuusageirqmean) {
        this.systemcpuusageirqmean = systemcpuusageirqmean;
    }

    public Double getSystemcpuusageirqstd() {
        return systemcpuusageirqstd;
    }

    public void setSystemcpuusageirqstd(Double systemcpuusageirqstd) {
        this.systemcpuusageirqstd = systemcpuusageirqstd;
    }

    public Double getSystemcpuusageirq55quantile() {
        return systemcpuusageirq55quantile;
    }

    public void setSystemcpuusageirq55quantile(Double systemcpuusageirq55quantile) {
        this.systemcpuusageirq55quantile = systemcpuusageirq55quantile;
    }

    public Double getSystemcpuusageirq75quantile() {
        return systemcpuusageirq75quantile;
    }

    public void setSystemcpuusageirq75quantile(Double systemcpuusageirq75quantile) {
        this.systemcpuusageirq75quantile = systemcpuusageirq75quantile;
    }

    public Double getSystemcpuusageirq95quantile() {
        return systemcpuusageirq95quantile;
    }

    public void setSystemcpuusageirq95quantile(Double systemcpuusageirq95quantile) {
        this.systemcpuusageirq95quantile = systemcpuusageirq95quantile;
    }

    public Double getSystemcpuusageirq99quantile() {
        return systemcpuusageirq99quantile;
    }

    public void setSystemcpuusageirq99quantile(Double systemcpuusageirq99quantile) {
        this.systemcpuusageirq99quantile = systemcpuusageirq99quantile;
    }

    public Double getSystemcpuusagesoftirq() {
        return systemcpuusagesoftirq;
    }

    public void setSystemcpuusagesoftirq(Double systemcpuusagesoftirq) {
        this.systemcpuusagesoftirq = systemcpuusagesoftirq;
    }

    public Double getSystemcpuusagesoftirqmin() {
        return systemcpuusagesoftirqmin;
    }

    public void setSystemcpuusagesoftirqmin(Double systemcpuusagesoftirqmin) {
        this.systemcpuusagesoftirqmin = systemcpuusagesoftirqmin;
    }

    public Double getSystemcpuusagesoftirqmax() {
        return systemcpuusagesoftirqmax;
    }

    public void setSystemcpuusagesoftirqmax(Double systemcpuusagesoftirqmax) {
        this.systemcpuusagesoftirqmax = systemcpuusagesoftirqmax;
    }

    public Double getSystemcpuusagesoftirqmean() {
        return systemcpuusagesoftirqmean;
    }

    public void setSystemcpuusagesoftirqmean(Double systemcpuusagesoftirqmean) {
        this.systemcpuusagesoftirqmean = systemcpuusagesoftirqmean;
    }

    public Double getSystemcpuusagesoftirqstd() {
        return systemcpuusagesoftirqstd;
    }

    public void setSystemcpuusagesoftirqstd(Double systemcpuusagesoftirqstd) {
        this.systemcpuusagesoftirqstd = systemcpuusagesoftirqstd;
    }

    public Double getSystemcpuusagesoftirq55quantile() {
        return systemcpuusagesoftirq55quantile;
    }

    public void setSystemcpuusagesoftirq55quantile(Double systemcpuusagesoftirq55quantile) {
        this.systemcpuusagesoftirq55quantile = systemcpuusagesoftirq55quantile;
    }

    public Double getSystemcpuusagesoftirq75quantile() {
        return systemcpuusagesoftirq75quantile;
    }

    public void setSystemcpuusagesoftirq75quantile(Double systemcpuusagesoftirq75quantile) {
        this.systemcpuusagesoftirq75quantile = systemcpuusagesoftirq75quantile;
    }

    public Double getSystemcpuusagesoftirq95quantile() {
        return systemcpuusagesoftirq95quantile;
    }

    public void setSystemcpuusagesoftirq95quantile(Double systemcpuusagesoftirq95quantile) {
        this.systemcpuusagesoftirq95quantile = systemcpuusagesoftirq95quantile;
    }

    public Double getSystemcpuusagesoftirq99quantile() {
        return systemcpuusagesoftirq99quantile;
    }

    public void setSystemcpuusagesoftirq99quantile(Double systemcpuusagesoftirq99quantile) {
        this.systemcpuusagesoftirq99quantile = systemcpuusagesoftirq99quantile;
    }

    public Double getSystemload1() {
        return systemload1;
    }

    public void setSystemload1(Double systemload1) {
        this.systemload1 = systemload1;
    }

    public Double getSystemload1min() {
        return systemload1min;
    }

    public void setSystemload1min(Double systemload1min) {
        this.systemload1min = systemload1min;
    }

    public Double getSystemload1max() {
        return systemload1max;
    }

    public void setSystemload1max(Double systemload1max) {
        this.systemload1max = systemload1max;
    }

    public Double getSystemload1mean() {
        return systemload1mean;
    }

    public void setSystemload1mean(Double systemload1mean) {
        this.systemload1mean = systemload1mean;
    }

    public Double getSystemload1std() {
        return systemload1std;
    }

    public void setSystemload1std(Double systemload1std) {
        this.systemload1std = systemload1std;
    }

    public Double getSystemload155quantile() {
        return systemload155quantile;
    }

    public void setSystemload155quantile(Double systemload155quantile) {
        this.systemload155quantile = systemload155quantile;
    }

    public Double getSystemload175quantile() {
        return systemload175quantile;
    }

    public void setSystemload175quantile(Double systemload175quantile) {
        this.systemload175quantile = systemload175quantile;
    }

    public Double getSystemload195quantile() {
        return systemload195quantile;
    }

    public void setSystemload195quantile(Double systemload195quantile) {
        this.systemload195quantile = systemload195quantile;
    }

    public Double getSystemload199quantile() {
        return systemload199quantile;
    }

    public void setSystemload199quantile(Double systemload199quantile) {
        this.systemload199quantile = systemload199quantile;
    }

    public Double getSystemload5() {
        return systemload5;
    }

    public void setSystemload5(Double systemload5) {
        this.systemload5 = systemload5;
    }

    public Double getSystemload5min() {
        return systemload5min;
    }

    public void setSystemload5min(Double systemload5min) {
        this.systemload5min = systemload5min;
    }

    public Double getSystemload5max() {
        return systemload5max;
    }

    public void setSystemload5max(Double systemload5max) {
        this.systemload5max = systemload5max;
    }

    public Double getSystemload5mean() {
        return systemload5mean;
    }

    public void setSystemload5mean(Double systemload5mean) {
        this.systemload5mean = systemload5mean;
    }

    public Double getSystemload5std() {
        return systemload5std;
    }

    public void setSystemload5std(Double systemload5std) {
        this.systemload5std = systemload5std;
    }

    public Double getSystemload555quantile() {
        return systemload555quantile;
    }

    public void setSystemload555quantile(Double systemload555quantile) {
        this.systemload555quantile = systemload555quantile;
    }

    public Double getSystemload575quantile() {
        return systemload575quantile;
    }

    public void setSystemload575quantile(Double systemload575quantile) {
        this.systemload575quantile = systemload575quantile;
    }

    public Double getSystemload595quantile() {
        return systemload595quantile;
    }

    public void setSystemload595quantile(Double systemload595quantile) {
        this.systemload595quantile = systemload595quantile;
    }

    public Double getSystemload599quantile() {
        return systemload599quantile;
    }

    public void setSystemload599quantile(Double systemload599quantile) {
        this.systemload599quantile = systemload599quantile;
    }

    public Double getSystemload15() {
        return systemload15;
    }

    public void setSystemload15(Double systemload15) {
        this.systemload15 = systemload15;
    }

    public Double getSystemload15min() {
        return systemload15min;
    }

    public void setSystemload15min(Double systemload15min) {
        this.systemload15min = systemload15min;
    }

    public Double getSystemload15max() {
        return systemload15max;
    }

    public void setSystemload15max(Double systemload15max) {
        this.systemload15max = systemload15max;
    }

    public Double getSystemload15mean() {
        return systemload15mean;
    }

    public void setSystemload15mean(Double systemload15mean) {
        this.systemload15mean = systemload15mean;
    }

    public Double getSystemload15std() {
        return systemload15std;
    }

    public void setSystemload15std(Double systemload15std) {
        this.systemload15std = systemload15std;
    }

    public Double getSystemload1555quantile() {
        return systemload1555quantile;
    }

    public void setSystemload1555quantile(Double systemload1555quantile) {
        this.systemload1555quantile = systemload1555quantile;
    }

    public Double getSystemload1575quantile() {
        return systemload1575quantile;
    }

    public void setSystemload1575quantile(Double systemload1575quantile) {
        this.systemload1575quantile = systemload1575quantile;
    }

    public Double getSystemload1595quantile() {
        return systemload1595quantile;
    }

    public void setSystemload1595quantile(Double systemload1595quantile) {
        this.systemload1595quantile = systemload1595quantile;
    }

    public Double getSystemload1599quantile() {
        return systemload1599quantile;
    }

    public void setSystemload1599quantile(Double systemload1599quantile) {
        this.systemload1599quantile = systemload1599quantile;
    }

    public Double getSystemcpuiowait() {
        return systemcpuiowait;
    }

    public void setSystemcpuiowait(Double systemcpuiowait) {
        this.systemcpuiowait = systemcpuiowait;
    }

    public Double getSystemcpuiowaitmin() {
        return systemcpuiowaitmin;
    }

    public void setSystemcpuiowaitmin(Double systemcpuiowaitmin) {
        this.systemcpuiowaitmin = systemcpuiowaitmin;
    }

    public Double getSystemcpuiowaitmax() {
        return systemcpuiowaitmax;
    }

    public void setSystemcpuiowaitmax(Double systemcpuiowaitmax) {
        this.systemcpuiowaitmax = systemcpuiowaitmax;
    }

    public Double getSystemcpuiowaitmean() {
        return systemcpuiowaitmean;
    }

    public void setSystemcpuiowaitmean(Double systemcpuiowaitmean) {
        this.systemcpuiowaitmean = systemcpuiowaitmean;
    }

    public Double getSystemcpuiowaitstd() {
        return systemcpuiowaitstd;
    }

    public void setSystemcpuiowaitstd(Double systemcpuiowaitstd) {
        this.systemcpuiowaitstd = systemcpuiowaitstd;
    }

    public Double getSystemcpuiowait55quantile() {
        return systemcpuiowait55quantile;
    }

    public void setSystemcpuiowait55quantile(Double systemcpuiowait55quantile) {
        this.systemcpuiowait55quantile = systemcpuiowait55quantile;
    }

    public Double getSystemcpuiowait75quantile() {
        return systemcpuiowait75quantile;
    }

    public void setSystemcpuiowait75quantile(Double systemcpuiowait75quantile) {
        this.systemcpuiowait75quantile = systemcpuiowait75quantile;
    }

    public Double getSystemcpuiowait95quantile() {
        return systemcpuiowait95quantile;
    }

    public void setSystemcpuiowait95quantile(Double systemcpuiowait95quantile) {
        this.systemcpuiowait95quantile = systemcpuiowait95quantile;
    }

    public Double getSystemcpuiowait99quantile() {
        return systemcpuiowait99quantile;
    }

    public void setSystemcpuiowait99quantile(Double systemcpuiowait99quantile) {
        this.systemcpuiowait99quantile = systemcpuiowait99quantile;
    }

    public Double getSystemcpuguest() {
        return systemcpuguest;
    }

    public void setSystemcpuguest(Double systemcpuguest) {
        this.systemcpuguest = systemcpuguest;
    }

    public Double getSystemcpuguestmin() {
        return systemcpuguestmin;
    }

    public void setSystemcpuguestmin(Double systemcpuguestmin) {
        this.systemcpuguestmin = systemcpuguestmin;
    }

    public Double getSystemcpuguestmax() {
        return systemcpuguestmax;
    }

    public void setSystemcpuguestmax(Double systemcpuguestmax) {
        this.systemcpuguestmax = systemcpuguestmax;
    }

    public Double getSystemcpuguestmean() {
        return systemcpuguestmean;
    }

    public void setSystemcpuguestmean(Double systemcpuguestmean) {
        this.systemcpuguestmean = systemcpuguestmean;
    }

    public Double getSystemcpugueststd() {
        return systemcpugueststd;
    }

    public void setSystemcpugueststd(Double systemcpugueststd) {
        this.systemcpugueststd = systemcpugueststd;
    }

    public Double getSystemcpuguest55quantile() {
        return systemcpuguest55quantile;
    }

    public void setSystemcpuguest55quantile(Double systemcpuguest55quantile) {
        this.systemcpuguest55quantile = systemcpuguest55quantile;
    }

    public Double getSystemcpuguest75quantile() {
        return systemcpuguest75quantile;
    }

    public void setSystemcpuguest75quantile(Double systemcpuguest75quantile) {
        this.systemcpuguest75quantile = systemcpuguest75quantile;
    }

    public Double getSystemcpuguest95quantile() {
        return systemcpuguest95quantile;
    }

    public void setSystemcpuguest95quantile(Double systemcpuguest95quantile) {
        this.systemcpuguest95quantile = systemcpuguest95quantile;
    }

    public Double getSystemcpuguest99quantile() {
        return systemcpuguest99quantile;
    }

    public void setSystemcpuguest99quantile(Double systemcpuguest99quantile) {
        this.systemcpuguest99quantile = systemcpuguest99quantile;
    }

    public Double getSystemcpusteal() {
        return systemcpusteal;
    }

    public void setSystemcpusteal(Double systemcpusteal) {
        this.systemcpusteal = systemcpusteal;
    }

    public Double getSystemcpustealmin() {
        return systemcpustealmin;
    }

    public void setSystemcpustealmin(Double systemcpustealmin) {
        this.systemcpustealmin = systemcpustealmin;
    }

    public Double getSystemcpustealmax() {
        return systemcpustealmax;
    }

    public void setSystemcpustealmax(Double systemcpustealmax) {
        this.systemcpustealmax = systemcpustealmax;
    }

    public Double getSystemcpustealmean() {
        return systemcpustealmean;
    }

    public void setSystemcpustealmean(Double systemcpustealmean) {
        this.systemcpustealmean = systemcpustealmean;
    }

    public Double getSystemcpustealstd() {
        return systemcpustealstd;
    }

    public void setSystemcpustealstd(Double systemcpustealstd) {
        this.systemcpustealstd = systemcpustealstd;
    }

    public Double getSystemcpusteal55quantile() {
        return systemcpusteal55quantile;
    }

    public void setSystemcpusteal55quantile(Double systemcpusteal55quantile) {
        this.systemcpusteal55quantile = systemcpusteal55quantile;
    }

    public Double getSystemcpusteal75quantile() {
        return systemcpusteal75quantile;
    }

    public void setSystemcpusteal75quantile(Double systemcpusteal75quantile) {
        this.systemcpusteal75quantile = systemcpusteal75quantile;
    }

    public Double getSystemcpusteal95quantile() {
        return systemcpusteal95quantile;
    }

    public void setSystemcpusteal95quantile(Double systemcpusteal95quantile) {
        this.systemcpusteal95quantile = systemcpusteal95quantile;
    }

    public Double getSystemcpusteal99quantile() {
        return systemcpusteal99quantile;
    }

    public void setSystemcpusteal99quantile(Double systemcpusteal99quantile) {
        this.systemcpusteal99quantile = systemcpusteal99quantile;
    }

    public Long getSystemmemcommitlimit() {
        return systemmemcommitlimit;
    }

    public void setSystemmemcommitlimit(Long systemmemcommitlimit) {
        this.systemmemcommitlimit = systemmemcommitlimit;
    }

    public Long getSystemmemcommittedas() {
        return systemmemcommittedas;
    }

    public void setSystemmemcommittedas(Long systemmemcommittedas) {
        this.systemmemcommittedas = systemmemcommittedas;
    }

    public Long getSystemmemcommitted() {
        return systemmemcommitted;
    }

    public void setSystemmemcommitted(Long systemmemcommitted) {
        this.systemmemcommitted = systemmemcommitted;
    }

    public Long getSystemmemnonpaged() {
        return systemmemnonpaged;
    }

    public void setSystemmemnonpaged(Long systemmemnonpaged) {
        this.systemmemnonpaged = systemmemnonpaged;
    }

    public Long getSystemmempaged() {
        return systemmempaged;
    }

    public void setSystemmempaged(Long systemmempaged) {
        this.systemmempaged = systemmempaged;
    }

    public Long getSystemmemshared() {
        return systemmemshared;
    }

    public void setSystemmemshared(Long systemmemshared) {
        this.systemmemshared = systemmemshared;
    }

    public Long getSystemmemslab() {
        return systemmemslab;
    }

    public void setSystemmemslab(Long systemmemslab) {
        this.systemmemslab = systemmemslab;
    }

    public Long getSystemmemtotal() {
        return systemmemtotal;
    }

    public void setSystemmemtotal(Long systemmemtotal) {
        this.systemmemtotal = systemmemtotal;
    }

    public Long getSystemmemfree() {
        return systemmemfree;
    }

    public void setSystemmemfree(Long systemmemfree) {
        this.systemmemfree = systemmemfree;
    }

    public Long getSystemmemused() {
        return systemmemused;
    }

    public void setSystemmemused(Long systemmemused) {
        this.systemmemused = systemmemused;
    }

    public Long getSystemmembuffered() {
        return systemmembuffered;
    }

    public void setSystemmembuffered(Long systemmembuffered) {
        this.systemmembuffered = systemmembuffered;
    }

    public Long getSystemmemcached() {
        return systemmemcached;
    }

    public void setSystemmemcached(Long systemmemcached) {
        this.systemmemcached = systemmemcached;
    }

    public Double getSystemmemfreepercent() {
        return systemmemfreepercent;
    }

    public void setSystemmemfreepercent(Double systemmemfreepercent) {
        this.systemmemfreepercent = systemmemfreepercent;
    }

    public Double getSystemmemusedpercent() {
        return systemmemusedpercent;
    }

    public void setSystemmemusedpercent(Double systemmemusedpercent) {
        this.systemmemusedpercent = systemmemusedpercent;
    }

    public Long getSystemswapcached() {
        return systemswapcached;
    }

    public void setSystemswapcached(Long systemswapcached) {
        this.systemswapcached = systemswapcached;
    }

    public Long getSystemswapfree() {
        return systemswapfree;
    }

    public void setSystemswapfree(Long systemswapfree) {
        this.systemswapfree = systemswapfree;
    }

    public Double getSystemswapfreepercent() {
        return systemswapfreepercent;
    }

    public void setSystemswapfreepercent(Double systemswapfreepercent) {
        this.systemswapfreepercent = systemswapfreepercent;
    }

    public Long getSystemswaptotal() {
        return systemswaptotal;
    }

    public void setSystemswaptotal(Long systemswaptotal) {
        this.systemswaptotal = systemswaptotal;
    }

    public Long getSystemswapused() {
        return systemswapused;
    }

    public void setSystemswapused(Long systemswapused) {
        this.systemswapused = systemswapused;
    }

    public Double getSystemswapusedpercent() {
        return systemswapusedpercent;
    }

    public void setSystemswapusedpercent(Double systemswapusedpercent) {
        this.systemswapusedpercent = systemswapusedpercent;
    }

    public Integer getSystemdisks() {
        return systemdisks;
    }

    public void setSystemdisks(Integer systemdisks) {
        this.systemdisks = systemdisks;
    }

    public Integer getSystemfilesmax() {
        return systemfilesmax;
    }

    public void setSystemfilesmax(Integer systemfilesmax) {
        this.systemfilesmax = systemfilesmax;
    }

    public Integer getSystemfilesallocated() {
        return systemfilesallocated;
    }

    public void setSystemfilesallocated(Integer systemfilesallocated) {
        this.systemfilesallocated = systemfilesallocated;
    }

    public Integer getSystemfilesleft() {
        return systemfilesleft;
    }

    public void setSystemfilesleft(Integer systemfilesleft) {
        this.systemfilesleft = systemfilesleft;
    }

    public Double getSystemfilesusedpercent() {
        return systemfilesusedpercent;
    }

    public void setSystemfilesusedpercent(Double systemfilesusedpercent) {
        this.systemfilesusedpercent = systemfilesusedpercent;
    }

    public Integer getSystemfilesused() {
        return systemfilesused;
    }

    public void setSystemfilesused(Integer systemfilesused) {
        this.systemfilesused = systemfilesused;
    }

    public Integer getSystemfilesnotused() {
        return systemfilesnotused;
    }

    public void setSystemfilesnotused(Integer systemfilesnotused) {
        this.systemfilesnotused = systemfilesnotused;
    }

    public Integer getSystemnetcards() {
        return systemnetcards;
    }

    public void setSystemnetcards(Integer systemnetcards) {
        this.systemnetcards = systemnetcards;
    }

    public Double getSystemnetworkreceivebytesps() {
        return systemnetworkreceivebytesps;
    }

    public void setSystemnetworkreceivebytesps(Double systemnetworkreceivebytesps) {
        this.systemnetworkreceivebytesps = systemnetworkreceivebytesps;
    }

    public Double getSystemnetworkreceivebytespsmin() {
        return systemnetworkreceivebytespsmin;
    }

    public void setSystemnetworkreceivebytespsmin(Double systemnetworkreceivebytespsmin) {
        this.systemnetworkreceivebytespsmin = systemnetworkreceivebytespsmin;
    }

    public Double getSystemnetworkreceivebytespsmax() {
        return systemnetworkreceivebytespsmax;
    }

    public void setSystemnetworkreceivebytespsmax(Double systemnetworkreceivebytespsmax) {
        this.systemnetworkreceivebytespsmax = systemnetworkreceivebytespsmax;
    }

    public Double getSystemnetworkreceivebytespsmean() {
        return systemnetworkreceivebytespsmean;
    }

    public void setSystemnetworkreceivebytespsmean(Double systemnetworkreceivebytespsmean) {
        this.systemnetworkreceivebytespsmean = systemnetworkreceivebytespsmean;
    }

    public Double getSystemnetworkreceivebytespsstd() {
        return systemnetworkreceivebytespsstd;
    }

    public void setSystemnetworkreceivebytespsstd(Double systemnetworkreceivebytespsstd) {
        this.systemnetworkreceivebytespsstd = systemnetworkreceivebytespsstd;
    }

    public Double getSystemnetworkreceivebytesps55quantile() {
        return systemnetworkreceivebytesps55quantile;
    }

    public void setSystemnetworkreceivebytesps55quantile(Double systemnetworkreceivebytesps55quantile) {
        this.systemnetworkreceivebytesps55quantile = systemnetworkreceivebytesps55quantile;
    }

    public Double getSystemnetworkreceivebytesps75quantile() {
        return systemnetworkreceivebytesps75quantile;
    }

    public void setSystemnetworkreceivebytesps75quantile(Double systemnetworkreceivebytesps75quantile) {
        this.systemnetworkreceivebytesps75quantile = systemnetworkreceivebytesps75quantile;
    }

    public Double getSystemnetworkreceivebytesps95quantile() {
        return systemnetworkreceivebytesps95quantile;
    }

    public void setSystemnetworkreceivebytesps95quantile(Double systemnetworkreceivebytesps95quantile) {
        this.systemnetworkreceivebytesps95quantile = systemnetworkreceivebytesps95quantile;
    }

    public Double getSystemnetworkreceivebytesps99quantile() {
        return systemnetworkreceivebytesps99quantile;
    }

    public void setSystemnetworkreceivebytesps99quantile(Double systemnetworkreceivebytesps99quantile) {
        this.systemnetworkreceivebytesps99quantile = systemnetworkreceivebytesps99quantile;
    }

    public Double getSystemnetworksendbytesps() {
        return systemnetworksendbytesps;
    }

    public void setSystemnetworksendbytesps(Double systemnetworksendbytesps) {
        this.systemnetworksendbytesps = systemnetworksendbytesps;
    }

    public Double getSystemnetworksendbytespsmin() {
        return systemnetworksendbytespsmin;
    }

    public void setSystemnetworksendbytespsmin(Double systemnetworksendbytespsmin) {
        this.systemnetworksendbytespsmin = systemnetworksendbytespsmin;
    }

    public Double getSystemnetworksendbytespsmax() {
        return systemnetworksendbytespsmax;
    }

    public void setSystemnetworksendbytespsmax(Double systemnetworksendbytespsmax) {
        this.systemnetworksendbytespsmax = systemnetworksendbytespsmax;
    }

    public Double getSystemnetworksendbytespsmean() {
        return systemnetworksendbytespsmean;
    }

    public void setSystemnetworksendbytespsmean(Double systemnetworksendbytespsmean) {
        this.systemnetworksendbytespsmean = systemnetworksendbytespsmean;
    }

    public Double getSystemnetworksendbytespsstd() {
        return systemnetworksendbytespsstd;
    }

    public void setSystemnetworksendbytespsstd(Double systemnetworksendbytespsstd) {
        this.systemnetworksendbytespsstd = systemnetworksendbytespsstd;
    }

    public Double getSystemnetworksendbytesps55quantile() {
        return systemnetworksendbytesps55quantile;
    }

    public void setSystemnetworksendbytesps55quantile(Double systemnetworksendbytesps55quantile) {
        this.systemnetworksendbytesps55quantile = systemnetworksendbytesps55quantile;
    }

    public Double getSystemnetworksendbytesps75quantile() {
        return systemnetworksendbytesps75quantile;
    }

    public void setSystemnetworksendbytesps75quantile(Double systemnetworksendbytesps75quantile) {
        this.systemnetworksendbytesps75quantile = systemnetworksendbytesps75quantile;
    }

    public Double getSystemnetworksendbytesps95quantile() {
        return systemnetworksendbytesps95quantile;
    }

    public void setSystemnetworksendbytesps95quantile(Double systemnetworksendbytesps95quantile) {
        this.systemnetworksendbytesps95quantile = systemnetworksendbytesps95quantile;
    }

    public Double getSystemnetworksendbytesps99quantile() {
        return systemnetworksendbytesps99quantile;
    }

    public void setSystemnetworksendbytesps99quantile(Double systemnetworksendbytesps99quantile) {
        this.systemnetworksendbytesps99quantile = systemnetworksendbytesps99quantile;
    }

    public Integer getSystemnetworktcpconnectionnum() {
        return systemnetworktcpconnectionnum;
    }

    public void setSystemnetworktcpconnectionnum(Integer systemnetworktcpconnectionnum) {
        this.systemnetworktcpconnectionnum = systemnetworktcpconnectionnum;
    }

    public Integer getSystemnetworktcplisteningnum() {
        return systemnetworktcplisteningnum;
    }

    public void setSystemnetworktcplisteningnum(Integer systemnetworktcplisteningnum) {
        this.systemnetworktcplisteningnum = systemnetworktcplisteningnum;
    }

    public Integer getSystemnetworktcpestablishednum() {
        return systemnetworktcpestablishednum;
    }

    public void setSystemnetworktcpestablishednum(Integer systemnetworktcpestablishednum) {
        this.systemnetworktcpestablishednum = systemnetworktcpestablishednum;
    }

    public Integer getSystemnetworktcpsynsentnum() {
        return systemnetworktcpsynsentnum;
    }

    public void setSystemnetworktcpsynsentnum(Integer systemnetworktcpsynsentnum) {
        this.systemnetworktcpsynsentnum = systemnetworktcpsynsentnum;
    }

    public Integer getSystemnetworktcpsynrecvnum() {
        return systemnetworktcpsynrecvnum;
    }

    public void setSystemnetworktcpsynrecvnum(Integer systemnetworktcpsynrecvnum) {
        this.systemnetworktcpsynrecvnum = systemnetworktcpsynrecvnum;
    }

    public Integer getSystemnetworktcpfinwait1num() {
        return systemnetworktcpfinwait1num;
    }

    public void setSystemnetworktcpfinwait1num(Integer systemnetworktcpfinwait1num) {
        this.systemnetworktcpfinwait1num = systemnetworktcpfinwait1num;
    }

    public Integer getSystemnetworktcpfinwait2num() {
        return systemnetworktcpfinwait2num;
    }

    public void setSystemnetworktcpfinwait2num(Integer systemnetworktcpfinwait2num) {
        this.systemnetworktcpfinwait2num = systemnetworktcpfinwait2num;
    }

    public Integer getSystemnetworktcptimewaitnum() {
        return systemnetworktcptimewaitnum;
    }

    public void setSystemnetworktcptimewaitnum(Integer systemnetworktcptimewaitnum) {
        this.systemnetworktcptimewaitnum = systemnetworktcptimewaitnum;
    }

    public Integer getSystemnetworktcpclosednum() {
        return systemnetworktcpclosednum;
    }

    public void setSystemnetworktcpclosednum(Integer systemnetworktcpclosednum) {
        this.systemnetworktcpclosednum = systemnetworktcpclosednum;
    }

    public Integer getSystemnetworktcpclosewaitnum() {
        return systemnetworktcpclosewaitnum;
    }

    public void setSystemnetworktcpclosewaitnum(Integer systemnetworktcpclosewaitnum) {
        this.systemnetworktcpclosewaitnum = systemnetworktcpclosewaitnum;
    }

    public Integer getSystemnetworktcpclosingnum() {
        return systemnetworktcpclosingnum;
    }

    public void setSystemnetworktcpclosingnum(Integer systemnetworktcpclosingnum) {
        this.systemnetworktcpclosingnum = systemnetworktcpclosingnum;
    }

    public Integer getSystemnetworktcplastacknum() {
        return systemnetworktcplastacknum;
    }

    public void setSystemnetworktcplastacknum(Integer systemnetworktcplastacknum) {
        this.systemnetworktcplastacknum = systemnetworktcplastacknum;
    }

    public Integer getSystemnetworktcpnonenum() {
        return systemnetworktcpnonenum;
    }

    public void setSystemnetworktcpnonenum(Integer systemnetworktcpnonenum) {
        this.systemnetworktcpnonenum = systemnetworktcpnonenum;
    }

    public Long getSystemnetworktcpactiveopens() {
        return systemnetworktcpactiveopens;
    }

    public void setSystemnetworktcpactiveopens(Long systemnetworktcpactiveopens) {
        this.systemnetworktcpactiveopens = systemnetworktcpactiveopens;
    }

    public Long getSystemnetworktcppassiveopens() {
        return systemnetworktcppassiveopens;
    }

    public void setSystemnetworktcppassiveopens(Long systemnetworktcppassiveopens) {
        this.systemnetworktcppassiveopens = systemnetworktcppassiveopens;
    }

    public Long getSystemnetworktcpattemptfails() {
        return systemnetworktcpattemptfails;
    }

    public void setSystemnetworktcpattemptfails(Long systemnetworktcpattemptfails) {
        this.systemnetworktcpattemptfails = systemnetworktcpattemptfails;
    }

    public Long getSystemnetworktcpestabresets() {
        return systemnetworktcpestabresets;
    }

    public void setSystemnetworktcpestabresets(Long systemnetworktcpestabresets) {
        this.systemnetworktcpestabresets = systemnetworktcpestabresets;
    }

    public Long getSystemnetworktcpretranssegs() {
        return systemnetworktcpretranssegs;
    }

    public void setSystemnetworktcpretranssegs(Long systemnetworktcpretranssegs) {
        this.systemnetworktcpretranssegs = systemnetworktcpretranssegs;
    }

    public Long getSystemnetworktcpextlistenoverflows() {
        return systemnetworktcpextlistenoverflows;
    }

    public void setSystemnetworktcpextlistenoverflows(Long systemnetworktcpextlistenoverflows) {
        this.systemnetworktcpextlistenoverflows = systemnetworktcpextlistenoverflows;
    }

    public Long getSystemnetworkudpindatagrams() {
        return systemnetworkudpindatagrams;
    }

    public void setSystemnetworkudpindatagrams(Long systemnetworkudpindatagrams) {
        this.systemnetworkudpindatagrams = systemnetworkudpindatagrams;
    }

    public Long getSystemnetworkudpoutdatagrams() {
        return systemnetworkudpoutdatagrams;
    }

    public void setSystemnetworkudpoutdatagrams(Long systemnetworkudpoutdatagrams) {
        this.systemnetworkudpoutdatagrams = systemnetworkudpoutdatagrams;
    }

    public Long getSystemnetworkudpinerrors() {
        return systemnetworkudpinerrors;
    }

    public void setSystemnetworkudpinerrors(Long systemnetworkudpinerrors) {
        this.systemnetworkudpinerrors = systemnetworkudpinerrors;
    }

    public Long getSystemnetworkudpnoports() {
        return systemnetworkudpnoports;
    }

    public void setSystemnetworkudpnoports(Long systemnetworkudpnoports) {
        this.systemnetworkudpnoports = systemnetworkudpnoports;
    }

    public Long getSystemnetworkudpsendbuffererrors() {
        return systemnetworkudpsendbuffererrors;
    }

    public void setSystemnetworkudpsendbuffererrors(Long systemnetworkudpsendbuffererrors) {
        this.systemnetworkudpsendbuffererrors = systemnetworkudpsendbuffererrors;
    }

    public Long getHeartbeattime() {
        return heartbeattime;
    }

    public void setHeartbeattime(Long heartbeattime) {
        this.heartbeattime = heartbeattime;
    }

    public Long getHeartbeattimeminute() {
        return heartbeattimeminute;
    }

    public void setHeartbeattimeminute(Long heartbeattimeminute) {
        this.heartbeattimeminute = heartbeattimeminute;
    }

    public Long getHeartbeattimehour() {
        return heartbeattimehour;
    }

    public void setHeartbeattimehour(Long heartbeattimehour) {
        this.heartbeattimehour = heartbeattimehour;
    }

    public Long getHeartbeatTimeDay() {
        return heartbeatTimeDay;
    }

    public void setHeartbeatTimeDay(Long heartbeatTimeDay) {
        this.heartbeatTimeDay = heartbeatTimeDay;
    }

    public Double getSystemNetCardsBandWidth() {
        return systemNetCardsBandWidth;
    }

    public void setSystemNetCardsBandWidth(Double systemNetCardsBandWidth) {
        this.systemNetCardsBandWidth = systemNetCardsBandWidth;
    }

    public Double getSystemNetworkSendAndReceiveBytesPs() {
        return systemNetworkSendAndReceiveBytesPs;
    }

    public void setSystemNetworkSendAndReceiveBytesPs(Double systemNetworkSendAndReceiveBytesPs) {
        this.systemNetworkSendAndReceiveBytesPs = systemNetworkSendAndReceiveBytesPs;
    }

    public Double getSystemNetworkSendAndReceiveBytesPsMin() {
        return systemNetworkSendAndReceiveBytesPsMin;
    }

    public void setSystemNetworkSendAndReceiveBytesPsMin(Double systemNetworkSendAndReceiveBytesPsMin) {
        this.systemNetworkSendAndReceiveBytesPsMin = systemNetworkSendAndReceiveBytesPsMin;
    }

    public Double getSystemNetworkSendAndReceiveBytesPsMax() {
        return systemNetworkSendAndReceiveBytesPsMax;
    }

    public void setSystemNetworkSendAndReceiveBytesPsMax(Double systemNetworkSendAndReceiveBytesPsMax) {
        this.systemNetworkSendAndReceiveBytesPsMax = systemNetworkSendAndReceiveBytesPsMax;
    }

    public Double getSystemNetworkSendAndReceiveBytesPsMean() {
        return systemNetworkSendAndReceiveBytesPsMean;
    }

    public void setSystemNetworkSendAndReceiveBytesPsMean(Double systemNetworkSendAndReceiveBytesPsMean) {
        this.systemNetworkSendAndReceiveBytesPsMean = systemNetworkSendAndReceiveBytesPsMean;
    }

    public Double getSystemNetworkSendAndReceiveBytesPsStd() {
        return systemNetworkSendAndReceiveBytesPsStd;
    }

    public void setSystemNetworkSendAndReceiveBytesPsStd(Double systemNetworkSendAndReceiveBytesPsStd) {
        this.systemNetworkSendAndReceiveBytesPsStd = systemNetworkSendAndReceiveBytesPsStd;
    }

    public Double getSystemNetworkSendAndReceiveBytesPs55Quantile() {
        return systemNetworkSendAndReceiveBytesPs55Quantile;
    }

    public void setSystemNetworkSendAndReceiveBytesPs55Quantile(Double systemNetworkSendAndReceiveBytesPs55Quantile) {
        this.systemNetworkSendAndReceiveBytesPs55Quantile = systemNetworkSendAndReceiveBytesPs55Quantile;
    }

    public Double getSystemNetworkSendAndReceiveBytesPs75Quantile() {
        return systemNetworkSendAndReceiveBytesPs75Quantile;
    }

    public void setSystemNetworkSendAndReceiveBytesPs75Quantile(Double systemNetworkSendAndReceiveBytesPs75Quantile) {
        this.systemNetworkSendAndReceiveBytesPs75Quantile = systemNetworkSendAndReceiveBytesPs75Quantile;
    }

    public Double getSystemNetworkSendAndReceiveBytesPs95Quantile() {
        return systemNetworkSendAndReceiveBytesPs95Quantile;
    }

    public void setSystemNetworkSendAndReceiveBytesPs95Quantile(Double systemNetworkSendAndReceiveBytesPs95Quantile) {
        this.systemNetworkSendAndReceiveBytesPs95Quantile = systemNetworkSendAndReceiveBytesPs95Quantile;
    }

    public Double getSystemNetworkSendAndReceiveBytesPs99Quantile() {
        return systemNetworkSendAndReceiveBytesPs99Quantile;
    }

    public void setSystemNetworkSendAndReceiveBytesPs99Quantile(Double systemNetworkSendAndReceiveBytesPs99Quantile) {
        this.systemNetworkSendAndReceiveBytesPs99Quantile = systemNetworkSendAndReceiveBytesPs99Quantile;
    }

    public Double getSystemNetWorkBandWidthUsedPercent() {
        return systemNetWorkBandWidthUsedPercent;
    }

    public void setSystemNetWorkBandWidthUsedPercent(Double systemNetWorkBandWidthUsedPercent) {
        this.systemNetWorkBandWidthUsedPercent = systemNetWorkBandWidthUsedPercent;
    }

    public Double getSystemNetWorkBandWidthUsedPercentMin() {
        return systemNetWorkBandWidthUsedPercentMin;
    }

    public void setSystemNetWorkBandWidthUsedPercentMin(Double systemNetWorkBandWidthUsedPercentMin) {
        this.systemNetWorkBandWidthUsedPercentMin = systemNetWorkBandWidthUsedPercentMin;
    }

    public Double getSystemNetWorkBandWidthUsedPercentMax() {
        return systemNetWorkBandWidthUsedPercentMax;
    }

    public void setSystemNetWorkBandWidthUsedPercentMax(Double systemNetWorkBandWidthUsedPercentMax) {
        this.systemNetWorkBandWidthUsedPercentMax = systemNetWorkBandWidthUsedPercentMax;
    }

    public Double getSystemNetWorkBandWidthUsedPercentMean() {
        return systemNetWorkBandWidthUsedPercentMean;
    }

    public void setSystemNetWorkBandWidthUsedPercentMean(Double systemNetWorkBandWidthUsedPercentMean) {
        this.systemNetWorkBandWidthUsedPercentMean = systemNetWorkBandWidthUsedPercentMean;
    }

    public Double getSystemNetWorkBandWidthUsedPercentStd() {
        return systemNetWorkBandWidthUsedPercentStd;
    }

    public void setSystemNetWorkBandWidthUsedPercentStd(Double systemNetWorkBandWidthUsedPercentStd) {
        this.systemNetWorkBandWidthUsedPercentStd = systemNetWorkBandWidthUsedPercentStd;
    }

    public Double getSystemNetWorkBandWidthUsedPercent55Quantile() {
        return systemNetWorkBandWidthUsedPercent55Quantile;
    }

    public void setSystemNetWorkBandWidthUsedPercent55Quantile(Double systemNetWorkBandWidthUsedPercent55Quantile) {
        this.systemNetWorkBandWidthUsedPercent55Quantile = systemNetWorkBandWidthUsedPercent55Quantile;
    }

    public Double getSystemNetWorkBandWidthUsedPercent75Quantile() {
        return systemNetWorkBandWidthUsedPercent75Quantile;
    }

    public void setSystemNetWorkBandWidthUsedPercent75Quantile(Double systemNetWorkBandWidthUsedPercent75Quantile) {
        this.systemNetWorkBandWidthUsedPercent75Quantile = systemNetWorkBandWidthUsedPercent75Quantile;
    }

    public Double getSystemNetWorkBandWidthUsedPercent95Quantile() {
        return systemNetWorkBandWidthUsedPercent95Quantile;
    }

    public void setSystemNetWorkBandWidthUsedPercent95Quantile(Double systemNetWorkBandWidthUsedPercent95Quantile) {
        this.systemNetWorkBandWidthUsedPercent95Quantile = systemNetWorkBandWidthUsedPercent95Quantile;
    }

    public Double getSystemNetWorkBandWidthUsedPercent99Quantile() {
        return systemNetWorkBandWidthUsedPercent99Quantile;
    }

    public void setSystemNetWorkBandWidthUsedPercent99Quantile(Double systemNetWorkBandWidthUsedPercent99Quantile) {
        this.systemNetWorkBandWidthUsedPercent99Quantile = systemNetWorkBandWidthUsedPercent99Quantile;
    }
}
