package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "采集任务指标", description = "")
public class CollectTaskMetricVO {
    private Long id;

    @ApiModelProperty(value = "平均读取时间ns")
    private Long readTimeMean;

    @ApiModelProperty(value = "日志过滤剩余数")
    private Integer filterRemained;

    @ApiModelProperty(value = "channel容量")
    private String channelCapacity;

    @ApiModelProperty(value = "文件是否存在")
    private Boolean isFileExist;

    @ApiModelProperty(value = "采集路径id")
    private Long pathId;

    @ApiModelProperty(value = "消息发送类型，例如kafka")
    private String type;

    @ApiModelProperty(value = "读取数")
    private Integer readCount;

    @ApiModelProperty(value = "平均发送时间ns")
    private Long sendTimeMean;

    @ApiModelProperty(value = "主文件名")
    private String masterFile;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "agent宿主机名")
    private String hostname;

    @ApiModelProperty(value = "心跳时间")
    private Long heartbeatTime;

    @ApiModelProperty(value = "宿主机ip")
    private String hostIp;

    @ApiModelProperty(value = "清洗数量")
    private Integer sinkNum;

    @ApiModelProperty(value = "平均flush时间ns")
    private Long flushTimeMean;

    @ApiModelProperty(value = "最晚采集日志文件名")
    private String latestFile;

    @ApiModelProperty(value = "日志过大截断数")
    private Integer filterTooLargeCount;

    @ApiModelProperty(value = "channel类型")
    @JSONField(name = "channel.type")
    private String channelType;

    @ApiModelProperty(value = "log model版本")
    private Integer logModelVersion;

    @ApiModelProperty(value = "topic名称")
    private String topic;

    @ApiModelProperty(value = "flush次数")
    private Integer flushCount;

    @ApiModelProperty(value = "最大flush时间ns")
    private Long flushTimeMax;

    @ApiModelProperty(value = "过滤条数")
    private Integer filterOut;

    @ApiModelProperty(value = "关联文件数")
    private Integer relatedFiles;

    @ApiModelProperty(value = "log model所在的主机名（采集容器则为容器名，采集主机则为主机名）")
    private String logModelHostName;

    @ApiModelProperty(value = "cluster id")
    private Long clusterId;

    @ApiModelProperty(value = "cpu限流时长")
    private Integer limitRate;

    @ApiModelProperty(value = "平均control时间")
    private Long controlTimeMean;

    @ApiModelProperty(value = "限流时长")
    private Long limitTime;

    @ApiModelProperty(value = "log model id（采集任务id）")
    private Long logModeId;

    @ApiModelProperty(value = "平均flush时间")
    private Long flushTimeMin;

    @ApiModelProperty(value = "最小读取时间")
    private Long readTimeMin;

    @ApiModelProperty(value = "最大发送时间")
    private Long sendTimeMax;

    @ApiModelProperty(value = "")
    private Integer dynamicLimiter;

    @ApiModelProperty(value = "采集路径唯一key")
    private String logPathKey;

    @ApiModelProperty(value = "日志最大时间间隔")
    private Long maxTimeGap;

    @ApiModelProperty(value = "发送byte数")
    private Integer sendByte;

    @ApiModelProperty(value = "最小发送时间")
    private Long sendTimeMin;

    @ApiModelProperty(value = "日志业务时间（格式化）")
    private String logTimeStr;

    @ApiModelProperty(value = "最大control时间")
    private Long controlTimeMax;

    @ApiModelProperty(value = "发送日志数量")
    private Integer sendCount;

    @ApiModelProperty(value = "消息源类型")
    @JSONField(name = "source.type")
    private String sourceType;

    @ApiModelProperty(value = "日志业务时间戳")
    private Long logTime;

    @ApiModelProperty(value = "flush失败次数")
    private Integer flushFailedCount;

    @ApiModelProperty(value = "channel大小")
    private Integer channelSize;

    @ApiModelProperty(value = "超长截断总数")
    private Integer filterTotalTooLargeCount;

    @ApiModelProperty(value = "正在采集的文件列表")
    private List<CollectFileVO> collectFiles;

    @ApiModelProperty(value = "最小control时间")
    private Long controlTimeMin;

    @ApiModelProperty(value = "读取byte数")
    private Integer readByte;

    @ApiModelProperty(value = "最大读取时间")
    private Long readTimeMax;

    @ApiModelProperty(value = "*无效字段* 时间格式是否合法")
    private Boolean validTimeConfig;

    @ApiModelProperty(value = "任务状态 0停止 1运行中 2完成")
    private Integer taskStatus;

}
