package com.didichuxing.datachannel.agentmanager.common.bean.po.logcollecttask;

import com.alibaba.fastjson.annotation.JSONField;
import com.didichuxing.datachannel.agentmanager.common.bean.po.BasePO;
import lombok.Data;


@Data
public class CollectTaskMetricPO extends BasePO {
    @JSONField(deserialize = false, serialize = false)
    private Long id;

    private Long readTimeMean;

    private Integer filterRemained;

    private String channelCapacity;

    private Boolean isFileExist;

    private Long pathId;

    private String type;

    private Integer readCount;

    private Long sendTimeMean;

    private String masterFile;

    private String path;

    private String hostname;

    private Long heartbeatTime;

    private String hostIp;

    private Integer sinkNum;

    private Long flushTimeMean;

    private String latestFile;

    private Integer filterTooLargeCount;

    @JSONField(name = "channel.type")
    private String channelType;

    private Integer logModelVersion;

    private String topic;

    private Integer flushCount;

    private Long flushTimeMax;

    private Integer filterOut;

    private Integer relatedFiles;

    private String logModelHostName;

    private Long clusterId;

    private Integer limitRate;

    private Long controlTimeMean;

    private Long limitTime;

    private Long logModeId;

    private Long flushTimeMin;

    private Long readTimeMin;

    private Long sendTimeMax;

    private Integer dynamicLimiter;

    private String logPathKey;

    private Long maxTimeGap;

    private Integer sendByte;

    private Long sendTimeMin;

    private String logTimeStr;

    private Long controlTimeMax;

    private Integer sendCount;

    @JSONField(name = "source.type")
    private String sourceType;

    private Long logTime;

    private Integer flushFailedCount;

    private Integer channelSize;

    private Integer filterTotalTooLargeCount;

    private String collectFiles;

    private Long controlTimeMin;

    private Integer readByte;

    private Long readTimeMax;

    private Boolean validTimeConfig;

}