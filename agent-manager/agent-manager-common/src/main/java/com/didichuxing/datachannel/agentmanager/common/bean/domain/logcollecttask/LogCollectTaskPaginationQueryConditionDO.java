package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LogCollectTaskPaginationQueryConditionDO {

    /**
     * 日志采集任务名
     */
    private String logCollectTaskName;
    /**
     * 日志采集任务id
     */
    private Long logCollectTaskId;
    /**
     * 采集任务类型 0：常规流式采集 1：按指定时间范围采集
     */
    private List<Integer> logCollectTaskTypeList;
    /**
     * 日志采集任务健康度
     */
    private List<Integer> logCollectTaskHealthLevelList;
    /**
     * 服务id
     */
    private List<Long> serviceIdList;
    /**
     * kafka创建时间开始查询时间
     */
    private Date createTimeStart;
    /**
     * kafka创建时间结束查询时间
     */
    private Date createTimeEnd;
    /**
     * 从第几行开始
     */
    private Integer limitFrom;
    /**
     * 获取满足条件的 top limitSize 结果集行数
     */
    private Integer limitSize;

    /**
     * 排序依照的字段
     */
    private String sortColumn;

    /**
     * 是否升序
     */
    private Boolean asc;

    /**
     * 查询关键字
     */
    private String queryTerm;

    /**
     * 日志采集任务状态 0：暂停 1：运行 2：已完成（状态2仅针对 "按指定时间范围采集" 类型）
     */
    private List<Integer> logCollectTaskStatusList;

    public LogCollectTaskPaginationQueryConditionDO() {

    }

}
