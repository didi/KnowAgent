package com.didichuxing.datachannel.agentmanager.common.metrics;

public class ProcessMetrics {

    private String  hostname;

    private Long    procpid;

    private Long    procstartuptime;

    private Double  proccpuutil;

    private Double  proccpuutilmin;

    private Double  proccpuutilmax;

    private Double  proccpuutilmean;

    private Double  proccpuutilstd;

    private Double  proccpuutil55quantile;

    private Double  proccpuutil75quantile;

    private Double  proccpuutil95quantile;

    private Double  proccpuutil99quantile;

    private Long    procmemused;

    private Long    procnetworksendbytesps;

    private Long    procnetworksendbytespsmin;

    private Long    procnetworksendbytespsmax;

    private Long    procnetworksendbytespsmean;

    private Long    procnetworksendbytespsstd;

    private Long    procnetworksendbytesps55quantile;

    private Long    procnetworksendbytesps75quantile;

    private Long    procnetworksendbytesps95quantile;

    private Long    procnetworksendbytesps99quantile;

    private Long    procnetworkreceivebytesps;

    private Long    procnetworkreceivebytespsmin;

    private Long    procnetworkreceivebytespsmax;

    private Long    procnetworkreceivebytespsmean;

    private Long    procnetworkreceivebytespsstd;

    private Long    procnetworkreceivebytesps55quantile;

    private Long    procnetworkreceivebytesps75quantile;

    private Long    procnetworkreceivebytesps95quantile;

    private Long    procnetworkreceivebytesps99quantile;

    private Long    jvmprocfullgccount;

    private Integer procopenfdcount;

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

    public Long getProcpid() {
        return procpid;
    }

    public void setProcpid(Long procpid) {
        this.procpid = procpid;
    }

    public Long getProcstartuptime() {
        return procstartuptime;
    }

    public void setProcstartuptime(Long procstartuptime) {
        this.procstartuptime = procstartuptime;
    }

    public Double getProccpuutil() {
        return proccpuutil;
    }

    public void setProccpuutil(Double proccpuutil) {
        this.proccpuutil = proccpuutil;
    }

    public Double getProccpuutilmin() {
        return proccpuutilmin;
    }

    public void setProccpuutilmin(Double proccpuutilmin) {
        this.proccpuutilmin = proccpuutilmin;
    }

    public Double getProccpuutilmax() {
        return proccpuutilmax;
    }

    public void setProccpuutilmax(Double proccpuutilmax) {
        this.proccpuutilmax = proccpuutilmax;
    }

    public Double getProccpuutilmean() {
        return proccpuutilmean;
    }

    public void setProccpuutilmean(Double proccpuutilmean) {
        this.proccpuutilmean = proccpuutilmean;
    }

    public Double getProccpuutilstd() {
        return proccpuutilstd;
    }

    public void setProccpuutilstd(Double proccpuutilstd) {
        this.proccpuutilstd = proccpuutilstd;
    }

    public Double getProccpuutil55quantile() {
        return proccpuutil55quantile;
    }

    public void setProccpuutil55quantile(Double proccpuutil55quantile) {
        this.proccpuutil55quantile = proccpuutil55quantile;
    }

    public Double getProccpuutil75quantile() {
        return proccpuutil75quantile;
    }

    public void setProccpuutil75quantile(Double proccpuutil75quantile) {
        this.proccpuutil75quantile = proccpuutil75quantile;
    }

    public Double getProccpuutil95quantile() {
        return proccpuutil95quantile;
    }

    public void setProccpuutil95quantile(Double proccpuutil95quantile) {
        this.proccpuutil95quantile = proccpuutil95quantile;
    }

    public Double getProccpuutil99quantile() {
        return proccpuutil99quantile;
    }

    public void setProccpuutil99quantile(Double proccpuutil99quantile) {
        this.proccpuutil99quantile = proccpuutil99quantile;
    }

    public Long getProcmemused() {
        return procmemused;
    }

    public void setProcmemused(Long procmemused) {
        this.procmemused = procmemused;
    }

    public Long getProcnetworksendbytesps() {
        return procnetworksendbytesps;
    }

    public void setProcnetworksendbytesps(Long procnetworksendbytesps) {
        this.procnetworksendbytesps = procnetworksendbytesps;
    }

    public Long getProcnetworksendbytespsmin() {
        return procnetworksendbytespsmin;
    }

    public void setProcnetworksendbytespsmin(Long procnetworksendbytespsmin) {
        this.procnetworksendbytespsmin = procnetworksendbytespsmin;
    }

    public Long getProcnetworksendbytespsmax() {
        return procnetworksendbytespsmax;
    }

    public void setProcnetworksendbytespsmax(Long procnetworksendbytespsmax) {
        this.procnetworksendbytespsmax = procnetworksendbytespsmax;
    }

    public Long getProcnetworksendbytespsmean() {
        return procnetworksendbytespsmean;
    }

    public void setProcnetworksendbytespsmean(Long procnetworksendbytespsmean) {
        this.procnetworksendbytespsmean = procnetworksendbytespsmean;
    }

    public Long getProcnetworksendbytespsstd() {
        return procnetworksendbytespsstd;
    }

    public void setProcnetworksendbytespsstd(Long procnetworksendbytespsstd) {
        this.procnetworksendbytespsstd = procnetworksendbytespsstd;
    }

    public Long getProcnetworksendbytesps55quantile() {
        return procnetworksendbytesps55quantile;
    }

    public void setProcnetworksendbytesps55quantile(Long procnetworksendbytesps55quantile) {
        this.procnetworksendbytesps55quantile = procnetworksendbytesps55quantile;
    }

    public Long getProcnetworksendbytesps75quantile() {
        return procnetworksendbytesps75quantile;
    }

    public void setProcnetworksendbytesps75quantile(Long procnetworksendbytesps75quantile) {
        this.procnetworksendbytesps75quantile = procnetworksendbytesps75quantile;
    }

    public Long getProcnetworksendbytesps95quantile() {
        return procnetworksendbytesps95quantile;
    }

    public void setProcnetworksendbytesps95quantile(Long procnetworksendbytesps95quantile) {
        this.procnetworksendbytesps95quantile = procnetworksendbytesps95quantile;
    }

    public Long getProcnetworksendbytesps99quantile() {
        return procnetworksendbytesps99quantile;
    }

    public void setProcnetworksendbytesps99quantile(Long procnetworksendbytesps99quantile) {
        this.procnetworksendbytesps99quantile = procnetworksendbytesps99quantile;
    }

    public Long getProcnetworkreceivebytesps() {
        return procnetworkreceivebytesps;
    }

    public void setProcnetworkreceivebytesps(Long procnetworkreceivebytesps) {
        this.procnetworkreceivebytesps = procnetworkreceivebytesps;
    }

    public Long getProcnetworkreceivebytespsmin() {
        return procnetworkreceivebytespsmin;
    }

    public void setProcnetworkreceivebytespsmin(Long procnetworkreceivebytespsmin) {
        this.procnetworkreceivebytespsmin = procnetworkreceivebytespsmin;
    }

    public Long getProcnetworkreceivebytespsmax() {
        return procnetworkreceivebytespsmax;
    }

    public void setProcnetworkreceivebytespsmax(Long procnetworkreceivebytespsmax) {
        this.procnetworkreceivebytespsmax = procnetworkreceivebytespsmax;
    }

    public Long getProcnetworkreceivebytespsmean() {
        return procnetworkreceivebytespsmean;
    }

    public void setProcnetworkreceivebytespsmean(Long procnetworkreceivebytespsmean) {
        this.procnetworkreceivebytespsmean = procnetworkreceivebytespsmean;
    }

    public Long getProcnetworkreceivebytespsstd() {
        return procnetworkreceivebytespsstd;
    }

    public void setProcnetworkreceivebytespsstd(Long procnetworkreceivebytespsstd) {
        this.procnetworkreceivebytespsstd = procnetworkreceivebytespsstd;
    }

    public Long getProcnetworkreceivebytesps55quantile() {
        return procnetworkreceivebytesps55quantile;
    }

    public void setProcnetworkreceivebytesps55quantile(Long procnetworkreceivebytesps55quantile) {
        this.procnetworkreceivebytesps55quantile = procnetworkreceivebytesps55quantile;
    }

    public Long getProcnetworkreceivebytesps75quantile() {
        return procnetworkreceivebytesps75quantile;
    }

    public void setProcnetworkreceivebytesps75quantile(Long procnetworkreceivebytesps75quantile) {
        this.procnetworkreceivebytesps75quantile = procnetworkreceivebytesps75quantile;
    }

    public Long getProcnetworkreceivebytesps95quantile() {
        return procnetworkreceivebytesps95quantile;
    }

    public void setProcnetworkreceivebytesps95quantile(Long procnetworkreceivebytesps95quantile) {
        this.procnetworkreceivebytesps95quantile = procnetworkreceivebytesps95quantile;
    }

    public Long getProcnetworkreceivebytesps99quantile() {
        return procnetworkreceivebytesps99quantile;
    }

    public void setProcnetworkreceivebytesps99quantile(Long procnetworkreceivebytesps99quantile) {
        this.procnetworkreceivebytesps99quantile = procnetworkreceivebytesps99quantile;
    }

    public Long getJvmprocfullgccount() {
        return jvmprocfullgccount;
    }

    public void setJvmprocfullgccount(Long jvmprocfullgccount) {
        this.jvmprocfullgccount = jvmprocfullgccount;
    }

    public Integer getProcopenfdcount() {
        return procopenfdcount;
    }

    public void setProcopenfdcount(Integer procopenfdcount) {
        this.procopenfdcount = procopenfdcount;
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
