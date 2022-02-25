package com.didichuxing.datachannel.agentmanager.common.bean.po.metrics;

public class MetricsLogCollectTaskPO {
    private Long id;

    private Long collecttaskid;

    private Long pathid;

    private String agenthostname;

    private String collecttaskhostname;

    private String agenthostip;

    private Long businesstimestamp;

    private Long maxbusinesstimestampdelay;

    private Long limittime;

    private Long toolargetruncatenum;

    private Long toolargetruncatenumtotal;

    private Integer collectpathisexists;

    private Integer disorderexists;

    private Integer sliceerrorexists;

    private Long readbytes;

    private Long readcount;

    private Long sendbytes;

    private Long sendcount;

    private Double readtimeperevent;

    private Double readtimepereventmin;

    private Double readtimepereventmax;

    private Double readtimepereventmean;

    private Double readtimepereventstd;

    private Double readtimeperevent55quantile;

    private Double readtimeperevent75quantile;

    private Double readtimeperevent95quantile;

    private Double readtimeperevent99quantile;

    private Double sendtime;

    private Double sendtimemin;

    private Double sendtimemax;

    private Double sendtimemean;

    private Double sendtimestd;

    private Double sendtime55quantile;

    private Double sendtime75quantile;

    private Double sendtime95quantile;

    private Double sendtime99quantile;

    private Double flushtime;

    private Double flushtimemin;

    private Double flushtimemax;

    private Double flushtimemean;

    private Double flushtimestd;

    private Double flushtime55quantile;

    private Double flushtime75quantile;

    private Double flushtime95quantile;

    private Double flushtime99quantile;

    private Double processtimeperevent;

    private Double processtimepereventmin;

    private Double processtimepereventmax;

    private Double processtimepereventmean;

    private Double processtimepereventstd;

    private Double processtimeperevent55quantile;

    private Double processtimeperevent75quantile;

    private Double processtimeperevent95quantile;

    private Double processtimeperevent99quantile;

    private Long flushtimes;

    private Long flushfailedtimes;

    private Long filtereventsnum;

    private Long channelbytesmax;

    private Long channelcountmax;

    private Double channelbytessize;

    private Double channelbytessizemin;

    private Double channelbytessizemax;

    private Double channelbytessizemean;

    private Double channelbytessizestd;

    private Double channelbytessize55quantile;

    private Double channelbytessize75quantile;

    private Double channelbytessize95quantile;

    private Double channelbytessize99quantile;

    private Double channelcountsize;

    private Double channelcountsizemin;

    private Double channelcountsizemax;

    private Double channelcountsizemean;

    private Double channelcountsizestd;

    private Double channelcountsize55quantile;

    private Double channelcountsize75quantile;

    private Double channelcountsize95quantile;

    private Double channelcountsize99quantile;

    private Double channelusedpercent;

    private Double channelusedpercentmin;

    private Double channelusedpercentmax;

    private Double channelusedpercentmean;

    private Double channelusedpercentstd;

    private Double channelusedpercent55quantile;

    private Double channelusedpercent75quantile;

    private Double channelusedpercent95quantile;

    private Double channelusedpercent99quantile;

    private Long receiverclusterid;

    private String receiverclustertopic;

    private String collectfiles;

    private Integer relatedfiles;

    private String latestfile;

    private String masterfile;

    private String path;

    private Integer collecttasktype;

    private Integer sinknum;

    private Integer collecttaskversion;

    private Long dynamiclimiterthreshold;

    private Long heartbeattime;

    private Long heartbeattimeminute;

    private Long heartbeattimehour;

    private Long heartbeatTimeDay;

    private String serviceNames;

    public String getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(String serviceNames) {
        this.serviceNames = serviceNames;
    }

    public Long getHeartbeatTimeDay() {
        return heartbeatTimeDay;
    }

    public void setHeartbeatTimeDay(Long heartbeatTimeDay) {
        this.heartbeatTimeDay = heartbeatTimeDay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollecttaskid() {
        return collecttaskid;
    }

    public void setCollecttaskid(Long collecttaskid) {
        this.collecttaskid = collecttaskid;
    }

    public Long getPathid() {
        return pathid;
    }

    public void setPathid(Long pathid) {
        this.pathid = pathid;
    }

    public String getAgenthostname() {
        return agenthostname;
    }

    public void setAgenthostname(String agenthostname) {
        this.agenthostname = agenthostname;
    }

    public String getCollecttaskhostname() {
        return collecttaskhostname;
    }

    public void setCollecttaskhostname(String collecttaskhostname) {
        this.collecttaskhostname = collecttaskhostname;
    }

    public String getAgenthostip() {
        return agenthostip;
    }

    public void setAgenthostip(String agenthostip) {
        this.agenthostip = agenthostip;
    }

    public Long getBusinesstimestamp() {
        return businesstimestamp;
    }

    public void setBusinesstimestamp(Long businesstimestamp) {
        this.businesstimestamp = businesstimestamp;
    }

    public Long getMaxbusinesstimestampdelay() {
        return maxbusinesstimestampdelay;
    }

    public void setMaxbusinesstimestampdelay(Long maxbusinesstimestampdelay) {
        this.maxbusinesstimestampdelay = maxbusinesstimestampdelay;
    }

    public Long getLimittime() {
        return limittime;
    }

    public void setLimittime(Long limittime) {
        this.limittime = limittime;
    }

    public Long getToolargetruncatenum() {
        return toolargetruncatenum;
    }

    public void setToolargetruncatenum(Long toolargetruncatenum) {
        this.toolargetruncatenum = toolargetruncatenum;
    }

    public Long getToolargetruncatenumtotal() {
        return toolargetruncatenumtotal;
    }

    public void setToolargetruncatenumtotal(Long toolargetruncatenumtotal) {
        this.toolargetruncatenumtotal = toolargetruncatenumtotal;
    }

    public Integer getCollectpathisexists() {
        return collectpathisexists;
    }

    public void setCollectpathisexists(Integer collectpathisexists) {
        this.collectpathisexists = collectpathisexists;
    }

    public Integer getDisorderexists() {
        return disorderexists;
    }

    public void setDisorderexists(Integer disorderexists) {
        this.disorderexists = disorderexists;
    }

    public Integer getSliceerrorexists() {
        return sliceerrorexists;
    }

    public void setSliceerrorexists(Integer sliceerrorexists) {
        this.sliceerrorexists = sliceerrorexists;
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

    public Long getSendbytes() {
        return sendbytes;
    }

    public void setSendbytes(Long sendbytes) {
        this.sendbytes = sendbytes;
    }

    public Long getSendcount() {
        return sendcount;
    }

    public void setSendcount(Long sendcount) {
        this.sendcount = sendcount;
    }

    public Double getReadtimeperevent() {
        return readtimeperevent;
    }

    public void setReadtimeperevent(Double readtimeperevent) {
        this.readtimeperevent = readtimeperevent;
    }

    public Double getReadtimepereventmin() {
        return readtimepereventmin;
    }

    public void setReadtimepereventmin(Double readtimepereventmin) {
        this.readtimepereventmin = readtimepereventmin;
    }

    public Double getReadtimepereventmax() {
        return readtimepereventmax;
    }

    public void setReadtimepereventmax(Double readtimepereventmax) {
        this.readtimepereventmax = readtimepereventmax;
    }

    public Double getReadtimepereventmean() {
        return readtimepereventmean;
    }

    public void setReadtimepereventmean(Double readtimepereventmean) {
        this.readtimepereventmean = readtimepereventmean;
    }

    public Double getReadtimepereventstd() {
        return readtimepereventstd;
    }

    public void setReadtimepereventstd(Double readtimepereventstd) {
        this.readtimepereventstd = readtimepereventstd;
    }

    public Double getReadtimeperevent55quantile() {
        return readtimeperevent55quantile;
    }

    public void setReadtimeperevent55quantile(Double readtimeperevent55quantile) {
        this.readtimeperevent55quantile = readtimeperevent55quantile;
    }

    public Double getReadtimeperevent75quantile() {
        return readtimeperevent75quantile;
    }

    public void setReadtimeperevent75quantile(Double readtimeperevent75quantile) {
        this.readtimeperevent75quantile = readtimeperevent75quantile;
    }

    public Double getReadtimeperevent95quantile() {
        return readtimeperevent95quantile;
    }

    public void setReadtimeperevent95quantile(Double readtimeperevent95quantile) {
        this.readtimeperevent95quantile = readtimeperevent95quantile;
    }

    public Double getReadtimeperevent99quantile() {
        return readtimeperevent99quantile;
    }

    public void setReadtimeperevent99quantile(Double readtimeperevent99quantile) {
        this.readtimeperevent99quantile = readtimeperevent99quantile;
    }

    public Double getSendtime() {
        return sendtime;
    }

    public void setSendtime(Double sendtime) {
        this.sendtime = sendtime;
    }

    public Double getSendtimemin() {
        return sendtimemin;
    }

    public void setSendtimemin(Double sendtimemin) {
        this.sendtimemin = sendtimemin;
    }

    public Double getSendtimemax() {
        return sendtimemax;
    }

    public void setSendtimemax(Double sendtimemax) {
        this.sendtimemax = sendtimemax;
    }

    public Double getSendtimemean() {
        return sendtimemean;
    }

    public void setSendtimemean(Double sendtimemean) {
        this.sendtimemean = sendtimemean;
    }

    public Double getSendtimestd() {
        return sendtimestd;
    }

    public void setSendtimestd(Double sendtimestd) {
        this.sendtimestd = sendtimestd;
    }

    public Double getSendtime55quantile() {
        return sendtime55quantile;
    }

    public void setSendtime55quantile(Double sendtime55quantile) {
        this.sendtime55quantile = sendtime55quantile;
    }

    public Double getSendtime75quantile() {
        return sendtime75quantile;
    }

    public void setSendtime75quantile(Double sendtime75quantile) {
        this.sendtime75quantile = sendtime75quantile;
    }

    public Double getSendtime95quantile() {
        return sendtime95quantile;
    }

    public void setSendtime95quantile(Double sendtime95quantile) {
        this.sendtime95quantile = sendtime95quantile;
    }

    public Double getSendtime99quantile() {
        return sendtime99quantile;
    }

    public void setSendtime99quantile(Double sendtime99quantile) {
        this.sendtime99quantile = sendtime99quantile;
    }

    public Double getFlushtime() {
        return flushtime;
    }

    public void setFlushtime(Double flushtime) {
        this.flushtime = flushtime;
    }

    public Double getFlushtimemin() {
        return flushtimemin;
    }

    public void setFlushtimemin(Double flushtimemin) {
        this.flushtimemin = flushtimemin;
    }

    public Double getFlushtimemax() {
        return flushtimemax;
    }

    public void setFlushtimemax(Double flushtimemax) {
        this.flushtimemax = flushtimemax;
    }

    public Double getFlushtimemean() {
        return flushtimemean;
    }

    public void setFlushtimemean(Double flushtimemean) {
        this.flushtimemean = flushtimemean;
    }

    public Double getFlushtimestd() {
        return flushtimestd;
    }

    public void setFlushtimestd(Double flushtimestd) {
        this.flushtimestd = flushtimestd;
    }

    public Double getFlushtime55quantile() {
        return flushtime55quantile;
    }

    public void setFlushtime55quantile(Double flushtime55quantile) {
        this.flushtime55quantile = flushtime55quantile;
    }

    public Double getFlushtime75quantile() {
        return flushtime75quantile;
    }

    public void setFlushtime75quantile(Double flushtime75quantile) {
        this.flushtime75quantile = flushtime75quantile;
    }

    public Double getFlushtime95quantile() {
        return flushtime95quantile;
    }

    public void setFlushtime95quantile(Double flushtime95quantile) {
        this.flushtime95quantile = flushtime95quantile;
    }

    public Double getFlushtime99quantile() {
        return flushtime99quantile;
    }

    public void setFlushtime99quantile(Double flushtime99quantile) {
        this.flushtime99quantile = flushtime99quantile;
    }

    public Double getProcesstimeperevent() {
        return processtimeperevent;
    }

    public void setProcesstimeperevent(Double processtimeperevent) {
        this.processtimeperevent = processtimeperevent;
    }

    public Double getProcesstimepereventmin() {
        return processtimepereventmin;
    }

    public void setProcesstimepereventmin(Double processtimepereventmin) {
        this.processtimepereventmin = processtimepereventmin;
    }

    public Double getProcesstimepereventmax() {
        return processtimepereventmax;
    }

    public void setProcesstimepereventmax(Double processtimepereventmax) {
        this.processtimepereventmax = processtimepereventmax;
    }

    public Double getProcesstimepereventmean() {
        return processtimepereventmean;
    }

    public void setProcesstimepereventmean(Double processtimepereventmean) {
        this.processtimepereventmean = processtimepereventmean;
    }

    public Double getProcesstimepereventstd() {
        return processtimepereventstd;
    }

    public void setProcesstimepereventstd(Double processtimepereventstd) {
        this.processtimepereventstd = processtimepereventstd;
    }

    public Double getProcesstimeperevent55quantile() {
        return processtimeperevent55quantile;
    }

    public void setProcesstimeperevent55quantile(Double processtimeperevent55quantile) {
        this.processtimeperevent55quantile = processtimeperevent55quantile;
    }

    public Double getProcesstimeperevent75quantile() {
        return processtimeperevent75quantile;
    }

    public void setProcesstimeperevent75quantile(Double processtimeperevent75quantile) {
        this.processtimeperevent75quantile = processtimeperevent75quantile;
    }

    public Double getProcesstimeperevent95quantile() {
        return processtimeperevent95quantile;
    }

    public void setProcesstimeperevent95quantile(Double processtimeperevent95quantile) {
        this.processtimeperevent95quantile = processtimeperevent95quantile;
    }

    public Double getProcesstimeperevent99quantile() {
        return processtimeperevent99quantile;
    }

    public void setProcesstimeperevent99quantile(Double processtimeperevent99quantile) {
        this.processtimeperevent99quantile = processtimeperevent99quantile;
    }

    public Long getFlushtimes() {
        return flushtimes;
    }

    public void setFlushtimes(Long flushtimes) {
        this.flushtimes = flushtimes;
    }

    public Long getFlushfailedtimes() {
        return flushfailedtimes;
    }

    public void setFlushfailedtimes(Long flushfailedtimes) {
        this.flushfailedtimes = flushfailedtimes;
    }

    public Long getFiltereventsnum() {
        return filtereventsnum;
    }

    public void setFiltereventsnum(Long filtereventsnum) {
        this.filtereventsnum = filtereventsnum;
    }

    public Long getChannelbytesmax() {
        return channelbytesmax;
    }

    public void setChannelbytesmax(Long channelbytesmax) {
        this.channelbytesmax = channelbytesmax;
    }

    public Long getChannelcountmax() {
        return channelcountmax;
    }

    public void setChannelcountmax(Long channelcountmax) {
        this.channelcountmax = channelcountmax;
    }

    public Double getChannelbytessize() {
        return channelbytessize;
    }

    public void setChannelbytessize(Double channelbytessize) {
        this.channelbytessize = channelbytessize;
    }

    public Double getChannelbytessizemin() {
        return channelbytessizemin;
    }

    public void setChannelbytessizemin(Double channelbytessizemin) {
        this.channelbytessizemin = channelbytessizemin;
    }

    public Double getChannelbytessizemax() {
        return channelbytessizemax;
    }

    public void setChannelbytessizemax(Double channelbytessizemax) {
        this.channelbytessizemax = channelbytessizemax;
    }

    public Double getChannelbytessizemean() {
        return channelbytessizemean;
    }

    public void setChannelbytessizemean(Double channelbytessizemean) {
        this.channelbytessizemean = channelbytessizemean;
    }

    public Double getChannelbytessizestd() {
        return channelbytessizestd;
    }

    public void setChannelbytessizestd(Double channelbytessizestd) {
        this.channelbytessizestd = channelbytessizestd;
    }

    public Double getChannelbytessize55quantile() {
        return channelbytessize55quantile;
    }

    public void setChannelbytessize55quantile(Double channelbytessize55quantile) {
        this.channelbytessize55quantile = channelbytessize55quantile;
    }

    public Double getChannelbytessize75quantile() {
        return channelbytessize75quantile;
    }

    public void setChannelbytessize75quantile(Double channelbytessize75quantile) {
        this.channelbytessize75quantile = channelbytessize75quantile;
    }

    public Double getChannelbytessize95quantile() {
        return channelbytessize95quantile;
    }

    public void setChannelbytessize95quantile(Double channelbytessize95quantile) {
        this.channelbytessize95quantile = channelbytessize95quantile;
    }

    public Double getChannelbytessize99quantile() {
        return channelbytessize99quantile;
    }

    public void setChannelbytessize99quantile(Double channelbytessize99quantile) {
        this.channelbytessize99quantile = channelbytessize99quantile;
    }

    public Double getChannelcountsize() {
        return channelcountsize;
    }

    public void setChannelcountsize(Double channelcountsize) {
        this.channelcountsize = channelcountsize;
    }

    public Double getChannelcountsizemin() {
        return channelcountsizemin;
    }

    public void setChannelcountsizemin(Double channelcountsizemin) {
        this.channelcountsizemin = channelcountsizemin;
    }

    public Double getChannelcountsizemax() {
        return channelcountsizemax;
    }

    public void setChannelcountsizemax(Double channelcountsizemax) {
        this.channelcountsizemax = channelcountsizemax;
    }

    public Double getChannelcountsizemean() {
        return channelcountsizemean;
    }

    public void setChannelcountsizemean(Double channelcountsizemean) {
        this.channelcountsizemean = channelcountsizemean;
    }

    public Double getChannelcountsizestd() {
        return channelcountsizestd;
    }

    public void setChannelcountsizestd(Double channelcountsizestd) {
        this.channelcountsizestd = channelcountsizestd;
    }

    public Double getChannelcountsize55quantile() {
        return channelcountsize55quantile;
    }

    public void setChannelcountsize55quantile(Double channelcountsize55quantile) {
        this.channelcountsize55quantile = channelcountsize55quantile;
    }

    public Double getChannelcountsize75quantile() {
        return channelcountsize75quantile;
    }

    public void setChannelcountsize75quantile(Double channelcountsize75quantile) {
        this.channelcountsize75quantile = channelcountsize75quantile;
    }

    public Double getChannelcountsize95quantile() {
        return channelcountsize95quantile;
    }

    public void setChannelcountsize95quantile(Double channelcountsize95quantile) {
        this.channelcountsize95quantile = channelcountsize95quantile;
    }

    public Double getChannelcountsize99quantile() {
        return channelcountsize99quantile;
    }

    public void setChannelcountsize99quantile(Double channelcountsize99quantile) {
        this.channelcountsize99quantile = channelcountsize99quantile;
    }

    public Double getChannelusedpercent() {
        return channelusedpercent;
    }

    public void setChannelusedpercent(Double channelusedpercent) {
        this.channelusedpercent = channelusedpercent;
    }

    public Double getChannelusedpercentmin() {
        return channelusedpercentmin;
    }

    public void setChannelusedpercentmin(Double channelusedpercentmin) {
        this.channelusedpercentmin = channelusedpercentmin;
    }

    public Double getChannelusedpercentmax() {
        return channelusedpercentmax;
    }

    public void setChannelusedpercentmax(Double channelusedpercentmax) {
        this.channelusedpercentmax = channelusedpercentmax;
    }

    public Double getChannelusedpercentmean() {
        return channelusedpercentmean;
    }

    public void setChannelusedpercentmean(Double channelusedpercentmean) {
        this.channelusedpercentmean = channelusedpercentmean;
    }

    public Double getChannelusedpercentstd() {
        return channelusedpercentstd;
    }

    public void setChannelusedpercentstd(Double channelusedpercentstd) {
        this.channelusedpercentstd = channelusedpercentstd;
    }

    public Double getChannelusedpercent55quantile() {
        return channelusedpercent55quantile;
    }

    public void setChannelusedpercent55quantile(Double channelusedpercent55quantile) {
        this.channelusedpercent55quantile = channelusedpercent55quantile;
    }

    public Double getChannelusedpercent75quantile() {
        return channelusedpercent75quantile;
    }

    public void setChannelusedpercent75quantile(Double channelusedpercent75quantile) {
        this.channelusedpercent75quantile = channelusedpercent75quantile;
    }

    public Double getChannelusedpercent95quantile() {
        return channelusedpercent95quantile;
    }

    public void setChannelusedpercent95quantile(Double channelusedpercent95quantile) {
        this.channelusedpercent95quantile = channelusedpercent95quantile;
    }

    public Double getChannelusedpercent99quantile() {
        return channelusedpercent99quantile;
    }

    public void setChannelusedpercent99quantile(Double channelusedpercent99quantile) {
        this.channelusedpercent99quantile = channelusedpercent99quantile;
    }

    public Long getReceiverclusterid() {
        return receiverclusterid;
    }

    public void setReceiverclusterid(Long receiverclusterid) {
        this.receiverclusterid = receiverclusterid;
    }

    public String getReceiverclustertopic() {
        return receiverclustertopic;
    }

    public void setReceiverclustertopic(String receiverclustertopic) {
        this.receiverclustertopic = receiverclustertopic;
    }

    public String getCollectfiles() {
        return collectfiles;
    }

    public void setCollectfiles(String collectfiles) {
        this.collectfiles = collectfiles;
    }

    public Integer getRelatedfiles() {
        return relatedfiles;
    }

    public void setRelatedfiles(Integer relatedfiles) {
        this.relatedfiles = relatedfiles;
    }

    public String getLatestfile() {
        return latestfile;
    }

    public void setLatestfile(String latestfile) {
        this.latestfile = latestfile;
    }

    public String getMasterfile() {
        return masterfile;
    }

    public void setMasterfile(String masterfile) {
        this.masterfile = masterfile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getCollecttasktype() {
        return collecttasktype;
    }

    public void setCollecttasktype(Integer collecttasktype) {
        this.collecttasktype = collecttasktype;
    }

    public Integer getSinknum() {
        return sinknum;
    }

    public void setSinknum(Integer sinknum) {
        this.sinknum = sinknum;
    }

    public Integer getCollecttaskversion() {
        return collecttaskversion;
    }

    public void setCollecttaskversion(Integer collecttaskversion) {
        this.collecttaskversion = collecttaskversion;
    }

    public Long getDynamiclimiterthreshold() {
        return dynamiclimiterthreshold;
    }

    public void setDynamiclimiterthreshold(Long dynamiclimiterthreshold) {
        this.dynamiclimiterthreshold = dynamiclimiterthreshold;
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
}