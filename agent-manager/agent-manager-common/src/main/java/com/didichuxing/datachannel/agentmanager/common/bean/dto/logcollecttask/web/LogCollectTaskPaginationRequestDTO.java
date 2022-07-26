package com.didichuxing.datachannel.agentmanager.common.bean.dto.logcollecttask.web;

import com.didichuxing.datachannel.agentmanager.common.bean.dto.PaginationRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LogCollectTaskPaginationRequestDTO extends PaginationRequestDTO {

    @ApiModelProperty(value = "日志采集任务名")
    private String logCollectTaskName;

    @ApiModelProperty(value = "采集任务类型 0：常规流式采集 1：按指定时间范围采集")
    private List<Integer> logCollectTaskTypeList;

    @ApiModelProperty(value = "日志采集任务健康度 ", notes="")
    private List<Integer> logCollectTaskHealthLevelList;

    @ApiModelProperty(value = "日志采集任务id", notes="")
    private Long logCollectTaskId;

    @ApiModelProperty(value = "服务id")
    private List<Long> serviceIdList;

    @ApiModelProperty(value = "日志采集任务创建时间起始检索时间", notes="")
    private Long locCollectTaskCreateTimeStart;

    @ApiModelProperty(value = "日志采集任务创建时间结束检索时间", notes="")
    private Long locCollectTaskCreateTimeEnd;

    @ApiModelProperty(value = "排序依照的字段，可选log_collect_task_finish_time create_time", notes="")
    private String sortColumn;

    @ApiModelProperty(value = "是否升序", notes="")
    private Boolean asc;

    @ApiModelProperty(value = "日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 \"按指定时间范围采集\" 类型）", notes="")
    private List<Integer> logCollectTaskStatusList;

}
