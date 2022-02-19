package com.didichuxing.datachannel.agentmanager.common.metrics;

public class AgentBusinessMetrics {

    private String  hostname;

    private String  agentversion;

    private Long    writecount;

    private Long    writebytes;

    private Integer runningcollecttasknum;

    private Integer runningcollectpathnum;

    private Long    heartbeattime;

    private Long    heartbeattimeminute;

    private Long    heartbeattimehour;

    private Long    heartbeatTimeDay;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getAgentversion() {
        return agentversion;
    }

    public void setAgentversion(String agentversion) {
        this.agentversion = agentversion;
    }

    public Long getWritecount() {
        return writecount;
    }

    public void setWritecount(Long writecount) {
        this.writecount = writecount;
    }

    public Long getWritebytes() {
        return writebytes;
    }

    public void setWritebytes(Long writebytes) {
        this.writebytes = writebytes;
    }

    public Integer getRunningcollecttasknum() {
        return runningcollecttasknum;
    }

    public void setRunningcollecttasknum(Integer runningcollecttasknum) {
        this.runningcollecttasknum = runningcollecttasknum;
    }

    public Integer getRunningcollectpathnum() {
        return runningcollectpathnum;
    }

    public void setRunningcollectpathnum(Integer runningcollectpathnum) {
        this.runningcollectpathnum = runningcollectpathnum;
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
}
