package com.didichuxing.datachannel.agentmanager.common.bean.vo.logcollecttask;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "采集任务指标", description = "")
public class CollectTaskMetricVO {

    @ApiModelProperty(value = "指标记录 id")
    private Long id;

    @ApiModelProperty(value = "日志采集任务 id")
    private Long collecttaskid;

    @ApiModelProperty(value = "日志采集路径 id")
    private Long pathid;

    @ApiModelProperty(value = "agent宿主机主机名")
    private String agenthostname;

    @ApiModelProperty(value = "日志采集任务对应主机名")
    private String collecttaskhostname;

    @ApiModelProperty(value = "agent宿主机 ip")
    private String agenthostip;

    @ApiModelProperty(value = "业务时间戳")
    private Long businesstimestamp;

    @ApiModelProperty(value = "业务时间最大延时")
    private Long maxbusinesstimestampdelay;

    @ApiModelProperty(value = "限流时长")
    private Long limittime;

    @ApiModelProperty(value = "采样周期内日志过长被截断条数")
    private Long toolargetruncatenum;

    @ApiModelProperty(value = "agent 启动以来日志过长被截断条数")
    private Long toolargetruncatenumtotal;

    @ApiModelProperty(value = "日志采集路径是否存在")
    private Integer collectpathisexists;

    @ApiModelProperty(value = "待采集日志路径是否存在乱序")
    private Integer disorderexists;

    @ApiModelProperty(value = "待采集日志路径是否存在日志切片错误")
    private Integer sliceerrorexists;

    @ApiModelProperty(value = "采样周期内读取字节数")
    private Long readbytes;

    @ApiModelProperty(value = "采样周期内读取日志条数")
    private Long readcount;

    @ApiModelProperty(value = "采样周期内发送字节数")
    private Long sendbytes;

    @ApiModelProperty(value = "采样周期内发送日志条数")
    private Long sendcount;

    @ApiModelProperty(value = "采样周期内单条日志读取耗时")
    private Double readtimeperevent;

    @ApiModelProperty(value = "采样周期内单条日志读取最小耗时")
    private Double readtimepereventmin;

    @ApiModelProperty(value = "采样周期内单条日志读取最大耗时")
    private Double readtimepereventmax;

    @ApiModelProperty(value = "采样周期内单条日志读取平均耗时")
    private Double readtimepereventmean;

    @ApiModelProperty(value = "采样周期内单条日志读取耗时标准差")
    private Double readtimepereventstd;

    @ApiModelProperty(value = "采样周期内单条日志读取耗时55分位数")
    private Double readtimeperevent55quantile;

    @ApiModelProperty(value = "采样周期内单条日志读取耗时75分位数")
    private Double readtimeperevent75quantile;

    @ApiModelProperty(value = "采样周期内单条日志读取耗时95分位数")
    private Double readtimeperevent95quantile;

    @ApiModelProperty(value = "采样周期内单条日志读取耗时99分位数")
    private Double readtimeperevent99quantile;

    @ApiModelProperty(value = "采样周期内单次发送耗时")
    private Double sendtime;

    @ApiModelProperty(value = "采样周期内单次发送最小耗时")
    private Double sendtimemin;

    @ApiModelProperty(value = "采样周期内单次发送最大耗时")
    private Double sendtimemax;

    @ApiModelProperty(value = "采样周期内单次发送耗时均值")
    private Double sendtimemean;

    @ApiModelProperty(value = "采样周期内单次发送耗时标准差")
    private Double sendtimestd;

    @ApiModelProperty(value = "采样周期内单次发送耗时55分位数")
    private Double sendtime55quantile;

    @ApiModelProperty(value = "采样周期内单次发送耗时75分位数")
    private Double sendtime75quantile;

    @ApiModelProperty(value = "采样周期内单次发送耗时95分位数")
    private Double sendtime95quantile;

    @ApiModelProperty(value = "采样周期内单次发送耗时99分位数")
    private Double sendtime99quantile;

    @ApiModelProperty(value = "采样周期内单次flush耗时")
    private Double flushtime;

    @ApiModelProperty(value = "采样周期内单次flush最小耗时")
    private Double flushtimemin;

    @ApiModelProperty(value = "采样周期内单次flush最大耗时")
    private Double flushtimemax;

    @ApiModelProperty(value = "采样周期内单次flush平均耗时")
    private Double flushtimemean;

    @ApiModelProperty(value = "采样周期内单次flush耗时标准差")
    private Double flushtimestd;

    @ApiModelProperty(value = "采样周期内单次flush耗时55分位数")
    private Double flushtime55quantile;

    @ApiModelProperty(value = "采样周期内单次flush耗时75分位数")
    private Double flushtime75quantile;

    @ApiModelProperty(value = "采样周期内单次flush耗时95分位数")
    private Double flushtime95quantile;

    @ApiModelProperty(value = "采样周期内单次flush耗时99分位数")
    private Double flushtime99quantile;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时")
    private Double processtimeperevent;

    @ApiModelProperty(value = "采样周期内单条日志处理最小耗时")
    private Double processtimepereventmin;

    @ApiModelProperty(value = "采样周期内单条日志处理最大耗时")
    private Double processtimepereventmax;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时均值")
    private Double processtimepereventmean;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时标准差")
    private Double processtimepereventstd;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时 55 分位数")
    private Double processtimeperevent55quantile;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时 75 分位数")
    private Double processtimeperevent75quantile;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时 95 分位数")
    private Double processtimeperevent95quantile;

    @ApiModelProperty(value = "采样周期内单条日志处理耗时 99 分位数")
    private Double processtimeperevent99quantile;

    @ApiModelProperty(value = "采样周期内flush数")
    private Long flushtimes;

    @ApiModelProperty(value = "采样周期内flush失败数")
    private Long flushfailedtimes;

    @ApiModelProperty(value = "采样周期内日志过滤条数")
    private Long filtereventsnum;

    @ApiModelProperty(value = "channel 最大容量 - 字节数")
    private Long channelbytesmax;

    @ApiModelProperty(value = "channel 最大容量 - 日志条数")
    private Long channelcountmax;

    @ApiModelProperty(value = "channel 日志字节数")
    private Double channelbytessize;

    @ApiModelProperty(value = "channel 日志字节数最小值")
    private Double channelbytessizemin;

    @ApiModelProperty(value = "channel 日志字节数最大值")
    private Double channelbytessizemax;

    @ApiModelProperty(value = "channel 日志字节数均值")
    private Double channelbytessizemean;

    @ApiModelProperty(value = "channel 日志字节数标准差")
    private Double channelbytessizestd;

    @ApiModelProperty(value = "channel 日志字节数 55 分位数")
    private Double channelbytessize55quantile;

    @ApiModelProperty(value = "channel 日志字节数 75 分位数")
    private Double channelbytessize75quantile;

    @ApiModelProperty(value = "channel 日志字节数 95 分位数")
    private Double channelbytessize95quantile;

    @ApiModelProperty(value = "channel 日志字节数 99 分位数")
    private Double channelbytessize99quantile;

    @ApiModelProperty(value = "channel 日志条数")
    private Double channelcountsize;

    @ApiModelProperty(value = "channel 日志条数最小值")
    private Double channelcountsizemin;

    @ApiModelProperty(value = "channel 日志条数最大值")
    private Double channelcountsizemax;

    @ApiModelProperty(value = "channel 日志条数均值")
    private Double channelcountsizemean;

    @ApiModelProperty(value = "channel 日志条数标准差")
    private Double channelcountsizestd;

    @ApiModelProperty(value = "channel 日志条数 55 分位数")
    private Double channelcountsize55quantile;

    @ApiModelProperty(value = "channel 日志条数 75 分位数")
    private Double channelcountsize75quantile;

    @ApiModelProperty(value = "channel 日志条数 95 分位数")
    private Double channelcountsize95quantile;

    @ApiModelProperty(value = "channel 日志条数 99 分位数")
    private Double channelcountsize99quantile;

    @ApiModelProperty(value = "channel 使用率")
    private Double channelusedpercent;

    @ApiModelProperty(value = "channel 使用率最小值")
    private Double channelusedpercentmin;

    @ApiModelProperty(value = "channel 使用率最大值")
    private Double channelusedpercentmax;

    @ApiModelProperty(value = "channel 使用率均值")
    private Double channelusedpercentmean;

    @ApiModelProperty(value = "channel 使用率标准差")
    private Double channelusedpercentstd;

    @ApiModelProperty(value = "channel 使用率 55 分位数")
    private Double channelusedpercent55quantile;

    @ApiModelProperty(value = "channel 使用率 75 分位数")
    private Double channelusedpercent75quantile;

    @ApiModelProperty(value = "channel 使用率 95 分位数")
    private Double channelusedpercent95quantile;

    @ApiModelProperty(value = "channel 使用率 99 分位数")
    private Double channelusedpercent99quantile;

    @ApiModelProperty(value = "数据流对应下游接收端 id")
    private Long receiverclusterid;

    @ApiModelProperty(value = "数据流对应下游接收端 topic")
    private String receiverclustertopic;

    @ApiModelProperty(value = "采集路径关联的文件（集）信息")
    private String collectfiles;

    @ApiModelProperty(value = "采集路径关联的文件数")
    private Integer relatedfiles;

    @ApiModelProperty(value = "待采集最后一个文件名")
    private String latestfile;

    @ApiModelProperty(value = "待采集主文件路径h")
    private String masterfile;

    @ApiModelProperty(value = "采集路径")
    private String path;

    @ApiModelProperty(value = "日志采集任务类型 0：流式 1：时间范围采集")
    private Integer collecttasktype;

    @ApiModelProperty(value = "sink 端数")
    private Integer sinknum;

    @ApiModelProperty(value = "日志采集任务版本号")
    private Integer collecttaskversion;

    @ApiModelProperty(value = "当前限流阈值")
    private Long dynamiclimiterthreshold;

    @ApiModelProperty(value = "心跳时间 精度：毫秒")
    private Long heartbeattime;

    @ApiModelProperty(value = "心跳时间 精度：分钟")
    private Long heartbeattimeminute;

    @ApiModelProperty(value = "心跳时间 精度：小时")
    private Long heartbeattimehour;

    @ApiModelProperty(value = "心跳时间 精度：日")
    private Long heartbeatTimeDay;

    @ApiModelProperty(value = "服务名集")
    private String serviceNames;

    @ApiModelProperty(value = "任务状态 0停止 1运行中 2完成")
    private Integer taskStatus;

}
