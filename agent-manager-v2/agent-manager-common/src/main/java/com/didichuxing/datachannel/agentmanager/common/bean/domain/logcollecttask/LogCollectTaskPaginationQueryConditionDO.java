package com.didichuxing.datachannel.agentmanager.common.bean.domain.logcollecttask;

import java.util.Date;
import java.util.List;

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
     * 项目id
     */
    private Long projectId;

    public LogCollectTaskPaginationQueryConditionDO() {
    }

    public String getLogCollectTaskName() {
        return logCollectTaskName;
    }

    public void setLogCollectTaskName(String logCollectTaskName) {
        this.logCollectTaskName = logCollectTaskName;
    }

    public Long getLogCollectTaskId() {
        return logCollectTaskId;
    }

    public void setLogCollectTaskId(Long logCollectTaskId) {
        this.logCollectTaskId = logCollectTaskId;
    }

    public List<Integer> getLogCollectTaskTypeList() {
        return logCollectTaskTypeList;
    }

    public void setLogCollectTaskTypeList(List<Integer> logCollectTaskTypeList) {
        this.logCollectTaskTypeList = logCollectTaskTypeList;
    }

    public List<Integer> getLogCollectTaskHealthLevelList() {
        return logCollectTaskHealthLevelList;
    }

    public void setLogCollectTaskHealthLevelList(List<Integer> logCollectTaskHealthLevelList) {
        this.logCollectTaskHealthLevelList = logCollectTaskHealthLevelList;
    }

    public List<Long> getServiceIdList() {
        return serviceIdList;
    }

    public void setServiceIdList(List<Long> serviceIdList) {
        this.serviceIdList = serviceIdList;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Integer getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom(Integer limitFrom) {
        this.limitFrom = limitFrom;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }
}
