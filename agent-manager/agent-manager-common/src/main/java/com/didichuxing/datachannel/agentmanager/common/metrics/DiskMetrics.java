package com.didichuxing.datachannel.agentmanager.common.metrics;

public class DiskMetrics {

    private String hostname;

    private String systemdiskpath;

    private String systemdiskfstype;

    private Long   heartbeattime;

    private Long   heartbeattimeminute;

    private Long   heartbeattimehour;

    private Long   heartbeatTimeDay;

    private Long   systemdiskbytesfree;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getSystemdiskpath() {
        return systemdiskpath;
    }

    public void setSystemdiskpath(String systemdiskpath) {
        this.systemdiskpath = systemdiskpath;
    }

    public String getSystemdiskfstype() {
        return systemdiskfstype;
    }

    public void setSystemdiskfstype(String systemdiskfstype) {
        this.systemdiskfstype = systemdiskfstype;
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

    public Long getSystemdiskbytesfree() {
        return systemdiskbytesfree;
    }

    public void setSystemdiskbytesfree(Long systemdiskbytesfree) {
        this.systemdiskbytesfree = systemdiskbytesfree;
    }

}
