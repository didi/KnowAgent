package com.didichuxing.datachannel.agentmanager.common.metrics;

public class AgentBusinessMetrics {

    private String  hostname;

    private String  agentversion;

    private Long    writecount;

    private Long    writebytes;

    private Long readbytes;

    private Long readcount;

    private Integer runningcollecttasknum;

    private Integer runningcollectpathnum;

    private Long errorlogscount;

    private Long errorlogssendfailedcount;

    private Integer normalcollectthreadnummax;

    private Integer normalcollectthreadnumsize;

    private Integer normalcollectthreadqueuemax;

    private Integer normalcollectthreadqueuesize;

    private Integer temporarycollectthreadnummax;

    private Integer temporarycollectthreadnumsize;

    private Integer temporarycollectthreadqueuemax;

    private Integer temporarycollectthreadqueuesize;

    private Integer collecttasknum;

    private Integer pausecollecttasknum;

    private Integer collectpathnum;

    private Integer pausecollectpathnum;

    private Long    heartbeattime;

    private Long    heartbeattimeminute;

    private Long    heartbeattimehour;

    private Long    heartbeatTimeDay;

    private Long limittps;

    private Double cpulimit;

    public Long getErrorlogscount() {
        return errorlogscount;
    }

    public void setErrorlogscount(Long errorlogscount) {
        this.errorlogscount = errorlogscount;
    }

    public Long getErrorlogssendfailedcount() {
        return errorlogssendfailedcount;
    }

    public Integer getCollecttasknum() {
        return collecttasknum;
    }

    public void setCollecttasknum(Integer collecttasknum) {
        this.collecttasknum = collecttasknum;
    }

    public Integer getPausecollecttasknum() {
        return pausecollecttasknum;
    }

    public void setPausecollecttasknum(Integer pausecollecttasknum) {
        this.pausecollecttasknum = pausecollecttasknum;
    }

    public Integer getCollectpathnum() {
        return collectpathnum;
    }

    public void setCollectpathnum(Integer collectpathnum) {
        this.collectpathnum = collectpathnum;
    }

    public Integer getPausecollectpathnum() {
        return pausecollectpathnum;
    }

    public void setPausecollectpathnum(Integer pausecollectpathnum) {
        this.pausecollectpathnum = pausecollectpathnum;
    }

    public void setErrorlogssendfailedcount(Long errorlogssendfailedcount) {
        this.errorlogssendfailedcount = errorlogssendfailedcount;
    }

    public Long getReadbytes() {
        return readbytes;
    }

    public void setReadbytes(Long readbytes) {
        this.readbytes = readbytes;
    }

    public Long getReadcount() {
        return readcount;
    }

    public void setReadcount(Long readcount) {
        this.readcount = readcount;
    }

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

    public Integer getNormalcollectthreadnummax() {
        return normalcollectthreadnummax;
    }

    public void setNormalcollectthreadnummax(Integer normalcollectthreadnummax) {
        this.normalcollectthreadnummax = normalcollectthreadnummax;
    }

    public Integer getNormalcollectthreadnumsize() {
        return normalcollectthreadnumsize;
    }

    public void setNormalcollectthreadnumsize(Integer normalcollectthreadnumsize) {
        this.normalcollectthreadnumsize = normalcollectthreadnumsize;
    }

    public Integer getNormalcollectthreadqueuemax() {
        return normalcollectthreadqueuemax;
    }

    public void setNormalcollectthreadqueuemax(Integer normalcollectthreadqueuemax) {
        this.normalcollectthreadqueuemax = normalcollectthreadqueuemax;
    }

    public Integer getNormalcollectthreadqueuesize() {
        return normalcollectthreadqueuesize;
    }

    public void setNormalcollectthreadqueuesize(Integer normalcollectthreadqueuesize) {
        this.normalcollectthreadqueuesize = normalcollectthreadqueuesize;
    }

    public Integer getTemporarycollectthreadnummax() {
        return temporarycollectthreadnummax;
    }

    public void setTemporarycollectthreadnummax(Integer temporarycollectthreadnummax) {
        this.temporarycollectthreadnummax = temporarycollectthreadnummax;
    }

    public Integer getTemporarycollectthreadnumsize() {
        return temporarycollectthreadnumsize;
    }

    public void setTemporarycollectthreadnumsize(Integer temporarycollectthreadnumsize) {
        this.temporarycollectthreadnumsize = temporarycollectthreadnumsize;
    }

    public Integer getTemporarycollectthreadqueuemax() {
        return temporarycollectthreadqueuemax;
    }

    public void setTemporarycollectthreadqueuemax(Integer temporarycollectthreadqueuemax) {
        this.temporarycollectthreadqueuemax = temporarycollectthreadqueuemax;
    }

    public Integer getTemporarycollectthreadqueuesize() {
        return temporarycollectthreadqueuesize;
    }

    public void setTemporarycollectthreadqueuesize(Integer temporarycollectthreadqueuesize) {
        this.temporarycollectthreadqueuesize = temporarycollectthreadqueuesize;
    }

    public void setHeartbeattimeminute(Long heartbeattimeminute) {
        this.heartbeattimeminute = heartbeattimeminute;
    }

    public Long getHeartbeattimehour() {
        return heartbeattimehour;
    }

    public Long getLimittps() {
        return limittps;
    }

    public void setLimittps(Long limittps) {
        this.limittps = limittps;
    }

    public Double getCpulimit() {
        return cpulimit;
    }

    public void setCpulimit(Double cpulimit) {
        this.cpulimit = cpulimit;
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
