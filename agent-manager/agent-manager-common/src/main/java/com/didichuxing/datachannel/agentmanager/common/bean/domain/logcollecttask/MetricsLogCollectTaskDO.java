package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import lombok.Data;

@Data
public class MetricsLogCollectTaskDO {

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

    private Integer taskStatus;

}
