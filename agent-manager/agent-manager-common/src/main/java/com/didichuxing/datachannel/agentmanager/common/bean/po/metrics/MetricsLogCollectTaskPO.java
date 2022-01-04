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

    private Byte collectpathisexists;

    private Byte disorderexists;

    private Byte sliceerrorexists;

    private Long readbytes;

    private Long readcount;

    private Long sendbytes;

    private Long sendcount;

    private Long readtimeperevent;

    private Long readtimepereventmin;

    private Long readtimepereventmax;

    private Long readtimepereventmean;

    private Long readtimepereventstd;

    private Long readtimeperevent55quantile;

    private Long readtimeperevent75quantile;

    private Long readtimeperevent95quantile;

    private Long readtimeperevent99quantile;

    private Long sendtime;

    private Long sendtimemin;

    private Long sendtimemax;

    private Long sendtimemean;

    private Long sendtimestd;

    private Long sendtime55quantile;

    private Long sendtime75quantile;

    private Long sendtime95quantile;

    private Long sendtime99quantile;

    private Long flushtime;

    private Long flushtimemin;

    private Long flushtimemax;

    private Long flushtimemean;

    private Long flushtimestd;

    private Long flushtime55quantile;

    private Long flushtime75quantile;

    private Long flushtime95quantile;

    private Long flushtime99quantile;

    private Long processtimeperevent;

    private Long processtimepereventmin;

    private Long processtimepereventmax;

    private Long processtimepereventmean;

    private Long processtimepereventstd;

    private Long processtimeperevent55quantile;

    private Long processtimeperevent75quantile;

    private Long processtimeperevent95quantile;

    private Long processtimeperevent99quantile;

    private Long flushtimes;

    private Long flushfailedtimes;

    private Long filtereventsnum;

    private Long channelbytesmax;

    private Long channelcountmax;

    private Long channelbytessize;

    private Long channelbytessizemin;

    private Long channelbytessizemax;

    private Long channelbytessizemean;

    private Long channelbytessizestd;

    private Long channelbytessize55quantile;

    private Long channelbytessize75quantile;

    private Long channelbytessize95quantile;

    private Long channelbytessize99quantile;

    private Long channelcountsize;

    private Long channelcountsizemin;

    private Long channelcountsizemax;

    private Long channelcountsizemean;

    private Long channelcountsizestd;

    private Long channelcountsize55quantile;

    private Long channelcountsize75quantile;

    private Long channelcountsize95quantile;

    private Long channelcountsize99quantile;

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

    public Byte getCollectpathisexists() {
        return collectpathisexists;
    }

    public void setCollectpathisexists(Byte collectpathisexists) {
        this.collectpathisexists = collectpathisexists;
    }

    public Byte getDisorderexists() {
        return disorderexists;
    }

    public void setDisorderexists(Byte disorderexists) {
        this.disorderexists = disorderexists;
    }

    public Byte getSliceerrorexists() {
        return sliceerrorexists;
    }

    public void setSliceerrorexists(Byte sliceerrorexists) {
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

    public Long getReadtimeperevent() {
        return readtimeperevent;
    }

    public void setReadtimeperevent(Long readtimeperevent) {
        this.readtimeperevent = readtimeperevent;
    }

    public Long getReadtimepereventmin() {
        return readtimepereventmin;
    }

    public void setReadtimepereventmin(Long readtimepereventmin) {
        this.readtimepereventmin = readtimepereventmin;
    }

    public Long getReadtimepereventmax() {
        return readtimepereventmax;
    }

    public void setReadtimepereventmax(Long readtimepereventmax) {
        this.readtimepereventmax = readtimepereventmax;
    }

    public Long getReadtimepereventmean() {
        return readtimepereventmean;
    }

    public void setReadtimepereventmean(Long readtimepereventmean) {
        this.readtimepereventmean = readtimepereventmean;
    }

    public Long getReadtimepereventstd() {
        return readtimepereventstd;
    }

    public void setReadtimepereventstd(Long readtimepereventstd) {
        this.readtimepereventstd = readtimepereventstd;
    }

    public Long getReadtimeperevent55quantile() {
        return readtimeperevent55quantile;
    }

    public void setReadtimeperevent55quantile(Long readtimeperevent55quantile) {
        this.readtimeperevent55quantile = readtimeperevent55quantile;
    }

    public Long getReadtimeperevent75quantile() {
        return readtimeperevent75quantile;
    }

    public void setReadtimeperevent75quantile(Long readtimeperevent75quantile) {
        this.readtimeperevent75quantile = readtimeperevent75quantile;
    }

    public Long getReadtimeperevent95quantile() {
        return readtimeperevent95quantile;
    }

    public void setReadtimeperevent95quantile(Long readtimeperevent95quantile) {
        this.readtimeperevent95quantile = readtimeperevent95quantile;
    }

    public Long getReadtimeperevent99quantile() {
        return readtimeperevent99quantile;
    }

    public void setReadtimeperevent99quantile(Long readtimeperevent99quantile) {
        this.readtimeperevent99quantile = readtimeperevent99quantile;
    }

    public Long getSendtime() {
        return sendtime;
    }

    public void setSendtime(Long sendtime) {
        this.sendtime = sendtime;
    }

    public Long getSendtimemin() {
        return sendtimemin;
    }

    public void setSendtimemin(Long sendtimemin) {
        this.sendtimemin = sendtimemin;
    }

    public Long getSendtimemax() {
        return sendtimemax;
    }

    public void setSendtimemax(Long sendtimemax) {
        this.sendtimemax = sendtimemax;
    }

    public Long getSendtimemean() {
        return sendtimemean;
    }

    public void setSendtimemean(Long sendtimemean) {
        this.sendtimemean = sendtimemean;
    }

    public Long getSendtimestd() {
        return sendtimestd;
    }

    public void setSendtimestd(Long sendtimestd) {
        this.sendtimestd = sendtimestd;
    }

    public Long getSendtime55quantile() {
        return sendtime55quantile;
    }

    public void setSendtime55quantile(Long sendtime55quantile) {
        this.sendtime55quantile = sendtime55quantile;
    }

    public Long getSendtime75quantile() {
        return sendtime75quantile;
    }

    public void setSendtime75quantile(Long sendtime75quantile) {
        this.sendtime75quantile = sendtime75quantile;
    }

    public Long getSendtime95quantile() {
        return sendtime95quantile;
    }

    public void setSendtime95quantile(Long sendtime95quantile) {
        this.sendtime95quantile = sendtime95quantile;
    }

    public Long getSendtime99quantile() {
        return sendtime99quantile;
    }

    public void setSendtime99quantile(Long sendtime99quantile) {
        this.sendtime99quantile = sendtime99quantile;
    }

    public Long getFlushtime() {
        return flushtime;
    }

    public void setFlushtime(Long flushtime) {
        this.flushtime = flushtime;
    }

    public Long getFlushtimemin() {
        return flushtimemin;
    }

    public void setFlushtimemin(Long flushtimemin) {
        this.flushtimemin = flushtimemin;
    }

    public Long getFlushtimemax() {
        return flushtimemax;
    }

    public void setFlushtimemax(Long flushtimemax) {
        this.flushtimemax = flushtimemax;
    }

    public Long getFlushtimemean() {
        return flushtimemean;
    }

    public void setFlushtimemean(Long flushtimemean) {
        this.flushtimemean = flushtimemean;
    }

    public Long getFlushtimestd() {
        return flushtimestd;
    }

    public void setFlushtimestd(Long flushtimestd) {
        this.flushtimestd = flushtimestd;
    }

    public Long getFlushtime55quantile() {
        return flushtime55quantile;
    }

    public void setFlushtime55quantile(Long flushtime55quantile) {
        this.flushtime55quantile = flushtime55quantile;
    }

    public Long getFlushtime75quantile() {
        return flushtime75quantile;
    }

    public void setFlushtime75quantile(Long flushtime75quantile) {
        this.flushtime75quantile = flushtime75quantile;
    }

    public Long getFlushtime95quantile() {
        return flushtime95quantile;
    }

    public void setFlushtime95quantile(Long flushtime95quantile) {
        this.flushtime95quantile = flushtime95quantile;
    }

    public Long getFlushtime99quantile() {
        return flushtime99quantile;
    }

    public void setFlushtime99quantile(Long flushtime99quantile) {
        this.flushtime99quantile = flushtime99quantile;
    }

    public Long getProcesstimeperevent() {
        return processtimeperevent;
    }

    public void setProcesstimeperevent(Long processtimeperevent) {
        this.processtimeperevent = processtimeperevent;
    }

    public Long getProcesstimepereventmin() {
        return processtimepereventmin;
    }

    public void setProcesstimepereventmin(Long processtimepereventmin) {
        this.processtimepereventmin = processtimepereventmin;
    }

    public Long getProcesstimepereventmax() {
        return processtimepereventmax;
    }

    public void setProcesstimepereventmax(Long processtimepereventmax) {
        this.processtimepereventmax = processtimepereventmax;
    }

    public Long getProcesstimepereventmean() {
        return processtimepereventmean;
    }

    public void setProcesstimepereventmean(Long processtimepereventmean) {
        this.processtimepereventmean = processtimepereventmean;
    }

    public Long getProcesstimepereventstd() {
        return processtimepereventstd;
    }

    public void setProcesstimepereventstd(Long processtimepereventstd) {
        this.processtimepereventstd = processtimepereventstd;
    }

    public Long getProcesstimeperevent55quantile() {
        return processtimeperevent55quantile;
    }

    public void setProcesstimeperevent55quantile(Long processtimeperevent55quantile) {
        this.processtimeperevent55quantile = processtimeperevent55quantile;
    }

    public Long getProcesstimeperevent75quantile() {
        return processtimeperevent75quantile;
    }

    public void setProcesstimeperevent75quantile(Long processtimeperevent75quantile) {
        this.processtimeperevent75quantile = processtimeperevent75quantile;
    }

    public Long getProcesstimeperevent95quantile() {
        return processtimeperevent95quantile;
    }

    public void setProcesstimeperevent95quantile(Long processtimeperevent95quantile) {
        this.processtimeperevent95quantile = processtimeperevent95quantile;
    }

    public Long getProcesstimeperevent99quantile() {
        return processtimeperevent99quantile;
    }

    public void setProcesstimeperevent99quantile(Long processtimeperevent99quantile) {
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

    public Long getChannelbytessize() {
        return channelbytessize;
    }

    public void setChannelbytessize(Long channelbytessize) {
        this.channelbytessize = channelbytessize;
    }

    public Long getChannelbytessizemin() {
        return channelbytessizemin;
    }

    public void setChannelbytessizemin(Long channelbytessizemin) {
        this.channelbytessizemin = channelbytessizemin;
    }

    public Long getChannelbytessizemax() {
        return channelbytessizemax;
    }

    public void setChannelbytessizemax(Long channelbytessizemax) {
        this.channelbytessizemax = channelbytessizemax;
    }

    public Long getChannelbytessizemean() {
        return channelbytessizemean;
    }

    public void setChannelbytessizemean(Long channelbytessizemean) {
        this.channelbytessizemean = channelbytessizemean;
    }

    public Long getChannelbytessizestd() {
        return channelbytessizestd;
    }

    public void setChannelbytessizestd(Long channelbytessizestd) {
        this.channelbytessizestd = channelbytessizestd;
    }

    public Long getChannelbytessize55quantile() {
        return channelbytessize55quantile;
    }

    public void setChannelbytessize55quantile(Long channelbytessize55quantile) {
        this.channelbytessize55quantile = channelbytessize55quantile;
    }

    public Long getChannelbytessize75quantile() {
        return channelbytessize75quantile;
    }

    public void setChannelbytessize75quantile(Long channelbytessize75quantile) {
        this.channelbytessize75quantile = channelbytessize75quantile;
    }

    public Long getChannelbytessize95quantile() {
        return channelbytessize95quantile;
    }

    public void setChannelbytessize95quantile(Long channelbytessize95quantile) {
        this.channelbytessize95quantile = channelbytessize95quantile;
    }

    public Long getChannelbytessize99quantile() {
        return channelbytessize99quantile;
    }

    public void setChannelbytessize99quantile(Long channelbytessize99quantile) {
        this.channelbytessize99quantile = channelbytessize99quantile;
    }

    public Long getChannelcountsize() {
        return channelcountsize;
    }

    public void setChannelcountsize(Long channelcountsize) {
        this.channelcountsize = channelcountsize;
    }

    public Long getChannelcountsizemin() {
        return channelcountsizemin;
    }

    public void setChannelcountsizemin(Long channelcountsizemin) {
        this.channelcountsizemin = channelcountsizemin;
    }

    public Long getChannelcountsizemax() {
        return channelcountsizemax;
    }

    public void setChannelcountsizemax(Long channelcountsizemax) {
        this.channelcountsizemax = channelcountsizemax;
    }

    public Long getChannelcountsizemean() {
        return channelcountsizemean;
    }

    public void setChannelcountsizemean(Long channelcountsizemean) {
        this.channelcountsizemean = channelcountsizemean;
    }

    public Long getChannelcountsizestd() {
        return channelcountsizestd;
    }

    public void setChannelcountsizestd(Long channelcountsizestd) {
        this.channelcountsizestd = channelcountsizestd;
    }

    public Long getChannelcountsize55quantile() {
        return channelcountsize55quantile;
    }

    public void setChannelcountsize55quantile(Long channelcountsize55quantile) {
        this.channelcountsize55quantile = channelcountsize55quantile;
    }

    public Long getChannelcountsize75quantile() {
        return channelcountsize75quantile;
    }

    public void setChannelcountsize75quantile(Long channelcountsize75quantile) {
        this.channelcountsize75quantile = channelcountsize75quantile;
    }

    public Long getChannelcountsize95quantile() {
        return channelcountsize95quantile;
    }

    public void setChannelcountsize95quantile(Long channelcountsize95quantile) {
        this.channelcountsize95quantile = channelcountsize95quantile;
    }

    public Long getChannelcountsize99quantile() {
        return channelcountsize99quantile;
    }

    public void setChannelcountsize99quantile(Long channelcountsize99quantile) {
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