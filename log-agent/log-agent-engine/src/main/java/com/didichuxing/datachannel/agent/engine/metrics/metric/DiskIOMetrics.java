package com.didichuxing.datachannel.agent.engine.metrics.metric;

public class DiskIOMetrics {

    private String hostname;

    private String systemdiskdevice;

    private Long   heartbeattime;

    private Long   heartbeattimeminute;

    private Long   heartbeattimehour;

    private Long   heartbeatTimeDay;

    private Double systemioutil;

    private Double systemioutilmin;

    private Double systemioutilmax;

    private Double systemioutilmean;

    private Double systemioutilstd;

    private Double systemioutil55quantile;

    private Double systemioutil75quantile;

    private Double systemioutil95quantile;

    private Double systemioutil99quantile;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSystemdiskdevice() {
        return systemdiskdevice;
    }

    public void setSystemdiskdevice(String systemdiskdevice) {
        this.systemdiskdevice = systemdiskdevice;
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

    public Double getSystemioutil() {
        return systemioutil;
    }

    public void setSystemioutil(Double systemioutil) {
        this.systemioutil = systemioutil;
    }

    public Double getSystemioutilmin() {
        return systemioutilmin;
    }

    public void setSystemioutilmin(Double systemioutilmin) {
        this.systemioutilmin = systemioutilmin;
    }

    public Double getSystemioutilmax() {
        return systemioutilmax;
    }

    public void setSystemioutilmax(Double systemioutilmax) {
        this.systemioutilmax = systemioutilmax;
    }

    public Double getSystemioutilmean() {
        return systemioutilmean;
    }

    public void setSystemioutilmean(Double systemioutilmean) {
        this.systemioutilmean = systemioutilmean;
    }

    public Double getSystemioutilstd() {
        return systemioutilstd;
    }

    public void setSystemioutilstd(Double systemioutilstd) {
        this.systemioutilstd = systemioutilstd;
    }

    public Double getSystemioutil55quantile() {
        return systemioutil55quantile;
    }

    public void setSystemioutil55quantile(Double systemioutil55quantile) {
        this.systemioutil55quantile = systemioutil55quantile;
    }

    public Double getSystemioutil75quantile() {
        return systemioutil75quantile;
    }

    public void setSystemioutil75quantile(Double systemioutil75quantile) {
        this.systemioutil75quantile = systemioutil75quantile;
    }

    public Double getSystemioutil95quantile() {
        return systemioutil95quantile;
    }

    public void setSystemioutil95quantile(Double systemioutil95quantile) {
        this.systemioutil95quantile = systemioutil95quantile;
    }

    public Double getSystemioutil99quantile() {
        return systemioutil99quantile;
    }

    public void setSystemioutil99quantile(Double systemioutil99quantile) {
        this.systemioutil99quantile = systemioutil99quantile;
    }
}
